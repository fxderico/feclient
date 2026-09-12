package dev.fede.nyx.imgui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.fede.nyx.NyxClient;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BindSetting;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.DoubleListSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public final class ImGuiClickGui {
   public static boolean visible = false;
   private static final int PANEL_BG = -15002606;
   private static final int PANEL_BORDER = 318767103;
   private static final int PANEL_INSET_HI = 234881023;
   private static final int HEADER_GRAD_TOP = 150994943;
   private static final int HEADER_GRAD_BOT = 16777215;
   private static final int HEADER_SEP = 234881023;
   private static final int HEADER_NAME = -593167;
   private static final int HEADER_COUNT = -9215140;
   private static final int MODULE_OFF = -6714234;
   private static final int CHEVRON = -8359317;
   private static final int SETTING_LABEL = -5398374;
   private static final int SUB_LABEL_ON = -2041386;
   private static final int MODE_CHIP_TEXT = -2041386;
   private static final int ACCENT = -29588;
   private static final int ACCENT_SOFT_14 = 620727404;
   private static final int ACCENT_SOFT_20 = 872385644;
   private static final int ROW_HOVER_OFF = 234881023;
   private static final int CHIP_BG = 268435455;
   private static final int SUB_TRACK_OFF = 536870911;
   private static final int SLIDER_TRACK = 402653183;
   private static final int SUB_PANEL_BG = 167772159;
   private static final int SHADOW_1 = 989855744;
   private static final int SHADOW_2 = 570425344;
   private static final int SHADOW_3 = 251658240;
   private static final int WHITE = -1;
   private static final int BLACK = -16777216;
   private static final float PANEL_W = 200.0F;
   private static final float HEADER_H = 46.0F;
   private static final float MODULE_H = 34.0F;
   private static final float SETTING_H = 22.0F;
   private static final float PANEL_GAP = 14.0F;
   private static final float HEADER_PAD_X = 15.0F;
   private static final float BODY_PAD = 8.0F;
   private static final float ROW_PAD_X = 11.0F;
   private static final float ROW_GAP = 2.0F;
   private static final float SUB_MARGIN_TOP = 3.0F;
   private static final float SUB_MARGIN_X = 4.0F;
   private static final float SUB_MARGIN_BOTTOM = 6.0F;
   private static final float SUB_PAD_TOP = 10.0F;
   private static final float SUB_PAD_X = 12.0F;
   private static final float SUB_PAD_BOTTOM = 12.0F;
   private static final float SUB_GAP = 14.0F;
   private static final float ROUND = 13.0F;
   private static final float ROUND_ROW = 8.0F;
   private static final float ROUND_SUB = 10.0F;
   private static final float ROUND_CHIP = 7.0F;
   private static final float ROUND_TRACK = 3.0F;
   private static final float PANEL_MAX_BODY_H = 560.0F;
   private static final float WHEEL_STEP_PX = 24.0F;
   private static final float FONT_HEADER = 16.0F;
   private static final float FONT_COUNT = 12.0F;
   private static final float FONT_MODULE = 15.0F;
   private static final float FONT_SETTING = 13.0F;
   private static final float FONT_MODE = 12.5F;
   private static final Map<Category, String> CAT_ICON = new EnumMap<>(Category.class);
   private static final String FA_CHEV_RIGHT = "\uf054";
   private static final String FA_CHEV_DOWN = "\uf078";
   private static final String FA_CHEV_UP = "\uf077";
   private static final String FA_FALLBACK = "\uf013";
   private static final Map<Category, ImGuiClickGui.PanelState> panels = new EnumMap<>(Category.class);
   private static Category dragging = null;
   private static float dragDX = 0.0F;
   private static float dragDY = 0.0F;
   private static BindSetting pendingBindCapture = null;
   private static Module pendingModuleBind = null;
   private static boolean rshiftPrev = false;
   private static final boolean[] glfwPrev = new boolean[349];
   private static String searchQuery = "";
   private static Object activeStringSetting = null;
   private static Object activeColorSetting = null;
   private static final Set<DoubleListSetting> dlsExpanded = Collections.newSetFromMap(new IdentityHashMap<>());
   private static final Set<String> loggedFailures = new HashSet<>();
   private static final float H_SLIDER = 46.0F;
   private static final float H_MODE_CHIP = 26.0F;
   private static final float H_SUB_TOGGLE = 24.0F;
   private static final float H_COLOR_OPEN = 138.0F;
   private static final float H_DLS_HEAD = 26.0F;
   private static final float H_DLS_ITEM = 30.0F;
   private static final int CHEV_DOWN = 0;
   private static final int CHEV_UP = 1;
   private static final int CHEV_RIGHT = 2;
   private static final int CHEV_LEFT = 3;
   private static final float SEARCH_H = 42.0F;
   private static final float SEARCH_W_MIN = 320.0F;
   private static final float SEARCH_W_MAX = 460.0F;
   private static final float SEARCH_TOP_Y = 16.0F;
   private static final float SEARCH_ROUND = 12.0F;
   private static final int SEARCH_BG = -301068265;
   private static final int SEARCH_INNER_HI = 184549375;
   private static final int SEARCH_BORDER = 452984831;
   private static final int SEARCH_BORDER_HI = 1617726719;
   private static final int SEARCH_ACCENT = -9663233;
   private static final int SEARCH_ACCENT_GL = 812420351;
   private static final int SEARCH_TEXT = -1512722;
   private static final int SEARCH_HINT = -11840671;
   private static final int SEARCH_MUTED = -9735552;
   private static final int SEARCH_ICON_ARGB = -6511697;
   private static final float SEARCH_ICON_PX = 14.0F;
   private static final float SEARCH_TEXT_PX = 14.0F;
   private static final float SEARCH_HINT_PX = 11.0F;
   private static final String FA_MAG_GLASS = "";

   public static boolean isCapturingBind() {
      return pendingBindCapture != null || pendingModuleBind != null;
   }

   private static boolean hoverRect(float x1, float y1, float x2, float y2) {
      return ImGui.isMouseHoveringRect(x1, y1, x2, y2, false);
   }

   private static int intOf(int argb) {
      int var1 = argb >>> 24 & 0xFF;
      int var2 = argb >>> 16 & 0xFF;
      int var3 = argb >>> 8 & 0xFF;
      int var4 = argb & 0xFF;
      return var1 << 24 | var4 << 16 | var3 << 8 | var2;
   }

   private ImGuiClickGui() {
   }

   private static void seedDefaults() {
      float var0 = 40.0F;

      for (Category var5 : Category.values()) {
         panels.put(var5, new ImGuiClickGui.PanelState(var0, 40.0F));
         var0 += 214.0F;
      }
   }

   public static void toggle() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0 != null) {
         if (!visible) {
            if (var0.currentScreen != null) {
               return;
            }

            visible = true;
            pendingBindCapture = null;
            pendingModuleBind = null;
            dragging = null;
            activeStringSetting = null;
            activeColorSetting = null;
            searchQuery = "";
            rshiftPrev = true;

            try {
               load();
            } catch (Throwable var5) {
               logFail("load", var5);
            }

            try {
               var0.setScreen(new InputCaptureScreen());
            } catch (Throwable var4) {
               visible = false;
               System.err.println("[ClickGui] setScreen(InputCaptureScreen) failed: null");
            }
         } else {
            visible = false;
            pendingBindCapture = null;
            pendingModuleBind = null;
            dragging = null;
            activeStringSetting = null;
            activeColorSetting = null;
            searchQuery = "";
            rshiftPrev = true;

            try {
               save();
            } catch (Throwable var3) {
               logFail("save", var3);
            }

            try {
               if (var0.currentScreen instanceof InputCaptureScreen) {
                  var0.setScreen(null);
               }
            } catch (Throwable var2) {
            }
         }
      }
   }

   public static void tick(MinecraftClient var0) {
      if (var0 != null && var0.getWindow() != null) {
         long var1 = var0.getWindow().getHandle();
         if (var1 != 0L) {
            boolean var3;
            try {
               var3 = GLFW.glfwGetKey(var1, 344) == 1;
            } catch (Throwable var5) {
               return;
            }

            if (var3 && !rshiftPrev && !visible && var0.currentScreen == null) {
               toggle();
            }

            rshiftPrev = var3;
         }
      }
   }

   public static void render() {
      if (visible) {
         MinecraftClient var0 = MinecraftClient.getInstance();
         if (var0 != null) {
            if (var0.currentScreen != null && !(var0.currentScreen instanceof InputCaptureScreen)) {
               visible = false;
               pendingBindCapture = null;
               pendingModuleBind = null;
               dragging = null;
            } else {
               try {
                  ImGuiIO var1 = ImGui.getIO();
                  var1.setWantCaptureMouse(true);
                  var1.setWantCaptureKeyboard(true);
               } catch (Throwable var13) {
               }

               ImDrawList var14;
               try {
                  var14 = ImGui.getForegroundDrawList();
               } catch (Throwable var12) {
                  logFail("get-fg-drawlist", var12);
                  return;
               }

               for (Category var5 : Category.values()) {
                  try {
                     drawPanel(var14, var5);
                  } catch (Throwable var11) {
                     logFail("panel:" + var5.name(), var11);
                  }
               }

               try {
                  drawSearchBar(var14);
               } catch (Throwable var10) {
                  logFail("search-bar", var10);
               }

               try {
                  handleDrag();
               } catch (Throwable var9) {
                  logFail("drag", var9);
               }

               if (pendingBindCapture != null || pendingModuleBind != null) {
                  try {
                     captureBindKey(var0);
                  } catch (Throwable var8) {
                     logFail("bind-capture", var8);
                  }
               } else if (activeStringSetting == null) {
                  try {
                     captureSearchChars(var0);
                  } catch (Throwable var7) {
                     logFail("search-capture", var7);
                  }
               }
            }
         }
      }
   }

   private static void drawPanel(ImDrawList var0, Category var1) {
      ImGuiClickGui.PanelState var2 = panels.get(var1);
      if (var2 != null) {
         ArrayList var3 = new ArrayList();
         int var4 = 0;
         String var5 = searchQuery == null ? "" : searchQuery.trim().toLowerCase(Locale.ROOT);

         for (Module var7 : NyxClient.MODULES.listOf(var1)) {
            if (var7 != null && !var7.isEnabled()) {
               if (!var5.isEmpty()) {
                  String var8 = var7.getString();
                  if (var8 == null || !var8.toLowerCase(Locale.ROOT).contains(var5)) {
                     continue;
                  }
               }

               var3.add(var7);
               if (var7.isEnabled3()) {
                  var4++;
               }
            }
         }

         if (var5.isEmpty() || !var3.isEmpty()) {
            float var62 = var2.floatVal;
            float var63 = var2.floatVal2;
            float var64 = 200.0F;
            float var9 = var2.collapsed ? 0.0F : measureBody(var3, var2);
            float var10 = var2.collapsed ? 0.0F : Math.min(var9, 560.0F);
            float var11 = 46.0F + var10;
            paintDropShadow(var0, var62, var63, var64, var11);
            var0.addRectFilled(var62, var63, var62 + var64, var63 + var11, -15002606, 13.0F);
            var0.addRect(var62, var63, var62 + var64, var63 + var11, 318767103, 13.0F, 0, 1.0F);
            var0.addLine(var62 + 7.8F, var63 + 1.5F, var62 + var64 - 7.8F, var63 + 1.5F, 234881023, 1.0F);
            var0.addRectFilledMultiColor(var62, var63, var62 + var64, var63 + 46.0F, 150994943, 150994943, 16777215, 16777215);
            if (!var2.collapsed) {
               var0.addLine(var62 + 0.5F, var63 + 46.0F - 0.5F, var62 + var64 - 0.5F, var63 + 46.0F - 0.5F, 234881023, 1.0F);
            }

            String var12 = CAT_ICON.getOrDefault(var1, "\uf013");
            String var13 = var1.getString();
            String var14 = var4 + "/" + var3.size();
            boolean var15 = pushFont(16.0F);

            float var17;
            try {
               var17 = calcTextW(var12);
               float var16 = calcTextW(var13);
            } finally {
               popFont(var15);
            }

            float var18 = var63 + 15.0F - 1.0F;
            var15 = pushFont(16.0F);

            try {
               var0.addText(var62 + 15.0F, var18, -29588, var12);
               var0.addText(var62 + 15.0F + var17 + 8.0F, var18, -593167, var13);
            } finally {
               popFont(var15);
            }

            boolean var19 = pushFont(12.0F);

            try {
               float var20 = calcTextW(var14);
               float var21 = var63 + 17.0F;
               var0.addText(var62 + var64 - 15.0F - var20, var21, -9215140, var14);
            } finally {
               popFont(var19);
            }

            float var66 = var62 + var64 - 15.0F - 22.0F;
            boolean var67 = ImGui.isMouseClicked(0);
            if (var67 && dragging == null && hoverRect(var62, var63, var66, var63 + 46.0F)) {
               dragging = var1;
               dragDX = ImGui.getMousePosX() - var2.floatVal;
               dragDY = ImGui.getMousePosY() - var2.floatVal2;
            }

            if (var67 && hoverRect(var66, var63, var62 + var64, var63 + 46.0F)) {
               var2.collapsed = !var2.collapsed;
            }

            if (!var2.collapsed) {
               float var23 = var63 + 46.0F;
               float var24 = var62 + var64;
               float var25 = var63 + 46.0F + var10;
               float var26 = Math.max(0.0F, var9 - var10);
               if (hoverRect(var62, var23, var24, var25)) {
                  float var27;
                  try {
                     var27 = ImGui.getIO().getMouseWheel();
                  } catch (Throwable var57) {
                     var27 = 0.0F;
                  }

                  if (var27 != 0.0F) {
                     var2.scroll -= var27 * 24.0F;
                  }
               }

               if (var2.scroll < 0.0F) {
                  var2.scroll = 0.0F;
               }

               if (var2.scroll > var26) {
                  var2.scroll = var26;
               }

               var0.pushClipRect(var62, var23, var24, var25, true);

               try {
                  float var68 = var23 + 8.0F - var2.scroll;

                  for (Module var29 : (java.util.List<Module>)var3) {
                     try {
                        var68 = drawModuleRow(var0, var29, var2, var62, var68, var64);
                        var68 += 2.0F;
                     } catch (Throwable var56) {
                        logFail("row:" + safeName(var29), var56);
                        var68 += 36.0F;
                     }
                  }
               } finally {
                  var0.popClipRect();
               }
            }
         }
      }
   }

   private static float measureBody(List<Module> mods, ImGuiClickGui.PanelState ps) {
      float var2 = 8.0F;

      for (int var3 = 0; var3 < mods.size(); var3++) {
         Module var4 = mods.get(var3);
         if (var3 > 0) {
            var2 += 2.0F;
         }

         var2 += 34.0F;
         if (ps.expanded.contains(var4)) {
            float var5 = measureSubPanelContent(var4);
            var2 += 13.0F + var5 + 12.0F + 6.0F;
         }
      }

      return var2 + 8.0F;
   }

   private static float measureSubPanelContent(Module m) {
      float var1 = 0.0F;
      boolean var2 = true;
      var1 += 26.0F;
      var2 = false;

      for (Setting var4 : m.getList()) {
         if (var4 != null) {
            boolean var5;
            try {
               var5 = var4.isVisible();
            } catch (Throwable var7) {
               var5 = true;
            }

            if (var5) {
               if (!var2) {
                  var1 += 14.0F;
               }

               var1 += settingHeight(var4);
               var2 = false;
            }
         }
      }

      return var1;
   }

   private static float settingHeight(Setting s) {
      if (s instanceof BooleanSetting) {
         return 24.0F;
      } else if (s instanceof NumberSetting) {
         return 46.0F;
      } else if (s instanceof ModeSetting) {
         return 26.0F;
      } else if (s instanceof BindSetting) {
         return 26.0F;
      } else if (s instanceof StringSetting) {
         return 26.0F;
      } else if (s instanceof ColorSetting var2) {
         return var2 == activeColorSetting ? 164.0F : 26.0F;
      } else if (s instanceof DoubleListSetting var1) {
         return dlsExpanded.contains(var1) ? 26.0F + var1.size() * 30.0F : 26.0F;
      } else {
         return 26.0F;
      }
   }

   private static float drawModuleRow(ImDrawList var0, Module var1, ImGuiClickGui.PanelState var2, float var3, float var4, float var5) {
      boolean var6 = var2.expanded.contains(var1);
      boolean var7 = var1.isEnabled3();
      float var8 = var3 + 8.0F;
      float var9 = var3 + var5 - 8.0F;
      boolean var10 = hoverRect(var8, var4, var9, var4 + 34.0F);
      int var11;
      if (var7 && var10) {
         var11 = 872385644;
      } else if (var7) {
         var11 = 620727404;
      } else if (var10) {
         var11 = 234881023;
      } else {
         var11 = 0;
      }

      if (var11 != 0) {
         var0.addRectFilled(var8, var4, var9, var4 + 34.0F, var11, 8.0F);
      }

      boolean var12 = pushFont(15.0F);

      try {
         int var13 = var7 ? -29588 : -6714234;
         float var14 = var4 + 9.5F - 1.0F;
         String var15 = var1.getString();
         float var16 = var9 - 11.0F - (var8 + 11.0F) - 16.0F;
         String var17 = fitText(var15, var16);
         var0.addText(var8 + 11.0F, var14, var13, var17);
      } finally {
         popFont(var12);
      }

      boolean var44 = !var1.getList().isEmpty();
      if (var44) {
         float var45 = var9 - 11.0F - 4.0F;
         float var47 = var4 + 17.0F;
         drawChevron(var0, var45, var47, 4.5F, var6 ? 1 : 0, -8359317, 1.6F);
      }

      if (var10 && ImGui.isMouseClicked(0)) {
         if (!var44 || !(ImGui.getMousePosX() > var9 - 11.0F - 14.0F)) {
            var1.run19();
         } else if (var6) {
            var2.expanded.remove(var1);
         } else {
            var2.expanded.add(var1);
         }
      }

      if (var10 && ImGui.isMouseClicked(1) && var44) {
         if (var6) {
            var2.expanded.remove(var1);
         } else {
            var2.expanded.add(var1);
         }
      }

      float var46 = var4 + 34.0F;
      if (var6) {
         float var48 = var3 + 8.0F + 4.0F;
         float var49 = var3 + var5 - 8.0F - 4.0F;
         float var50 = var46 + 3.0F;
         float var18 = var50 + 10.0F;
         float var19 = measureSubPanelContent(var1);
         float var20 = var50 + 10.0F + var19 + 12.0F;
         var0.addRectFilled(var48, var50, var49, var20, 167772159, 10.0F);
         float var22 = var48 + 12.0F;
         float var23 = var49 - 12.0F - var22;
         boolean var24 = true;
         float var21 = drawModuleBindVape(var0, var1, var22, var18, var23);
         var24 = false;

         for (Setting var26 : var1.getList()) {
            if (var26 != null) {
               boolean var27;
               try {
                  var27 = var26.isVisible();
               } catch (Throwable var41) {
                  var27 = true;
               }

               if (var27) {
                  if (!var24) {
                     var21 += 14.0F;
                  }

                  try {
                     var21 = drawSettingVape(var0, var1, var26, var22, var21, var23);
                  } catch (Throwable var43) {
                     String var29 = "widget:" + var26.getClass().getSimpleName();
                     if (loggedFailures.add(var29)) {
                        System.err.println("[ClickGui] draw null failed: null");
                     }

                     boolean var30 = pushFont(13.0F);

                     try {
                        var0.addText(var22, var21, -6714234, var26.getName() + " [err]");
                     } finally {
                        popFont(var30);
                     }

                     var21 += 17.0F;
                  }

                  var24 = false;
               }
            }
         }

         var46 = var20 + 6.0F;
      }

      return var46;
   }

   private static float drawModuleBindVape(ImDrawList var0, Module var1, float var2, float var3, float var4) {
      boolean var5 = pendingModuleBind == var1;
      String var7 = var5 ? "..." : keyName(var1.getInt());
      boolean var8 = pushFont(13.0F);

      try {
         var0.addText(var2, var3 + 6.5F - 1.0F, -5398374, "Keybind");
      } finally {
         popFont(var8);
      }

      boolean var9 = pushFont(12.5F);

      try {
         float var10 = calcTextW(var7);
         float var13 = var10 + 9.0F * 2.0F;
         float var14 = 12.5F + 4.0F * 2.0F;
         float var15 = var2 + var4 - var13;
         float var16 = var3 + (26.0F - var14) * 0.5F;
         var0.addRectFilled(var15, var16, var15 + var13, var16 + var14, var5 ? 872385644 : 268435455, 7.0F);
         var0.addText(var15 + 9.0F, var16 + 4.0F - 1.0F, var5 ? -29588 : -2041386, var7);
         if (hoverRect(var15, var16, var15 + var13, var16 + var14) && ImGui.isMouseClicked(0)) {
            if (var5) {
               pendingModuleBind = null;
            } else {
               pendingModuleBind = var1;
               pendingBindCapture = null;
               Arrays.fill(glfwPrev, false);
            }
         }
      } finally {
         popFont(var9);
      }

      return var3 + 26.0F;
   }

   private static float drawSettingVape(ImDrawList dl, Module owner, Setting s, float x, float y, float w) {
      if (s instanceof BooleanSetting var16) {
         return drawBoolVape(dl, var16, x, y, w);
      } else if (s instanceof NumberSetting var15) {
         return drawNumberVape(dl, var15, x, y, w);
      } else if (s instanceof ModeSetting var14) {
         return drawModeVape(dl, var14, x, y, w);
      } else if (s instanceof ColorSetting var13) {
         return drawColorVape(dl, var13, x, y, w);
      } else if (s instanceof BindSetting var12) {
         return drawBindVape(dl, owner, var12, x, y, w);
      } else if (s instanceof StringSetting var11) {
         return drawStringVape(dl, var11, x, y, w);
      } else if (s instanceof DoubleListSetting var10) {
         return drawDoubleListVape(dl, var10, x, y, w);
      } else {
         boolean var6 = pushFont(13.0F);

         try {
            dl.addText(x, y, -6714234, s.getName() + " [" + s.getClass().getSimpleName() + "]");
         } finally {
            popFont(var6);
         }

         return y + 26.0F;
      }
   }

   private static float drawBoolVape(ImDrawList var0, BooleanSetting var1, float var2, float var3, float var4) {
      boolean var5 = var1.getValue();
      int var6 = var5 ? -2041386 : -6714234;
      boolean var7 = pushFont(13.0F);

      try {
         var0.addText(var2, var3 + 5.5F - 1.0F, var6, var1.getName());
      } finally {
         popFont(var7);
      }

      float var8 = 30.0F;
      float var10 = var2 + var4 - var8;
      float var11 = var3 + (24.0F - 16.0F) * 0.5F;
      var0.addRectFilled(var10, var11, var10 + var8, var11 + 16.0F, var5 ? -29588 : 536870911, 9.0F);
      float var13 = var11 + 2.5F;
      float var14 = var5 ? var10 + 16.0F : var10 + 2.5F;
      var0.addCircleFilled(var14 + 11.0F * 0.5F, var13 + 11.0F * 0.5F, 11.0F * 0.5F, -1);
      if (hoverRect(var10, var11, var10 + var8, var11 + 16.0F) && ImGui.isMouseClicked(0)) {
         var1.setValue(!var5);
      }

      return var3 + 24.0F;
   }

   private static float drawNumberVape(ImDrawList dl, NumberSetting n, float x, float y, float w) {
      boolean var5 = pushFont(13.0F);

      try {
         dl.addText(x, y, -5398374, n.getName());
         String var6 = formatNumber(n);
         float var7 = calcTextW(var6);
         dl.addText(x + w - var7, y, -29588, var6);
      } finally {
         popFont(var5);
      }

      float var19 = y + 13.0F + 8.0F;
      float var20 = var19 + 5.0F;
      float var8 = 4.0F;
      dl.addRectFilled(x, var20, x + w, var20 + var8, 402653183, 3.0F);
      double var9 = Math.max(1.0E-9, n.getMax() - n.getMin());
      float var11 = (float)Math.max(0.0, Math.min(1.0, (n.getValue() - n.getMin()) / var9));
      float var12 = w * var11;
      if (var12 > 0.5F) {
         dl.addRectFilled(x, var20, x + var12, var20 + var8, -29588, 3.0F);
      }

      float var13 = x + var12;
      float var14 = var19 + 7.0F;
      dl.addCircleFilled(var13, var14 + 1.0F, 6.0F, 989855744);
      dl.addCircleFilled(var13, var14, 6.0F, -1);
      if (hoverRect(x, var19, x + w, var19 + 14.0F) && ImGui.isMouseDown(0)) {
         double var15 = (ImGui.getMousePosX() - x) / w;
         if (var15 < 0.0) {
            var15 = 0.0;
         }

         if (var15 > 1.0) {
            var15 = 1.0;
         }

         n.setValue(n.getMin() + var15 * (n.getMax() - n.getMin()));
      }

      return y + 46.0F;
   }

   private static float drawModeVape(ImDrawList var0, ModeSetting var1, float var2, float var3, float var4) {
      boolean var5 = pushFont(13.0F);

      try {
         var0.addText(var2, var3 + 6.5F - 1.0F, -5398374, var1.getName());
      } finally {
         popFont(var5);
      }

      String var6 = var1.getMode() == null ? "" : var1.getMode();
      boolean var7 = pushFont(12.5F);

      try {
         float var8 = calcTextW(var6);
         float var11 = var8 + 9.0F * 2.0F;
         float var12 = 12.5F + 4.0F * 2.0F;
         float var13 = var2 + var4 - var11;
         float var14 = var3 + (26.0F - var12) * 0.5F;
         var0.addRectFilled(var13, var14, var13 + var11, var14 + var12, 268435455, 7.0F);
         var0.addText(var13 + 9.0F, var14 + 4.0F - 1.0F, -2041386, var6);
         if (hoverRect(var13, var14, var13 + var11, var14 + var12)) {
            if (ImGui.isMouseClicked(0)) {
               var1.cycle();
            }

            if (ImGui.isMouseClicked(1)) {
               var1.cycleBack();
            }
         }
      } finally {
         popFont(var7);
      }

      return var3 + 26.0F;
   }

   private static float drawBindVape(ImDrawList var0, Module var1, BindSetting var2, float var3, float var4, float var5) {
      boolean var6 = pendingBindCapture == var2;
      boolean var7 = pushFont(13.0F);

      try {
         var0.addText(var3, var4 + 6.5F - 1.0F, -5398374, var2.getName());
      } finally {
         popFont(var7);
      }

      String var8 = var6 ? "..." : var2.keyName();
      boolean var9 = pushFont(12.5F);

      try {
         float var10 = calcTextW(var8);
         float var13 = var10 + 9.0F * 2.0F;
         float var14 = 12.5F + 4.0F * 2.0F;
         float var15 = var3 + var5 - var13;
         float var16 = var4 + (26.0F - var14) * 0.5F;
         var0.addRectFilled(var15, var16, var15 + var13, var16 + var14, var6 ? 872385644 : 268435455, 7.0F);
         var0.addText(var15 + 9.0F, var16 + 4.0F - 1.0F, var6 ? -29588 : -2041386, var8);
         if (hoverRect(var15, var16, var15 + var13, var16 + var14) && ImGui.isMouseClicked(0)) {
            if (var6) {
               pendingBindCapture = null;
            } else {
               pendingBindCapture = var2;
               pendingModuleBind = null;
               Arrays.fill(glfwPrev, false);
            }
         }
      } finally {
         popFont(var9);
      }

      return var4 + 26.0F;
   }

   private static float drawStringVape(ImDrawList var0, StringSetting var1, float var2, float var3, float var4) {
      boolean var5 = activeStringSetting == var1;
      boolean var6 = pushFont(13.0F);

      try {
         var0.addText(var2, var3 + 6.5F - 1.0F, -5398374, var1.getName());
      } finally {
         popFont(var6);
      }

      boolean var7 = pushFont(12.5F);

      try {
         float var8 = 0.0F;
         boolean var9 = pushFont(13.0F);

         try {
            var8 = calcTextW(var1.getName());
         } finally {
            popFont(var9);
         }

         float var10 = 9.0F;
         float var12 = 12.5F + 4.0F * 2.0F;
         float var13 = var2 + var8 + 8.0F;
         float var14 = var2 + var4 - var13;
         float var15 = Math.max(30.0F, var14);
         float var16 = var2 + var4 - var15;
         float var17 = var3 + (26.0F - var12) * 0.5F;
         var0.addRectFilled(var16, var17, var16 + var15, var17 + var12, var5 ? 872385644 : 268435455, 7.0F);
         String var18 = var1.getValue() == null ? "" : var1.getValue();
         String var19 = var18;
         float var20 = var15 - var10 * 2.0F;

         while (var19.length() > 0 && calcTextW(var19) > var20) {
            var19 = var19.substring(1);
         }

         if (var5 && System.currentTimeMillis() / 500L % 2L == 0L) {
            var19 = "null|";
         }

         var0.addText(var16 + var10, var17 + 4.0F - 1.0F, var5 ? -29588 : -2041386, var19);
         if (hoverRect(var16, var17, var16 + var15, var17 + var12) && ImGui.isMouseClicked(0)) {
            activeStringSetting = var1;
         }
      } finally {
         popFont(var7);
      }

      if (var5) {
         captureTypedChars(var1);
      }

      return var3 + 26.0F;
   }

   private static float drawColorVape(ImDrawList var0, ColorSetting var1, float var2, float var3, float var4) {
      boolean var5 = pushFont(13.0F);

      try {
         var0.addText(var2, var3 + 6.5F - 1.0F, -5398374, var1.getName());
      } finally {
         popFont(var5);
      }

      float var6 = 30.0F;
      float var8 = var2 + var4 - var6;
      float var9 = var3 + (26.0F - 14.0F) * 0.5F;
      drawCheckerboard(var0, var8, var9, var6, 14.0F, 4.0F);
      var0.addRectFilled(var8, var9, var8 + var6, var9 + 14.0F, intOf(var1.getValue()), 7.0F);
      var0.addRect(var8, var9, var8 + var6, var9 + 14.0F, 318767103, 7.0F, 0, 1.0F);
      if (hoverRect(var8, var9, var8 + var6, var9 + 14.0F) && ImGui.isMouseClicked(0)) {
         activeColorSetting = activeColorSetting == var1 ? null : var1;
      }

      float var10 = var3 + 26.0F;
      if (activeColorSetting == var1) {
         var10 = drawColorPicker(var0, var1, var2, var10, var4);
      }

      return var10;
   }

   private static float drawDoubleListVape(ImDrawList var0, DoubleListSetting var1, float var2, float var3, float var4) {
      boolean var5 = dlsExpanded.contains(var1);
      boolean var6 = pushFont(13.0F);

      try {
         var0.addText(var2, var3 + 6.5F - 1.0F, -5398374, var1.getName() + " (" + var1.size() + ")");
      } finally {
         popFont(var6);
      }

      float var7 = 14.0F;
      float var9 = var2 + var4 - var7 * 2.0F - 4.0F - 18.0F;
      float var10 = var2 + var4 - var7 - 18.0F;
      float var11 = var2 + var4 - 12.0F;
      float var13 = var3 + (26.0F - var7) * 0.5F;
      var0.addRectFilled(var9, var13, var9 + var7, var13 + var7, 268435455, 7.0F);
      var0.addRectFilled(var10, var13, var10 + var7, var13 + var7, 268435455, 7.0F);
      boolean var14 = pushFont(12.5F);

      try {
         float var15 = calcTextW("-");
         float var16 = calcTextW("+");
         var0.addText(var9 + (var7 - var15) * 0.5F, var13 + 1.0F, var1.isEmpty() ? -6714234 : -2041386, "-");
         var0.addText(var10 + (var7 - var16) * 0.5F, var13 + 1.0F, -2041386, "+");
      } finally {
         popFont(var14);
      }

      drawChevron(var0, var11, var3 + 26.0F * 0.5F, 4.5F, var5 ? 1 : 0, -8359317, 1.6F);
      if (hoverRect(var9, var13, var9 + var7, var13 + var7) && ImGui.isMouseClicked(0) && !var1.isEmpty()) {
         var1.remove(var1.size() - 1);
      }

      if (hoverRect(var10, var13, var10 + var7, var13 + var7) && ImGui.isMouseClicked(0)) {
         var1.add(var1.getMin());
      }

      if (hoverRect(var2, var3, var9 - 2.0F, var3 + 26.0F) && ImGui.isMouseClicked(0)) {
         if (var5) {
            dlsExpanded.remove(var1);
         } else {
            dlsExpanded.add(var1);
         }

         var5 = !var5;
      }

      float var41 = var3 + 26.0F;
      if (var5) {
         List var42 = var1.getValues();
         if (var42 != null) {
            for (int var17 = 0; var17 < var42.size(); var17++) {
               double var18 = (Double)var42.get(var17);
               boolean var20 = pushFont(13.0F);

               try {
                  var0.addText(var2, var41 + 3.0F, -5398374, "#" + var17);
                  String var21 = String.format(Locale.ROOT, "%.2f", var18);
                  float var22 = calcTextW(var21);
                  var0.addText(var2 + var4 - var22, var41 + 3.0F, -29588, var21);
               } finally {
                  popFont(var20);
               }

               float var43 = var2 + 24.0F;
               float var44 = var41 + 13.0F + 4.0F;
               float var23 = var4 - 24.0F;
               if (var23 < 20.0F) {
                  var23 = 20.0F;
               }

               var0.addRectFilled(var43, var44, var43 + var23, var44 + 3.0F, 402653183, 3.0F);
               double var24 = Math.max(1.0E-9, var1.getMax() - var1.getMin());
               float var26 = (float)Math.max(0.0, Math.min(1.0, (var18 - var1.getMin()) / var24));
               var0.addRectFilled(var43, var44, var43 + var23 * var26, var44 + 3.0F, -29588, 3.0F);
               if (hoverRect(var43, var44 - 4.0F, var43 + var23, var44 + 7.0F) && ImGui.isMouseDown(0)) {
                  double var27 = (ImGui.getMousePosX() - var43) / var23;
                  if (var27 < 0.0) {
                     var27 = 0.0;
                  }

                  if (var27 > 1.0) {
                     var27 = 1.0;
                  }

                  var1.set(var17, var1.getMin() + var27 * (var1.getMax() - var1.getMin()));
               }

               var41 += 30.0F;
            }
         }
      }

      return var41;
   }

   private static float drawColorPicker(ImDrawList var0, ColorSetting var1, float var2, float var3, float var4) {
      float var7 = Math.max(40.0F, var4 - 8.0F - 4.0F);
      float var10 = var3 + 2.0F;
      float var11 = var2 + var7 + 4.0F;
      float var12 = var10 + 88.0F + 4.0F;
      int var14 = var1.getValue();
      int var15 = var14 >>> 24 & 0xFF;
      int var16 = var14 >>> 16 & 0xFF;
      int var17 = var14 >>> 8 & 0xFF;
      int var18 = var14 & 0xFF;
      float[] var19 = Color.RGBtoHSB(var16, var17, var18, null);
      float var20 = var19[0];
      float var21 = var19[1];
      float var22 = var19[2];
      int var23 = intOf(0xFF000000 | Color.HSBtoRGB(var20, 1.0F, 1.0F) & 16777215);
      var0.addRectFilledMultiColor(var2, var10, var2 + var7, var10 + 88.0F, -1, var23, var23, -1);
      var0.addRectFilledMultiColor(var2, var10, var2 + var7, var10 + 88.0F, 0, 0, -16777216, -16777216);
      float var24 = var2 + var21 * var7;
      float var25 = var10 + (1.0F - var22) * 88.0F;
      var0.addCircle(var24, var25, 4.0F, -1, 12, 1.5F);
      int[] var26 = new int[]{-16776961, -16711681, -16711936, -256, -65536, -65281, -16776961};
      float var27 = 88.0F / 6.0F;

      for (int var28 = 0; var28 < 6; var28++) {
         var0.addRectFilledMultiColor(
            var11, var10 + var28 * var27, var11 + 8.0F, var10 + (var28 + 1) * var27, var26[var28], var26[var28], var26[var28 + 1], var26[var28 + 1]
         );
      }

      float var38 = var10 + var20 * 88.0F;
      var0.addLine(var11 - 2.0F, var38, var11 + 8.0F + 2.0F, var38, -1, 1.5F);
      drawCheckerboard(var0, var2, var12, var7, 6.0F, 4.0F);
      int var29 = intOf(0xFF000000 | var14 & 16777215);
      int var30 = intOf(0 | var14 & 16777215);
      var0.addRectFilledMultiColor(var2, var12, var2 + var7, var12 + 6.0F, var30, var29, var29, var30);
      float var31 = var2 + var15 / 255.0F * var7;
      var0.addLine(var31, var12 - 2.0F, var31, var12 + 6.0F + 2.0F, -1, 1.5F);
      boolean var32 = ImGui.isMouseDown(0);
      float var33 = ImGui.getMousePosX();
      float var34 = ImGui.getMousePosY();
      if (var32 && hoverRect(var2, var10, var2 + var7, var10 + 88.0F)) {
         float var35 = clamp01((var33 - var2) / var7);
         float var36 = 1.0F - clamp01((var34 - var10) / 88.0F);
         int var37 = Color.HSBtoRGB(var20, var35, var36) & 16777215;
         var1.setValue(var15 << 24 | var37);
      }

      if (var32 && hoverRect(var11, var10, var11 + 8.0F, var10 + 88.0F)) {
         float var39 = clamp01((var34 - var10) / 88.0F);
         int var41 = Color.HSBtoRGB(var39, Math.max(0.001F, var21), Math.max(0.001F, var22)) & 16777215;
         var1.setValue(var15 << 24 | var41);
      }

      if (var32 && hoverRect(var2, var12, var2 + var7, var12 + 6.0F)) {
         int var40 = (int)(clamp01((var33 - var2) / var7) * 255.0F);
         var1.setValue(var40 << 24 | var14 & 16777215);
      }

      return var3 + 88.0F + 32.0F;
   }

   private static void drawChevron(ImDrawList dl, float cx, float cy, float size, int direction, int color, float thickness) {
      float var7 = size * 0.5F;
      switch (direction) {
         case 0:
            dl.addLine(cx - size, cy - var7, cx, cy + var7, color, thickness);
            dl.addLine(cx, cy + var7, cx + size, cy - var7, color, thickness);
            break;
         case 1:
            dl.addLine(cx - size, cy + var7, cx, cy - var7, color, thickness);
            dl.addLine(cx, cy - var7, cx + size, cy + var7, color, thickness);
            break;
         case 2:
            dl.addLine(cx - var7, cy - size, cx + var7, cy, color, thickness);
            dl.addLine(cx + var7, cy, cx - var7, cy + size, color, thickness);
            break;
         case 3:
            dl.addLine(cx + var7, cy - size, cx - var7, cy, color, thickness);
            dl.addLine(cx - var7, cy, cx + var7, cy + size, color, thickness);
      }
   }

   private static void paintDropShadow(ImDrawList dl, float x, float y, float w, float h) {
      dl.addRectFilled(x - 6.0F, y + 8.0F, x + w + 6.0F, y + h + 14.0F, 251658240, 17.0F);
      dl.addRectFilled(x - 3.0F, y + 6.0F, x + w + 3.0F, y + h + 10.0F, 570425344, 15.0F);
      dl.addRectFilled(x - 1.0F, y + 4.0F, x + w + 1.0F, y + h + 6.0F, 989855744, 14.0F);
   }

   private static void drawCheckerboard(ImDrawList var0, float var1, float var2, float var3, float var4, float var5) {
      int var8 = (int)Math.ceil(var3 / var5);
      int var9 = (int)Math.ceil(var4 / var5);

      for (int var10 = 0; var10 < var8; var10++) {
         for (int var11 = 0; var11 < var9; var11++) {
            int var12 = (var10 + var11 & 1) == 0 ? -4144960 : -10461088;
            float var13 = var1 + var10 * var5;
            float var14 = var2 + var11 * var5;
            float var15 = Math.min(var13 + var5, var1 + var3);
            float var16 = Math.min(var14 + var5, var2 + var4);
            var0.addRectFilled(var13, var14, var15, var16, var12);
         }
      }
   }

   private static void handleDrag() {
      if (dragging != null) {
         if (ImGui.isMouseDown(0)) {
            ImGuiClickGui.PanelState var0 = panels.get(dragging);
            if (var0 != null) {
               var0.floatVal = ImGui.getMousePosX() - dragDX;
               var0.floatVal2 = ImGui.getMousePosY() - dragDY;
            }
         } else {
            dragging = null;

            try {
               save();
            } catch (Throwable var1) {
               logFail("save-drag", var1);
            }
         }
      }
   }

   private static void drawSearchBar(ImDrawList var0) {
      if (var0 != null) {
         ImGuiIO var1 = ImGui.getIO();
         float var2 = var1.getDisplaySizeX();
         boolean var3 = searchQuery != null && !searchQuery.isEmpty();
         float var4 = Math.max(320.0F, Math.min(460.0F, var2 * 0.3F));
         float var5 = (var2 - var4) * 0.5F;
         float var7 = var5 + var4;
         float var8 = 16.0F + 42.0F;
         if (var3) {
            int var9 = 822053996;
            var0.addRect(var5 - 3.0F, 16.0F - 3.0F, var7 + 3.0F, var8 + 3.0F, var9, 15.0F, 0, 1.0F);
            var0.addRect(var5 - 2.0F, 16.0F - 2.0F, var7 + 2.0F, var8 + 2.0F, var9, 14.0F, 0, 1.0F);
         }

         var0.addRectFilled(var5 - 2.0F, 16.0F + 4.0F, var7 + 2.0F, var8 + 8.0F, 1342177280, 14.0F);
         var0.addRectFilled(var5, 16.0F, var7, var8, -300478450, 12.0F);
         var0.addRect(var5, 16.0F, var7, var8, argb2imu(var3 ? 1617726719 : 452984831), 12.0F, 0, 1.0F);
         var0.addLine(var5 + 7.2000003F, 16.0F + 1.5F, var7 - 7.2000003F, 16.0F + 1.5F, 184549375, 1.0F);
         boolean var37 = pushFont(14.0F);
         float var10 = var5 + 18.0F;
         float var11 = 16.0F + 14.0F - 1.0F;

         try {
            var0.addText(var10, var11, argb2imu(var3 ? -9663233 : -6511697), "");
         } finally {
            popFont(var37);
         }

         float var12 = 15.0F;
         float var13 = var10 + var12 + 10.0F;
         var0.addLine(var13, 16.0F + 10.0F, var13, var8 - 10.0F, 452984831, 1.0F);
         boolean var14 = pushFont(14.0F);

         try {
            String var15 = searchQuery == null ? "" : searchQuery;
            float var16 = var13 + 12.0F;
            float var17 = 16.0F + 14.0F - 1.0F;
            if (var15.isEmpty()) {
               var0.addText(var16, var17, -8359317, "Cracked By Dexter");
            } else {
               var0.addText(var16, var17, -1119512, var15);
               float var18 = var16 + calcTextW(var15) + 2.0F;
               long var19 = System.currentTimeMillis();
               if (var19 / 500L % 2L == 0L) {
                  var0.addRectFilled(var18, 16.0F + 10.0F, var18 + 2.0F, var8 - 10.0F, -29588, 1.0F);
               }
            }
         } finally {
            popFont(var14);
         }

         boolean var38 = pushFont(11.0F);

         try {
            String var39;
            int var40;
            if (var3) {
               int var41 = countSearchResults();
               var39 = var41 == 1 ? "1 result" : var41 + " results";
               var40 = var41 == 0 ? -9735552 : -11840671;
            } else {
               var39 = "ESC to clear";
               var40 = -11840671;
            }

            float var42 = calcTextW(var39);
            float var43 = 16.0F + 15.5F;
            if (var3) {
               float var20 = var7 - 16.0F - var42 - 8.0F;
               float var21 = var7 - 12.0F;
               float var22 = var43 - 3.0F;
               float var23 = var43 + 11.0F + 3.0F;
               var0.addRectFilled(var20, var22, var21, var23, 352321535, 6.0F);
            }

            var0.addText(var7 - 16.0F - var42, var43, argb2imu(var40), var39);
         } finally {
            popFont(var38);
         }
      }
   }

   private static int countSearchResults() {
      String var0 = searchQuery == null ? "" : searchQuery.trim().toLowerCase(Locale.ROOT);
      if (var0.isEmpty()) {
         return 0;
      } else {
         int var1 = 0;

         try {
            for (Category var5 : Category.values()) {
               for (Module var7 : NyxClient.MODULES.listOf(var5)) {
                  if (var7 != null && !var7.isEnabled()) {
                     String var8 = var7.getString();
                     if (var8 != null && var8.toLowerCase(Locale.ROOT).contains(var0)) {
                        var1++;
                     }
                  }
               }
            }
         } catch (Throwable var9) {
         }

         return var1;
      }
   }

   private static void captureSearchChars(MinecraftClient var0) {
      if (var0 != null && var0.getWindow() != null) {
         long var1 = var0.getWindow().getHandle();
         if (var1 != 0L) {
            boolean var3;
            try {
               var3 = GLFW.glfwGetKey(var1, 340) == 1 || GLFW.glfwGetKey(var1, 344) == 1;
            } catch (Throwable var15) {
               return;
            }

            boolean var4;
            try {
               var4 = GLFW.glfwGetKey(var1, 259) == 1;
            } catch (Throwable var12) {
               return;
            }

            if (var4 && !glfwPrev[259] && searchQuery != null && !searchQuery.isEmpty()) {
               searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
            }

            glfwPrev[259] = var4;

            boolean var5;
            try {
               var5 = GLFW.glfwGetKey(var1, 256) == 1;
            } catch (Throwable var11) {
               return;
            }

            if (var5 && !glfwPrev[256]) {
               searchQuery = "";
            }

            glfwPrev[256] = var5;
            StringBuilder var6 = null;

            for (int var7 = 65; var7 <= 90; var7++) {
               boolean var8;
               try {
                  var8 = GLFW.glfwGetKey(var1, var7) == 1;
               } catch (Throwable var14) {
                  continue;
               }

               if (var8 && !glfwPrev[var7]) {
                  char var9 = (char)(var7 - 65 + (var3 ? 65 : 97));
                  if (var6 == null) {
                     var6 = new StringBuilder();
                  }

                  var6.append(var9);
               }

               glfwPrev[var7] = var8;
            }

            for (int var16 = 48; var16 <= 57; var16++) {
               boolean var19;
               try {
                  var19 = GLFW.glfwGetKey(var1, var16) == 1;
               } catch (Throwable var13) {
                  continue;
               }

               if (var19 && !glfwPrev[var16]) {
                  char var21 = (char)(48 + (var16 - 48));
                  if (var6 == null) {
                     var6 = new StringBuilder();
                  }

                  var6.append(var21);
               }

               glfwPrev[var16] = var19;
            }

            boolean var17;
            try {
               var17 = GLFW.glfwGetKey(var1, 32) == 1;
            } catch (Throwable var10) {
               return;
            }

            if (var17 && !glfwPrev[32]) {
               if (var6 == null) {
                  var6 = new StringBuilder();
               }

               var6.append(' ');
            }

            glfwPrev[32] = var17;
            if (var6 != null) {
               String var18 = searchQuery == null ? "" : searchQuery;
               String var20 = "nullnull";
               if (var20.length() > 64) {
                  var20 = var20.substring(0, 64);
               }

               searchQuery = var20;
            }
         }
      }
   }

   private static int argb2imu(int argb) {
      int var1 = argb >>> 24 & 0xFF;
      int var2 = argb >>> 16 & 0xFF;
      int var3 = argb >>> 8 & 0xFF;
      int var4 = argb & 0xFF;
      return var1 << 24 | var4 << 16 | var3 << 8 | var2;
   }

   private static void captureBindKey(MinecraftClient var0) {
      if (var0 != null && var0.getWindow() != null) {
         long var1 = var0.getWindow().getHandle();
         if (var1 != 0L) {
            for (int var3 = 32; var3 <= 348; var3++) {
               boolean var4;
               try {
                  var4 = GLFW.glfwGetKey(var1, var3) == 1;
               } catch (Throwable var6) {
                  continue;
               }

               if (var4 && !glfwPrev[var3]) {
                  if (var3 == 256 || var3 == 344) {
                     pendingBindCapture = null;
                     pendingModuleBind = null;
                  } else if (var3 != 261 && var3 != 259) {
                     if (pendingBindCapture != null) {
                        pendingBindCapture.setValue(var3);
                        pendingBindCapture = null;
                     } else if (pendingModuleBind != null) {
                        pendingModuleBind.run7(var3);
                        pendingModuleBind = null;
                     }
                  } else if (pendingBindCapture != null) {
                     pendingBindCapture.setValue(-1);
                     pendingBindCapture = null;
                  } else if (pendingModuleBind != null) {
                     pendingModuleBind.run7(0);
                     pendingModuleBind = null;
                  }

                  glfwPrev[var3] = var4;
                  break;
               }

               glfwPrev[var3] = var4;
            }
         }
      }
   }

   private static void captureTypedChars(StringSetting var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null && var1.getWindow() != null) {
         long var2 = var1.getWindow().getHandle();
         if (var2 != 0L) {
            boolean var4;
            try {
               var4 = GLFW.glfwGetKey(var2, 340) == 1 || GLFW.glfwGetKey(var2, 344) == 1;
            } catch (Throwable var22) {
               return;
            }

            boolean var5;
            try {
               var5 = GLFW.glfwGetKey(var2, 259) == 1;
            } catch (Throwable var18) {
               return;
            }

            if (var5 && !glfwPrev[259]) {
               String var6 = var0.getValue() == null ? "" : var0.getValue();
               if (!var6.isEmpty()) {
                  var0.setValue(var6.substring(0, var6.length() - 1));
               }
            }

            glfwPrev[259] = var5;

            boolean var7;
            boolean var23;
            try {
               var23 = GLFW.glfwGetKey(var2, 257) == 1;
               var7 = GLFW.glfwGetKey(var2, 256) == 1;
            } catch (Throwable var17) {
               return;
            }

            if ((!var23 || glfwPrev[257]) && (!var7 || glfwPrev[256])) {
               glfwPrev[257] = var23;
               glfwPrev[256] = var7;
               StringBuilder var8 = null;

               for (int var9 = 65; var9 <= 90; var9++) {
                  boolean var10;
                  try {
                     var10 = GLFW.glfwGetKey(var2, var9) == 1;
                  } catch (Throwable var21) {
                     continue;
                  }

                  if (var10 && !glfwPrev[var9]) {
                     char var11 = (char)(var9 - 65 + (var4 ? 65 : 97));
                     if (var8 == null) {
                        var8 = new StringBuilder();
                     }

                     var8.append(var11);
                  }

                  glfwPrev[var9] = var10;
               }

               char[] var24 = new char[]{')', '!', '@', '#', '$', '%', '^', '&', '*', '('};

               for (int var25 = 48; var25 <= 57; var25++) {
                  boolean var27;
                  try {
                     var27 = GLFW.glfwGetKey(var2, var25) == 1;
                  } catch (Throwable var20) {
                     continue;
                  }

                  if (var27 && !glfwPrev[var25]) {
                     char var12 = var4 ? var24[var25 - 48] : (char)(48 + (var25 - 48));
                     if (var8 == null) {
                        var8 = new StringBuilder();
                     }

                     var8.append(var12);
                  }

                  glfwPrev[var25] = var27;
               }

               int[] var26 = new int[]{32, 45, 61, 46, 44, 47, 59, 39};
               char[] var28 = new char[]{' ', '-', '=', '.', ',', '/', ';', '\''};
               char[] var29 = new char[]{' ', '_', '+', '>', '<', '?', ':', '"'};

               for (int var13 = 0; var13 < var26.length; var13++) {
                  int var14 = var26[var13];

                  boolean var15;
                  try {
                     var15 = GLFW.glfwGetKey(var2, var14) == 1;
                  } catch (Throwable var19) {
                     continue;
                  }

                  if (var15 && !glfwPrev[var14]) {
                     char var16 = var4 ? var29[var13] : var28[var13];
                     if (var8 == null) {
                        var8 = new StringBuilder();
                     }

                     var8.append(var16);
                  }

                  glfwPrev[var14] = var15;
               }

               if (var8 != null) {
                  if (var0.getValue() == null) {
                     String var10000 = "";
                  } else {
                     var0.getValue();
                  }

                  var0.setValue("nullnull");
               }
            } else {
               activeStringSetting = null;
               glfwPrev[257] = var23;
               glfwPrev[256] = var7;
            }
         }
      }
   }

   private static Path configPath() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      Path var1 = var0 != null && var0.runDirectory != null ? var0.runDirectory.toPath() : Path.of(".");
      return var1.resolve("codeengine").resolve("clickgui-imgui.json");
   }

   private static void load() {
      Path var0 = configPath();
      if (Files.exists(var0)) {
         String var1;
         try {
            var1 = Files.readString(var0, StandardCharsets.UTF_8);
         } catch (Throwable var14) {
            logFail("load-read", var14);
            return;
         }

         JsonObject var2;
         try {
            var2 = JsonParser.parseString(var1).getAsJsonObject();
         } catch (Throwable var13) {
            logFail("load-parse", var13);
            return;
         }

         JsonArray var3 = var2.has("panels") && var2.get("panels").isJsonArray() ? var2.getAsJsonArray("panels") : null;
         if (var3 != null) {
            Iterator var4 = var3.iterator();

            while (true) {
               JsonObject var6;
               Category var8;
               while (true) {
                  if (!var4.hasNext()) {
                     return;
                  }

                  JsonElement var5 = (JsonElement)var4.next();
                  if (var5.isJsonObject()) {
                     var6 = var5.getAsJsonObject();
                     String var7 = var6.has("cat") ? var6.get("cat").getAsString() : null;
                     if (var7 != null) {
                        try {
                           var8 = Category.valueOf(var7);
                           break;
                        } catch (IllegalArgumentException var15) {
                        }
                     }
                  }
               }

               ImGuiClickGui.PanelState var9 = panels.get(var8);
               if (var9 != null) {
                  if (var6.has("x")) {
                     var9.floatVal = var6.get("x").getAsFloat();
                  }

                  if (var6.has("y")) {
                     var9.floatVal2 = var6.get("y").getAsFloat();
                  }

                  if (var6.has("collapsed")) {
                     var9.collapsed = var6.get("collapsed").getAsBoolean();
                  }

                  var9.expanded.clear();
                  if (var6.has("expanded") && var6.get("expanded").isJsonArray()) {
                     HashSet var10 = new HashSet();

                     for (JsonElement var12 : var6.getAsJsonArray("expanded")) {
                        if (var12.isJsonPrimitive()) {
                           var10.add(var12.getAsString());
                        }
                     }

                     for (Module var17 : NyxClient.MODULES.listOf(var8)) {
                        if (var17 != null && var10.contains(var17.getString())) {
                           var9.expanded.add(var17);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void save() {
      JsonObject var0 = new JsonObject();
      JsonArray var1 = new JsonArray();

      for (Entry var3 : panels.entrySet()) {
         JsonObject var4 = new JsonObject();
         var4.addProperty("cat", ((Category)var3.getKey()).name());
         var4.addProperty("x", ((ImGuiClickGui.PanelState)var3.getValue()).floatVal);
         var4.addProperty("y", ((ImGuiClickGui.PanelState)var3.getValue()).floatVal2);
         var4.addProperty("collapsed", ((ImGuiClickGui.PanelState)var3.getValue()).collapsed);
         JsonArray var5 = new JsonArray();

         for (Module var7 : ((ImGuiClickGui.PanelState)var3.getValue()).expanded) {
            if (var7 != null) {
               var5.add(var7.getString());
            }
         }

         var4.add("expanded", var5);
         var1.add(var4);
      }

      var0.add("panels", var1);
      Path var9 = configPath();

      try {
         Files.createDirectories(var9.getParent());
         Gson var10 = new GsonBuilder().setPrettyPrinting().create();
         Files.writeString(var9, var10.toJson(var0), StandardCharsets.UTF_8);
      } catch (Throwable var8) {
         logFail("save-write", var8);
      }
   }

   private static boolean pushFont(float size) {
      ImFont var1 = ImGuiFonts.POPPINS;
      if (var1 == null) {
         return false;
      } else {
         try {
            ImGui.pushFont(var1, size);
            return true;
         } catch (Throwable var3) {
            return false;
         }
      }
   }

   private static void popFont(boolean pushed) {
      if (pushed) {
         try {
            ImGui.popFont();
         } catch (Throwable var2) {
         }
      }
   }

   private static String fitText(String var0, float var1) {
      if (var0 != null && !var0.isEmpty() && !(var1 <= 0.0F)) {
         if (calcTextW(var0) <= var1) {
            return var0;
         } else {
            float var3 = calcTextW("…");
            StringBuilder var4 = new StringBuilder();

            for (int var5 = 0; var5 < var0.length(); var5++) {
               var4.append(var0.charAt(var5));
               if (calcTextW(var4.toString()) + var3 > var1) {
                  var4.deleteCharAt(var4.length() - 1);
                  break;
               }
            }

            return var4.append("…").toString();
         }
      } else {
         return var0;
      }
   }

   private static float calcTextW(String s) {
      try {
         ImVec2 var1 = ImGui.calcTextSize(s);
         return var1.x;
      } catch (Throwable var2) {
         return s == null ? 0.0F : s.length() * 6.0F;
      }
   }

   private static String formatNumber(NumberSetting n) {
      return n.isInteger() ? Integer.toString(n.getValueInt()) : String.format(Locale.ROOT, n.getIncrement() >= 0.1 ? "%.2f" : "%.3f", n.getValue());
   }

   private static String keyName(int key) {
      if (key <= 0) {
         return "NONE";
      } else {
         try {
            return new BindSetting("_", key).keyName();
         } catch (Throwable var2) {
            return "KEY_" + key;
         }
      }
   }

   private static float clamp01(float f) {
      if (f < 0.0F) {
         return 0.0F;
      } else {
         return f > 1.0F ? 1.0F : f;
      }
   }

   private static String safeName(Module m) {
      try {
         return m == null ? "?" : m.getString();
      } catch (Throwable var2) {
         return "?";
      }
   }

   private static void logFail(String var0, Throwable var1) {
      if (loggedFailures.add(var0)) {
         System.err.println("[ClickGui] null failed: null");
      }
   }

   static {
      CAT_ICON.put(Category.COMBAT, "\uf6de");
      CAT_ICON.put(Category.MOVEMENT, "\uf70c");
      CAT_ICON.put(Category.RENDER, "\uf06e");
      CAT_ICON.put(Category.PLAYER, "\uf007");
      CAT_ICON.put(Category.WORLD, "\uf57d");
      CAT_ICON.put(Category.CLIENT, "\uf013");
      CAT_ICON.put(Category.ADDONS, "\uf12e");
      seedDefaults();
   }

   private static final class PanelState {
      float floatVal;
      float floatVal2;
      boolean collapsed;
      float scroll;
      final Set<Module> expanded = Collections.newSetFromMap(new IdentityHashMap<>());

      PanelState(float x, float y) {
         this.floatVal = x;
         this.floatVal2 = y;
         this.scroll = 0.0F;
      }
   }
}

