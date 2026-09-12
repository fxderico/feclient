package dev.fede.nyx.module.modules.client;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.module.modules.combat.KillAura;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.AutoCrystalModuleUtil;
import dev.fede.nyx.util.CodeEngineScreenUtil2;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.Text;

public class TargetHUDModule extends Module {
   public static volatile TargetHUDModule targetHUDModule;
   private static volatile LivingEntity class1309 = null;
   private static volatile float floatVal = 0.0F;
   private static volatile float floatVal2 = 0.0F;
   private static final int intVal = 236;
   private static final int intVal2 = 10;
   private static final int intVal3 = 8;
   private static final int intVal4 = 48;
   private static final int intVal5 = 12;
   private static final int intVal6 = 6;
   private static final int intVal7 = 11;
   private static final int intVal8 = 2;
   private static final int intVal9 = 3;
   private static final int intVal10 = 16;
   private static final int intVal11 = 8;
   private static final int intVal12 = 5;
   private static final int intVal13 = 6;
   private static final int intVal14 = 11538451;
   private static final float floatVal3 = 22.0F;
   private static final float floatVal4 = 2.4F;
   private static final float floatVal5 = 200.0F;
   private static final float floatVal6 = 200.0F;
   private static final float floatVal7 = 300.0F;
   private final NumberSetting posX;
   private final NumberSetting posY;
   private final NumberSetting scale = new NumberSetting("Scale", 1.5, 0.5, 3.0, 0.1);
   private final BooleanSetting showHead = new BooleanSetting("ShowHead", true);
   private final BooleanSetting showHeldItem = new BooleanSetting("ShowHeldItem", false);
   private final BooleanSetting showArmor = new BooleanSetting("ShowArmor", false);
   private final BooleanSetting showHealthNumbers = new BooleanSetting("ShowHealthNumbers", false);
   private final BooleanSetting showDistance = new BooleanSetting("ShowDistance", false);
   private final BooleanSetting healthBarGlow = new BooleanSetting("HealthBarGlow", true);
   private final NumberSetting glowStrength = new NumberSetting("GlowStrength", 1.0, 0.0, 2.0, 0.05);
   private final BooleanSetting showWhenIdle = new BooleanSetting("ShowWhenIdle", true);
   private final NumberSetting stayOpenMs = new NumberSetting("StayOpenMs", 2000.0, 500.0, 10000.0, 100.0);
   private final NumberSetting fallbackRange = new NumberSetting("FallbackRange", 24.0, 4.0, 64.0, 1.0);
   private final NumberSetting backgroundAlpha = new NumberSetting("BackgroundAlpha", 0.85, 0.0, 1.0, 0.05);
   private final ColorSetting backgroundColor = new ColorSetting("BackgroundColor", -535686635);
   private final ColorSetting healthBarColor = new ColorSetting("HealthBarColor", -3850769);
   private final BooleanSetting healthBarGradient = new BooleanSetting("HealthBarGradient", false);
   private final BooleanSetting chromaBorder = new BooleanSetting("ChromaBorder", false);
   private final ColorSetting borderColor = new ColorSetting("BorderColor", 0);
   private LivingEntity class13092;
   private long longVal;
   private float floatVal8 = 0.0F;
   private long longVal2 = 0L;
   private float floatVal9 = 0.0F;
   private float floatVal10 = 0.0F;
   private long longVal3 = 0L;
   private float floatVal11 = 0.0F;
   private LivingEntity class13093 = null;
   private float floatVal12 = 0.0F;
   private float floatVal13 = 0.0F;
   private long longVal4 = 0L;

   public TargetHUDModule() {
      super("TargetHUD", "Vape-style widget showing the KillAura target's stats", Category.CLIENT);
      int var1 = 1280;
      int var2 = 720;

      try {
         if (class310 != null && class310.getWindow() != null) {
            var1 = class310.getWindow().getScaledWidth();
            var2 = class310.getWindow().getScaledHeight();
         }
      } catch (Throwable var4) {
      }

      this.posX = new NumberSetting("PosX", var1 / 2.0 + 60.0, 0.0, 4000.0, 1.0);
      this.posY = new NumberSetting("PosY", var2 / 2.0 + 80.0, 0.0, 4000.0, 1.0);
      this.run6(
         new Setting[]{
            this.posX,
            this.posY,
            this.scale,
            this.showHead,
            this.showHeldItem,
            this.showArmor,
            this.showHealthNumbers,
            this.showDistance,
            this.healthBarGlow,
            this.glowStrength,
            this.healthBarColor,
            this.showWhenIdle,
            this.stayOpenMs,
            this.fallbackRange,
            this.backgroundAlpha,
            this.backgroundColor,
            this.healthBarGradient,
            this.chromaBorder,
            this.borderColor
         }
      );
      targetHUDModule = this;
   }

   public static LivingEntity getclass1309() {
      return class1309;
   }

