# feclient

> ⚠️ **This is a merge project.** All credit goes to the original authors of the source
> clients it's built from — a native base client, CodeEngine, and WaterSRC. This repo
> exists solely as a personal merge/maintenance project and makes no claim of original
> authorship over that source material.

A merged Fabric client for **Minecraft 1.21.11**. Three separate source clients unified
under one `ModuleManager`, one ClickGUI, one config system.

---

## Structure

```
feclient/
├── src/main/java/dev/fede/
│   ├── FeClient.java              ← entrypoint
│   ├── module/
│   │   ├── Module.java            ← base class (native)
│   │   ├── Category.java          ← COMBAT/MOVEMENT/PLAYER/RENDER/WORLD/DONUT/ADDONS/CLIENT
│   │   ├── ModuleManager.java     ← registers every module from all three sources
│   │   ├── NyxModuleBridge.java   ← wraps CodeEngine modules into the GUI, mirrors their
│   │   │                            settings live (Boolean/Number/Mode/String/Color/Bind)
│   │   ├── WaterModuleBridge.java ← wraps WaterSRC modules into the GUI, same idea
│   │   ├── impl/                  ← native modules
│   │   └── Modules.java           ← small inline modules (ClickGUI, HUD, Spotify, etc.)
│   ├── nyx/                       ← CodeEngine source (repackaged dev.nyx → dev.fede.nyx)
│   │   ├── module/modules/        ← CodeEngine modules, by category package
│   │   ├── mixin/                 ← CodeEngine mixins
│   │   ├── setting/                ← CodeEngine setting types
│   │   ├── render/                 ← CodeEngine render utils
│   │   └── util/                   ← CodeEngine utilities
│   ├── water/                     ← WaterSRC source (repackaged com.water → dev.fede.water)
│   │   ├── module/modules/        ← WaterSRC modules
│   │   ├── mixin/                  ← WaterSRC mixins
│   │   └── utils/renderer/         ← Water's shader pipeline (blur/glow/arc/liquid glass)
│   ├── gui/                       ← NanoVG ClickGUI
│   ├── hud/                       ← HUD components
│   ├── config/                    ← config system
│   ├── render/                    ← rendering utils (ESP, XRay seed cave-diff, etc.)
│   └── spotify/                   ← Spotify now-playing bridge (Windows SMTC via PowerShell)
└── src/main/resources/
    ├── fabric.mod.json
    ├── feclient.mixins.json       ← native mixins
    ├── feclient.nyx.mixins.json   ← CodeEngine mixins
    └── feclient.sodium.mixins.json← Sodium-specific mixins
```

Module names and current categories are always accurate straight from
`ModuleManager.java` and each module's own `super(...)` call — that's the source of
truth, not a table in this file that will drift out of date.

---

## Building

**Requirements:** JDK 21, Gradle (via the included wrapper)

This project used to target multiple Minecraft versions via
[Stonecutter](https://stonecutter.kikugie.dev/). It's pinned to **1.21.11 only** for now
— the other version nodes (`26.1.x`/`26.2`/`26.3`) are commented out in
`settings.gradle` because their Loom config is mid-migration and doesn't currently
evaluate. Re-enabling them later is a two-line uncomment away, not a rewrite.

```bash
cd feclient
./gradlew build
```

Output: `versions/1.21.11/build/libs/feclient-1.0.0+1.21.11.jar`

Drop it in your Fabric `mods/` folder like any other mod.

---

## Notes

- **GUI**: NanoVG ClickGUI (RSHIFT to open by default)
- **CodeEngine modules** are bridged via `NyxModuleBridge` — they appear in the ClickGUI
  with live settings (sliders, dropdowns, toggles all read/write straight through to the
  real CodeEngine setting field) and respond to keybinds exactly like native modules
- **WaterSRC modules** are bridged via `WaterModuleBridge` — same idea
- WaterSRC's **shader pipeline** (blur, glow, arc, liquid glass) lives under
  `dev.fede.water.utils.renderer.*`
- CodeEngine's **ImGui** system is bundled (used by a handful of render-side modules —
  RegionMap, ESP, ItemESP, Nametags, Radar, KeystrokeHUD, notification toasts — for
  direct draw-list rendering), separate from the primary ClickGUI
- The `nyx/auth/` licensing package (`AuthGate`) is kept for compatibility but every
  check in it is a no-op — it always reports "valid," never phones home, doesn't gate
  anything. Its siblings (AntiDebug, AntiTamper, AuthService, AuthState, Integrity) have
  been removed outright.
- Duplicate module names between sources (e.g. both a native and a CodeEngine
  `AutoCrystal`) are deduplicated by name at registration — the native implementation
  wins when both exist, the CodeEngine one is silently skipped
- `AimAssistModule` loads its logic through `ProtectedContent`, a licensing-gated
  class loader left over from the original source's paid-tier system — the real
  implementation (`dev.fede.secured.AimAssistLogic`) was never included, so the module
  used to be fully visible and configurable in the GUI while doing nothing at all.
  That class now exists: a raycast-first, angle-fallback target lock (respects
  Players/Hostiles/Passive/Invisibles/Wall Check/FOV/Range) that STICKS to whichever
  entity it acquires — it doesn't re-target just because the crosshair drifts off —
  until the target dies, leaves range, or the module gets toggled off. Aims at the
  torso by default (`Target` setting), eases in per `Speed`/`Smoothness` rather than
  snapping, and the pixel math it hands back to the mouse hook is derived from the
  game's own sensitivity-curve constants so the pull speed doesn't silently change
  with the player's mouse sensitivity setting.

---

*feClient — built by fede*
