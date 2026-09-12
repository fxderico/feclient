package dev.fede.nyx.module;

import dev.fede.nyx.auth.AuthGate;
import dev.fede.nyx.module.modules.addons.BlockGlowModule;
import dev.fede.nyx.module.modules.addons.BlockOutlineModule;
import dev.fede.nyx.module.modules.addons.BreakParticlesModule;
import dev.fede.nyx.module.modules.addons.DragonWingsModule;
import dev.fede.nyx.module.modules.addons.GlintCustomiserModule;
import dev.fede.nyx.module.modules.addons.HitParticlesModule;
import dev.fede.nyx.module.modules.addons.KillEffectsModule;
import dev.fede.nyx.module.modules.addons.PlayerParticlesModule;
import dev.fede.nyx.module.modules.addons.SpotifyHUDModule;
import dev.fede.nyx.module.modules.client.ChatFilter;
import dev.fede.nyx.module.modules.client.ChromaXPModule;
import dev.fede.nyx.module.modules.client.ClickGUIModule;
import dev.fede.nyx.module.modules.client.ClickSounds;
import dev.fede.nyx.module.modules.client.CustomTitleModule;
import dev.fede.nyx.module.modules.client.HUDModule;
import dev.fede.nyx.module.modules.client.KeystrokeHUDModule;
import dev.fede.nyx.module.modules.client.RPCModule;
import dev.fede.nyx.module.modules.client.TabGUIModule;
import dev.fede.nyx.module.modules.client.TargetHUDModule;
import dev.fede.nyx.module.modules.client.ThemeSelectorModule;
import dev.fede.nyx.module.modules.combat.AntiKnockbackModule;
import dev.fede.nyx.module.modules.combat.AutoArmor;
import dev.fede.nyx.module.modules.combat.AutoClickerModule;
import dev.fede.nyx.module.modules.combat.AutoCrystalModule;
import dev.fede.nyx.module.modules.combat.AutoLogModule;
import dev.fede.nyx.module.modules.combat.AutoTotemModule;
import dev.fede.nyx.module.modules.combat.BacktrackModule;
import dev.fede.nyx.module.modules.combat.ChestStealerModule;
import dev.fede.nyx.module.modules.combat.CriticalsModule;
import dev.fede.nyx.module.modules.combat.HitboxesModule;
import dev.fede.nyx.module.modules.combat.KillAura;
import dev.fede.nyx.module.modules.combat.MaceAuraModule;
import dev.fede.nyx.module.modules.combat.ReachModule;
import dev.fede.nyx.module.modules.combat.TriggerBot;
import dev.fede.nyx.module.modules.combat.VelocityModule;
import dev.fede.nyx.module.modules.config.ConfigsModule;
import dev.fede.nyx.module.modules.donutsmp.AntiTrapModule;
import dev.fede.nyx.module.modules.donutsmp.AutoSell;
import dev.fede.nyx.module.modules.donutsmp.AutoSpawnerSellModule;
import dev.fede.nyx.module.modules.donutsmp.BlockEntityDebugModule;
import dev.fede.nyx.module.modules.donutsmp.ChunkFinderModule;
import dev.fede.nyx.module.modules.donutsmp.HoleESPModule;
import dev.fede.nyx.module.modules.donutsmp.ItemDropperModule;
import dev.fede.nyx.module.modules.donutsmp.LightFinderModule;
import dev.fede.nyx.module.modules.donutsmp.NetheriteFinderModule;
import dev.fede.nyx.module.modules.donutsmp.NicknameModule;
import dev.fede.nyx.module.modules.donutsmp.PlayerChunksModule;
import dev.fede.nyx.module.modules.donutsmp.PrimeChunkFinderModule;
import dev.fede.nyx.module.modules.donutsmp.RegionMap;
import dev.fede.nyx.module.modules.donutsmp.SeedChunkFinder;
import dev.fede.nyx.module.modules.donutsmp.SpawnerProtectModule;
import dev.fede.nyx.module.modules.movement.AirJumpModule;
import dev.fede.nyx.module.modules.movement.AntiVoidModule;
import dev.fede.nyx.module.modules.movement.AutoWalkModule;
import dev.fede.nyx.module.modules.movement.ChunkSpoof;
import dev.fede.nyx.module.modules.movement.EagleAuraModule;
import dev.fede.nyx.module.modules.movement.ElytraFlyModule;
import dev.fede.nyx.module.modules.movement.FlyModule;
import dev.fede.nyx.module.modules.movement.FreecamModule;
import dev.fede.nyx.module.modules.movement.FreelookModule;
import dev.fede.nyx.module.modules.movement.HighJumpModule;
import dev.fede.nyx.module.modules.movement.IceSpeedModule;
import dev.fede.nyx.module.modules.movement.InventoryMoveModule;
import dev.fede.nyx.module.modules.movement.JesusModule;
import dev.fede.nyx.module.modules.movement.LongJumpModule;
import dev.fede.nyx.module.modules.movement.NoFallModule;
import dev.fede.nyx.module.modules.movement.NoSlowModule;
import dev.fede.nyx.module.modules.movement.SpeedModule;
import dev.fede.nyx.module.modules.movement.SpiderModule;
import dev.fede.nyx.module.modules.movement.SprintModule;
import dev.fede.nyx.module.modules.movement.StepModule;
import dev.fede.nyx.module.modules.movement.StrafeModule;
import dev.fede.nyx.module.modules.player.AntiAFKModule;
import dev.fede.nyx.module.modules.player.AutoDropModule;
import dev.fede.nyx.module.modules.player.AutoEatModule;
import dev.fede.nyx.module.modules.player.AutoFish;
import dev.fede.nyx.module.modules.player.AutoRespawnModule;
import dev.fede.nyx.module.modules.player.AutoToolModule;
import dev.fede.nyx.module.modules.player.FastPlaceModule;
import dev.fede.nyx.module.modules.player.FastUseModule;
import dev.fede.nyx.module.modules.player.MiddleClickModule;
import dev.fede.nyx.module.modules.player.NoJumpDelayModule;
import dev.fede.nyx.module.modules.player.PortalGodModeModule;
import dev.fede.nyx.module.modules.player.SwingAnimationModule;
import dev.fede.nyx.module.modules.render.ArrowESPModule;
import dev.fede.nyx.module.modules.render.BedESPModule;
import dev.fede.nyx.module.modules.render.CauldronESPModule;
import dev.fede.nyx.module.modules.render.Chams;
import dev.fede.nyx.module.modules.render.ClearWorldModule;
import dev.fede.nyx.module.modules.render.CornerBoxESPModule;
import dev.fede.nyx.module.modules.render.ESP;
import dev.fede.nyx.module.modules.render.FullbrightModule;
import dev.fede.nyx.module.modules.render.HealthTagsModule;
import dev.fede.nyx.module.modules.render.HeatMapChunkRadarModule;
import dev.fede.nyx.module.modules.render.HoveredContainerPreviewModule;
import dev.fede.nyx.module.modules.render.ItemESPModule;
import dev.fede.nyx.module.modules.render.ItemPhysicsModule;
import dev.fede.nyx.module.modules.render.MovementTrailsModule;
import dev.fede.nyx.module.modules.render.NametagsModule;
import dev.fede.nyx.module.modules.render.NoHurtCamModule;
import dev.fede.nyx.module.modules.render.OutlineESPModule;
import dev.fede.nyx.module.modules.render.OutlinesModule;
import dev.fede.nyx.module.modules.render.PortalESP;
import dev.fede.nyx.module.modules.render.ProjectileArcModule;
import dev.fede.nyx.module.modules.render.RadarModule;
import dev.fede.nyx.module.modules.render.RaidPlannerModule;
import dev.fede.nyx.module.modules.render.SpawnerESPModule;
import dev.fede.nyx.module.modules.render.SpectatorDetectorModule;
import dev.fede.nyx.module.modules.render.StashFinder;
import dev.fede.nyx.module.modules.render.StorageESPModule;
import dev.fede.nyx.module.modules.render.TracersModule;
import dev.fede.nyx.module.modules.render.ViewModelModule;
import dev.fede.nyx.module.modules.render.VoidESPModule;
import dev.fede.nyx.module.modules.render.XRay;
import dev.fede.nyx.module.modules.render.ZoomModule;
import dev.fede.nyx.module.modules.render.donutsmp.SusChunkFinderModule;
import dev.fede.nyx.module.modules.world.AutoBridgeModule;
import dev.fede.nyx.module.modules.world.AutoMLGModule;
import dev.fede.nyx.module.modules.world.AutoSmeltModule;
import dev.fede.nyx.module.modules.world.AutoStoreModule;
import dev.fede.nyx.module.modules.world.AutoTreeModule;
import dev.fede.nyx.module.modules.world.AutoTunnel;
import dev.fede.nyx.module.modules.world.ChunkKeeperModule;
import dev.fede.nyx.module.modules.world.InfoOrb;
import dev.fede.nyx.module.modules.world.NukerModule;
import dev.fede.nyx.module.modules.world.RtpBaseFinder;
import dev.fede.nyx.module.modules.world.ScaffoldModule;
import dev.fede.nyx.module.modules.world.TimerSpeedModule;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.DrawContext;

