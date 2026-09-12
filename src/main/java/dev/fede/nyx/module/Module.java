package dev.fede.nyx.module;

import dev.fede.nyx.auth.AuthGate;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class Module {
   protected static final MinecraftClient class310 = MinecraftClient.getInstance();
   private final String string;
   private final String string2;
   private final Category category;
   private int intVal;
   private boolean bool;
   private boolean bool2;
   private final List<Setting> list = new ArrayList<>();
   volatile int intVal2 = 0;

   public Module(String name, String description, Category category, int key) {
      this.string = name;
      this.string2 = description;
      this.category = category;
      this.intVal = key;
   }

   public Module(String name, String description, Category category) {
      this(name, description, category, 0);
   }

   public void run() {
   }

   public void run2() {
   }

   public void run3() {
   }

   public void run4(DrawContext var1, float var2) {
   }

   public final void run5(boolean var1) {
      if (this.bool != var1) {
         long var2 = AuthGate.longOf2(
            ((1604004757838216636L | 4630780965522720649L) - ((1604004757016412160L | 821804476L) & 4630780965522720649L) ^ 5509901661285407754L)
               + -5449607144599606134L
               + (-8263755391114978658L ^ -9094858587283304536L ^ -1969981924667219654L)
               + (
                  (
                        -8263755391114978658L
                           & (-5155897417054219073L + 5859784400173364774L - ((-5155897417054219073L & 5859784400173364774L) << 1) ^ -8340360927157154805L)
                     )
                     << 1
               )
               + -6206093304531426349L * -335713501617051643L
         );
         if (var1
            && var2
               != (
                     ((-4918493743042002944L | 101944930588621L) ^ 3860883707911632430L)
                           + ((-4918391798111414323L & 3860883707911632430L) << 1)
                           + -8037657803434635380L
                        ^ -2762136513702188458L
                        ^ 7512536225755125003L
                        ^ -5113174416677072082L - 2805072652233643207L
                  )
                  + ((-9095165893634417273L & 2591738723150987578L) << 1)
                  + 4084500630934008754L) {
            this.intVal2 = ThreadLocalRandom.current().nextInt(3, 11);
         }

         this.bool = var1;
         if (var1) {
            this.run();
         } else {
            this.run2();
         }

         try {
            boolean var4 = class310 != null && class310.player != null && class310.world != null;
            if (var4 && !this.bool2 && NotificationUtils.isEnabled2()) {
               NotificationUtils.run(this.string, var1 ? "Enabled" : "Disabled", var1 ? INFO.UNKNOWN_2 : INFO.UNKNOWN_4, 1500L);
            }
         } catch (Throwable var5) {
         }
      }
   }

   public final void run19() {
      this.run5(!this.bool);
   }

   protected final void run6(Setting... var1) {
      for (Setting var5 : var1) {
         var5.setParent(this);
         this.list.add(var5);
      }
   }

   public List<Setting> getList() {
      return this.list;
   }

   public boolean isEnabled3() {
      return this.bool;
   }

   public String getString() {
      return this.string;
   }

   public String getString2() {
      String var1 = this.getString3();
      return var1 == null ? this.string : this.string + " " + var1;
   }

   public String getString3() {
      return null;
   }

   public String getString4() {
      return this.string2;
   }

   public Category getCategory() {
      return this.category;
   }

   public int getInt() {
      return this.intVal;
   }

   public void run7(int var1) {
      this.intVal = var1;
   }

   public boolean isEnabled() {
      return this.bool2;
   }

   protected void run8(boolean var1) {
      this.bool2 = var1;
   }
}

