package dev.fede.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.lwjgl.glfw.GLFW;

public class KeybindSetting extends Setting<Integer> {
   public static final int NONE = -1;

   public KeybindSetting(String name, String description, int defaultKey) {
      super(name, description, defaultKey);
   }

   public boolean isBound() {
      return this.get() != -1;
   }

   public boolean matches(int keyCode) {
      return this.isBound() && this.get() == keyCode;
   }

   public String keyName() {
      int key = this.get();
      if (key == -1) {
         return "None";
      } else if (key >= 0 && key <= 7) {
         return switch (key) {
            case 0 -> "LMB";
            case 1 -> "RMB";
            case 2 -> "MMB";
            default -> "MB" + (key + 1);
         };
      } else {
         String name = GLFW.glfwGetKeyName(key, 0);
         if (name != null) {
            return name.toUpperCase();
         } else {
            return switch (key) {
               case 32 -> "SPACE";
               case 257 -> "ENTER";
               case 258 -> "TAB";
               case 259 -> "BACK";
               case 260 -> "INSERT";
               case 261 -> "DELETE";
               case 262 -> "RIGHT";
               case 263 -> "LEFT";
               case 264 -> "DOWN";
               case 265 -> "UP";
               case 266 -> "PGUP";
               case 267 -> "PGDN";
               case 268 -> "HOME";
               case 269 -> "END";
               case 280 -> "CAPS";
               case 340 -> "LSHIFT";
               case 341 -> "LCTRL";
               case 342 -> "LALT";
               case 344 -> "RSHIFT";
               case 345 -> "RCTRL";
               case 346 -> "RALT";
               default -> key >= 290 && key <= 314 ? "F" + (key - 290 + 1) : "KEY" + key;
            };
         }
      }
   }

   @Override
   public JsonElement toJson() {
      return new JsonPrimitive(this.value);
   }

   @Override
   public void fromJson(JsonElement element) {
      if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
         this.value = element.getAsInt();
      }
   }
}

