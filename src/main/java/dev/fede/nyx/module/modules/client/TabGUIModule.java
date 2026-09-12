package dev.fede.nyx.module.modules.client;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BindSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class TabGUIModule extends Module {
   private static final int intVal = 12;
   private static final int intVal2 = 4;
   private static final int intVal3 = 2;
   private static final int intVal4 = 2;
   private final BindSetting openKey = new BindSetting("OpenKey", 265);
   private final NumberSetting posX = new NumberSetting("X", 4.0, 0.0, 4000.0, 1.0);
   private final NumberSetting posY = new NumberSetting("Y", 32.0, 0.0, 4000.0, 1.0);
   private final ColorSetting textColor = new ColorSetting("TextColor", -1513240);
   private final ColorSetting selectedColor = new ColorSetting("SelectedColor", -12976364);
   private final ColorSetting bgColor = new ColorSetting("BgColor", -1072557550);
   private int intVal5;
   private int intVal6;
   private boolean bool;
   private final Set<Integer> set = new HashSet<>();
   private boolean bool2;

   public TabGUIModule() {
      super("TabGUI", "Keyboard-navigable module list", Category.CLIENT);
      this.run6(new Setting[]{this.openKey, this.posX, this.posY, this.textColor, this.selectedColor, this.bgColor});
   }

   @Override
   public void run2() {
      this.set.clear();
      this.bool2 = false;
      this.bool = false;
      this.intVal5 = 0;
      this.intVal6 = 0;
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.textRenderer != null && class310.getWindow() != null) {
         TextRenderer var3 = class310.textRenderer;
         boolean var4 = class310.currentScreen != null;
         if (var4) {
            this.set.clear();
         } else {
            if (this.bool2) {
               this.run();
            }

            this.run3();
         }

         this.bool2 = var4;
         Category[] var5 = Category.values();
         if (this.intVal5 >= var5.length) {
            this.intVal5 = var5.length - 1;
         }

         if (this.intVal5 < 0) {
            this.intVal5 = 0;
         }

         int var6 = 0;

         for (Category var10 : var5) {
            int var11 = var3.getWidth(var10.getString());
            if (var11 > var6) {
               var6 = var11;
            }
         }

         var6 += 8;
         int var26 = var5.length * 12 + 4;
         int var27 = this.posX.getValueInt();
         int var28 = this.posY.getValueInt();
         int var29 = this.bgColor.getValue();
         int var30 = this.textColor.getValue();
         int var12 = this.selectedColor.getValue();
         var1.fill(var27, var28, var27 + var6, var28 + var26, var29);

         for (int var13 = 0; var13 < var5.length; var13++) {
            String var14 = var5[var13].getString();
            int var15 = var28 + 2 + var13 * 12;
            boolean var16 = var13 == this.intVal5;
            if (var16) {
               var1.fill(var27, var15, var27 + 2, var15 + 12, var12);
            }

            int var17 = var16 ? var12 : var30;
            int var10003 = var27 + 4;
            byte var10005 = 12;
            var1.drawTextWithShadow(var3, var14, var10003, var15 + 1, var17);
         }

         if (this.bool) {
            Category var31 = var5[this.intVal5];
            List var32 = this.listOf(var31);
            if (this.intVal6 >= var32.size()) {
               this.intVal6 = Math.max(0, var32.size() - 1);
            }

            if (this.intVal6 < 0) {
               this.intVal6 = 0;
            }

            int var33 = 0;
            if (var32.isEmpty()) {
               var33 = var3.getWidth("(empty)");
            } else {
               for (Module var37 : (java.util.List<Module>)var32) {
                  int var18 = var3.getWidth(var37.getString2());
                  if (var18 > var33) {
                     var33 = var18;
                  }
               }
            }

            var33 += 8;
            int var36 = Math.max(1, var32.size());
            int var38 = var36 * 12 + 4;
            int var39 = var27 + var6 + 2;
            int var19 = var28;
            var1.fill(var39, var28, var39 + var33, var28 + var38, var29);
            if (var32.isEmpty()) {
               int var40 = var39 + 4;
               int var10004 = var28 + 2;
               byte var42 = 12;
               var1.drawTextWithShadow(var3, "(empty)", var40, var10004 + 1, var30);
            } else {
               for (int var20 = 0; var20 < var32.size(); var20++) {
                  Module var21 = (Module)var32.get(var20);
                  int var22 = var19 + 2 + var20 * 12;
                  boolean var23 = var20 == this.intVal6;
                  if (var23) {
                     var1.fill(var39, var22, var39 + 2, var22 + 12, var12);
                  }

                  int var24;
                  if (var21.isEnabled3()) {
                     var24 = var12;
                  } else if (var23) {
                     var24 = var30;
                  } else {
                     var24 = -7697782;
                  }

                  String var10002 = var21.getString2();
                  int var41 = var39 + 4;
                  byte var43 = 12;
                  var1.drawTextWithShadow(var3, var10002, var41, var22 + 1, var24);
               }
            }
         }
      }
   }

   public void run3() {
      long var1 = class310.getWindow().getHandle();
      int var3 = this.openKey.isUnbound() ? -1 : this.openKey.getValue();
      boolean var4 = check2(var1, 265) || var3 != -1 && check2(var1, var3);
      boolean var5 = check2(var1, 264);
      boolean var6 = check2(var1, 263);
      boolean var7 = check2(var1, 262);
      boolean var8 = check2(var1, 257) || check2(var1, 335);
      boolean var9 = this.check(-1, var4);
      boolean var10 = this.check(264, var5);
      boolean var11 = this.check(263, var6);
      boolean var12 = this.check(262, var7);
      boolean var13 = this.check(-2, var8);
      if (var9) {
         this.run5();
      }

      if (var10) {
         this.run6();
      }

      if (var11) {
         this.run7();
      }

      if (var12) {
         this.run8();
      }

      if (var13) {
         this.run9();
      }
   }

   public void run() {
      long var1 = class310.getWindow().getHandle();
      int var3 = this.openKey.isUnbound() ? -1 : this.openKey.getValue();
      boolean var4 = check2(var1, 265) || var3 != -1 && check2(var1, var3);
      boolean var5 = check2(var1, 264);
      boolean var6 = check2(var1, 263);
      boolean var7 = check2(var1, 262);
      boolean var8 = check2(var1, 257) || check2(var1, 335);
      if (var4) {
         this.set.add(-1);
      }

      if (var5) {
         this.set.add(264);
      }

      if (var6) {
         this.set.add(263);
      }

      if (var7) {
         this.set.add(262);
      }

      if (var8) {
         this.set.add(-2);
      }
   }

   private boolean check(int var1, boolean var2) {
      boolean var3 = this.set.contains(var1);
      if (var2 && !var3) {
         this.set.add(var1);
         return true;
      } else {
         if (!var2 && var3) {
            this.set.remove(var1);
         }

         return false;
      }
   }

   private static boolean check2(long var0, int var2) {
      if (var2 == -1) {
         return false;
      } else {
         try {
            return GLFW.glfwGetKey(var0, var2) == 1;
         } catch (Throwable var4) {
            return false;
         }
      }
   }

   private void run5() {
      if (!this.bool) {
         this.intVal5 = intOf3(this.intVal5 - 1, Category.values().length);
      } else {
         List var1 = this.listOf(Category.values()[this.intVal5]);
         if (!var1.isEmpty()) {
            this.intVal6 = intOf3(this.intVal6 - 1, var1.size());
         }
      }
   }

   private void run6() {
      if (!this.bool) {
         this.intVal5 = intOf3(this.intVal5 + 1, Category.values().length);
      } else {
         List var1 = this.listOf(Category.values()[this.intVal5]);
         if (!var1.isEmpty()) {
            this.intVal6 = intOf3(this.intVal6 + 1, var1.size());
         }
      }
   }

   private void run7() {
      if (this.bool) {
         this.bool = false;
         this.intVal6 = 0;
      }
   }

   private void run8() {
      if (!this.bool) {
         this.bool = true;
         this.intVal6 = 0;
      }
   }

   private void run9() {
      if (!this.bool) {
         this.bool = true;
         this.intVal6 = 0;
      } else {
         List var1 = this.listOf(Category.values()[this.intVal5]);
         if (!var1.isEmpty()) {
            Module var2 = (Module)var1.get(Math.max(0, Math.min(this.intVal6, var1.size() - 1)));
            var2.run19();
         }
      }
   }

   private static int intOf3(int var0, int var1) {
      if (var1 <= 0) {
         return 0;
      } else {
         int var2 = var0 % var1;
         return var2 < 0 ? var2 + var1 : var2;
      }
   }

   private List<Module> listOf(Category var1) {
      ArrayList var2 = new ArrayList();
      if (NyxClient.MODULES == null) {
         return var2;
      } else {
         for (Module var4 : NyxClient.MODULES.listOf(var1)) {
            if (!var4.isEnabled()) {
               var2.add(var4);
            }
         }

         return var2;
      }
   }
}

