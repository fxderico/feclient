package dev.fede.module;

import dev.fede.settings.KeybindSetting;
import dev.fede.settings.Setting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class Module {
   private String name;
   private String description;
   private Category category;
   private KeybindSetting keybind;
   private final List<Setting<?>> settings = new ArrayList<>();
   private boolean enabled;
   private BiConsumer<Module, Boolean> toggleCallback;

   protected Module(String name, String description, Category category) {
      this.name = name;
      this.description = description;
      this.category = category;
      this.keybind = new KeybindSetting("Keybind", "Toggles null", -1);
   }

   protected <T extends Setting<?>> T addSetting(T setting) {
      this.settings.add(setting);
      return setting;
   }

   public String getName() {
      return this.name;
   }

   /** Human-readable name with spaces, e.g. "KillAura" → "Kill Aura" */
   public String getDisplayName() {
      return formatDisplayName(this.name);
   }

   public String getDescription() {
      return this.description != null ? this.description : "";
   }

   private static final java.util.Set<String> ABBREVS = new java.util.HashSet<>(
       java.util.Arrays.asList("ESP","HUD","AFK","FOV","FOV","GUI","XP","TPA","MLG","SMP","NPC","PVP","RTP","TP")
   );

   public static String formatDisplayName(String name) {
       if (name == null || name.isBlank()) return name;
       // insert space before capital letter that follows a lowercase letter
       String spaced = name.replaceAll("([a-z])([A-Z])", "$1 $2")
                           .replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2");
       // capitalise each word, uppercase known abbreviations
       String[] words = spaced.split(" ");
       StringBuilder sb = new StringBuilder();
       for (String w : words) {
           if (w.isEmpty()) continue;
           if (sb.length() > 0) sb.append(' ');
           String up = w.toUpperCase();
           if (ABBREVS.contains(up)) {
               sb.append(up);
           } else {
               sb.append(Character.toUpperCase(w.charAt(0)));
               sb.append(w.substring(1));
           }
       }
       return sb.toString();
   }

   public Category getCategory() {
      return this.category;
   }

   public KeybindSetting getKeybind() {
      return this.keybind;
   }

   public List<Setting<?>> getSettings() {
      return Collections.unmodifiableList(this.settings);
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      if (this.enabled != enabled) {
         this.enabled = enabled;
         if (enabled) {
            this.onEnable();
         } else {
            this.onDisable();
         }

         if (this.toggleCallback != null) {
            this.toggleCallback.accept(this, enabled);
         }
      }
   }

   void setToggleCallback(BiConsumer<Module, Boolean> callback) {
      this.toggleCallback = callback;
   }

   public void toggle() {
      this.setEnabled(!this.enabled);
   }

   /** Returns the GLFW key code bound to this module (or -1 if none). */
   public int getKeyCode() {
      return this.keybind != null ? this.keybind.get() : -1;
   }

   /** Sets the GLFW key code bound to this module. Pass -1 to clear. */
   public void setKeyCode(int keyCode) {
      if (this.keybind != null) this.keybind.set(keyCode);
   }

   protected void onEnable() {
   }

   protected void onDisable() {
   }

   public void onTick() {
   }

   public boolean onKeyPress(int keyCode) {
      return false;
   }

   /** Optional suffix shown in the arraylist HUD (e.g. active mode name). Override to supply. */
   public String getSuffix() {
      return "";
   }

   /** Whether this module is currently "active" — for modules whose enabled != active state. */
   public boolean isActive() {
      return this.enabled;
   }
}

