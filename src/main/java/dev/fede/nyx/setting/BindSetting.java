package dev.fede.nyx.setting;

import org.lwjgl.glfw.GLFW;

public class BindSetting extends Setting {
   private int value;

   public BindSetting(String name, int defaultKey) {
      super(name);
      this.value = defaultKey;
   }

   public int getValue() {
      return this.value;
   }

   public void setValue(int key) {
      this.value = key;
   }

   public boolean isUnbound() {
      return this.value == -1;
   }

   public String keyName() {
      if (this.value == -1) {
         return "NONE";
      } else {
         String var1 = namedKey(this.value);
         if (var1 != null) {
            return var1;
         } else {
            try {
               String var2 = GLFW.glfwGetKeyName(this.value, 0);
               if (var2 != null && !var2.isEmpty()) {
                  return var2.toUpperCase();
               }
            } catch (Throwable var3) {
            }

            return "KEY_" + this.value;
         }
      }
   }

   private static String namedKey(int key) {
      switch (key) {
         case 32:
            return "SPACE";
         case 256:
            return "ESCAPE";
         case 257:
            return "ENTER";
         case 258:
            return "TAB";
         case 259:
            return "BACKSPACE";
         case 260:
            return "INSERT";
         case 261:
            return "DELETE";
         case 262:
            return "RIGHT";
         case 263:
            return "LEFT";
         case 264:
            return "DOWN";
         case 265:
            return "UP";
         case 266:
            return "PGUP";
         case 267:
            return "PGDN";
         case 268:
            return "HOME";
         case 269:
            return "END";
         case 280:
            return "CAPS";
         case 281:
            return "SCRLK";
         case 282:
            return "NUMLK";
         case 283:
            return "PRTSC";
         case 284:
            return "PAUSE";
         case 290:
            return "F1";
         case 291:
            return "F2";
         case 292:
            return "F3";
         case 293:
            return "F4";
         case 294:
            return "F5";
         case 295:
            return "F6";
         case 296:
            return "F7";
         case 297:
            return "F8";
         case 298:
            return "F9";
         case 299:
            return "F10";
         case 300:
            return "F11";
         case 301:
            return "F12";
         case 340:
            return "LSHIFT";
         case 341:
            return "LCTRL";
         case 342:
            return "LALT";
         case 343:
            return "LSUPER";
         case 344:
            return "RSHIFT";
         case 345:
            return "RCTRL";
         case 346:
            return "RALT";
         case 347:
            return "RSUPER";
         case 348:
            return "MENU";
         default:
            return null;
      }
   }
}

