package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.module.modules.movement.FreecamModule;
import dev.fede.nyx.module.modules.movement.FreelookModule;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.storage.Chest;
import dev.fede.nyx.storage.Chest$KindUtils;
import dev.fede.nyx.storage.ClientPlayNetworkHandlerMixinUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;

public class StorageESPModule extends Module {
   private final BooleanSetting chest = new BooleanSetting("Chest", true);
   private final BooleanSetting trappedChest = new BooleanSetting("TrappedChest", true);
   private final BooleanSetting barrel = new BooleanSetting("Barrel", true);
   private final BooleanSetting shulker = new BooleanSetting("Shulker", true);
   private final BooleanSetting enderChest = new BooleanSetting("EnderChest", true);
   private final BooleanSetting hopper = new BooleanSetting("Hopper", false);
   private final BooleanSetting dropper = new BooleanSetting("Dropper", false);
   private final BooleanSetting dispenser = new BooleanSetting("Dispenser", false);
   private final BooleanSetting furnace = new BooleanSetting("Furnace", false);
   private final BooleanSetting spawner = new BooleanSetting("Spawner", true);
   private final BooleanSetting vault = new BooleanSetting("Vault", true);
   private final BooleanSetting trialSpawner = new BooleanSetting("TrialSpawner", true);
   private final BooleanSetting decoratedPot = new BooleanSetting("DecoratedPot", true);
   private final BooleanSetting brushable = new BooleanSetting("BrushableBlock", true);
   private final BooleanSetting cauldron = new BooleanSetting("Cauldron", false);
   private final BooleanSetting piston = new BooleanSetting("Piston", true);
   private final BooleanSetting stickyPiston = new BooleanSetting("StickyPiston", true);
   private final BooleanSetting observer = new BooleanSetting("Observer", true);
   private final BooleanSetting comparator = new BooleanSetting("Comparator", true);
   private final BooleanSetting repeater = new BooleanSetting("Repeater", false);
   private final BooleanSetting noteBlock = new BooleanSetting("NoteBlock", false);
   private final BooleanSetting sculkSensor = new BooleanSetting("SculkSensor", true);
   private final BooleanSetting slime = new BooleanSetting("SlimeBlock", false);
   private final BooleanSetting honey = new BooleanSetting("HoneyBlock", false);
   private final BooleanSetting tnt = new BooleanSetting("TNT", true);
   private final BooleanSetting targetBlock = new BooleanSetting("TargetBlock", false);
   private final BooleanSetting redstoneLamp = new BooleanSetting("RedstoneLamp", false);
   private final NumberSetting maxDistance = new NumberSetting("Max Distance", 128.0, 16.0, 512.0, 1.0);
   private final NumberSetting lineWidth = new NumberSetting("Line Width", 1.0, 0.5, 5.0, 0.1);
   private final BooleanSetting throughWalls = new BooleanSetting("Through Walls", true);
   private final BooleanSetting borderOnly = new BooleanSetting("BorderOnly", true);
   private final BooleanSetting filled = new BooleanSetting("Filled", true);
   private final BooleanSetting perKindFill = new BooleanSetting("PerKindFill", true);
   private final ColorSetting fillColor = new ColorSetting("FillColor", 1090503756);
   private final BooleanSetting showTracers = new BooleanSetting("ShowTracers", false);
   private final NumberSetting tracerWidth = new NumberSetting("TracerWidth", 0.5, 0.1, 3.0, 0.1);
   private final ColorSetting tracerColor = new ColorSetting("TracerColor", -1056979892);
   private final NumberSetting tracerMaxDistance = new NumberSetting("TracerMaxDistance", 128.0, 16.0, 512.0, 1.0);
   private final BooleanSetting sticky = new BooleanSetting("StickyMemory", true);
   private static boolean bool = false;
   private static volatile boolean bool2 = false;
   private static volatile boolean bool3 = true;

