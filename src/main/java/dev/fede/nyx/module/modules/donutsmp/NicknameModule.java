package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;

public class NicknameModule extends Module {
   public static NicknameModule nicknameModule;
   private final StringSetting displayName = new StringSetting("DisplayName", "", 32);

   public NicknameModule() {
      super("Nickname", "Local-only display-name replacer (helper for HUD/nametag code)", Category.DONUTSMP);
      this.run6(new Setting[]{this.displayName});
      nicknameModule = this;
   }

   public static String stringOf(String var0, String var1) {
      NicknameModule var2 = nicknameModule;
      if (var2 != null && var2.isEnabled3()) {
         String var3 = var2.displayName.getValue();
         return var3 != null && !var3.isEmpty() ? var3 : var0;
      } else {
         return var0;
      }
   }

   @Override
   public String getString() {
      return this.displayName.getValue();
   }
}

