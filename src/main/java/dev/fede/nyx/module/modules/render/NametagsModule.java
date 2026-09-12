package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.imgui.ImGuiFonts;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.ChatFilterHelper;
import dev.fede.nyx.util.Matrix4fUtils;
import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3x2fStack;

public class NametagsModule extends Module {
   public static NametagsModule nametagsModule;
   private final BooleanSetting players = new BooleanSetting("Players", true);
   private final BooleanSetting mobs = new BooleanSetting("Mobs", false);
   private final BooleanSetting passive = new BooleanSetting("Passive", false);
   private final BooleanSetting self = new BooleanSetting("Self", false);
   private final BooleanSetting showHealth = new BooleanSetting("ShowHealth", true);
   private final BooleanSetting showHealthBar = new BooleanSetting("ShowHealthBar", true);
   private final BooleanSetting showArmor = new BooleanSetting("ShowArmor", false);
   private final BooleanSetting showPing = new BooleanSetting("ShowPing", true);
   private final BooleanSetting showHeldItem = new BooleanSetting("ShowHeldItem", true);
   private final BooleanSetting showDistance = new BooleanSetting("ShowDistance", false);
   private final BooleanSetting highlightFriends = new BooleanSetting("HighlightFriends", true);
   private final NumberSetting scale = new NumberSetting("Scale", 1.1, 0.5, 3.0, 0.05);
   private final NumberSetting maxDistance = new NumberSetting("MaxDistance", 96.0, 16.0, 256.0, 1.0);
   private final NumberSetting backgroundAlpha = new NumberSetting("BackgroundAlpha", 0.75, 0.0, 1.0, 0.01);
   private final BooleanSetting distanceFade = new BooleanSetting("DistanceFade", true);
   private final BooleanSetting drawBackground = new BooleanSetting("DrawBackground", true);
   private final BooleanSetting drawBorder = new BooleanSetting("DrawBorder", true);
   private final BooleanSetting premiumStyle = new BooleanSetting("PremiumStyle", true);
   private final NumberSetting cardRoundness = new NumberSetting("CardRoundness", 8.0, 0.0, 16.0, 1.0);
   private final BooleanSetting accentGlow = new BooleanSetting("AccentGlow", true);
   private final NumberSetting nameFontSize = new NumberSetting("NameFontSize", 18.0, 8.0, 32.0, 1.0);
   private final NumberSetting secondaryFont = new NumberSetting("SecondaryFontSize", 14.0, 8.0, 24.0, 1.0);
   private final BooleanSetting pingPill = new BooleanSetting("PingPill", true);
   private final ColorSetting nameColor = new ColorSetting("NameColor", -986379);
   private final ColorSetting secondaryColor = new ColorSetting("SecondaryTextColor", -4670264);
   private final ColorSetting mutedColor = new ColorSetting("MutedTextColor", -8617329);
   private static final int intVal = -15592421;
   private static final int intVal2 = -15197406;
   private static final int intVal3 = 352321535;
   private static final int intVal4 = 268435455;
   private static final int intVal5 = 352321535;
   private static final int intVal6 = 1711276032;
   private static final int intVal7 = 268435455;
   private static final int intVal8 = -9663233;
   private static final int intVal9 = -13382534;
   private static final int intVal10 = -43691;
   private static final int intVal11 = -14186;
   private static final int intVal12 = -5214977;
   private static final int intVal13 = -11870592;
   private static final int intVal14 = -278748;
   private static final int intVal15 = -1096636;
   private static final int intVal16 = -11870592;
   private static final int intVal17 = -278748;
   private static final int intVal18 = -1096636;
   private static final float floatVal = 3.0F;
   private static final float floatVal2 = 8.0F;
   private static final float floatVal3 = 5.0F;
   private static final float floatVal4 = 3.0F;
   private static final float floatVal5 = 4.0F;
   private static final float floatVal6 = 4.0F;
   private static final float floatVal7 = 6.0F;
   private static final float floatVal8 = 2.0F;
   private static final float floatVal9 = 4.0F;
   private static final Comparator<NametagsModule.Inner1> comparator = Comparator.comparingDouble(NametagsModule::doubleOf);
   private final List<NametagsModule.Inner1> list = new ArrayList<>();
   private static final ConcurrentLinkedQueue<NametagsModule.Inner2> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
   private static final int intVal19 = 16;
   private static final int intVal20 = 1;

