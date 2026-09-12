package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

public class SpectatorDetectorModule extends Module {
   private static final int intVal = 96;
   private final StringSetting onlyStaffRanks = new StringSetting("OnlyStaffRanks", "[Owner],[Admin],[Mod],[Helper],[Staff]", 96);
   private final BooleanSetting alertOnEnter = new BooleanSetting("AlertOnEnter", true);
   private final BooleanSetting playSound = new BooleanSetting("PlaySound", true);
   private final ColorSetting alertColor = new ColorSetting("AlertColor", -50373);
   private static final int intVal2 = -1072623343;
   private static final int intVal3 = -16777216;
   private static final int intVal4 = -1;
   private static final int intVal5 = 4;
   private static final int intVal6 = 4;
   private static final int intVal7 = 4;
   private static final int intVal8 = 3;
   private static final int intVal9 = 1;
   private static final double doubleVal = 48.0;
   private final Set<String> set = new HashSet<>();
   private final List<SpectatorDetectorModule.Inner1> list = new ArrayList<>();

   public SpectatorDetectorModule() {
      super("SpectatorDetector", "Warns when staff or invisible spectators appear in the tab list", Category.RENDER);
      this.run6(new Setting[]{this.onlyStaffRanks, this.alertOnEnter, this.playSound, this.alertColor});
   }

   @Override
   public void run() {
      this.set.clear();
      this.list.clear();
   }

   @Override
   public void run2() {
      this.set.clear();
      this.list.clear();
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null) {
         ClientPlayNetworkHandler var1 = class310.getNetworkHandler();
         if (var1 != null) {
            List var2 = listOf(this.onlyStaffRanks.getValue());
            UUID var3 = class310.player.getUuid();
            Set var4 = this.getSet();
            Collection var5 = var1.getPlayerList();
            ArrayList var6 = new ArrayList(Math.max(4, var5.size() / 4));

            for (PlayerListEntry var8 : (java.util.List<PlayerListEntry>)var5) {
               if (var8 != null) {
                  UUID var9 = var8.getProfile().id();
                  if (var9 != null && !var9.equals(var3)) {
                     String var10 = stringOf(var8);
                     GameMode var11 = var8.getGameMode();
                     boolean var12 = check(var10, var2);
                     boolean var13 = (var11 == GameMode.CREATIVE || var11 == GameMode.SPECTATOR) && !var4.contains(var9);
                     if (var12 || var13) {
                        String var14;
                        if (var12 && var13) {
                           var14 = "STAFF+HIDDEN";
                        } else if (var12) {
                           var14 = "STAFF";
                        } else {
                           var14 = "HIDDEN " + stringOf2(var11);
                        }

                        var6.add(new SpectatorDetectorModule.Inner1(var9, var10, stringOf2(var11), var14));
                        if (this.alertOnEnter.getValue()) {
                           String var15 = var9 + "|" + stringOf2(var11) + "|" + var10;
                           if (this.set.add(var15)) {
                              NotificationUtils.run8("SpectatorDetector", "null: null", INFO.UNKNOWN_3);
                              if (this.playSound.getValue()) {
                                 this.run5();
                              }
                           }
                        }
                     }
                  }
               }
            }

            this.list.clear();
            this.list.addAll(var6);
         }
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.textRenderer != null && !this.list.isEmpty()) {
         TextRenderer var3 = class310.textRenderer;
         Objects.requireNonNull(var3);
         int var5 = var3.getWidth("Spectators / Staff");
         ArrayList var6 = new ArrayList(this.list.size());

         for (SpectatorDetectorModule.Inner1 var8 : this.list) {
            String var9 = "[" + var8.string + "] " + var8.name + "  §7(" + var8.reason + ")";
            var6.add(var9);
            int var10 = var3.getWidth("[" + var8.string + "] " + var8.name + "  (" + var8.reason + ")");
            if (var10 > var5) {
               var5 = var10;
            }
         }

         int var18 = 1 + var6.size();
         int var19 = var5 + 8;
         int var20 = var18 * 9 + Math.max(0, var18 - 1) + 6;
         byte var21 = 4;
         int var12 = var21 + var19;
         int var13 = 4 + var20;
         var1.fill(3, 3, var12 + 1, var13 + 1, -16777216);
         var1.fill(var21, 4, var12, var13, -1072623343);
         byte var15 = 7;
         var1.drawTextWithShadow(var3, Text.literal("Spectators / Staff"), 8, var15, this.alertColor.getValue());
         var15 = 17;

         for (String var17 : (java.util.List<String>)var6) {
            var1.drawTextWithShadow(var3, Text.literal(var17), 8, var15, -1);
            var15 += 10;
         }
      }
   }

   private static List<String> listOf(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String[] var1 = var0.split(",");
         ArrayList var2 = new ArrayList(var1.length);

         for (String var6 : var1) {
            String var7 = var6.trim();
            if (!var7.isEmpty()) {
               var2.add(var7.toLowerCase(Locale.ROOT));
            }
         }

         return var2;
      } else {
         return List.of();
      }
   }

   private static boolean check(String var0, List<String> var1) {
      if (!var1.isEmpty() && var0 != null && !var0.isEmpty()) {
         String var2 = var0.toLowerCase(Locale.ROOT);

         for (String var4 : var1) {
            if (var2.contains(var4)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static String stringOf(PlayerListEntry var0) {
      Text var1 = var0.getDisplayName();
      if (var1 != null) {
         String var2 = var1.getString();
         if (var2 != null && !var2.isEmpty()) {
            return var2;
         }
      }

      return var0.getProfile().name();
   }

   private Set<UUID> getSet() {
      HashSet var1 = new HashSet();
      if (class310.world != null && class310.player != null) {
         for (Entity var5 : class310.world.getEntities()) {
            if (var5 instanceof PlayerEntity var6 && var6.getEntityPos().squaredDistanceTo(class310.player.getEntityPos()) <= 2304.0) {
               var1.add(var6.getUuid());
            }
         }

         return var1;
      } else {
         return var1;
      }
   }

   private static String stringOf2(GameMode var0) {
      if (var0 == null) {
         return "?";
      } else {
         return switch (var0) {
            case SURVIVAL -> "S";
            case CREATIVE -> "C";
            case ADVENTURE -> "A";
            case SPECTATOR -> "SP";
            default -> "?";
         };
      }
   }

   private void run5() {
      try {
         // sound suppressed
      } catch (Throwable var2) {
      }
   }

   @Override
   public String getString3() {
      int var1 = this.list.size();
      return var1 == 0 ? null : "§c" + var1;
   }

record Inner1(UUID id, String name, String string, String reason) {


   public String getString() {
      return this.string;
   }
}
}