public final class ModuleManager {
   private final List<Module> list = new ArrayList<>();

   public void run() {
      this.run2(new KillAura());
      this.run2(new TriggerBot());
      this.run2(new AutoClickerModule());
      this.run2(new AutoCrystalModule());
      this.run2(new AutoTotemModule());
      this.run2(new MaceAuraModule());
      this.run2(new ChestStealerModule());
      this.run2(new AutoArmor());
      this.run2(new ReachModule());
      this.run2(new CriticalsModule());
      this.run2(new VelocityModule());
      this.run2(new AntiKnockbackModule());
      this.run2(new BacktrackModule());
      this.run2(new HitboxesModule());
      this.run2(new AutoLogModule());
      this.run2(new SprintModule());
      this.run2(new SpeedModule());
      this.run2(new FlyModule());
      this.run2(new FreecamModule());
      this.run2(new FreelookModule());
      this.run2(new ChunkSpoof());
      this.run2(new AirJumpModule());
      this.run2(new AntiVoidModule());
      this.run2(new EagleAuraModule());
      this.run2(new ElytraFlyModule());
      this.run2(new HighJumpModule());
      this.run2(new IceSpeedModule());
      this.run2(new InventoryMoveModule());
      this.run2(new JesusModule());
      this.run2(new LongJumpModule());
      this.run2(new NoFallModule());
      this.run2(new NoSlowModule());
      this.run2(new SpiderModule());
      this.run2(new StepModule());
      this.run2(new StrafeModule());
      this.run2(new AutoWalkModule());
      this.run2(new AntiAFKModule());
      this.run2(new AutoEatModule());
      this.run2(new AutoFish());
      this.run2(new AutoToolModule());
      this.run2(new AutoDropModule());
      this.run2(new AutoRespawnModule());
      this.run2(new FastPlaceModule());
      this.run2(new FastUseModule());
      this.run2(new MiddleClickModule());
      this.run2(new NoJumpDelayModule());
      this.run2(new PortalGodModeModule());
      this.run2(new SwingAnimationModule());
      this.run2(new NukerModule());
      this.run2(new ScaffoldModule());
      this.run2(new AutoBridgeModule());
      this.run2(new AutoTunnel());
      this.run2(new AutoMLGModule());
      this.run2(new AutoSmeltModule());
      this.run2(new AutoStoreModule());
      this.run2(new AutoTreeModule());
      this.run2(new TimerSpeedModule());
      this.run2(new InfoOrb());
      this.run2(new RtpBaseFinder());
      this.run2(new ChunkKeeperModule());
      this.run2(new ESP());
      this.run2(new NoHurtCamModule());
      this.run2(new FullbrightModule());
      this.run2(new StorageESPModule());
      this.run2(new StashFinder());
      this.run2(new SusChunkFinderModule());
      this.run2(new SpawnerESPModule());
      this.run2(new MovementTrailsModule());
      this.run2(new ProjectileArcModule());
      this.run2(new VoidESPModule());
      this.run2(new ItemESPModule());
      this.run2(new Chams());
      this.run2(new CornerBoxESPModule());
      this.run2(new OutlinesModule());
      this.run2(new OutlineESPModule());
      this.run2(new TracersModule());
      this.run2(new RadarModule());
      this.run2(new NametagsModule());
      this.run2(new HealthTagsModule());
      this.run2(new ArrowESPModule());
      this.run2(new XRay());
      this.run2(new ClearWorldModule());
      this.run2(new ItemPhysicsModule());
      this.run2(new ViewModelModule());
      this.run2(new ZoomModule());
      this.run2(new CauldronESPModule());
      this.run2(new BedESPModule());
      this.run2(new PortalESP());
      this.run2(new HeatMapChunkRadarModule());
      this.run2(new RaidPlannerModule());
      this.run2(new HoveredContainerPreviewModule());
      this.run2(new SpectatorDetectorModule());
      this.run2(new HUDModule());
      this.run2(new ClickGUIModule());
      this.run2(new ConfigsModule());
      this.run2(new RPCModule());
      this.run2(new CustomTitleModule());
      this.run2(new KeystrokeHUDModule());
      this.run2(new TargetHUDModule());
      this.run2(new TabGUIModule());
      this.run2(new ChatFilter());
      this.run2(new ThemeSelectorModule());
      this.run2(new ChromaXPModule());
      this.run2(new ClickSounds());
      this.run2(new NicknameModule());
      this.run2(new AutoSell());
      this.run2(new AutoSpawnerSellModule());
      this.run2(new AntiTrapModule());
      this.run2(new ItemDropperModule());
      this.run2(new SpawnerProtectModule());
      this.run2(new PrimeChunkFinderModule());
      this.run2(new SeedChunkFinder());
      this.run2(new BlockEntityDebugModule());
      this.run2(new LightFinderModule());
      this.run2(new ChunkFinderModule());
      this.run2(new PlayerChunksModule());
      this.run2(new HoleESPModule());
      this.run2(new NetheriteFinderModule());
      this.run2(new RegionMap());
      this.run2(new PlayerParticlesModule());
      this.run2(new BlockGlowModule());
      this.run2(new BlockOutlineModule());
      this.run2(new GlintCustomiserModule());
      this.run2(new HitParticlesModule());
      this.run2(new BreakParticlesModule());
      this.run2(new DragonWingsModule());
      this.run2(new SpotifyHUDModule());
      this.run2(new KillEffectsModule());
      this.list.sort(ModuleManager::intOf);
   }

