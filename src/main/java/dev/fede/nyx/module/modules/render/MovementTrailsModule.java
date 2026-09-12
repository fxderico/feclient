package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class MovementTrailsModule extends Module {
   private final BooleanSetting self = new BooleanSetting("Self", true);
   private final BooleanSetting others = new BooleanSetting("Others", true);
   private final BooleanSetting fadeWithAge = new BooleanSetting("FadeWithAge", true);
   private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", true);
   private final BooleanSetting snapToGround = new BooleanSetting("SnapToGround", true);
   private final NumberSetting maxTrailLength = new NumberSetting("MaxTrailLength", 200.0, 20.0, 1000.0, 1.0);
   private final NumberSetting minSampleDistance = new NumberSetting("MinSampleDistance", 0.3, 0.05, 2.0, 0.05);
   private final NumberSetting lineWidth = new NumberSetting("LineWidth", 1.5, 0.5, 4.0, 0.1);
   private final NumberSetting maxDistance = new NumberSetting("MaxDistance", 128.0, 16.0, 512.0, 1.0);
   private final ColorSetting colorSelf = new ColorSetting("ColorSelf", -9459492);
   private final ColorSetting colorOthers = new ColorSetting("ColorOthers", -965016);
   private static final Map<UUID, Deque<Vec3d>> map = new ConcurrentHashMap<>();
   private static final Map<UUID, Long> map2 = new ConcurrentHashMap<>();
   private static final long longVal = 30000L;
   private static final long longVal2 = 1000L;
   private long longVal3 = 0L;

   public MovementTrailsModule() {
      super("MovementTrails", "Draws ground-line trails behind visible players", Category.RENDER);
      this.run6(
         new Setting[]{
            this.self,
            this.others,
            this.fadeWithAge,
            this.throughWalls,
            this.snapToGround,
            this.maxTrailLength,
            this.minSampleDistance,
            this.lineWidth,
            this.maxDistance,
            this.colorSelf,
            this.colorOthers
         }
      );
   }

   @Override
   public void run2() {
      map.clear();
      map2.clear();
      this.longVal3 = 0L;
   }

   @Override
   public void run() {
      if (class310.world != null && class310.player != null) {
         boolean var1 = this.self.getValue();
         boolean var2 = this.others.getValue();
         if (!var1 && !var2) {
            this.run3();
         } else {
            int var3 = this.maxTrailLength.getValueInt();
            double var4 = doubleOf(this.minSampleDistance.getValue());
            double var6 = doubleOf(this.maxDistance.getValue());
            boolean var8 = this.snapToGround.getValue();
            long var9 = System.currentTimeMillis();

            for (PlayerEntity var12 : class310.world.getPlayers()) {
               boolean var13 = var12 == class310.player;
               if ((var13 ? var1 : var2) && !var12.isRemoved() && !(class310.player.squaredDistanceTo(var12) > var6)) {
                  double var14 = var12.getX();
                  double var16 = var12.getZ();
                  double var18 = var8 ? Math.floor(var12.getY()) + 0.01 : var12.getY();
                  Vec3d var20 = new Vec3d(var14, var18, var16);
                  UUID var21 = var12.getUuid();
                  Deque var22 = map.computeIfAbsent(var21, MovementTrailsModule::dequeOf);
                  Vec3d var23 = (Vec3d)var22.peekLast();
                  if (var23 == null || var23.squaredDistanceTo(var20) >= var4) {
                     var22.addLast(var20);

                     while (var22.size() > var3) {
                        var22.pollFirst();
                     }
                  }

                  map2.put(var21, var9);
               }
            }

            this.run3();
         }
      }
   }

   public void run3() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.longVal3 >= 1000L) {
         this.longVal3 = var1;
         Iterator var3 = map2.entrySet().iterator();

         while (var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            if (var1 - (Long)var4.getValue() > 30000L) {
               map.remove(var4.getKey());
               var3.remove();
            }
         }
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null && !map.isEmpty()) {
         int var3 = this.colorSelf.getValue();
         int var4 = this.colorOthers.getValue();
         float var5 = this.lineWidth.getValueFloat();
         boolean var6 = this.throughWalls.getValue();
         boolean var7 = this.fadeWithAge.getValue();
         UUID var8 = class310.player.getUuid();

         for (Entry var10 : map.entrySet()) {
            Deque var11 = (Deque)var10.getValue();
            int var12 = var11.size();
            if (var12 >= 2) {
               int var13 = ((UUID)var10.getKey()).equals(var8) ? var3 : var4;
               int var14 = var13 >>> 24 & 0xFF;
               int var15 = var13 & 16777215;
               Vec3d[] var16 = (Vec3d[])(var11.toArray(new Object[0]));
               int var17 = var16.length - 1;

               for (int var18 = 0; var18 < var17; var18++) {
                  int var19;
                  if (var7) {
                     double var20 = (double)(var18 + 1) / var17;
                     int var22 = (int)Math.round(var14 * var20);
                     if (var22 < 4) {
                        continue;
                     }

                     var19 = var22 << 24 | var15;
                  } else {
                     var19 = var13;
                  }

                  ListUtils.run12(var16[var18], var16[var18 + 1], var19, var5, var6);
               }
            }
         }
      }
   }

   private static double doubleOf(double var0) {
      return var0 * var0;
   }

   private static Deque dequeOf(UUID var0) {
      return new ArrayDeque<>();
   }
}