   public static float getFloat() {
      return floatVal;
   }

   public static float getFloat2() {
      return floatVal2;
   }

   public static int getInt3() {
      return targetHUDModule == null ? 0 : targetHUDModule.posX.getValueInt();
   }

   public static int getInt2() {
      return targetHUDModule == null ? 0 : targetHUDModule.posY.getValueInt();
   }

   public static float getFloat3() {
      return targetHUDModule == null ? 1.0F : targetHUDModule.scale.getValueFloat();
   }

   public static boolean isEnabled_s() {
      return targetHUDModule != null && targetHUDModule.showHead.getValue();
   }

   public static boolean isEnabled2() {
      return targetHUDModule != null && targetHUDModule.showHeldItem.getValue();
   }

   public static boolean isEnabled4() {
      return targetHUDModule != null && targetHUDModule.showArmor.getValue();
   }

   public static boolean isEnabled5() {
      return targetHUDModule != null && targetHUDModule.showHealthNumbers.getValue();
   }

   public static boolean isEnabled6() {
      return targetHUDModule != null && targetHUDModule.showDistance.getValue();
   }

   public static boolean isEnabled3_s() {
      return targetHUDModule != null && targetHUDModule.showWhenIdle.getValue();
   }

   public static int getInt5() {
      return targetHUDModule == null ? -3850769 : targetHUDModule.healthBarColor.getValue();
   }

   public static int getInt_s() {
      return targetHUDModule == null ? -535686635 : targetHUDModule.backgroundColor.getValue();
   }

   public static float getFloat4() {
      return targetHUDModule == null ? 1.0F : targetHUDModule.backgroundAlpha.getValueFloat();
   }

   public static long getLong() {
      return targetHUDModule == null ? 2000L : targetHUDModule.stayOpenMs.getValueLong();
   }

