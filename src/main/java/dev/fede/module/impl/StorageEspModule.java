package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.render.StorageEspRenderer;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.IconListSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class StorageEspModule extends Module {
   public final ModeSetting mode = this.addSetting(new ModeSetting("Mode", "Box style — hollow outline or translucent fill", "Outline", "Outline", "Full"));
   public final SliderSetting range = this.addSetting(new SliderSetting("Range", "Max distance a container is highlighted", 128.0, 16.0, 256.0, 8.0));
   public final SliderSetting highlightAlpha = this.addSetting(new SliderSetting("Highlight Alpha", "Box opacity (0-255)", 200.0, 0.0, 255.0, 1.0));
   public final BooleanSetting tracers = this.addSetting(new BooleanSetting("Tracers", "Draw lines from the crosshair to each container", false));
   public final IconListSetting containers = this.addSetting(new IconListSetting("Containers", "Which container types to highlight"));
   public BooleanSetting hideOpened2;
   private final Set<BlockPos> interactedBlocks = new HashSet<>();

   public StorageEspModule() {
      super("StorageESP", "Highlights chests, barrels, shulkers", Category.RENDER);

      for (StorageEspModule.StorageType type : StorageEspModule.StorageType.values()) {
         this.containers.add(type.key, type.label, type.icon, type.defaultEnabled, type.defaultColor);
      }

      this.hideOpened2 = this.addSetting(new BooleanSetting("Hide Opened", "Don't highlight containers you've already opened", false));
   }

   public boolean isTypeEnabled(StorageEspModule.StorageType type) {
      return this.containers.isEnabled(type.key);
   }

   public int colorFor(StorageEspModule.StorageType type) {
      return this.containers.color(type.key);
   }

   public boolean hideOpened() {
      return this.hideOpened2.get();
   }

   public boolean isInteracted(int x, int y, int z) {
      return !this.interactedBlocks.isEmpty() && this.interactedBlocks.contains(new BlockPos(x, y, z));
   }

   public void trackInteraction(BlockPos pos) {
      if (pos != null) {
         MinecraftClient mc = MinecraftClient.getInstance();
         if (mc.world != null) {
            this.interactedBlocks.add(pos.toImmutable());
            BlockEntity be = mc.world.getBlockEntity(pos);
            if (be instanceof ChestBlockEntity) {
               BlockState state = mc.world.getBlockState(pos);
               if (state.getBlock() instanceof ChestBlock) {
                  ChestType ct = (ChestType)state.get(ChestBlock.CHEST_TYPE);
                  if (ct != ChestType.SINGLE) {
                     Direction facing = (Direction)state.get(ChestBlock.FACING);
                     BlockPos other = pos.offset(ct == ChestType.LEFT ? facing.rotateYClockwise() : facing.rotateYCounterclockwise());
                     this.interactedBlocks.add(other);
                  }
               }
            }
         }
      }
   }

   @Override
   protected void onEnable() {
      this.interactedBlocks.clear();
   }

   @Override
   public void onTick() {
      StorageEspRenderer.scan(this);
   }

   @Override
   protected void onDisable() {
      StorageEspRenderer.clear();
   }

   public enum StorageType {
      CHEST("chest", "Chest", Items.CHEST, -22016, true),
      TRAPPED("trapped", "Trapped Chest", Items.TRAPPED_CHEST, -65536, true),
      ENDER("ender", "Ender Chest", Items.ENDER_CHEST, -8912641, true),
      SHULKER("shulker", "Shulker Box", Items.SHULKER_BOX, -47873, true),
      BARREL("barrel", "Barrel", Items.BARREL, -7842560, true),
      SPAWNER("spawner", "Spawner", Items.SPAWNER, -16711936, true),
      HOPPER("hopper", "Hopper", Items.HOPPER, -7829368, false),
      FURNACE("furnace", "Furnace", Items.FURNACE, -7566196, false);

      final String key;
      final String label;
      final Item icon;
      final int defaultColor;
      final boolean defaultEnabled;

      private StorageType(String key, String label, Item icon, int defaultColor, boolean defaultEnabled) {
         this.key = key;
         this.label = label;
         this.icon = icon;
         this.defaultColor = defaultColor;
         this.defaultEnabled = defaultEnabled;
      }

      private static StorageEspModule.StorageType[] $values() {
         return new StorageEspModule.StorageType[]{CHEST, TRAPPED, ENDER, SHULKER, BARREL, SPAWNER, HOPPER, FURNACE};
      }
   }
}

