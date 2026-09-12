package dev.fede.nyx.mixin;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.IntPredicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayNetworkHandler.class})
public class BacktrackEntityUpdateMixin {
   @Inject(
      method = {"onEntity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$onEntity(EntityS2CPacket var1, CallbackInfo var2) {
      this.maybeIntercept(var1, extractEntityId(var1), var2);
   }

   @Inject(
      method = {"onEntityPosition"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$onEntityPosition(EntityPositionS2CPacket var1, CallbackInfo var2) {
      this.maybeIntercept(var1, var1.entityId(), var2);
   }

   @Inject(
      method = {"onEntityPositionSync"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$onEntityPositionSync(EntityPositionSyncS2CPacket var1, CallbackInfo var2) {
      this.maybeIntercept(var1, var1.id(), var2);
   }

   private void maybeIntercept(Packet<?> var1, int var2, CallbackInfo var3) {
      if (BacktrackEntityUpdateMixin.BacktrackBuffer.enabled && BacktrackEntityUpdateMixin.BacktrackBuffer.delayMs > 0L) {
         if (!BacktrackEntityUpdateMixin.BacktrackBuffer.bypass.get()) {
            if (BacktrackEntityUpdateMixin.BacktrackBuffer.tracked.contains(var2)) {
               MinecraftClient var4 = MinecraftClient.getInstance();
               if (var4.world != null) {
                  Entity var5 = var4.world.getEntityById(var2);
                  if (var5 != null) {
                     BacktrackEntityUpdateMixin.BacktrackBuffer.snapshot(var2, var5.getEntityPos());
                  }

                  BacktrackEntityUpdateMixin.BacktrackBuffer.enqueue(var2, var1);
                  var3.cancel();
               }
            }
         }
      }
   }

   private static int extractEntityId(EntityS2CPacket var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1.world == null) {
         return -1;
      } else {
         Entity var2 = var0.getEntity(var1.world);
         return var2 == null ? -1 : var2.getId();
      }
   }

   public static final class BacktrackBuffer {
      public static volatile boolean enabled;
      public static volatile long delayMs;
      public static volatile double rangeGateSq;
      public static final ThreadLocal<Boolean> bypass = ThreadLocal.withInitial(() -> false);
      public static volatile Set<Integer> tracked = Collections.emptySet();
      private static final Map<Integer, Deque<Vec3d>> SNAPSHOTS = new HashMap<>();
      private static final int MAX_SNAPSHOTS_PER_ENTITY = 32;
      private static final Map<Integer, Deque<BacktrackEntityUpdateMixin.BacktrackBuffer.Deferred>> DEFERRED = new HashMap<>();

      private BacktrackBuffer() {
      }

      public static synchronized void publishTracked(Set<Integer> ids) {
         tracked = (Set<Integer>)(ids.isEmpty() ? Collections.emptySet() : new HashSet<>(ids));
         SNAPSHOTS.keySet().removeIf(id -> !tracked.contains(id));
         drainSpecific(id -> !tracked.contains(id));
      }

      public static synchronized void snapshot(int var0, Vec3d var1) {
         if (var1 != null) {
            Deque var2 = SNAPSHOTS.computeIfAbsent(var0, k -> new ArrayDeque<>());
            var2.addLast(var1);

            while (var2.size() > 32) {
               var2.pollFirst();
            }
         }
      }

      public static synchronized Vec3d lastSnapshot(int entityId) {
         Deque var1 = SNAPSHOTS.get(entityId);
         return var1 == null ? null : (Vec3d)var1.peekLast();
      }

      public static synchronized void enqueue(int var0, Packet<?> var1) {
         Deque var2 = DEFERRED.computeIfAbsent(var0, k -> new ArrayDeque<>());
         var2.addLast(new BacktrackEntityUpdateMixin.BacktrackBuffer.Deferred(var1, System.currentTimeMillis() + delayMs));
      }

      public static synchronized void drainReady(ClientPlayNetworkHandler var0) {
         if (var0 != null) {
            long var1 = System.currentTimeMillis();
            Iterator var3 = DEFERRED.entrySet().iterator();

            while (var3.hasNext()) {
               Entry var4 = (Entry)var3.next();
               Deque var5 = (Deque)var4.getValue();

               while (!var5.isEmpty() && ((BacktrackEntityUpdateMixin.BacktrackBuffer.Deferred)var5.peekFirst()).releaseAt <= var1) {
                  BacktrackEntityUpdateMixin.BacktrackBuffer.Deferred var6 = (BacktrackEntityUpdateMixin.BacktrackBuffer.Deferred)var5.pollFirst();
                  dispatch(var0, var6.packet);
               }

               if (var5.isEmpty()) {
                  var3.remove();
               }
            }
         }
      }

      public static synchronized void flushAll() {
         MinecraftClient var0 = MinecraftClient.getInstance();
         ClientPlayNetworkHandler var1 = var0.getNetworkHandler();
         if (var1 == null) {
            DEFERRED.clear();
         } else {
            for (Deque var3 : DEFERRED.values()) {
               while (!var3.isEmpty()) {
                  BacktrackEntityUpdateMixin.BacktrackBuffer.Deferred var4 = (BacktrackEntityUpdateMixin.BacktrackBuffer.Deferred)var3.pollFirst();
                  dispatch(var1, var4.packet);
               }
            }

            DEFERRED.clear();
            SNAPSHOTS.clear();
         }
      }

      private static void drainSpecific(IntPredicate shouldFlushId) {
         MinecraftClient var1 = MinecraftClient.getInstance();
         ClientPlayNetworkHandler var2 = var1.getNetworkHandler();
         if (var2 == null) {
            DEFERRED.entrySet().removeIf(e -> shouldFlushId.test(e.getKey()));
         } else {
            Iterator var3 = DEFERRED.entrySet().iterator();

            while (var3.hasNext()) {
               Entry var4 = (Entry)var3.next();
               if (shouldFlushId.test((Integer)var4.getKey())) {
                  Deque var5 = (Deque)var4.getValue();

                  while (!var5.isEmpty()) {
                     BacktrackEntityUpdateMixin.BacktrackBuffer.Deferred var6 = (BacktrackEntityUpdateMixin.BacktrackBuffer.Deferred)var5.pollFirst();
                     dispatch(var2, var6.packet);
                  }

                  var3.remove();
               }
            }
         }
      }

      private static void dispatch(ClientPlayNetworkHandler var0, Packet<?> var1) {
         bypass.set(Boolean.TRUE);

         try {
            if (var1 instanceof EntityS2CPacket var2) {
               var0.onEntity(var2);
            } else if (var1 instanceof EntityPositionS2CPacket var3) {
               var0.onEntityPosition(var3);
            } else if (var1 instanceof EntityPositionSyncS2CPacket var4) {
               var0.onEntityPositionSync(var4);
            }
         } finally {
            bypass.set(Boolean.FALSE);
         }
      }

      private record Deferred(Packet<?> packet, long releaseAt) {

      }
   }
}