   public void run2(Module var1) {
      this.list.add(var1);
   }

   public List<Module> getList() {
      return this.list;
   }

   public List<Module> listOf(Category var1) {
      ArrayList var2 = new ArrayList();

      for (Module var4 : this.list) {
         if (var4.getCategory() == var1) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public Module moduleOf(String var1) {
      for (Module var3 : this.list) {
         String var4 = var3.getString();
         if (var4 != null && var4.equalsIgnoreCase(var1)) {
            return var3;
         }
      }

      return null;
   }

   public <T extends Module> T moduleOf2(Class<T> var1) {
      for (Module var3 : this.list) {
         if (var1.isInstance(var3)) {
            return (T)var3;
         }
      }

      return null;
   }

   public List<Module> getList2() {
      ArrayList var1 = new ArrayList();

      for (Module var3 : this.list) {
         if (var3.isEnabled3() && !var3.isEnabled()) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public void run19() {
      long var1 = AuthGate.longOf2(
         -3422585741523350903L
            + 2666338812178162145L
            + (2599399728744360644L + -3949627212233036619L - ((2599399728744360644L & -3949627212233036619L) << 1) ^ 5471379938523795610L)
      );
      boolean var3 = var1
         == (-4739761716533812204L ^ (4824520453420071402L - 4333091673098724192L) * (35075122919329388L ^ 1435732774640025552L ^ -958190864739999375L))
            + ((-4739761716533812204L & 5997077816759561346L) << 1)
            + (
               3095125188204428896L + 2389576297733067386L - ((3095125188204428896L & (5329933507047258344L ^ 7556689097156356754L)) << 1)
                  ^ -2738896019664049742L
            );

      for (Module var5 : this.list) {
         if (var5.isEnabled3()) {
            if (!var3 || var5.intVal2 > 0) {
               if (var5.intVal2 > 0) {
                  var5.intVal2--;
               }

               if (ThreadLocalRandom.current().nextInt(4) != 0) {
                  continue;
               }
            }

            try {
               var5.run3();
            } catch (Throwable var7) {
            }
         }
      }
   }

   public void run4(DrawContext var1, float var2) {
      long var3 = AuthGate.longOf2(
         (7563699457033838592L | 8067L) + 5822559294571241366L + (-5880548572536382908L + 2387304481136370975L + -8855961524904952571L) * 4107121646880347705L
      );
      boolean var5 = var3
         == (
               (8157355931307343872L | 27512292643553L | 2689778161267000804L)
                     + (6374472894055163225L - -1782910549544824200L & (-4177132482394129402L ^ -7331324873752854083L ^ 8724101403753281631L))
                     + 7533161428489921962L
                  ^ 83201708675035422L
            )
            + (
               (
                     (
                           4837996882689226950L + 3146974560083371743L - ((4837996882689226950L & 3146974560083371743L) << 1)
                              ^ 1119828207028670201L
                              ^ -8633612475119209943L - -1145854177435597926L
                        )
                        & (
                           (
                                    -5216901210979081150L + -4881457122885982557L - ((-5216901210979081150L & -4881457122885982557L) << 1)
                                          ^ -7121334838195516463L
                                       | -2720364014689949372L
                                 )
                                 - (-7569516536006457040L & -2720364014689949372L)
                              ^ 5795675331040714721L + 4257242623563262606L + -4438222224093001989L
                        )
                  )
                  << 1
            )
            + 5199572019610396573L;

      for (Module var7 : this.list) {
         if (var7.isEnabled3() && (var5 && var7.intVal2 <= 0 || ThreadLocalRandom.current().nextInt(4) == 0)) {
            try {
               var7.run4(var1, var2);
            } catch (Throwable var9) {
            }
         }
      }
   }

   public void run6(int var1) {
      if (var1 != 0) {
         for (Module var3 : this.list) {
            if (var3.getInt() == var1) {
               var3.run19();
            }
         }
      }
   }

   private static int intOf(Module var0, Module var1) {
      String s0 = var0.getString();
      String s1 = var1.getString();
      if (s0 == null && s1 == null) return 0;
      if (s0 == null) return 1;
      if (s1 == null) return -1;
      return s0.compareToIgnoreCase(s1);
   }
}