   public NametagsModule() {
      super("Nametags", "Premium ImGui nametag plates", Category.RENDER);
      nametagsModule = this;
      this.run6(
         new Setting[]{
            this.players,
            this.mobs,
            this.passive,
            this.self,
            this.showHealth,
            this.showHealthBar,
            this.showArmor,
            this.showPing,
            this.showHeldItem,
            this.showDistance,
            this.highlightFriends,
            this.scale,
            this.maxDistance,
            this.backgroundAlpha,
            this.distanceFade,
            this.drawBackground,
            this.drawBorder,
            this.premiumStyle,
            this.cardRoundness,
            this.accentGlow,
            this.nameFontSize,
            this.secondaryFont,
            this.pingPill,
            this.nameColor,
            this.secondaryColor,
            this.mutedColor
         }
      );
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      Matrix3x2fStack var4 = var1.getMatrices();

      NametagsModule.Inner2 var3;
      while ((var3 = concurrentLinkedQueue.poll()) != null) {
         float var5 = var3.scale;
         var4.pushMatrix();

         try {
            var4.translate(var3.floatVal, var3.floatVal2);
            var4.scale(var5, var5);

            for (int var6 = 0; var6 < var3.stacks.length; var6++) {
               ItemStack var7 = var3.stacks[var6];
               int var8 = var6 * 17;

               try {
                  var1.drawItem(var7, var8, 0);
                  var1.drawStackOverlay(class310.textRenderer, var7, var8, 0);
               } catch (Throwable var13) {
               }
            }
         } finally {
            var4.popMatrix();
         }
      }
   }

