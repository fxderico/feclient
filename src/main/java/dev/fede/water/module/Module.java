package dev.fede.water.module;

import dev.fede.water.module.modules.client.WaterPlus;
import dev.fede.water.setting.Setting;
import dev.fede.water.utils.UiSoundManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;

public abstract class Module {
   private final String name;
   private final Category category;
   private boolean enabled;
   private int bind = 0;
   private boolean expanded = false;
   public boolean wasBindPressed = false;
   private final List<Setting<?>> settings = new ArrayList<>();
   protected static final MinecraftClient mc = MinecraftClient.getInstance();

   public Module(String name, Category category) {
      this.name = name;
      this.category = category;
      this.enabled = false;
   }

   public void addSetting(Setting<?> setting) {
      this.settings.add(setting);
   }

   public List<Setting<?>> getSettings() {
      return this.settings;
   }

   public String getName() {
      return this.name;
   }

   public Category getCategory() {
      return this.category;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
      if (enabled) {
         UiSoundManager.bj();
         this.onEnable();
      } else {
         this.onDisable();
      }

      ModuleManager.INSTANCE.f();

      try {
         if (WaterPlus.notificationsEnabled()) {
      // toast suppressed
         }
      } catch (Exception var2) {
      }
   }

   public ItemStack getModuleIcon() {
      Item var1 = resolveItemByName(this.name);
      return var1 != null && var1 != Items.AIR ? new ItemStack(var1) : new ItemStack(this.categoryDefaultItem());
   }

   private static Item resolveItemByName(String moduleName) {
      moduleName = moduleName.toLowerCase().replace(" ", "_");

      return switch (moduleName) {
         case "fullbright" -> Items.GLOWSTONE;
         case "storage_esp" -> Items.CHEST;
         case "extra_esp" -> Items.GLASS;
         case "nametags" -> Items.NAME_TAG;
         case "sprint" -> Items.FEATHER;
         case "freecam" -> Items.ENDER_EYE;
         case "killaura" -> Items.DIAMOND_SWORD;
         case "auto_crystal" -> Items.END_CRYSTAL;
         case "triggerbot" -> Items.BOW;
         case "hitbox" -> Items.BARRIER;
         case "auto_totem" -> Items.TOTEM_OF_UNDYING;
         case "hover_totem" -> Items.TOTEM_OF_UNDYING;
         case "auto_inv_totem" -> Items.TOTEM_OF_UNDYING;
         case "elytra_swap" -> Items.ELYTRA;
         case "shield_breaker" -> Items.SHIELD;
         case "anchor_macro" -> Items.RESPAWN_ANCHOR;
         case "mace_swap" -> Items.MACE;
         case "double_anchor" -> Items.RESPAWN_ANCHOR;
         case "auto_double_hand" -> Items.SHIELD;
         case "speraswap" -> Items.MACE;
         case "freelook" -> Items.SPYGLASS;
         case "skinscraper" -> Items.LEATHER_CHESTPLATE;
         case "skin_changer" -> Items.LEATHER_CHESTPLATE;
         case "auto_tool" -> Items.DIAMOND_PICKAXE;
         case "fast_place" -> Items.PISTON;
         case "coordsnapper" -> Items.COMPASS;
         case "nameprotect" -> Items.BOOK;
         case "autolog" -> Items.PAPER;
         case "autotpa" -> Items.ENDER_PEARL;
         case "tunnel_base_finder" -> Items.DIAMOND_PICKAXE;
         case "tab_detector" -> Items.PLAYER_HEAD;
         case "chat_macro" -> Items.WRITABLE_BOOK;
         case "weather_notifier" -> Items.LIGHTNING_ROD;
         case "spawner_notifier" -> Items.SPAWNER;
         case "block_esp" -> Items.GLASS;
         case "spawner_protect" -> Items.SPAWNER;
         case "homesetter" -> Items.RED_BED;
         case "swing_speed" -> Items.CLOCK;
         case "hud" -> Items.MAP;
         case "water_+" -> Items.WATER_BUCKET;
         case "friends" -> Items.PLAYER_HEAD;
         case "fakeroles" -> Items.PAPER;
         case "fakestats" -> Items.PAPER;
         case "antitrap" -> Items.TRIPWIRE_HOOK;
         case "activitydebug" -> Items.DEBUG_STICK;
         case "bonedropper" -> Items.BONE;
         case "auto_chunk_loader" -> Items.ENDER_CHEST;
         case "sus_chunk_finder" -> Items.SUSPICIOUS_SAND;
         case "radiusdebug" -> Items.STICK;
         case "spotify_hud" -> Items.JUKEBOX;
         default -> null;
      };
   }

   private Item categoryDefaultItem() {
      return switch (this.category) {
         case field_a_1 -> Items.DIAMOND_SWORD;
         case b -> Items.ENDER_EYE;
         case c -> Items.COMPASS;
         case e -> Items.WATER_BUCKET;
         default -> Items.PAPER;
      };
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void toggle() {
      this.setEnabled(!this.enabled);
   }

   public void onBindPressed() {
      this.toggle();
   }

   public int getBind() {
      return this.bind;
   }

   public void setBind(int bind) {
      this.bind = bind;
      ModuleManager.INSTANCE.f();
   }

   void applyBind(int bind) {
      this.bind = bind;
   }

   void applyEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isExpanded() {
      return this.expanded;
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public void onTick() {
   }

   public void onRender(MatrixStack matrices, float tickDelta) {
   }

   public void onPacketReceive(Packet<?> packet) {
   }

   public boolean onPacketSend(Packet<?> packet) {
      return false;
   }

   static String _c8cc94c8a16() {
      return "_";
   }
}

