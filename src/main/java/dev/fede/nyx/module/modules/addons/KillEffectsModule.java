package dev.fede.nyx.module.modules.addons;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class KillEffectsModule extends Module {
   private static final long longVal = 3000L;
   private static final int intVal = 128;
   private final ModeSetting effect = new ModeSetting(
      "Effect", "Explosion", "Explosion", "Lightning", "Fire Ring", "Heart Burst", "Blood Splatter", "Ghost", "Wither"
   );
   private final BooleanSetting sound = new BooleanSetting("Sound", true);
   private final BooleanSetting localOnly = new BooleanSetting("LocalOnly", false);
   private final NumberSetting scale = new NumberSetting("Scale", 1.0, 0.5, 3.0, 0.1);
   private final BooleanSetting playerKillsOnly = new BooleanSetting("PlayerKillsOnly", false);
   private final NumberSetting cooldownMs = new NumberSetting("Cooldown", 500.0, 200.0, 3000.0, 50.0);
   private final Map<Integer, Long> map = new LinkedHashMap<Integer, Long>(16, 0.75F, false) {
      final KillEffectsModule killEffectsModule = KillEffectsModule.this;

      @Override
      protected boolean removeEldestEntry(Entry<Integer, Long> eldest) {
         return this.size() > 128;
      }
   };
   private boolean bool = false;
   private long longVal2 = 0L;
   private static volatile KillEffectsModule killEffectsModule;

   public KillEffectsModule() {
      super("KillEffects", "Cosmetic burst on entity deaths", Category.ADDONS);
      this.run6(new Setting[]{this.effect, this.sound, this.localOnly, this.scale, this.playerKillsOnly, this.cooldownMs});
      killEffectsModule = this;
   }

   @Override
   public void run() {
      this.map.clear();
      this.bool = false;
      this.longVal2 = 0L;
   }

   @Override
   public void run2() {
      this.map.clear();
   }

   @Override
   public void run3() {
      ClientWorld var1 = class310.world;
      if (var1 != null && class310.player != null) {
         boolean var2 = class310.player.handSwinging;
         boolean var3 = var2 && !this.bool;
         this.bool = var2;
         if (var3) {
            if (class310.crosshairTarget instanceof EntityHitResult var5) {
               Entity var6 = var5.getEntity();
               if (var6 != null && var6 != class310.player) {
                  this.map.put(var6.getId(), System.currentTimeMillis());
               }
            }
         }
      }
   }

   public static void run4(Entity var0) {
      KillEffectsModule var1 = killEffectsModule;
      if (var1 != null && var1.isEnabled3()) {
         if (var0 != null) {
            var1.run5(var0);
         }
      }
   }

   private void run5(Entity var1) {
      ClientWorld var2 = class310.world;
      if (var2 != null) {
         if (!this.playerKillsOnly.getValue() || var1 instanceof PlayerEntity) {
            if (this.localOnly.getValue()) {
               Long var3 = this.map.remove(var1.getId());
               if (var3 == null) {
                  return;
               }

               if (System.currentTimeMillis() - var3 > 3000L) {
                  return;
               }
            }

            long var12 = System.currentTimeMillis();
            if (var12 - this.longVal2 >= this.cooldownMs.getValueLong()) {
               this.longVal2 = var12;
               Vec3d var5 = var1.getEntityPos();
               double var6 = var5.x;
               double var8 = var5.y + var1.getHeight() * 0.5;
               double var10 = var5.z;
               this.run6(var2, this.effect.getMode(), var6, var8, var10);
            }
         }
      }
   }

   private void run6(ClientWorld var1, String var2, double var3, double var5, double var7) {
      float var9 = MathHelper.clamp(this.scale.getValueFloat(), 0.5F, 3.0F);
      switch (var2.hashCode()) {
         case -1957276939:
            if (var2.equals("Explosion")) {
               this.run7(var1, var3, var5, var7, var9);
               return;
            }
            break;
         case -1703702509:
            if (var2.equals("Wither")) {
               this.run13(var1, var3, var5, var7, var9);
               return;
            }
            break;
         case -1604554070:
            if (var2.equals("Lightning")) {
               this.run8(var1, var3, var5, var7, var9);
               return;
            }
            break;
         case -465119098:
            if (var2.equals("Heart Burst")) {
               this.run10(var1, var3, var5, var7, var9);
               return;
            }
            break;
         case 68778607:
            if (var2.equals("Ghost")) {
               this.run12(var1, var3, var5, var7, var9);
               return;
            }
            break;
         case 1165442661:
            if (var2.equals("Blood Splatter")) {
               this.run11(var1, var3, var5, var7, var9);
               return;
            }
            break;
         case 1658546650:
            if (var2.equals("Fire Ring")) {
               this.run9(var1, var3, var5, var7, var9);
               return;
            }
      }

      this.run7(var1, var3, var5, var7, var9);
   }

   private void run7(ClientWorld var1, double var2, double var4, double var6, float var8) {
      var1.addParticleClient(ParticleTypes.EXPLOSION_EMITTER, var2, var4, var6, 0.0, 0.0, 0.0);
      int var9 = (int)(12.0F * var8);
      ThreadLocalRandom var10 = ThreadLocalRandom.current();

      for (int var11 = 0; var11 < var9; var11++) {
         double var12 = (var10.nextDouble() - 0.5) * 2.0 * var8;
         double var14 = (var10.nextDouble() - 0.5) * 2.0 * var8;
         double var16 = (var10.nextDouble() - 0.5) * 2.0 * var8;
         var1.addParticleClient(ParticleTypes.LARGE_SMOKE, var2 + var12 * 0.4, var4 + var14 * 0.4, var6 + var16 * 0.4, var12 * 0.15, var14 * 0.15, var16 * 0.15);
      }

      this.run14((SoundEvent)SoundEvents.ENTITY_GENERIC_EXPLODE.value(), 1.0F * var8, 1.0F);
   }

   private void run8(ClientWorld var1, double var2, double var4, double var6, float var8) {
      try {
         LightningEntity var9 = new LightningEntity(EntityType.LIGHTNING_BOLT, var1);
         var9.setCosmetic(true);
         var9.refreshPositionAfterTeleport(var2, var4, var6);
         var1.addEntity(var9);
      } catch (Throwable var16) {
         ThreadLocalRandom var10 = ThreadLocalRandom.current();

         for (int var11 = 0; var11 < 24; var11++) {
            double var12 = (var10.nextDouble() - 0.5) * 0.6;
            double var14 = (var10.nextDouble() - 0.5) * 0.6;
            var1.addParticleClient(ParticleTypes.ELECTRIC_SPARK, var2 + var12, var4 + var10.nextDouble() * 2.5 * var8, var6 + var14, 0.0, 0.02, 0.0);
         }
      }

      this.run14(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0F * var8, 1.0F);
   }

   private void run9(ClientWorld var1, double var2, double var4, double var6, float var8) {
      double var10 = 1.5 * var8;
      double var12 = var4 - 0.5;

      for (int var14 = 0; var14 < 16; var14++) {
         double var15 = (Math.PI * 2) * var14 / 16;
         double var17 = var2 + Math.cos(var15) * var10;
         double var19 = var6 + Math.sin(var15) * var10;
         var1.addParticleClient(ParticleTypes.FLAME, var17, var12, var19, 0.0, 0.05, 0.0);
      }

      this.run14(SoundEvents.ITEM_FIRECHARGE_USE, 1.0F * var8, 1.0F);
   }

   private void run10(ClientWorld var1, double var2, double var4, double var6, float var8) {
      ThreadLocalRandom var9 = ThreadLocalRandom.current();
      int var10 = (int)(20.0F * var8);

      for (int var11 = 0; var11 < var10; var11++) {
         double var12 = (var9.nextDouble() - 0.5) * 0.8 * var8;
         double var14 = (var9.nextDouble() - 0.5) * 0.8 * var8;
         var1.addParticleClient(ParticleTypes.HEART, var2 + var12, var4 + var9.nextDouble() * 0.4, var6 + var14, 0.0, 0.05, 0.0);
      }

      this.run14(SoundEvents.ENTITY_VILLAGER_YES, 1.0F * var8, 1.2F);
   }

   private void run11(ClientWorld var1, double var2, double var4, double var6, float var8) {
      ThreadLocalRandom var9 = ThreadLocalRandom.current();
      int var10 = (int)(30.0F * var8);
      DustParticleEffect var11 = new DustParticleEffect(9109504, 1.0F);

      for (int var12 = 0; var12 < var10; var12++) {
         double var13 = (var9.nextDouble() - 0.5) * 2.0;
         double var15 = (var9.nextDouble() - 0.5) * 2.0;
         double var17 = (var9.nextDouble() - 0.5) * 2.0;
         var1.addParticleClient(var11, var2 + var13 * 0.3, var4 + var15 * 0.3, var6 + var17 * 0.3, var13 * 0.2 * var8, var15 * 0.2 * var8, var17 * 0.2 * var8);
      }

      this.run14(SoundEvents.ENTITY_PLAYER_HURT, 1.0F * var8, 0.9F);
   }

   private void run12(ClientWorld var1, double var2, double var4, double var6, float var8) {
      ThreadLocalRandom var9 = ThreadLocalRandom.current();
      int var10 = (int)(25.0F * var8);

      for (int var11 = 0; var11 < var10; var11++) {
         double var12 = (var9.nextDouble() - 0.5) * 0.6 * var8;
         double var14 = (var9.nextDouble() - 0.5) * 0.6 * var8;
         var1.addParticleClient(
            ParticleTypes.WHITE_SMOKE,
            var2 + var12,
            var4 + var9.nextDouble() * 1.5 * var8,
            var6 + var14,
            var12 * 0.05,
            0.08 + var9.nextDouble() * 0.05,
            var14 * 0.05
         );
      }

      SoundEvent var17;
      try {
         var17 = (SoundEvent)SoundEvents.PARTICLE_SOUL_ESCAPE.value();
      } catch (Throwable var16) {
         var17 = (SoundEvent)SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value();
      }

      this.run14(var17, 0.8F * var8, 1.0F);
   }

   private void run13(ClientWorld var1, double var2, double var4, double var6, float var8) {
      ThreadLocalRandom var9 = ThreadLocalRandom.current();
      int var10 = (int)(25.0F * var8);

      for (int var11 = 0; var11 < var10; var11++) {
         double var12 = (var9.nextDouble() - 0.5) * 1.5 * var8;
         double var14 = (var9.nextDouble() - 0.5) * 1.5 * var8;
         double var16 = (var9.nextDouble() - 0.5) * 1.5 * var8;
         SimpleParticleType var18 = var11 % 3 == 0 ? ParticleTypes.SQUID_INK : ParticleTypes.SMOKE;
         var1.addParticleClient(var18, var2 + var12 * 0.4, var4 + var14 * 0.4, var6 + var16 * 0.4, var12 * 0.1, var14 * 0.1, var16 * 0.1);
      }

      this.run14(SoundEvents.ENTITY_WITHER_DEATH, 0.5F * var8, 1.0F);
   }

   private void run14(SoundEvent var1, float var2, float var3) {
      if (this.sound.getValue()) {
         if (var1 != null && class310.world != null && class310.player != null) {
            try {
               class310.world
                  .playSoundClient(class310.player.getX(), class310.player.getY(), class310.player.getZ(), var1, SoundCategory.PLAYERS, var2, var3, false);
            } catch (Throwable var5) {
            }
         }
      }
   }
}

