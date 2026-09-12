package dev.fede.nyx.imgui;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.module.modules.addons.SmtcMediaClient;
import dev.fede.nyx.auth.AuthGate;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.module.modules.addons.SpotifyHUDModule;
import dev.fede.nyx.module.modules.client.HUDModule;
import dev.fede.nyx.module.modules.client.KeystrokeHUDModule;
import dev.fede.nyx.module.modules.client.TargetHUDModule;
import dev.fede.nyx.module.modules.donutsmp.RegionMap;
import dev.fede.nyx.module.modules.render.ESP;
import dev.fede.nyx.module.modules.render.ItemESPModule;
import dev.fede.nyx.module.modules.render.NametagsModule;
import dev.fede.nyx.module.modules.render.RadarModule;
import dev.fede.nyx.ui.NotificationUtils;
import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImVec2;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public final class ImGuiHud {
   private static String spSmoothTitle = "";
   private static long spAnchorRealMs = 0L;
   private static long spAnchorPosMs = 0L;
   private static boolean spWasPlaying = false;

   private ImGuiHud() {
   }

   public static void render() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0 != null && var0.getWindow() != null) {
         if ((AuthGate.getLong() ^ (4337339287489740800L | 80156L) + -3083466098849768613L + 5867698884700561808L)
            == (
                  (-1468173478522781696L | 431522069214189L | -8560421568133838503L) - (-1467741956453567507L & -8560421568133838503L)
                     ^ -5815128799011615951L
                     ^ (2612238188347658226L ^ 3874620198018061836L)
                        + ((2612238188347658226L & (-7594761660876546185L ^ -6675295584286865029L)) << 1)
                        + -3758981327313277503L
               )
               + (
                  (4504668383521625007L + -3663259210140556045L + 358952246475088165L + 4353721253878325958L + -9166446019572730632L & 2727877059052442559L)
                     << 1
               )
               + (
                  4501822829172082879L
                        + 8489329727478426423L
                        + 7195337234384925122L
                        + 1357729902484337199L
                        - ((1739745717325882808L & 1357729902484337199L) << 1)
                     ^ 2575242982711724479L
               )) {
            try {
               NotificationUtils.run3(var0);
            } catch (Throwable var14) {
               System.err.println("[c] notifications draw failed: null");
            }

            HUDModule var1 = HUDModule.hUDModule;
            if (var1 != null && var1.isEnabled3()) {
               if (var1.isEnabled()) {
                  try {
                     drawWatermark(var1, var0);
                  } catch (Throwable var13) {
                     System.err.println("[c] watermark draw failed: null");
                  }
               }

               if (var1.isEnabled2()) {
                  try {
                     drawArrayList(var1, var0);
                  } catch (Throwable var12) {
                     System.err.println("[c] arraylist draw failed: null");
                  }
               }

               try {
                  drawTargetHUD(var1, var0);
               } catch (Throwable var11) {
                  System.err.println("[c] targethud draw failed: null");
               }

               try {
                  drawInfoPanel(var1, var0);
               } catch (Throwable var10) {
                  System.err.println("[c] infopanel draw failed: null");
               }

               if (SpotifyHUDModule.spotifyHUDModule != null && SpotifyHUDModule.spotifyHUDModule.isEnabled3()) {
                  try {
                     drawSpotifyHUD(var0);
                  } catch (Throwable var9) {
                     System.err.println("[c] spotifyhud draw failed: null");
                  }
               }

               if (RadarModule.radarModule != null && RadarModule.radarModule.isEnabled3()) {
                  try {
                     RadarModule.radarModule.run(ImGui.getForegroundDrawList(), var0);
                  } catch (Throwable var8) {
                     System.err.println("[c] radar draw failed: null");
                  }
               }

               if (KeystrokeHUDModule.keystrokeHUDModule != null && KeystrokeHUDModule.keystrokeHUDModule.isEnabled3()) {
                  try {
                     KeystrokeHUDModule.keystrokeHUDModule.run4(var0);
                  } catch (Throwable var7) {
                     System.err.println("[c] keystrokehud draw failed: null");
                  }
               }

               if (RegionMap.regionMap != null && RegionMap.regionMap.isEnabled3()) {
                  try {
                     RegionMap.regionMap.run(ImGui.getForegroundDrawList(), var0);
                  } catch (Throwable var6) {
                     System.err.println("[c] regionmap draw failed: null");
                  }
               }

               if (NametagsModule.nametagsModule != null && NametagsModule.nametagsModule.isEnabled3()) {
                  try {
                     NametagsModule.nametagsModule.run3(var0);
                  } catch (Throwable var5) {
                     System.err.println("[c] nametags draw failed: null");
                  }
               }

               if (ItemESPModule.itemESPModule != null && ItemESPModule.itemESPModule.isEnabled3()) {
                  try {
                     ItemESPModule.itemESPModule.run3(var0);
                  } catch (Throwable var4) {
                     System.err.println("[c] itemesp draw failed: null");
                  }
               }

               if (ESP.eSP != null && ESP.eSP.isEnabled3()) {
                  try {
                     ESP.eSP.run3(var0);
                  } catch (Throwable var3) {
                     System.err.println("[c] esp draw failed: null");
                  }
               }
            }
         }
      }
   }

   private static void drawSpotifyHUD(MinecraftClient var0) {
      SpotifyHUDModule var1 = SpotifyHUDModule.spotifyHUDModule;
      Object var2 = var1.getSmtcMediaClienta();
      if (var2 != null) {
         float var13;
         float var14;
         label499: {
            float var9 = ImGui.getIO().getDisplaySizeX();
            float var10 = ImGui.getIO().getDisplaySizeY();
            float var11 = var1.posX.getValueFloat();
            float var12 = var1.posY.getValueFloat();
            String var15 = var1.anchor.getMode();
            switch (var15.hashCode()) {
               case -913702425:
                  if (var15.equals("TopRight")) {
                     var13 = var9 - 320.0F - var11;
                     var14 = var12;
                     break label499;
                  }
                  break;
               case 310672626:
                  if (var15.equals("BottomLeft")) {
                     var13 = var11;
                     var14 = var10 - 94.0F - var12;
                     break label499;
                  }
                  break;
               case 524532444:
                  if (var15.equals("TopLeft")) {
                     var13 = var11;
                     var14 = var12;
                     break label499;
                  }
                  break;
               case 1046577809:
                  if (var15.equals("BottomRight")) {
                     var13 = var9 - 320.0F - var11;
                     var14 = var10 - 94.0F - var12;
                     break label499;
                  }
            }

            var13 = var11;
            var14 = var10 - 94.0F - var12;
         }

         int var91 = -16119025;
         int var26 = argb2imu32(-16119025, 1.0F);
         int var27 = argb2imu32(352321535, 1.0F);
         int var28 = argb2imu32(1073741824, 1.0F);
         int var29 = argb2imu32(-723464, 1.0F);
         int var30 = argb2imu32(-7696224, 1.0F);
         int var31 = argb2imu32(-986378, 1.0F);
         int var32 = argb2imu32(-7696224, 1.0F);
         int var33 = argb2imu32(872415231, 1.0F);
         int var34 = argb2imu32(-14829228, 1.0F);
         int var35 = argb2imu32(452984831, 1.0F);
         int var36 = argb2imu32(-2130706433, 1.0F);
         ImDrawList var37 = ImGui.getForegroundDrawList();
         var37.addRectFilled(var13 - 2.0F, var14 + 3.0F, var13 + 320.0F + 2.0F, var14 + 94.0F + 6.0F, var28, 12.0F);
         var37.addRectFilled(var13, var14, var13 + 320.0F, var14 + 94.0F, var26, 10.0F);
         var37.addRect(var13, var14, var13 + 320.0F, var14 + 94.0F, var27, 10.0F, 0, 1.0F);
         float var38 = var13 + 10.0F;
         if (var1.showArt.getValue()) {
            float var39 = var13 + 10.0F;
            float var40 = var14 + 10.0F;
            float var41 = var39 + 74.0F;
            float var42 = var40 + 74.0F;
            int var43 = var1.getInt();
            if (var43 > 0) {
               var37.addImageRounded(var43, var39, var40, var41, var42, 0.0F, 0.0F, 1.0F, 1.0F, var29, 6.0F);
            } else {
               var37.addRectFilled(var39, var40, var41, var42, var35, 6.0F);
               float var44 = (var39 + var41) * 0.5F;
               float var45 = (var40 + var42) * 0.5F;
               var37.addLine(var44 + 6.0F, var45 - 12.0F, var44 + 6.0F, var45 + 6.0F, var36, 2.0F);
               var37.addLine(var44 + 6.0F, var45 - 12.0F, var44 + 14.0F, var45 - 14.0F, var36, 2.0F);
               var37.addCircleFilled(var44 + 2.0F, var45 + 8.0F, 4.2F, var36, 14);
            }

            var38 = var41 + 10.0F;
         }

         ImFont var92 = ImGuiFonts.POPPINS;
         float var93 = var13 + 320.0F - 10.0F;
         float var94 = 14.0F;
         float var95 = 11.0F;
         float var96 = var14 + 10.0F + 2.0F;
         float var97 = var93 - var38;
         if (var92 != null) {
            ImGui.pushFont(var92, 14.0F);
         }

         try {
            String var98 = ellipsize(((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).title(), var97);
            if (var92 != null) {
               var37.addText(var92, 14, var38, var96, var29, var98);
            } else {
               var37.addText(var38, var96, var29, var98);
            }
         } finally {
            if (var92 != null) {
               ImGui.popFont();
            }
         }

         String var99 = ((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).artist();
         if (var99 != null && !var99.isEmpty()) {
            if (var92 != null) {
               ImGui.pushFont(var92, 11.0F);
            }

            try {
               String var46 = ellipsize(var99, var97);
               if (var92 != null) {
                  var37.addText(var92, 11, var38, var96 + 18.0F, var30, var46);
               } else {
                  var37.addText(var38, var96 + 18.0F, var30, var46);
               }
            } finally {
               if (var92 != null) {
                  ImGui.popFont();
               }
            }
         }

         long var100 = ((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).durationMs();
         long var48 = System.currentTimeMillis();
         long var50 = ((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).playing() ? var48 - ((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).lastPollMs() : 0L;
         long var52 = ((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).positionMs() + var50;
         String var54 = ((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).title();
         boolean var55 = var54 == null ? !spSmoothTitle.isEmpty() : !var54.equals(spSmoothTitle);
         boolean var56 = ((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).playing() && !spWasPlaying;
         if (var55 || var56 || spAnchorRealMs == 0L) {
            spSmoothTitle = var54 == null ? "" : var54;
            spAnchorRealMs = var48;
            spAnchorPosMs = ((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).positionMs() + (((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).playing() ? var48 - ((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).lastPollMs() : 0L);
         } else if (((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).playing()) {
            long var57 = spAnchorPosMs + (var48 - spAnchorRealMs);
            long var59 = var52 - var57;
            if (var59 > 800L) {
               spAnchorRealMs = var48;
               spAnchorPosMs = var52;
            } else if (var59 < -2000L) {
               spAnchorRealMs = var48;
               spAnchorPosMs = var52;
            }
         } else {
            spAnchorRealMs = var48;
            spAnchorPosMs = ((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).positionMs();
         }

         spWasPlaying = ((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).playing();
         long var101 = ((dev.fede.nyx.module.modules.addons.SmtcMediaClient.Inner1)var2).playing() ? spAnchorPosMs + (var48 - spAnchorRealMs) : spAnchorPosMs;
         if (var100 > 0L && var101 > var100) {
            var101 = var100;
         }

         if (var101 < 0L) {
            var101 = 0L;
         }

         float var102 = 0.0F;
         if (var100 > 0L) {
            var102 = (float)Math.min(1.0, Math.max(0.0, (double)var101 / var100));
         }

         String var60 = formatMs(var101);
         String var61 = formatMs(var100);
         float var67 = var14 + 94.0F - 10.0F - 6.0F;
         float var68 = var67 - 2.0F;
         float var69 = var68 + 4.0F;
         float var70 = var67 - 4.5F - 0.5F;
         if (var92 != null) {
            ImGui.pushFont(var92, 9.0F);
         }

         try {
            float var71 = ImGui.calcTextSize(var60).x;
            float var72 = ImGui.calcTextSize(var61).x;
            float var73 = var38 + var71 + 6.0F;
            float var74 = var73 + 200.0F;
            float var75 = var93 - var72 - 6.0F;
            if (var74 > var75) {
               var74 = var75;
            }

            if (var74 < var73 + 20.0F) {
               var74 = var73 + 20.0F;
            }

            if (var92 != null) {
               var37.addText(var92, 9, var38, var70, var31, var60);
            } else {
               var37.addText(var38, var70, var31, var60);
            }

            float var76 = var74 + 6.0F;
            if (var92 != null) {
               var37.addText(var92, 9, var76, var70, var32, var61);
            } else {
               var37.addText(var76, var70, var32, var61);
            }

            var37.addRectFilled(var73, var68, var74, var69, var33, 2.0F);
            if (var102 > 0.0F) {
               float var77 = var73 + (var74 - var73) * var102;
               var37.addRectFilled(var73, var68, var77, var69, var34, 2.0F);
            }
         } finally {
            if (var92 != null) {
               ImGui.popFont();
            }
         }
      }
   }

   private static String normalizeSource(String source) {
      if (source == null) {
         return "";
      } else {
         String var1 = source.trim();
         if (var1.isEmpty()) {
            return "";
         } else {
            int var2 = var1.indexOf(33);
            if (var2 >= 0 && var2 + 1 < var1.length()) {
               var1 = var1.substring(var2 + 1);
            }

            int var3 = var1.lastIndexOf(46);
            if (var3 >= 0) {
               String var4 = var1.substring(var3 + 1);
               if (var4.equalsIgnoreCase("exe")) {
                  var1 = var1.substring(0, var3);
               } else {
                  var1 = var4;
               }
            }

            if (var1.length() > 14) {
               var1 = var1.substring(0, 14);
            }

            return var1;
         }
      }
   }

   private static String formatMs(long ms) {
      if (ms <= 0L) {
         return "0:00";
      } else {
         long var2 = ms / 1000L;
         long var4 = var2 / 3600L;
         long var6 = var2 % 3600L / 60L;
         long var8 = var2 % 60L;
         return var4 > 0L ? String.format("%d:%02d:%02d", var4, var6, var8) : String.format("%d:%02d", var6, var8);
      }
   }

   private static String ellipsize(String var0, float var1) {
      if (var0 != null && !var0.isEmpty()) {
         if (ImGui.calcTextSize(var0).x <= var1) {
            return var0;
         } else if (var1 <= ImGui.calcTextSize("…").x) {
            return "…";
         } else {
            int var3 = 0;
            int var4 = var0.length();

            while (var3 < var4) {
               int var5 = var3 + var4 + 1 >>> 1;
               String var6 = var0.substring(0, var5) + "…";
               if (ImGui.calcTextSize(var6).x <= var1) {
                  var3 = var5;
               } else {
                  var4 = var5 - 1;
               }
            }

            return var3 == 0 ? "…" : var0.substring(0, var3) + "…";
         }
      } else {
         return "";
      }
   }

   private static void drawWatermark(HUDModule var0, MinecraftClient var1) {
      ImFont var2 = ImGuiFonts.POPPINS;
      boolean var3 = var2 != null;
      if (var3) {
         ImGui.pushFont(var2, 20.0F);
      }

      try {
         int var4 = var1.getCurrentFps();
         float var11 = clamp01((float)var0.getDouble() / Math.max(0.003921569F, 0.9411765F));
         var11 = Math.min(1.0F, var11);
         int var12 = argb2imu32(-267909112, var11);
         int var13 = argb2imu32(-1, 1.0F);
         int var14 = argb2imu32(-1, 1.0F);
         int var15 = argb2imu32(-5226241, 1.0F);
         int var16 = argb2imu32(-3116801, 1.0F);
         int var17 = argb2imu32(-1073741824, 1.0F);
         String var20 = var4 + " FPS";
         float var21 = ImGui.calcTextSize("CODE ENGINE").x;
         float var22 = ImGui.calcTextSize("V1.0").x;
         float var23 = ImGui.calcTextSize(var20).x;
         float var24 = ImGui.getTextLineHeight();
         float var30 = var24 + 14.0F;
         float var31 = var30 - 14.0F;
         float var32 = var31 + 24.0F;
         float var33 = var21 + 8.0F + var22 + 24.0F;
         float var34 = var23 + 24.0F;
         float var35 = var0.getInt2();
         float var36 = var0.getInt3();
         ImDrawList var37 = ImGui.getForegroundDrawList();
         float var39 = var35 + var32;
         var37.addRectFilled(var35, var36, var39, var36 + var30, var12, 8.0F);
         int var40 = WatermarkLogoCache.glId();
         float var41 = var35 + var32 / 2.0F;
         float var42 = var36 + var30 / 2.0F;
         if (var40 == 0) {
            float var58 = var31 * 0.4F;
            var37.addTriangleFilled(var41, var42 - var58, var41 + var58, var42, var41, var42 + var58, var15);
            var37.addTriangleFilled(var41, var42 - var58, var41, var42 + var58, var41 - var58, var42, var15);
         } else {
            int var43 = WatermarkLogoCache.width();
            int var44 = WatermarkLogoCache.height();
            float var45 = var43 > 0 && var44 > 0 ? (float)var43 / var44 : 1.0F;
            float var46 = var30 - 4.0F;
            float var47 = var32 - 4.0F;
            float var48 = var46;
            float var49 = var46 * var45;
            if (var49 > var47) {
               var49 = var47;
               var48 = var47 / var45;
            }

            float var50 = var41 - var49 * 0.5F;
            float var51 = var42 - var48 * 0.5F;
            float var52 = var50 + var49;
            float var53 = var51 + var48;
            var37.addImage(var40, var50, var51, var52, var53, 0.0F, 0.0F, 1.0F, 1.0F, argb2imu32(-1, 1.0F));
         }

         float var59 = var39 + 5.0F;
         float var60 = var59 + var33;
         var37.addRectFilled(var59, var36, var60, var36 + var30, var12, 8.0F);
         float var61 = var59 + 12.0F;
         float var63 = var36 + (var30 - var24) / 2.0F;
         var37.addText(var61 + 1.0F, var63 + 1.0F, var17, "CODE ENGINE");
         var37.addText(var61, var63, var13, "CODE ENGINE");
         var61 += var21 + 8.0F;
         var37.addText(var61 + 1.0F, var63 + 1.0F, var17, "V1.0");
         var37.addText(var61, var63, var14, "V1.0");
         float var64 = var60 + 5.0F;
         float var65 = var64 + var34;
         var37.addRectFilled(var64, var36, var65, var36 + var30, var12, 8.0F);
         float var66 = var64 + 12.0F;
         var37.addText(var66 + 1.0F, var63 + 1.0F, var17, var20);
         var37.addText(var66, var63, var13, var20);
      } finally {
         if (var3) {
            ImGui.popFont();
         }
      }
   }

   private static void drawArrayList(HUDModule var0, MinecraftClient var1) {
      ImGuiFonts.tryEnsureLoaded();
      List var2 = NyxClient.MODULES.getList2();
      if (var2 != null && !var2.isEmpty()) {
         boolean var3 = var0.isEnabled4();
         int var4 = var0.getInt8();
         ImFont var5 = ImGuiFonts.POPPINS;
         boolean var6 = var5 != null;
         if (var6) {
            ImGui.pushFont(var5, 20.0F);
         }

         try {
            int var7 = var2.size();
            String[] var8 = new String[var7];
            String[] var9 = new String[var7];
            float[] var10 = new float[var7];
            float[] var11 = new float[var7];
            float[] var12 = new float[var7];
            float var14 = 0.0F;

            for (int var15 = 0; var15 < var7; var15++) {
               Module var16 = (Module)var2.get(var15);
               var8[var15] = stripFormatCodes(var16.getString());
               String var17 = var3 ? var16.getString3() : null;
               String var18 = var17 != null && !var17.isEmpty() ? stripFormatCodes(var17) : null;
               var9[var15] = var18 != null && !var18.isEmpty() ? var18 : null;
               var10[var15] = ImGui.calcTextSize(var8[var15]).x;
               var11[var15] = var9[var15] != null ? ImGui.calcTextSize(var9[var15]).x : 0.0F;
               var12[var15] = var10[var15] + (var9[var15] != null ? 6.0F + var11[var15] : 0.0F);
               if (var12[var15] > var14) {
                  var14 = var12[var15];
               }
            }

            Integer[] var71 = new Integer[var7];

            for (int var72 = 0; var72 < var7; var72++) {
               var71[var72] = var72;
            }

            Arrays.sort(var71, (a, b) -> Float.compare(var12[b], var12[a]));
            float var73 = 6.0F;
            float var74 = 1.0F;
            float var75 = ImGui.getTextLineHeight();
            float var19 = var75 * 1.2F;
            float var20 = var14 + 12.0F;
            float var21 = var7 * var19 + Math.max(0, var7 - 1) * 1.0F;
            float var22 = ImGui.getIO().getDisplaySizeX();
            float var23 = var22 - var0.getInt4();
            float var24 = var0.getInt5();
            ImGui.setNextWindowPos(var23, var24, 1, 1.0F, 0.0F);
            ImGui.setNextWindowSize(var20, var21, 1);
            ImGui.pushStyleVar(2, 0.0F, 0.0F);
            ImGui.pushStyleVar(14, 0.0F, 0.0F);

            try {
               if (ImGui.begin("##nyx-arraylist", 209903)) {
                  ImDrawList var26 = ImGui.getWindowDrawList();
                  ImVec2 var27 = ImGui.getWindowPos();
                  long var33 = System.currentTimeMillis();
                  float var35 = (float)(var33 % 1800L) / 1800.0F;
                  int var36 = Math.min(255, var4 * 176 >>> 8);
                  int var37 = argb2imu32(var36 << 24 | 1052692, 1.0F);
                  int var38 = argb2imu32(-1, 1.0F);

                  for (int var40 = 0; var40 < var7; var40++) {
                     int var41 = var71[var40];
                     String var42 = var8[var41];
                     String var43 = var9[var41];
                     float var44 = var12[var41];
                     float var45 = var27.y + var40 * (var19 + 1.0F);
                     float var46 = var45 + var19;
                     float var47 = var27.x + var20;
                     float var48 = var47 - (var44 + 12.0F);
                     float var49 = var7 > 1 ? (float)var40 / (var7 - 1) : 0.5F;
                     float var50 = var49 + var35;
                     float var51 = 0.5F + 0.5F * (float)Math.sin((Math.PI * 2) * var50);
                     int var52 = lerpArgb(-10773290, -4926209, var51);
                     int var53 = argb2imu32(var52, 1.0F);
                     float var54 = Math.max(0.0F, (var51 - 0.35F) / 0.65F);
                     if (var54 > 0.02F) {
                        int var55 = var52 & 16777215;

                        for (int var56 = 3; var56 >= 1; var56--) {
                           float var57 = var56 / 3.0F;
                           float var58 = (1.0F - var57) * (1.0F - var57);
                           int var59 = Math.round(102.0F * var58 * var54);
                           if (var59 > 0) {
                              int var60 = var59 << 24 | var55;
                              int var61 = argb2imu32(var60, 1.0F);
                              float var62 = var56 * 1.5F;
                              var26.addRectFilled(var48 - var62, var45 - var62, var47 + var62, var46 + var62, var61, 5.0F + var62);
                           }
                        }
                     }

                     var26.addRectFilled(var48, var45, var47, var46, var37, 5.0F);
                     float var76 = var45 + (var19 - var75) * 0.5F;
                     if (var43 != null) {
                        float var77 = var47 - 6.0F - var11[var41];
                        float var79 = var77 - 6.0F - var10[var41];
                        var26.addText(var79, var76, var53, var42);
                        var26.addText(var77, var76, var38, var43);
                     } else {
                        float var78 = var47 - 6.0F - var10[var41];
                        var26.addText(var78, var76, var53, var42);
                     }
                  }

                  ImGui.dummy(var20, var21);
               }

               ImGui.end();
            } finally {
               ImGui.popStyleVar(2);
            }
         } finally {
            if (var6) {
               ImGui.popFont();
            }
         }
      }
   }

   private static void drawTargetHUD(HUDModule var0, MinecraftClient var1) {
      TargetHUDModule var2 = TargetHUDModule.targetHUDModule;
      if (var2 != null && var2.isEnabled3()) {
         LivingEntity var3 = TargetHUDModule.getclass1309();
         boolean var4 = var3 == null;
         if (!var4 || TargetHUDModule.isEnabled3_s()) {
            float var5 = MathHelper.clamp(TargetHUDModule.getFloat2(), 0.0F, 1.0F);
            if (!(var5 <= 0.01F)) {
               ImGuiFonts.tryEnsureLoaded();
               int var6 = withFade(-15197406, var5);
               int var7 = withFade(587202559, var5);
               int var8 = withFade(1073741824, var5);
               int var9 = withFade(-1, var5);
               int var10 = withFade(-7696224, var5);
               int var11 = withFade(-14012872, var5);
               int var12 = withFade(-1096636, var5);
               int var13 = withFade(-4670264, var5);
               int var14 = withFade(-9663233, var5);
               int var15 = withFade(721420287, var5);
               int var16 = withFade(1090519039, var5);
               int var17 = withFade(-14473425, var5);
               int var18 = withFade(867489023, var5);
               boolean var27 = !var4 && TargetHUDModule.isEnabled4();
               float var28 = var27 ? 96.0F : 80.0F;
               float var29 = TargetHUDModule.getInt3();
               float var30 = TargetHUDModule.getInt2();
               float var31 = var29 + 240.0F;
               float var32 = var30 + var28;
               ImDrawList var33 = ImGui.getForegroundDrawList();
               var33.addRectFilled(var29 + 2.0F, var30 + 3.0F, var31 + 2.0F, var32 + 3.0F, var8, 10.0F);
               var33.addRectFilled(var29, var30, var31, var32, var6, 8.0F);
               var33.addRect(var29 + 0.5F, var30 + 0.5F, var31 - 0.5F, var32 - 0.5F, var7, 8.0F, 0, 1.0F);
               float var34 = var29 + 10.0F;
               float var35 = var30 + 10.0F;
               int var36 = var4 ? -1 : resolveHeadTexture(var3);
               if (var36 > 0 && TargetHUDModule.isEnabled_s()) {
                  var33.addImageRounded(var36, var34, var35, var34 + 32.0F, var35 + 32.0F, 0.125F, 0.125F, 0.25F, 0.25F, var9, 4.0F);
                  var33.addImageRounded(var36, var34, var35, var34 + 32.0F, var35 + 32.0F, 0.625F, 0.125F, 0.75F, 0.25F, var9, 4.0F);
               } else {
                  var33.addRectFilled(var34, var35, var34 + 32.0F, var35 + 32.0F, var17, 4.0F);
                  var33.addRect(var34 + 0.5F, var35 + 0.5F, var34 + 32.0F - 0.5F, var35 + 32.0F - 0.5F, var18, 4.0F, 0, 1.0F);
               }

               float var37 = var34 + 32.0F + 12.0F;
               float var38 = var31 - 10.0F;
               float var39 = Math.max(0.0F, var38 - var37);
               ImFont var40 = ImGuiFonts.POPPINS;
               String var41 = var4 ? "No target" : var3.getName().getString();
               if (var40 != null) {
                  ImGui.pushFont(var40, 13.0F);
               }

               try {
                  String var42 = ellipsize(var41, var39);
                  if (var40 != null) {
                     var33.addText(var40, 13, var37, var30 + 10.0F - 1.0F, var9, var42);
                  } else {
                     var33.addText(var37, var30 + 10.0F - 1.0F, var9, var42);
                  }
               } finally {
                  if (var40 != null) {
                     ImGui.popFont();
                  }
               }

               float var79 = 0.0F;
               if (!var4 && var1.player != null) {
                  try {
                     var79 = var1.player.distanceTo(var3);
                  } catch (Throwable var75) {
                  }
               }

               String var43 = var4 ? "--" : String.format("%.1fm", var79);
               if (var40 != null) {
                  ImGui.pushFont(var40, 10.0F);
               }

               try {
                  if (var40 != null) {
                     var33.addText(var40, 10, var37, var30 + 10.0F + 14.0F, var10, var43);
                  } else {
                     var33.addText(var37, var30 + 10.0F + 14.0F, var10, var43);
                  }
               } finally {
                  if (var40 != null) {
                     ImGui.popFont();
                  }
               }

               float var44 = var4 ? 0.0F : Math.max(0.0F, TargetHUDModule.getFloat());
               float var45 = var4 ? 20.0F : Math.max(1.0F, var3.getMaxHealth());
               float var46 = var45 > 0.0F ? MathHelper.clamp(var44 / var45, 0.0F, 1.0F) : 0.0F;
               float var47 = var37;
               float var49 = var30 + 10.0F + 30.0F;
               float var50 = var49 + 6.0F;
               var33.addRectFilled(var37, var49, var38, var50, var11, 3.0F);
               if (var46 > 0.0F) {
                  float var51 = var37 + Math.max(6.0F, (var38 - var37) * var46);
                  if (var51 > var38) {
                     var51 = var38;
                  }

                  var33.addRectFilled(var37, var49, var51, var50, var12, 3.0F);
               }

               if (!var4) {
                  String var80 = String.format("%.1f / %.0f", var44, var45);
                  if (var40 != null) {
                     ImGui.pushFont(var40, 10.0F);
                  }

                  try {
                     if (var40 != null) {
                        var33.addText(var40, 10, var47, var50 + 3.0F, var13, var80);
                     } else {
                        var33.addText(var47, var50 + 3.0F, var13, var80);
                     }
                  } finally {
                     if (var40 != null) {
                        ImGui.popFont();
                     }
                  }
               }

               if (var27 && var3 != null) {
                  float var81 = 12.0F;
                  float var52 = 4.0F;
                  float var53 = var50 + 18.0F;
                  EquipmentSlot[] var54 = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

                  for (int var55 = 0; var55 < 4; var55++) {
                     float var56 = var37 + var55 * 16.0F;
                     ItemStack var57 = null;

                     try {
                        var57 = var3.getEquippedStack(var54[var55]);
                     } catch (Throwable var74) {
                     }

                     boolean var58 = var57 != null && !var57.isEmpty();
                     var33.addRectFilled(var56, var53, var56 + 12.0F, var53 + 12.0F, var58 ? var14 : var15, 2.0F);
                     if (var58) {
                        var33.addRect(var56 + 0.5F, var53 + 0.5F, var56 + 12.0F - 0.5F, var53 + 12.0F - 0.5F, var16, 2.0F, 0, 1.0F);
                     }
                  }
               }
            }
         }
      }
   }

   private static int resolveHeadTexture(LivingEntity var0) {
      SkinTextures var1 = null;
      if (var0 instanceof AbstractClientPlayerEntity var2) {
         try {
            var1 = var2.getSkin();
         } catch (Throwable var8) {
         }
      }

      if (var1 == null && var0 instanceof PlayerEntity var9) {
         MinecraftClient var3 = MinecraftClient.getInstance();
         ClientPlayNetworkHandler var4 = var3 != null ? var3.getNetworkHandler() : null;
         if (var4 != null) {
            PlayerListEntry var5 = var4.getPlayerListEntry(var9.getUuid());
            if (var5 != null) {
               try {
                  var1 = var5.getSkinTextures();
               } catch (Throwable var7) {
               }
            }
         }
      }

      if (var1 != null && var1.body() != null) {
         Identifier var10 = var1.body().texturePath();
         return ImGuiSkinCache.glHandleForSkin(var10);
      } else {
         return -1;
      }
   }

   private static void drawInfoPanel(HUDModule var0, MinecraftClient var1) {
      boolean var2 = var0.isEnabled7();
      boolean var3 = var0.isEnabled8();
      boolean var4 = var0.isEnabled9();
      if (var2 || var3 || var4) {
         String[] var5 = new String[3];
         String[] var6 = new String[3];
         int var7 = 0;
         if (var2) {
            var5[var7] = "FPS";
            var6[var7] = Integer.toString(var1.getCurrentFps());
            var7++;
         }

         if (var3 && var1.player != null) {
            var5[var7] = "XYZ";
            var6[var7] = String.format("%.1f  %.1f  %.1f", var1.player.getX(), var1.player.getY(), var1.player.getZ());
            var7++;
         }

         if (var4) {
            var5[var7] = "BPS";
            var6[var7] = String.format("%.2f", var0.getDouble2());
            var7++;
         }

         if (var7 != 0) {
            float var11 = ImGui.getTextLineHeightWithSpacing();
            float var12 = 0.0F;
            float var13 = 0.0F;

            for (int var14 = 0; var14 < var7; var14++) {
               ImVec2 var15 = ImGui.calcTextSize(var5[var14]);
               ImVec2 var16 = ImGui.calcTextSize(var6[var14]);
               if (var15.x > var12) {
                  var12 = var15.x;
               }

               if (var16.x > var13) {
                  var13 = var16.x;
               }
            }

            float var32 = var12 + 6.0F + var13;
            float var33 = var11 * var7;
            int var34 = var1.getWindow().getScaledHeight();
            float var17 = var0.getInt6();
            float var18 = var34 - var0.getInt7();
            ImGui.setNextWindowPos(var17, var18, 1, 0.0F, 1.0F);
            int var19 = argb2imu32(-1072425962, 1.0F);
            int var20 = argb2imu32(-5857126, 1.0F);
            int var21 = argb2imu32(-1, 1.0F);
            ImGui.pushStyleVar(2, 5.0F, 3.0F);
            ImGui.pushStyleVar(14, 0.0F, 0.0F);
            ImGui.pushStyleVar(3, 4.0F);

            try {
               if (ImGui.begin("##nyx_infopanel", 209391)) {
                  ImVec2 var23 = ImGui.getWindowPos();
                  ImVec2 var24 = ImGui.getWindowSize();
                  ImDrawList var25 = ImGui.getWindowDrawList();
                  var25.addRectFilled(var23.x, var23.y, var23.x + var24.x, var23.y + var24.y, var19, 4.0F);
                  float var26 = var23.x + 5.0F + var12 + 6.0F;

                  for (int var27 = 0; var27 < var7; var27++) {
                     float var28 = var23.y + 3.0F + var27 * var11;
                     var25.addText(var23.x + 5.0F, var28, var20, var5[var27]);
                     var25.addText(var26, var28, var21, var6[var27]);
                  }

                  ImGui.dummy(var32, var33);
               }

               ImGui.end();
            } finally {
               ImGui.popStyleVar(3);
            }
         }
      }
   }

   private static void textColored(int imU32, String s) {
      ImVec2 var2 = ImGui.calcTextSize(s);
      ImVec2 var3 = ImGui.getCursorScreenPos();
      ImGui.getWindowDrawList().addText(var3.x, var3.y, imU32, s);
      ImGui.dummy(var2.x, var2.y);
   }

   private static int argb2imu32(int argb, float alphaMul) {
      float var2 = clamp01(alphaMul);
      int var3 = argb >>> 24 & 0xFF;
      int var4 = argb >>> 16 & 0xFF;
      int var5 = argb >>> 8 & 0xFF;
      int var6 = argb & 0xFF;
      var3 = Math.max(0, Math.min(255, Math.round(var3 * var2)));
      return var3 << 24 | var6 << 16 | var5 << 8 | var4;
   }

   private static String stripFormatCodes(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         if (var0.indexOf(167) < 0) {
            return var0;
         } else {
            StringBuilder var1 = new StringBuilder(var0.length());
            int var2 = 0;

            while (var2 < var0.length()) {
               char var3 = var0.charAt(var2);
               if (var3 == 167 && var2 + 1 < var0.length()) {
                  var2 += 2;
               } else {
                  var1.append(var3);
                  var2++;
               }
            }

            return var1.toString();
         }
      } else {
         return var0;
      }
   }

   private static int lerpArgb(int a, int b, float t) {
      float var3 = clamp01(t);
      int var4 = a >>> 24 & 0xFF;
      int var5 = a >>> 16 & 0xFF;
      int var6 = a >>> 8 & 0xFF;
      int var7 = a & 0xFF;
      int var8 = b >>> 24 & 0xFF;
      int var9 = b >>> 16 & 0xFF;
      int var10 = b >>> 8 & 0xFF;
      int var11 = b & 0xFF;
      int var12 = var4 + Math.round((var8 - var4) * var3);
      int var13 = var5 + Math.round((var9 - var5) * var3);
      int var14 = var6 + Math.round((var10 - var6) * var3);
      int var15 = var7 + Math.round((var11 - var7) * var3);
      return var12 << 24 | var13 << 16 | var14 << 8 | var15;
   }

   private static float calcRowWidth(String name, String tag) {
      float var2 = ImGui.calcTextSize(name).x;
      if (tag != null && !tag.isEmpty()) {
         var2 += ImGui.calcTextSize(" · ").x + ImGui.calcTextSize(tag).x;
      }

      return var2;
   }

   private static int chromaArgb(long cycleMs, float extraDeg) {
      long var3 = System.currentTimeMillis() % Math.max(1L, cycleMs);
      float var5 = (float)var3 / (float)cycleMs + extraDeg / 360.0F;
      int var6 = Color.HSBtoRGB(var5, 0.85F, 1.0F);
      return 0xFF000000 | var6 & 16777215;
   }

   private static float clamp01(float v) {
      return v < 0.0F ? 0.0F : (v > 1.0F ? 1.0F : v);
   }

   private static int withFade(int argb, float fade) {
      float var2 = clamp01(fade);
      int var3 = (int)((argb >>> 24 & 0xFF) * var2) & 0xFF;
      int var4 = argb >>> 16 & 0xFF;
      int var5 = argb >>> 8 & 0xFF;
      int var6 = argb & 0xFF;
      return var3 << 24 | var6 << 16 | var5 << 8 | var4;
   }
}

