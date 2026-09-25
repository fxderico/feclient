package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * PearlAlert — the defensive twin of ProjectileArc. That one predicts YOUR
 * throwable; this one watches every ENEMY ender pearl in the air, forward-
 * simulates its flight to the landing block, and warns you (chat + sound +
 * optional world marker) when a pearl is about to drop near you — someone
 * pearling onto your base, a combo pearl, an escape you can chase. corz calls
 * it Pearl Catch; here it's yours.
 */
public class PearlAlertModule extends Module {
   private final NumberSetting nearBlocks = new NumberSetting("AlertRange", 12.0, 2.0, 48.0, 1.0);
   private final NumberSetting maxSteps = new NumberSetting("SimSteps", 120.0, 20.0, 300.0, 10.0);
   private final BooleanSetting ignoreOwn = new BooleanSetting("IgnoreOwn", true);
   private final BooleanSetting marker = new BooleanSetting("Marker", true);
   private final BooleanSetting chat = new BooleanSetting("Chat", true);
   private final BooleanSetting sound = new BooleanSetting("Sound", true);
   private final Set<Integer> alerted = new HashSet<>();
   // pearl entity id -> predicted landing (for the render pass)
   private final java.util.Map<Integer, Vec3d> landings = new java.util.HashMap<>();

   public PearlAlertModule() {
      super("PearlAlert", "Predicts enemy pearl landings and warns when one drops near you", Category.DONUTSMP);
      this.run6(new Setting[]{this.nearBlocks, this.maxSteps, this.ignoreOwn, this.marker, this.chat, this.sound});
   }

   @Override
   public void run() {
      this.alerted.clear();
      this.landings.clear();
   }

   @Override
   public void run2() {
      this.alerted.clear();
      this.landings.clear();
   }

   @Override
   public void run3() {
      if (class310.player == null || class310.world == null) {
         return;
      }

      try {
         this.landings.clear();
         Set<Integer> live = new HashSet<>();
         double near = this.nearBlocks.getValue();
         double nearSq = near * near;

         for (Entity e : class310.world.getEntities()) {
            if (!(e instanceof EnderPearlEntity pearl)) {
               continue;
            }
            live.add(pearl.getId());
            if (this.ignoreOwn.getValue() && pearl.getOwner() == class310.player) {
               continue;
            }

            Vec3d land = this.predictLanding(pearl);
            if (land == null) {
               continue;
            }
            this.landings.put(pearl.getId(), land);

            Vec3d self = new Vec3d(class310.player.getX(), class310.player.getY(), class310.player.getZ());
            if (land.squaredDistanceTo(self) <= nearSq && this.alerted.add(pearl.getId())) {
               this.fire(land);
            }
         }

         // forget pearls that have landed / despawned so they can re-alert next throw
         this.alerted.retainAll(live);
      } catch (Throwable ignored) {
      }
   }

   /** Forward-euler the pearl's flight until it hits a block or runs out of steps. */
   private Vec3d predictLanding(EnderPearlEntity pearl) {
      Vec3d pos = new Vec3d(pearl.getX(), pearl.getY(), pearl.getZ());
      Vec3d vel = pearl.getVelocity();
      int steps = (int)this.maxSteps.getValue();

      for (int i = 0; i < steps; i++) {
         Vec3d next = pos.add(vel);
         BlockPos bp = BlockPos.ofFloored(next);
         try {
            if (!class310.world.getBlockState(bp).getCollisionShape(class310.world, bp).isEmpty()) {
               return pos; // last air position = landing spot
            }
         } catch (Throwable t) {
            return pos;
         }
         pos = next;
         // pearl motion: horizontal+vertical drag 0.99, gravity 0.03/tick
         vel = new Vec3d(vel.x * 0.99, vel.y * 0.99 - 0.03, vel.z * 0.99);
      }

      return pos;
   }

   private void fire(Vec3d land) {
      if (this.chat.getValue() && class310.inGameHud != null) {
         try {
            class310.inGameHud
               .getChatHud()
               .addMessage(
                  Text.literal(
                     "§d[PearlAlert] §fpearl inbound §7~ §fX "
                        + (int)land.x
                        + " §7/ §fY "
                        + (int)land.y
                        + " §7/ §fZ "
                        + (int)land.z
                  )
               );
         } catch (Throwable ignored) {
         }
      }

      if (this.sound.getValue() && class310.player != null && class310.world != null) {
         try {
            class310.world
               .playSoundClient(
                  class310.player.getX(),
                  class310.player.getY(),
                  class310.player.getZ(),
                  SoundEvents.BLOCK_AMETHYST_BLOCK_HIT,
                  SoundCategory.PLAYERS,
                  1.0F,
                  0.7F,
                  false
               );
         } catch (Throwable ignored) {
         }
      }
   }

   @Override
   public void run4(DrawContext ctx, float tickDelta) {
      if (!this.marker.getValue() || this.landings.isEmpty()) {
         return;
      }

      for (Vec3d land : this.landings.values()) {
         BlockPos bp = BlockPos.ofFloored(land);
         Box box = new Box(bp.getX(), bp.getY(), bp.getZ(), bp.getX() + 1.0, bp.getY() + 1.0, bp.getZ() + 1.0);
         ListUtils.run5(box, 0x88FF33CC, 1.5F, true);
      }
   }

   @Override
   public String getString3() {
      int n = this.alerted.size();
      return n > 0 ? "§7" + n : null;
   }
}
