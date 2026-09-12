package dev.fede.water.module.modules.client;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import dev.fede.water.setting.Setting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public final class Friends extends Module {
   private static Friends field_a_1;
   private final Setting<String> field_a_2 = new Setting<>("Names", "");
   private final Setting<Boolean> b = new Setting<>("Anti Triggerbot", true);
   private final Setting<Boolean> c = new Setting<>("ESP Color", true);
   private final Setting<Boolean> d = new Setting<>("Auto Log", true);
   private final Setting<Boolean> e = new Setting<>("Spawner Protect", true);
   private final Setting<Color> f = new Setting<>("Friend Color", new Color(0, 200, 255));

   public Friends() {
      super("Friends", Category.e);
      this.addSetting(this.field_a_2);
      this.addSetting(this.b);
      this.addSetting(this.c);
      this.addSetting(this.d);
      this.addSetting(this.e);
      this.addSetting(this.f);
      field_a_1 = this;
   }

   public static boolean method_a_1(String name) {
      if (field_a_1 != null && field_a_1.isEnabled() && name != null && !name.isEmpty()) {
         name = name.trim().toLowerCase(Locale.ROOT);

         for (String var2 : method_a_2(field_a_1.field_a_2.getValue())) {
            if (var2.equalsIgnoreCase(name)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean method_a_1() {
      return field_a_1 != null && field_a_1.b.getValue();
   }

   public static boolean b() {
      return field_a_1 != null && field_a_1.c.getValue();
   }

   public static boolean c() {
      return field_a_1 != null && field_a_1.d.getValue();
   }

   public static boolean d() {
      return field_a_1 != null && field_a_1.e.getValue();
   }

   public static Color method_a_2() {
      if (field_a_1 == null) {
         return new Color(0, 200, 255);
      } else {
         Color var0 = field_a_1.f.getValue();
         if (var0 == null) {
            return new Color(0, 200, 255);
         } else {
            return var0.getAlpha() == 0 ? new Color(var0.getRed(), var0.getGreen(), var0.getBlue(), 255) : var0;
         }
      }
   }

   private static List<String> method_a_2(String raw) {
      if (raw != null && !raw.isBlank()) {
         raw = raw.replace('\n', ',').replace('\r', ',');
         LinkedHashSet var1 = new LinkedHashSet();

         for (String var4 : raw.split(",")) {
            var4 = var4 == null ? "" : var4.trim();
            if (!var4.isEmpty()) {
               var1.add(var4.toLowerCase(Locale.ROOT));
            }
         }

         return new ArrayList<>(var1);
      } else {
         return List.of();
      }
   }
}