   public static double getDouble() {
      return targetHUDModule == null ? 24.0 : targetHUDModule.fallbackRange.getValue();
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310 != null && class310.world != null && class310.player != null && class310.textRenderer != null) {
         TextRenderer var3 = class310.textRenderer;
         long var4 = System.nanoTime() / 1000000L;
         if (this.longVal2 == 0L) {
            this.longVal2 = var4;
         }

         LivingEntity var6 = this.getclass13092();
         LivingEntity var7;
         boolean var8;
         if (var6 != null && var6.isAlive()) {
            var7 = var6;
            var8 = false;
            this.class13092 = var6;
            this.longVal = var4;
         } else if (this.class13092 != null && this.class13092.isAlive() && var4 - this.longVal <= this.stayOpenMs.getValueLong()) {
            var7 = this.class13092;
            var8 = false;
         } else {
            var7 = null;
            var8 = true;
            this.class13092 = null;
         }

         boolean var9 = var7 != null || var8 && this.showWhenIdle.getValue();
         float var10 = var9 ? 1.0F : 0.0F;
         if (var10 != this.floatVal11) {
            this.run(var10, var4);
         }

         float var11 = floatOf2((float)(var4 - this.longVal3) / 200.0F);
         this.floatVal9 = floatOf(this.floatVal10, this.floatVal11, var11);
         if (this.floatVal9 <= 0.02F && this.floatVal11 == 0.0F) {
            this.longVal2 = var4;
         } else {
            if (var7 != null && var7 != this.class13093) {
               this.floatVal9 = 0.35F;
               this.run(1.0F, var4);
               if (this.class13093 != null) {
                  this.floatVal8 = var7.getHealth();
               }

               this.class13093 = var7;
            }

            float var12 = var7 != null ? Math.max(0.0F, var7.getHealth()) : 0.0F;
            if (var7 != null) {
               Math.max(1.0E-4F, var7.getMaxHealth());
            } else {
               float var10000 = 20.0F;
            }

            float var14 = Math.min(100.0F, (float)(var4 - this.longVal2));
            float var15 = 1.0F - (float)Math.exp(-var14 / 200.0F);
            if (Math.abs(this.floatVal8 - var12) > 4.0F) {
               this.floatVal8 = var12;
            }

            this.floatVal8 = this.floatVal8 + (var12 - this.floatVal8) * var15;
            if (Math.abs(this.floatVal8 - var12) < 0.05F) {
               this.floatVal8 = var12;
            }

            if (var7 != null && var12 < this.floatVal12 - 0.5F) {
               this.longVal4 = var4;
            }

            this.floatVal12 = var12;
            this.floatVal13 = Math.max(0.0F, 1.0F - (float)(var4 - this.longVal4) / 300.0F);
            this.longVal2 = var4;
            class1309 = var7;
            floatVal = this.floatVal8;
            floatVal2 = this.floatVal9;
         }
      }
   }

   private void run(float var1, long var2) {
      this.floatVal10 = this.floatVal9;
      this.floatVal11 = var1;
      this.longVal3 = var2;
   }

   private static float floatOf(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * floatOf2(var2);
   }

   private void run2(DrawContext var1, TextRenderer var2, int var3, int var4, int var5, Entity var6, float var7) {
      SkinTextures var8 = this.class8685Of(var6);
      if (var8 != null && var8.body() != null && var8.body().texturePath() != null) {
         int var9 = intOf(Math.round(255.0F * floatOf2(var7))) << 24 | 16777215;

         try {
            PlayerSkinDrawer.draw(var1, var8, var3, var4, var5, var9);
            return;
         } catch (Throwable var11) {
         }
      }

      run3(var1, var2, var3, var4, var5, var6, var7);
   }

   private SkinTextures class8685Of(Entity var1) {
      if (var1 instanceof AbstractClientPlayerEntity var2) {
         try {
            return var2.getSkin();
         } catch (Throwable var7) {
         }
      }

      if (var1 instanceof PlayerEntity var8 && class310 != null) {
         ClientPlayNetworkHandler var3 = class310.getNetworkHandler();
         if (var3 != null) {
            PlayerListEntry var4 = var3.getPlayerListEntry(var8.getUuid());
            if (var4 != null) {
               try {
                  return var4.getSkinTextures();
               } catch (Throwable var6) {
               }
            }
         }
      }

      return null;
   }

   private static void run3(DrawContext var0, TextRenderer var1, int var2, int var3, int var4, Entity var5, float var6) {
      int var7 = var5 instanceof PlayerEntity var8 ? var8.getUuid().hashCode() : var5.getName().getString().hashCode();
      float var17 = (var7 & 2147483647) % 1000 / 1000.0F;
      int var9 = CodeEngineScreenUtil2.intOf5(var17, 0.55F, 0.85F) & 16777215;
      int var10 = intOf((int)(255.0F * floatOf2(var6))) << 24 | var9;
      int var11 = intOf((int)(85.0F * floatOf2(var6))) << 24;
      var0.fill(var2, var3, var2 + var4, var3 + var4, var10);
      var0.fill(var2, var3 + var4 - 1, var2 + var4, var3 + var4, var11);
      var0.fill(var2 + var4 - 1, var3, var2 + var4, var3 + var4, var11);
      String var12 = var5.getName().getString();
      String var13 = var12 != null && !var12.isEmpty() ? var12.substring(0, 1).toUpperCase() : "?";
      int var14 = var1.getWidth(var13);
      int var15 = var2 + (var4 - var14) / 2;
      int var16 = var3 + (var4 - 9) / 2 + 1;
      var0.drawTextWithShadow(var1, Text.literal(var13), var15, var16, intOf4(-1, var6));
   }

   private LivingEntity getclass13092() {
      try {
         LivingEntity var1 = KillAura.getclass1309();
         if (var1 != null && var1.isAlive() && var1 != class310.player) {
            return var1;
         }
      } catch (Throwable var9) {
      }

      double var10 = this.fallbackRange.getValue() * this.fallbackRange.getValue();
      LivingEntity var3 = null;
      float var4 = Float.MAX_VALUE;

      for (Entity var6 : class310.world.getEntities()) {
         if (var6 instanceof LivingEntity var7
            && var7 != class310.player
            && var7.isAlive()
            && !var7.isInvisible()
            && !(class310.player.squaredDistanceTo(var7) > var10)) {
            float var8 = AutoCrystalModuleUtil.floatOf(AutoCrystalModuleUtil.floatArrayOf2(var7));
            if (var8 < var4) {
               var4 = var8;
               var3 = var7;
            }
         }
      }

      return var3;
   }

   private static int intOf4(int var0, float var1) {
      int var2 = var0 >>> 24 & 0xFF;
      int var3 = intOf(Math.round(var2 * floatOf2(var1)));
      return var0 & 16777215 | var3 << 24;
   }

   private static int intOf(int var0) {
      return var0 < 0 ? 0 : (var0 > 255 ? 255 : var0);
   }

   private static float floatOf2(float var0) {
      return var0 < 0.0F ? 0.0F : (var0 > 1.0F ? 1.0F : var0);
   }

   private static String stringOf(TextRenderer var0, String var1, int var2) {
      if (var2 > 0 && var1 != null && !var1.isEmpty()) {
         if (var0.getWidth(var1) <= var2) {
            return var1;
         } else {
            int var4 = var0.getWidth("...");
            if (var4 >= var2) {
               return "";
            } else {
               StringBuilder var5 = new StringBuilder();

               for (int var6 = 0; var6 < var1.length(); var6++) {
                  String var7 = var5.toString() + var1.charAt(var6);
                  int var8 = var0.getWidth(var7) + var4;
                  if (var8 > var2) {
                     break;
                  }

                  var5.append(var1.charAt(var6));
               }

               if (var5.length() == 0) {
                  return "";
               } else {
                  var5.append("...");
                  return var5.toString();
               }
            }
         }
      } else {
         return var1 == null ? "" : var1;
      }
   }
}