   public StorageESPModule() {
      super("StorageESP", "Highlights storage blocks (chests, shulkers, spawners...)", Category.RENDER);
      this.run6(
         new Setting[]{
            this.chest,
            this.trappedChest,
            this.barrel,
            this.shulker,
            this.enderChest,
            this.hopper,
            this.dropper,
            this.dispenser,
            this.furnace,
            this.spawner,
            this.vault,
            this.trialSpawner,
            this.decoratedPot,
            this.brushable,
            this.cauldron,
            this.piston,
            this.stickyPiston,
            this.observer,
            this.comparator,
            this.repeater,
            this.noteBlock,
            this.sculkSensor,
            this.slime,
            this.honey,
            this.tnt,
            this.targetBlock,
            this.redstoneLamp,
            this.maxDistance,
            this.lineWidth,
            this.throughWalls,
            this.borderOnly,
            this.filled,
            this.perKindFill,
            this.fillColor,
            this.showTracers,
            this.tracerWidth,
            this.tracerColor,
            this.tracerMaxDistance,
            this.sticky
         }
      );
      this.borderOnly.visibleWhen(this::getBoolean3);
      this.fillColor.visibleWhen(this::getBoolean2);
      this.perKindFill.visibleWhen(this::getBoolean);
      this.tracerWidth.visibleWhen(this.showTracers::getValue);
      this.tracerColor.visibleWhen(this.showTracers::getValue);
      this.tracerMaxDistance.visibleWhen(this.showTracers::getValue);
   }

   @Override
   public void run() {
      bool2 = true;
      bool3 = this.sticky.getValue();
      if (!bool) {
         bool = true;
         ClientChunkEvents.CHUNK_UNLOAD.register(StorageESPModule::run7);
         ClientChunkEvents.CHUNK_LOAD.register(StorageESPModule::run16);
      }

      if (class310.world != null && class310.player != null) {
         int var1 = Math.max((Integer)class310.options.getViewDistance().getValue(), 8);
         int var2 = Math.min(var1, 16);
         int var3 = (int)class310.player.getX() >> 4;
         int var4 = (int)class310.player.getZ() >> 4;

         for (int var5 = -var2; var5 <= var2; var5++) {
            for (int var6 = -var2; var6 <= var2; var6++) {
               ClientPlayNetworkHandlerMixinUtil.enqueueChunk(var3 + var5, var4 + var6);
            }
         }
      }
   }

   @Override
   public void run2() {
      bool2 = false;
      ClientPlayNetworkHandlerMixinUtil.clearQueue();
      Chest$KindUtils.clear();
   }

   @Override
   public void run3() {
      if (class310.world != null && class310.player != null) {
         bool3 = this.sticky.getValue();
         ClientPlayNetworkHandlerMixinUtil.tick(class310);
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null) {
         Vec3d var3 = class310.player.getEntityPos();
         double var4 = this.maxDistance.getValue();
         double var6 = var4 * var4;
         float var8 = this.lineWidth.getValueFloat();
         boolean var9 = this.throughWalls.getValue();
         boolean var10 = this.borderOnly.getValue();
         boolean var11 = this.filled.getValue();
         boolean var12 = this.perKindFill.getValue();
         int var13 = this.fillColor.getValue();
         int var14 = var13 >>> 24 & 0xFF;

         for (Chest var16 : Chest$KindUtils.all()) {
            Chest.Kind var17 = var16.kind();
            if (this.check(var17)) {
               Vec3d var18 = var16.center();
               double var19 = var18.squaredDistanceTo(var3);
               if (!(var19 > var6)) {
                  Box var21 = var16.worldBox();
                  int var22 = var17.color();
                  ListUtils.run5(var21, var22, var8, var9);
                  if (var11) {
                     int var23;
                     if (var12) {
                        var23 = var14 << 24 | var22 & 16777215;
                     } else {
                        var23 = var13;
                     }

                     if (ListUtils.isEnabled8()) {
                        ListUtils.run3(var21, var23, var9);
                     } else {
                        ListUtils.run14(var21.minX, var21.maxY, var21.minZ, var21.maxX, var21.maxZ, var23, var9);
                        ListUtils.run14(var21.minX, var21.minY, var21.minZ, var21.maxX, var21.maxZ, var23, var9);
                     }
                  } else if (!var10) {
                     int var36 = var22 & 16777215 | 671088640;
                     ListUtils.run14(var21.minX, var21.maxY, var21.minZ, var21.maxX, var21.maxZ, var36, var9);
                  }
               }
            }
         }

         if (this.showTracers.getValue()) {
            double var31 = this.tracerMaxDistance.getValue();
            double var32 = var31 * var31;
            int var33 = this.tracerColor.getValue();
            int var20 = var33 >>> 24 & 0xFF;
            boolean var34 = this.perKindFill.getValue();
            float var35 = this.tracerWidth.getValueFloat();
            Vec3d var37 = class310.gameRenderer.getCamera().getCameraPos();
            Vec3d var24;
            if (FreecamModule.bool) {
               double var25 = Math.toRadians(FreecamModule.floatVal);
               double var27 = Math.toRadians(FreecamModule.floatVal2);
               double var29 = Math.cos(var27);
               var24 = new Vec3d(-Math.sin(var25) * var29, -Math.sin(var27), Math.cos(var25) * var29);
            } else if (FreelookModule.bool) {
               double var38 = Math.toRadians(FreelookModule.getFloat());
               double var40 = Math.toRadians(FreelookModule.getFloat2());
               double var42 = Math.cos(var40);
               var24 = new Vec3d(-Math.sin(var38) * var42, -Math.sin(var40), Math.cos(var38) * var42);
            } else {
               var24 = class310.player.getRotationVec(var2);
            }

            Vec3d var39 = var37.add(var24.multiply(1.5));

            for (Chest var41 : Chest$KindUtils.all()) {
               if (this.check(var41.kind())) {
                  Vec3d var28 = var41.center();
                  if (!(var28.squaredDistanceTo(var3) > var32)) {
                     int var43 = var34 ? var20 << 24 | var41.kind().color() & 16777215 : var33;
                     ListUtils.run12(var39, var28, var43, var35, true);
                  }
               }
            }
         }
      }
   }

