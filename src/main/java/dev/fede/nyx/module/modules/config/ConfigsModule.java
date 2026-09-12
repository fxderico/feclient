package dev.fede.nyx.module.modules.config;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.config.Manager;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import dev.fede.nyx.util.PathUtils;
import java.util.List;

public class ConfigsModule extends Module {
   private static final String string = "-none-";
   private static final long longVal = 1000L;
   private final ModeSetting currentConfig = new ModeSetting("CurrentConfig", "-none-", "-none-");
   private final StringSetting newConfigName = new StringSetting("NewConfigName", "", 64);
   private final BooleanSetting saveAs = new BooleanSetting("SaveAs", false);
   private final BooleanSetting save = new BooleanSetting("Save", false);
   private final BooleanSetting load = new BooleanSetting("Load", false);
   private final BooleanSetting delete = new BooleanSetting("Delete", false);
   private long longVal2;

   public ConfigsModule() {
      super("Configs", "Save, load and delete named configuration files.", Category.CONFIG);
      this.run6(new Setting[]{this.currentConfig, this.newConfigName, this.saveAs, this.save, this.load, this.delete});
      this.run7();
   }

   @Override
   public void run() {
      this.run7();
   }

   @Override
   public void run2() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.longVal2 >= 1000L) {
         this.run7();
      }

      if (this.saveAs.getValue()) {
         this.saveAs.setValue(false);
         this.run3();
      }

      if (this.save.getValue()) {
         this.save.setValue(false);
         this.run4();
      }

      if (this.load.getValue()) {
         this.load.setValue(false);
         this.run5();
      }

      if (this.delete.getValue()) {
         this.delete.setValue(false);
         this.run6();
      }
   }

   public void run3() {
      String var1 = this.newConfigName.getValue();
      String var2 = PathUtils.sanitize(var1);
      if (var2.isEmpty()) {
         run8("Save failed", "Enter a name in NewConfigName first.", INFO.UNKNOWN_3);
      } else {
         boolean var3 = PathUtils.saveCurrent(NyxClient.MODULES, var2);
         if (var3) {
            run8("Config saved", "Wrote null.json", INFO.UNKNOWN_2);
            this.run7();
            this.currentConfig.setMode(var2);
            this.newConfigName.setValue("");
         } else {
            run8("Save failed", "Could not write null.json", INFO.UNKNOWN_4);
         }
      }
   }

   private void run4() {
      String var1 = this.currentConfig.getMode();
      if (var1 != null && !"-none-".equals(var1)) {
         boolean var2 = PathUtils.saveCurrent(NyxClient.MODULES, var1);
         if (var2) {
            run8("Config saved", "Overwrote null.json", INFO.UNKNOWN_2);
         } else {
            run8("Save failed", "Could not write null.json", INFO.UNKNOWN_4);
         }
      } else {
         run8("Save failed", "Pick a config in CurrentConfig, or use SaveAs.", INFO.UNKNOWN_3);
      }
   }

   private void run5() {
      String var1 = this.currentConfig.getMode();
      if (var1 != null && !"-none-".equals(var1)) {
         boolean var2 = PathUtils.load(NyxClient.MODULES, var1);
         if (var2) {
            try {
               Manager.INSTANCE.markDirty();
            } catch (Throwable var4) {
            }

            run8("Config loaded", "Applied null.json", INFO.UNKNOWN_2);
            NyxClient.LOGGER.info("[Configs] Loaded {}", var1);
         } else {
            run8("Load failed", "Missing or malformed null.json", INFO.UNKNOWN_4);
         }
      } else {
         run8("Load failed", "Pick a config in CurrentConfig first.", INFO.UNKNOWN_3);
      }
   }

   private void run6() {
      String var1 = this.currentConfig.getMode();
      if (var1 != null && !"-none-".equals(var1)) {
         boolean var2 = PathUtils.delete(var1);
         if (var2) {
            run8("Config deleted", "Removed null.json", INFO.UNKNOWN_2);
            this.run7();
            this.currentConfig.setMode("-none-");
         } else {
            run8("Delete failed", "Could not remove null.json", INFO.UNKNOWN_4);
         }
      } else {
         run8("Delete failed", "Pick a config in CurrentConfig first.", INFO.UNKNOWN_3);
      }
   }

   private void run7() {
      this.longVal2 = System.currentTimeMillis();
      List var1 = PathUtils.list();
      String var2 = this.currentConfig.getMode();
      List var3 = this.currentConfig.getModes();
      var3.clear();
      var3.add("-none-");
      var3.addAll(var1);
      if (var2 != null && var3.contains(var2)) {
         this.currentConfig.setMode(var2);
      } else {
         this.currentConfig.setMode("-none-");
      }
   }

   private static void run8(String var0, String var1, INFO var2) {
      try {
         NotificationUtils.run8(var0, var1, var2);
      } catch (Throwable var4) {
      }
   }
}

