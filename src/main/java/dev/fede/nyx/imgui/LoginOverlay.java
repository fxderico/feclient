package dev.fede.nyx.imgui;

import dev.fede.nyx.auth.AuthState;
import dev.fede.nyx.util.AntiDebugUtil;
import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public final class LoginOverlay {
   private static final int CARD_BG = -266857711;
   private static final int CARD_BORDER = 587202559;
   private static final int CARD_SHADOW = 1610612736;
   private static final int DIM_BG = -1342177280;
   private static final int TITLE_COL = -1;
   private static final int SUBTITLE_COL = -5200480;
   private static final int FOOTER_COL = -29588;
   private static final int INPUT_BG = -14936557;
   private static final int INPUT_BORDER_HL = -29588;
   private static final int PLACEHOLDER = -10069418;
   private static final int TEXT_COL = -593168;
   private static final int BTN_BG = -29588;
   private static final int BTN_BG_HOVER = -22132;
   private static final int BTN_BG_DISABLED = -11516609;
   private static final int BTN_TEXT = -1;
   private static final int BTN_TEXT_DIM = -4410192;
   private static final int STATUS_ERROR = -10127372;
   private static final int STATUS_WARN = -12733963;
   private static final int STATUS_OK = -8664721;
   private static final float CARD_W = 340.0F;
   private static final float CARD_H = 210.0F;
   private static final float ROUND = 10.0F;
   private static final float INPUT_W = 284.0F;
   private static final float INPUT_H = 34.0F;
   private static final float BTN_W = 284.0F;
   private static final float BTN_H = 34.0F;
   private static final StringBuilder INPUT = new StringBuilder();
   private static final int MAX_INPUT_LEN = 128;
   private static final boolean[] glfwPrev = new boolean[349];
   private static boolean firstFrame = true;

   private LoginOverlay() {
   }

   private static int argb(int a, int r, int g, int b) {
      return (a & 0xFF) << 24 | (b & 0xFF) << 16 | (g & 0xFF) << 8 | r & 0xFF;
   }

   public static void reset() {
      INPUT.setLength(0);

      for (int var0 = 0; var0 < glfwPrev.length; var0++) {
         glfwPrev[var0] = false;
      }

      firstFrame = true;
   }

   public static void render(MinecraftClient var0) {
      if (var0 != null) {
         try {
            ImGuiIO var1 = ImGui.getIO();
            var1.setWantCaptureMouse(true);
            var1.setWantCaptureKeyboard(true);
         } catch (Throwable var31) {
         }

         if (firstFrame) {
            primeGlfwEdges(var0);
            firstFrame = false;
         }

         float var33 = 0.0F;
         float var2 = 0.0F;

         try {
            ImGuiIO var3 = ImGui.getIO();
            var33 = var3.getDisplaySizeX();
            var2 = var3.getDisplaySizeY();
         } catch (Throwable var30) {
         }

         if (var33 <= 0.0F || var2 <= 0.0F) {
            var33 = var0.getWindow() != null ? var0.getWindow().getFramebufferWidth() : 1280.0F;
            var2 = var0.getWindow() != null ? var0.getWindow().getFramebufferHeight() : 720.0F;
         }

         float var34 = (var33 - 340.0F) * 0.5F;
         float var4 = (var2 - 210.0F) * 0.5F;

         ImDrawList var5;
         try {
            var5 = ImGui.getForegroundDrawList();
         } catch (Throwable var29) {
            return;
         }

         var5.addRectFilled(0.0F, 0.0F, var33, var2, DIM_BG);
         var5.addRectFilled(var34, var4 + 4.0F, var34 + 340.0F, var4 + 210.0F + 6.0F, CARD_SHADOW, 12.0F);
         var5.addRectFilled(var34, var4, var34 + 340.0F, var4 + 210.0F, CARD_BG, 10.0F);
         var5.addRect(var34 + 0.5F, var4 + 0.5F, var34 + 340.0F - 0.5F, var4 + 210.0F - 0.5F, CARD_BORDER, 10.0F, 0, 1.0F);
         ImFont var6 = ImGuiFonts.POPPINS;
         drawCenteredText(var5, var6, 20.0F, "Code Engine", var34, var4 + 28.0F, 340.0F, TITLE_COL);
         drawCenteredText(var5, var6, 11.0F, "Enter your license key to continue", var34, var4 + 54.0F, 340.0F, SUBTITLE_COL);
         float var7 = var34 + 28.0F;
         float var8 = var4 + 84.0F;
         var5.addRectFilled(var7, var8, var7 + 284.0F, var8 + 34.0F, INPUT_BG, 6.0F);
         String var9 = INPUT.toString();
         boolean var10 = var9.isEmpty();
         int var11 = var10 ? CARD_BORDER : INPUT_BORDER_HL;
         var5.addRect(var7 + 0.5F, var8 + 0.5F, var7 + 284.0F - 0.5F, var8 + 34.0F - 0.5F, var11, 6.0F, 0, 1.0F);
         String var12 = var10 ? "CE-XXXX-XXXX-XXXX-XXXX" : var9;
         int var13 = var10 ? PLACEHOLDER : TEXT_COL;
         boolean var14 = !var10 && System.currentTimeMillis() / 500L % 2L == 0L;
         boolean var15 = pushFont(var6, 13.0F);

         try {
            String var16 = fit(var12, 260.0F);
            float var17 = var7 + 12.0F;
            float var18 = var8 + 10.5F - 1.0F;
            if (var6 != null) {
               var5.addText(var6, 13, var17, var18, var13, var16);
            } else {
               var5.addText(var17, var18, var13, var16);
            }

            if (var14) {
               float var19 = var17 + calcTextW(var16) + 1.0F;
               var5.addLine(var19, var18 + 1.0F, var19, var18 + 14.0F, TEXT_COL, 1.0F);
            }
         } finally {
            popFont(var15);
         }

         float var35 = var34 + 28.0F;
         float var36 = var8 + 34.0F + 12.0F;
         boolean var37 = AuthState.bool;
         boolean var38 = ImGui.isMouseHoveringRect(var35, var36, var35 + 284.0F, var36 + 34.0F, false);
         int var20 = var37 ? BTN_BG_DISABLED : (var38 ? BTN_BG_HOVER : BTN_BG);
         var5.addRectFilled(var35, var36, var35 + 284.0F, var36 + 34.0F, var20, 6.0F);
         String var21 = var37 ? "Validating" + dots() : "Continue";
         int var22 = var37 ? BTN_TEXT_DIM : BTN_TEXT;
         drawCenteredText(var5, var6, 13.0F, var21, var35, var36 + 10.5F - 1.0F, 284.0F, var22);
         if (!var37 && var38 && ImGui.isMouseClicked(0)) {
            AuthState.run(INPUT.toString());
         }

         String var23 = statusText();
         int var24 = statusColor();
         if (var23 != null && !var23.isEmpty()) {
            drawCenteredText(var5, var6, 10.0F, var23, var34, var36 + 34.0F + 10.0F, 340.0F, var24);
         }

         drawCenteredText(
            var5,
            var6,
            9.0F,
            AntiDebugUtil.stringOf(new byte[]{71, -30, 38, 54, -67, -20, -32, -56, -43, 43, -1, -27, -52, -92, 16}),
            var34,
            var4 + 210.0F - 18.0F,
            340.0F,
            FOOTER_COL
         );
         pumpInput(var0);
      }
   }

   private static String statusText() {
      switch (AuthState.authStateState) {
         case LOCKED:
            return AuthState.string;
         case LOADING:
            return null;
         case AUTHENTICATED:
            return null;
         case authStateState3:
            return "Cannot reach server — check your connection";
         case authStateState:
         case authStateState2:
         case EXPIRED:
         default:
            return AuthState.string;
      }
   }

   private static int statusColor() {
      switch (AuthState.authStateState) {
         case LOCKED:
            return AuthState.string.isEmpty() ? SUBTITLE_COL : STATUS_ERROR;
         case LOADING:
         default:
            return SUBTITLE_COL;
         case AUTHENTICATED:
            return STATUS_OK;
         case authStateState3:
            return STATUS_WARN;
         case authStateState:
         case authStateState2:
         case EXPIRED:
            return STATUS_ERROR;
      }
   }

   private static String dots() {
      long var0 = System.currentTimeMillis() / 400L % 4L;
      switch ((int)var0) {
         case 0:
            return "";
         case 1:
            return ".";
         case 2:
            return "..";
         default:
            return "...";
      }
   }

   private static void primeGlfwEdges(MinecraftClient var0) {
      long var1 = handle(var0);
      if (var1 != 0L) {
         for (int var3 = 32; var3 <= 348; var3++) {
            try {
               glfwPrev[var3] = GLFW.glfwGetKey(var1, var3) == 1;
            } catch (Throwable var5) {
               glfwPrev[var3] = false;
            }
         }
      }
   }

   private static void pumpInput(MinecraftClient var0) {
      long var1 = handle(var0);
      if (var1 != 0L) {
         boolean var3 = key(var1, 340) || key(var1, 344);
         boolean var4 = key(var1, 259);
         if (var4 && !glfwPrev[259] && INPUT.length() > 0) {
            INPUT.setLength(INPUT.length() - 1);
         }

         glfwPrev[259] = var4;
         boolean var5 = key(var1, 257);
         if (var5 && !glfwPrev[257] && !AuthState.bool) {
            AuthState.run(INPUT.toString());
         }

         glfwPrev[257] = var5;
         boolean var6 = key(var1, 341) || key(var1, 345);
         boolean var7 = key(var1, 86);
         if (var6 && var7 && !glfwPrev[86]) {
            try {
               String var8 = GLFW.glfwGetClipboardString(var1);
               if (var8 != null && !var8.isEmpty()) {
                  for (int var9 = 0; var9 < var8.length() && INPUT.length() < 128; var9++) {
                     char var10 = var8.charAt(var9);
                     if (isLicenseChar(var10)) {
                        INPUT.append(Character.toUpperCase(var10));
                     }
                  }
               }
            } catch (Throwable var11) {
            }
         }

         glfwPrev[86] = var7;

         for (int var12 = 65; var12 <= 90; var12++) {
            boolean var15 = key(var1, var12);
            if (var15 && !glfwPrev[var12] && (!var6 || var12 != 86) && INPUT.length() < 128) {
               INPUT.append((char)(var12 - 65 + 65));
            }

            glfwPrev[var12] = var15;
         }

         for (int var13 = 48; var13 <= 57; var13++) {
            boolean var16 = key(var1, var13);
            if (var16 && !glfwPrev[var13] && !var3 && INPUT.length() < 128) {
               INPUT.append((char)(48 + (var13 - 48)));
            }

            glfwPrev[var13] = var16;
         }

         boolean var14 = key(var1, 45);
         if (var14 && !glfwPrev[45] && !var3 && INPUT.length() < 128) {
            INPUT.append('-');
         }

         glfwPrev[45] = var14;
      }
   }

   private static boolean key(long handle, int key) {
      try {
         return GLFW.glfwGetKey(handle, key) == 1;
      } catch (Throwable var4) {
         return false;
      }
   }

   private static long handle(MinecraftClient var0) {
      try {
         return var0 != null && var0.getWindow() != null ? var0.getWindow().getHandle() : 0L;
      } catch (Throwable var2) {
         return 0L;
      }
   }

   private static boolean isLicenseChar(char var0) {
      return true;
   }

   private static void drawCenteredText(ImDrawList dl, ImFont font, float size, String s, float x, float y, float w, int col) {
      boolean var8 = pushFont(font, size);

      try {
         float var9 = calcTextW(s);
         float var10 = x + (w - var9) * 0.5F;
         if (font != null) {
            dl.addText(font, (int)size, var10, y, col, s);
         } else {
            dl.addText(var10, y, col, s);
         }
      } finally {
         popFont(var8);
      }
   }

   private static boolean pushFont(ImFont f, float size) {
      if (f == null) {
         return false;
      } else {
         try {
            ImGui.pushFont(f, size);
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

   private static float calcTextW(String s) {
      try {
         ImVec2 var1 = ImGui.calcTextSize(s);
         return var1.x;
      } catch (Throwable var2) {
         return s == null ? 0.0F : s.length() * 6.0F;
      }
   }

   private static String fit(String var0, float var1) {
      if (var0 != null && !var0.isEmpty()) {
         if (calcTextW(var0) <= var1) {
            return var0;
         } else {
            int var2 = 0;
            int var3 = var0.length();

            while (var2 < var3) {
               int var4 = var2 + var3 >>> 1;
               String var5 = var0.substring(var4);
               if (calcTextW(var5) <= var1) {
                  var3 = var4;
               } else {
                  var2 = var4 + 1;
               }
            }

            return var0.substring(var2);
         }
      } else {
         return var0;
      }
   }
}

