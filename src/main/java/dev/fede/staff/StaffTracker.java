package dev.fede.staff;

import dev.fede.module.impl.StaffListModule;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.GameMode;

public final class StaffTracker {
   private static final int SCAN_INTERVAL = 10;
   private StaffListModule module;
   private volatile List<StaffEntry> current = List.of();
   private Set<String> lastNames = new HashSet<>();
   private boolean primed;
   private int ticks;
   private static volatile List<StaffEntry> debugInject;

   public StaffTracker(StaffListModule module) {
      this.module = module;
   }

   public List<StaffEntry> current() {
      List<StaffEntry> inj = debugInject;
      return inj != null ? inj : this.current;
   }

   public void reset() {
      this.lastNames = new HashSet<>();
      this.primed = false;
      this.current = List.of();
      this.ticks = 0;
   }

   public void clear() {
      this.reset();
   }

   public void tick() {
      if (debugInject == null) {
         if (++this.ticks % 10 == 0) {
            this.scan();
         }
      }
   }

   private void scan() {
      MinecraftClient mc = MinecraftClient.getInstance();
      ClientPlayNetworkHandler conn = mc.getNetworkHandler();
      if (conn != null && mc.player != null) {
         StaffDetector.DetectConfig cfg = this.module.detectConfig();
         UUID self = mc.player.getUuid();
         Set<UUID> listed = new HashSet<>();

         for (PlayerListEntry pi : conn.getListedPlayerListEntries()) {
            listed.add(pi.getProfile().id());
         }

         List<StaffEntry> found = new ArrayList<>();

         for (PlayerListEntry info : conn.getPlayerList()) {
            UUID id = info.getProfile().id();
            if (!id.equals(self)) {
               String name = info.getProfile().name();
               if (name != null && !name.isEmpty()) {
                  boolean vanished = info.getGameMode() == GameMode.SPECTATOR || !listed.contains(id);
                  if (!vanished || cfg.showVanished()) {
                     Team team = info.getScoreboardTeam();
                     StaffEntry entry = StaffDetector.classify(
                        name,
                        info.getDisplayName(),
                        team == null ? null : team.getPrefix(),
                        team == null ? null : team.getSuffix(),
                        team == null ? null : team.getName(),
                        vanished,
                        info.getLatency(),
                        cfg
                     );
                     if (entry != null) {
                        found.add(entry);
                     }
                  }
               }
            }
         }

         found.sort(
            Comparator.comparing(StaffEntry::vanished)
               .thenComparing(Comparator.comparingInt(StaffEntry::priority).reversed())
               .thenComparing(ex -> ex.name().toLowerCase(Locale.ROOT))
         );
         Set<String> names = new HashSet<>();

         for (StaffEntry e : found) {
            names.add(e.name());
         }

         if (this.primed) {
            for (StaffEntry e : found) {
               if (!this.lastNames.contains(e.name())) {
                  this.module.onStaffAppear(e);
               }
            }
         }

         this.lastNames = names;
         this.primed = true;
         this.current = List.copyOf(found);
      } else {
         this.current = List.of();
         this.lastNames = new HashSet<>();
         this.primed = false;
      }
   }

   public static void injectForTest(List<StaffEntry> entries) {
      debugInject = entries == null ? null : List.copyOf(entries);
   }

   public static void clearInject() {
      debugInject = null;
   }
}

