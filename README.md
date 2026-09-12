# feclient

> ⚠️ **This is a merge / skid.** All credit goes to the original authors of the source clients.  
> Sources: **67Client**, **CodeEngine (nyx)**, **WaterSRC**, **Zenith DLC 2.0**.  
> This repo exists solely as a personal merge project and makes no claim of original authorship.

Merged cheat client for **Minecraft 1.21.1 (Fabric)**.  

---

## Structure

```
feclient/
├── src/main/java/dev/fede/
│   ├── FeClient.java              ← main entrypoint
│   ├── module/
│   │   ├── Module.java            ← base class (67Client)
│   │   ├── Category.java          ← COMBAT/MOVEMENT/PLAYER/RENDER/WORLD/DONUT/ADDONS/CLIENT
│   │   ├── ModuleManager.java     ← registers ALL modules from all 3 clients
│   │   ├── NyxModuleBridge.java   ← wraps CodeEngine modules into the GUI
│   │   ├── WaterModuleBridge.java ← wraps WaterSRC modules into the GUI
│   │   ├── impl/                  ← 67Client native modules (~50)
│   │   └── Modules.java           ← placeholder/config modules
│   ├── nyx/                       ← CodeEngine source (repackaged dev.nyx → dev.fede.nyx)
│   │   ├── module/modules/        ← ~120 CodeEngine modules
│   │   ├── mixin/                 ← ~62 CodeEngine mixins
│   │   ├── setting/               ← CodeEngine setting types
│   │   ├── render/                ← CodeEngine render utils
│   │   └── util/                  ← CodeEngine utilities
│   ├── water/                     ← WaterSRC source (repackaged com.water → dev.fede.water)
│   │   ├── module/modules/        ← ~60 WaterSRC modules
│   │   ├── mixin/                 ← WaterSRC mixins
│   │   └── utils/renderer/        ← Water's shader pipeline (blur/glow/arc)
│   ├── gui/                       ← 67Client NanoVG ClickGUI
│   ├── hud/                       ← HUD components
│   ├── config/                    ← config system
│   └── render/                    ← rendering utils
└── src/main/resources/
    ├── fabric.mod.json
    ├── feclient.mixins.json       ← 67Client mixins
    └── feclient.nyx.mixins.json   ← CodeEngine mixins
```

---

## Module count

| Source     | Modules |
|-----------|---------|
| 67Client  | ~55     |
| CodeEngine| ~120    |
| WaterSRC  | ~45     |
| **Total** | **~220**|

---

## Categories

| Category | Contents |
|----------|----------|
| COMBAT   | AutoCrystal, AutoTotem, KillAura, Reach, Velocity, AimAssist, Triggerbot, Backtrack, AntiKB, Criticals, Hitbox, MaceBomber, MaceSwap, AnchorMacro, DoubleAnchor, AutoArmor, ChestStealer, ShieldBreaker, ElytraSwap, HoverTotem, SpearSwap, AutoDoubleHand, SingleAnchor, AutoLog, MaceAura |
| MOVEMENT | Sprint, Speed, Fly, NoFall, NoSlow, Step, Spider, AirJump, IceSpeed, ElytraFly, Jesus, Strafe, HighJump, LongJump, InventoryMove, AntiVoid, ChunkSpoof, EagleAura, Freecam, FreeLook, AutoWalk |
| PLAYER   | FastUse, FastPlace, AutoClicker, AutoTPA, CoordSnapper, SwingSpeed, ChatMacro, AutoEat, AutoFish, AutoTool, AntiAFK, AutoDrop, AutoRespawn, MiddleClick, NoJumpDelay, PortalGodMode, SwingAnimation, AutoMine, HomeSetter, TabDetector, TunnelBaseFinder, SkinChanger |
| RENDER   | PlayerESP, StorageESP, BlockESP, Freecam, Fullbright, Zoom, NameTags, HealthTags, Tracers, Chams, XRay, Outlines, CornerBoxESP, OutlineESP, Radar, ItemESP, VoidESP, ArrowESP, BedESP, CauldronESP, PortalESP, PearlESP, HoleESP, StashFinder, SpawnerESP, MovementTrails, ProjectileArc, ItemPhysics, ViewModel, MotionBlur, HeatMapChunkRadar, HoveredContainerPreview, SpectatorDetector, ExtraESP, NoRender, SpawnerNotifier, ClearWorld, NoHurtCam, JumpCircles, HitParticles, CustomCrosshair, CustomFov, CustomGlint, MobESP, BlockEntityESP, RegionMap |
| WORLD    | Nuker, Scaffold, AutoBridge, AutoTunnel, AutoMLG, AutoSmelt, AutoStore, AutoTree, TimerSpeed, InfoOrb, RtpBaseFinder, ChunkKeeper, AutoRender |
| DONUT    | ChunkFinder, SpawnerProtect, FakeRoles, FakeStats, GambleRigger, AntiTrap, AutoSell, AutoSpawnerSell, ItemDropper, PrimeChunkFinder, SeedChunkFinder, BlockEntityDebug, LightFinder, PlayerChunks, NetheriteFinder, RegionMap, StaffDetector, BoneDropper, TuffChunkV2, ActivityDebug, SuspiciousChunkFinder, Nickname, StaffList, FakePay, ArmorTrimHider |
| ADDONS   | CustomAccessories, SkinProtect, NameProtect, WeatherNotifier, BlockGlow, BlockOutline, GlintCustomiser, BreakParticles, DragonWings, KillEffects, PlayerParticles, SpotifyHUD |
| CLIENT   | ClickGUI, HUD, Spotify, ConfigShare, DiscordRPC, CustomTitle, KeystrokeHUD, TargetHUD, TabGUI, ChatFilter, ThemeSelector, ChromaXP, ClickSounds, WaterPlus, Friends |

---

## Building

**Requirements:** JDK 21, Gradle 8+

```bash
cd "C:\Users\fedes\Desktop\feclient"
gradlew build
```

Output: `build/libs/feclient-1.0.0.jar`

Install into Fabric like any other mod.

---

## Notes

- **GUI**: 67Client's NanoVG ClickGUI (RSHIFT to open by default)
- **CodeEngine modules** are bridged via `NyxModuleBridge` — they appear in the ClickGUI and respond to keybinds exactly like native modules
- **WaterSRC modules** are bridged via `WaterModuleBridge` — same
- WaterSRC's **shader pipeline** (blur, glow, arc, liquid glass) is available under `dev.fede.water.utils.renderer.*`
- CodeEngine's **ImGui** system is bundled as a nested jar — it's available but the primary GUI is 67Client's ClickGUI
- The `nyx/auth/` package is kept but its license checks are no-ops in this build
- Duplicate module names (e.g. both clients had an AutoCrystal) — 67Client's is primary, CodeEngine's is bridged with the same name; both will appear in the list

---

*feClient — built by fede*