   private static void run(List<ItemStack> var0, ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         var0.add(var1);
      }
   }

   public void run3(MinecraftClient var1) {
      if (var1 != null && var1.world != null && var1.player != null && var1.getWindow() != null) {
         concurrentLinkedQueue.clear();
         ImGuiFonts.tryEnsureLoaded();
         ImDrawList var2 = ImGui.getForegroundDrawList();
         ImFont var3 = ImGuiFonts.POPPINS;

         float var4;
         try {
            var4 = var1.getRenderTickCounter().getTickProgress(true);
         } catch (Throwable var32) {
            var4 = 1.0F;
         }

         double var5 = MathHelper.lerp(var4, var1.player.lastRenderX, var1.player.getX());
         double var7 = MathHelper.lerp(var4, var1.player.lastRenderY, var1.player.getY());
         double var9 = MathHelper.lerp(var4, var1.player.lastRenderZ, var1.player.getZ());
         Vec3d var11 = new Vec3d(var5, var7, var9);
         this.list.clear();
         double var12 = this.maxDistance.getValue();
         double var14 = var12 * var12;
         boolean var16 = this.self.getValue();

         for (Entity var18 : var1.world.getEntities()) {
            if (var18 instanceof LivingEntity var19 && var19.isAlive()) {
               boolean var20 = var19 == var1.player || var19 == var1.getCameraEntity();
               if ((!var20 || var16)
                  && (
                     var19 instanceof PlayerEntity
                        ? var20 || this.players.getValue()
                        : (
                           var19 instanceof HostileEntity
                              ? this.mobs.getValue()
                              : (var19 instanceof PassiveEntity ? this.passive.getValue() : this.mobs.getValue())
                        )
                  )) {
                  double var21 = MathHelper.lerp(var4, var19.lastRenderX, var19.getX());
                  double var23 = MathHelper.lerp(var4, var19.lastRenderY, var19.getY());
                  double var25 = MathHelper.lerp(var4, var19.lastRenderZ, var19.getZ());
                  Vec3d var27 = new Vec3d(var21, var23, var25);
                  double var28 = var27.squaredDistanceTo(var11);
                  if (!(var28 > var14)) {
                     this.list.add(new NametagsModule.Inner1(var19, var28, var20, var27));
                  }
               }
            }
         }

         if (!this.list.isEmpty()) {
            this.list.sort(comparator);
            double var33 = var1.getWindow().getScaleFactor();
            float var34 = Math.max(0.1F, (float)this.scale.getValue());
            float var35 = floatOf2((float)this.backgroundAlpha.getValue());
            float var36 = (float)this.cardRoundness.getValue();
            double var22 = this.maxDistance.getValue();

            for (int var24 = this.list.size() - 1; var24 >= 0; var24--) {
               NametagsModule.Inner1 var37 = this.list.get(var24);
               double var26 = Math.sqrt(var37.distSq);
               float var38 = floatOf(var26, var22);
               float var29 = var34 * var38;
               float var30 = Math.max(10.0F, (float)this.nameFontSize.getValue() * var38);
               float var31 = Math.max(9.0F, (float)this.secondaryFont.getValue() * var38);
               this.run2(var2, var3, var37, var14, var33, var29, var35, var36, var30, var31, var1);
            }
         }
      }
   }

   private static float floatOf(double var0, double var2) {
      if (var0 <= 8.0) {
         return 1.0F;
      } else {
         double var6 = (var0 - 8.0) / Math.max(1.0, var2 - 8.0);
         if (var6 < 0.0) {
            var6 = 0.0;
         }

         if (var6 > 1.0) {
            var6 = 1.0;
         }

         return (float)(1.0 - var6 * (1.0 - 0.4));
      }
   }

   private void run2(
      ImDrawList var1,
      ImFont var2,
      NametagsModule.Inner1 var3,
      double var4,
      double var6,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      MinecraftClient var13
   ) {
      LivingEntity var14 = var3.entity;
      Vec3d var15 = var3.interpPos.add(0.0, var14.getHeight() + 0.4, 0.0);
      double[] var16 = Matrix4fUtils.doubleArrayOf(var15);
      if (var16 != null) {
         float var17 = (float)(var16[0] * var6);
         float var18 = (float)(var16[1] * var6);
         float var19 = 1.0F;
         if (this.distanceFade.getValue()) {
            float var20 = (float)Math.min(1.0, Math.sqrt(var3.distSq) / Math.sqrt(var4));
            float var21 = var20 * var20 * (3.0F - 2.0F * var20);
            var19 = Math.max(0.15F, 1.0F - var21);
         }

         String var85 = var14.getDisplayName().getString();
         boolean var86 = this.highlightFriends.getValue() && var14 instanceof PlayerEntity && ChatFilterHelper.chatFilterHelper.check3(var85);
         int var22;
         if (var3.self) {
            var22 = -5214977;
         } else if (var86) {
            var22 = -13382534;
         } else if (var14 instanceof PlayerEntity) {
            var22 = -9663233;
         } else if (var14 instanceof HostileEntity) {
            var22 = -43691;
         } else if (var14 instanceof PassiveEntity) {
            var22 = -14186;
         } else {
            var22 = -43691;
         }

         int var23 = var86 ? -13382534 : this.nameColor.getValue();
         float var24 = 1.0F;
         if (var14.getMaxHealth() > 0.0F) {
            var24 = floatOf2(var14.getHealth() / var14.getMaxHealth());
         }

         int var25 = intOf(var24);
         String var26 = null;
         if (!this.showHealthBar.getValue() && this.showHealth.getValue()) {
            var26 = Math.round(var14.getHealth()) + " HP";
         }

         String var27 = null;
         int var28 = this.secondaryColor.getValue();
         if (this.showPing.getValue() && var14 instanceof PlayerEntity && var13.getNetworkHandler() != null) {
            PlayerListEntry var29 = var13.getNetworkHandler().getPlayerListEntry(var14.getUuid());
            if (var29 != null) {
               int var30 = var29.getLatency();
               var27 = Integer.toString(var30);
               var28 = intOf2(var30);
            }
         }

         double var87 = Math.sqrt(var3.distSq);
         String var31 = null;
         if (this.showDistance.getValue() && var87 >= 1.0) {
            var31 = String.format("%.0fm", var87);
         }

         ArrayList var33 = new ArrayList(5);
         if (this.showArmor.getValue()) {
            run(var33, var14.getEquippedStack(EquipmentSlot.HEAD));
            run(var33, var14.getEquippedStack(EquipmentSlot.CHEST));
            run(var33, var14.getEquippedStack(EquipmentSlot.LEGS));
            run(var33, var14.getEquippedStack(EquipmentSlot.FEET));
         }

         if (this.showHeldItem.getValue()) {
            run(var33, var14.getMainHandStack());
         }

         boolean var34 = !var33.isEmpty();
         int var35 = var33.size();
         float var36 = var35 * 16 + Math.max(0, var35 - 1);
         float var38 = var36 * var8 * (float)var6;
         float var39 = 16.0F * var8 * (float)var6;
         boolean var40 = var2 != null;
         float var43 = 0.0F;
         if (var40) {
            ImGui.pushFont(var2, var11);
         }

         float var41;
         float var42;
         try {
            var41 = ImGui.calcTextSize(var85).x;
            var42 = ImGui.getTextLineHeight();
         } finally {
            if (var40) {
               ImGui.popFont();
            }
         }

         float var44 = 0.0F;
         float var45 = 0.0F;
         float var47 = 0.0F;
         if (var40) {
            ImGui.pushFont(var2, var12);
         }

         try {
            var47 = ImGui.getTextLineHeight();
            if (var27 != null) {
               var44 = ImGui.calcTextSize(var27).x;
            }

            if (var31 != null) {
               var45 = ImGui.calcTextSize(var31).x;
            }

            if (var26 != null) {
               var43 = ImGui.calcTextSize(var26).x;
            }
         } finally {
            if (var40) {
               ImGui.popFont();
            }
         }

         float var48 = 0.0F;
         if (var27 != null) {
            var48 = this.pingPill.getValue() ? var44 + 12.0F : var44;
         }

         float var49 = 0.0F;
         if (var27 != null) {
            var49 += var48;
         }

         if (var31 != null) {
            var49 += (var49 > 0.0F ? 4.0F : 0.0F) + var45;
         }

         if (var26 != null) {
            var49 += (var49 > 0.0F ? 4.0F : 0.0F) + var43;
         }

         float var50 = var41 + (var49 > 0.0F ? 4.0F : 0.0F) + var49;
         float var51 = 0.0F;
         if (this.showHealthBar.getValue()) {
            var51 = Math.max(80.0F, var50);
         }

         float var52 = Math.max(0.0F, var34 ? var38 : 0.0F);
         float var53 = Math.max(Math.max(var50, var51), var52);
         float var55 = 11.0F + 8.0F + var53;
         float var56 = 10.0F + var42 + (this.showHealthBar.getValue() ? 7.0F : 0.0F) + 0.0F + (var34 ? 3.0F + var39 : 0.0F);
         float var57 = var17 - var55 * 0.5F;
         float var58 = var18 - var56 - 2.0F * var8;
         float var59 = var57 + var55;
         float var60 = var58 + var56;
         if (this.drawBackground.getValue() && this.premiumStyle.getValue()) {
            run5(var1, var57, var58, var59, var60, var10, var19);
         }

         if (this.drawBackground.getValue()) {
            int var61 = Math.round(255.0F * var9 * var19);
            int var62 = intOf4(var61 << 24 | 1184795, 1.0F);
            var1.addRectFilled(var57, var58, var59, var60, var62, var10);
            if (this.premiumStyle.getValue()) {
               float var63 = Math.max(6.0F, var56 * 0.22F);
               int var64 = intOf4((int)(255.0F * var9) << 24 | 1579810, var19);
               int var65 = intOf4(1579810, 0.0F);
               var1.addRectFilledMultiColor(var57 + 1.0F, var58 + 1.0F, var59 - 1.0F, var58 + var63, var64, var64, var65, var65);
            }
         }

         if (this.drawBorder.getValue()) {
            int var89 = intOf4(352321535, var19);
            var1.addRect(var57 + 0.5F, var58 + 0.5F, var59 - 0.5F, var60 - 0.5F, var89, var10, 0, 1.0F);
         }

         if (this.premiumStyle.getValue()) {
            float var90 = Math.max(2.0F, var10 * 0.6F);
            int var92 = intOf4(268435455, var19);
            var1.addLine(var57 + var90, var58 + 1.5F, var59 - var90, var58 + 1.5F, var92, 1.0F);
         }

         float var91 = var57 + 1.0F;
         float var93 = var58 + 1.0F;
         float var94 = var91 + 3.0F;
         float var95 = var60 - 1.0F;
         if (this.accentGlow.getValue()) {
            int var96 = var22 & 16777215;

            for (int var66 = 3; var66 >= 1; var66--) {
               int var67 = Math.round((var22 >>> 24 & 0xFF) * (0.3F / var66));
               if (var67 > 0) {
                  int var68 = intOf4(var67 << 24 | var96, var19);
                  float var69 = var66 * 1.0F;
                  var1.addRectFilled(var91 - var69, var93 - var69, var94 + var69, var95 + var69, var68, var10 * 0.4F + var69, 80);
               }
            }
         }

         int var97 = intOf4(var22, var19);
         var1.addRectFilled(var91, var93, var94, var95, var97, var10 * 0.4F, 80);
         float var98 = var57 + 11.0F;
         float var99 = var58 + 5.0F;
         int var100 = intOf4(var23, var19);
         run6(var1, var2, var98, var99, var100, var85, var11);
         float var101 = var59 - 8.0F;
         if (var26 != null) {
            int var70 = intOf4(intOf(var24), var19);
            float var71 = var99 + (var42 - var47) * 0.5F + 1.0F;
            float var72 = var101 - var43;
            run6(var1, var2, var72, var71, var70, var26, var12);
            var101 = var72 - 4.0F;
         }

         if (var31 != null) {
            int var104 = intOf4(this.mutedColor.getValue(), var19);
            float var107 = var99 + (var42 - var47) * 0.5F + 1.0F;
            float var111 = var101 - var45;
            run6(var1, var2, var111, var107, var104, var31, var12);
            var101 = var111 - 4.0F;
         }

         if (var27 != null) {
            float var105 = var99 + (var42 - var47) * 0.5F + 1.0F;
            if (this.pingPill.getValue()) {
               float var112 = var101 - var48;
               float var73 = var105 - 2.0F;
               float var74 = var105 + var47 + 2.0F;
               var1.addRectFilled(var112, var73, var101, var74, intOf4(268435455, var19), 4.0F);
               run6(var1, var2, var112 + 6.0F, var105, intOf4(var28, var19), var27, var12);
               var101 = var112 - 4.0F;
            } else {
               float var108 = var101 - var44;
               run6(var1, var2, var108, var105, intOf4(var28, var19), var27, var12);
               var101 = var108 - 4.0F;
            }
         }

         float var106 = var99 + var42;
         if (this.showHealthBar.getValue()) {
            float var109 = var57 + 11.0F;
            float var113 = var59 - 8.0F;
            float var115 = var106 + 3.0F;
            float var117 = var115 + 4.0F;
            int var76 = intOf4(352321535, var19);
            var1.addRectFilled(var109, var115, var113, var117, var76, 2.0F);
            if (var24 > 0.0F) {
               float var77 = Math.max(1.0F, (var113 - var109) * var24);
               int var78 = intOf4(var25, var19);
               var1.addRectFilled(var109, var115, var109 + var77, var117, var78, 2.0F);
            }

            var106 = var117;
         }

         if (var34) {
            float var110 = var106 + 3.0F;
            float var114 = var57 + 11.0F;
            float var116 = var114 / (float)var6;
            float var118 = var110 / (float)var6;
            concurrentLinkedQueue.add(new NametagsModule.Inner2(var116, var118, var8, (ItemStack[])(var33.toArray(new Object[0]))));
         }
      }
   }

   private static void run5(ImDrawList var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      int[] var7 = new int[]{53, 32, 16};
      int[] var8 = new int[]{3, 5, 7};

      for (int var9 = 0; var9 < 3; var9++) {
         int var10 = intOf4(var7[var9] << 24, var6);
         float var11 = var9;
         var0.addRectFilled(var1 - var11, var2 + var8[var9] - var11, var3 + var11, var4 + var8[var9] + var11, var10, var5 + var11);
      }
   }

   private static int intOf(float var0) {
      if (var0 > 0.6F) {
         return -11870592;
      } else {
         return var0 > 0.3F ? -278748 : -1096636;
      }
   }

   private static int intOf2(int var0) {
      if (var0 < 50) {
         return -11870592;
      } else {
         return var0 < 150 ? -278748 : -1096636;
      }
   }

   private static void run6(ImDrawList var0, ImFont var1, float var2, float var3, int var4, String var5, float var6) {
      int var7 = (int)var6;
      if (var1 != null) {
         var0.addText(var1, var7, var2, var3, var4, var5);
      } else {
         var0.addText(var2, var3, var4, var5);
      }
   }

   private static int intOf4(int var0, float var1) {
      float var2 = floatOf2(var1);
      int var3 = var0 >>> 24 & 0xFF;
      int var4 = var0 >>> 16 & 0xFF;
      int var5 = var0 >>> 8 & 0xFF;
      int var6 = var0 & 0xFF;
      var3 = Math.max(0, Math.min(255, Math.round(var3 * var2)));
      return var3 << 24 | var6 << 16 | var5 << 8 | var4;
   }

   private static float floatOf2(float var0) {
      return var0 < 0.0F ? 0.0F : (var0 > 1.0F ? 1.0F : var0);
   }

   private static double doubleOf(NametagsModule.Inner1 var0) {
      return var0.distSq;
   }

record Inner1(LivingEntity entity, double distSq, boolean self, Vec3d interpPos) {


   public boolean isSelf() {
      return this.self;
   }
}

record Inner2(float floatVal, float floatVal2, float scale, ItemStack[] stacks) {


   public float getFloat() {
      return this.floatVal;
   }

   public float getFloat2() {
      return this.floatVal2;
   }
}
}

