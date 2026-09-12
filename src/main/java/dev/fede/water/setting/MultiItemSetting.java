package dev.fede.water.setting;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class MultiItemSetting extends Setting<Set<String>> {
   private final List<ItemOption> i;

   public MultiItemSetting(String name, ItemOption... options) {
      super(name, new LinkedHashSet<>());
      this.i = List.of(options);
   }

   public void setValue(Set<String> value) {
      LinkedHashSet var2 = new LinkedHashSet();
      if (value != null) {
         for (String var3 : value) {
            if (var3 != null) {
               for (ItemOption var5 : this.i) {
                  if (var5.value().equalsIgnoreCase(var3)) {
                     var2.add(var5.value());
                     break;
                  }
               }
            }
         }
      }

      super.setValue(var2);
   }
}