   private boolean check(Chest.Kind var1) {
      return switch (var1) {
         case CHEST -> this.chest.getValue();
         case TRAPPED_CHEST -> this.trappedChest.getValue();
         case BARREL -> this.barrel.getValue();
         case SHULKER -> this.shulker.getValue();
         case ENDER_CHEST -> this.enderChest.getValue();
         case HOPPER -> this.hopper.getValue();
         case DROPPER -> this.dropper.getValue();
         case DISPENSER -> this.dispenser.getValue();
         case FURNACE, BLAST_FURNACE, SMOKER -> this.furnace.getValue();
         case BREWING_STAND -> this.furnace.getValue();
         case BEACON -> this.vault.getValue();
         case SPAWNER -> this.spawner.getValue();
         case VAULT -> this.vault.getValue();
         case TRIAL_SPAWNER -> this.trialSpawner.getValue();
         case DECORATED_POT -> this.decoratedPot.getValue();
         case BRUSHABLE_BLOCK -> this.brushable.getValue();
         case CAULDRON -> this.cauldron.getValue();
         case PISTON -> this.piston.getValue();
         case STICKY_PISTON -> this.stickyPiston.getValue();
         case OBSERVER -> this.observer.getValue();
         case COMPARATOR -> this.comparator.getValue();
         case REPEATER -> this.repeater.getValue();
         case NOTE_BLOCK -> this.noteBlock.getValue();
         case SCULK_SENSOR -> this.sculkSensor.getValue();
         case SLIME_BLOCK -> this.slime.getValue();
         case HONEY_BLOCK -> this.honey.getValue();
         case TNT -> this.tnt.getValue();
         case TARGET_BLOCK -> this.targetBlock.getValue();
         case REDSTONE_LAMP -> this.redstoneLamp.getValue();
         case OTHER -> false;
         default -> throw new MatchException(null, null);
      };
   }

   @Override
   public String getString3() {
      int var1 = Chest$KindUtils.count();
      return var1 > 0 ? "§7" + var1 : null;
   }

   private static void run16(ClientWorld var0, WorldChunk var1) {
      if (bool2) {
         ClientPlayNetworkHandlerMixinUtil.enqueueChunk(var1.getPos());
      }
   }

   private static void run7(ClientWorld var0, WorldChunk var1) {
      if (bool2) {
         if (!bool3) {
            Chest$KindUtils.onChunkUnload(var1);
         }
      }
   }

   private Boolean getBoolean() {
      return this.filled.getValue();
   }

   private Boolean getBoolean2() {
      return this.filled.getValue();
   }

   private Boolean getBoolean3() {
      return !this.filled.getValue();
   }
}

