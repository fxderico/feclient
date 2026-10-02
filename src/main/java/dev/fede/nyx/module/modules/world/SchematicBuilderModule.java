package dev.fede.nyx.module.modules.world;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * SchematicBuilder — an auto "printer" for Litematica .litematic files.
 *
 * Reads the newest (or a named) .litematic from the game's schematics/ folder,
 * decodes its regions (block-state palette + Litematica's long-straddling bit
 * array), anchors the build at your feet when you enable it, then each tick
 * places any missing block it can currently reach, swapping to the matching
 * hotbar item and placing against a solid neighbour face.
 *
 * MVP scope (honest): it builds the blocks you have in your hotbar, within reach,
 * where there's a face to place against. It does NOT yet rotate block-state
 * properties, pathfind to unreachable blocks, or dodge GrimAC's placement checks
 * — those are the obvious follow-ups. A translucent box shows the next target.
 */
public final class SchematicBuilderModule extends Module {
   private final StringSetting file = new StringSetting("File", "", 64);
   private final NumberSetting blocksPerTick = new NumberSetting("BlocksPerTick", 1.0, 1.0, 16.0, 1.0);
   private final NumberSetting reach = new NumberSetting("Reach", 4.5, 1.0, 6.0, 0.1);
   private final BooleanSetting swing = new BooleanSetting("SwingHand", true);
   private final BooleanSetting onlyReachable = new BooleanSetting("OnlyReachable", true);
   private final BooleanSetting render = new BooleanSetting("RenderGhost", true);
   private final BooleanSetting hud = new BooleanSetting("CountHUD", true);

   // decoded target blocks, in world space (anchored at enable)
   private final List<Target> targets = new ArrayList<>();
   private int placed;
   private int total;
   private String loadedName = "";

   public SchematicBuilderModule() {
      super("SchematicBuilder", "Auto-builds a Litematica .litematic from your schematics/ folder", Category.WORLD);
      this.run6(new Setting[]{this.file, this.blocksPerTick, this.reach, this.swing, this.onlyReachable, this.render, this.hud});
   }

   @Override
   public void run() {
      this.targets.clear();
      this.placed = 0;
      this.total = 0;
      this.loadedName = "";
      if (class310.player == null || class310.world == null) {
         msg("§cJoin a world first.");
         return;
      }
      try {
         load();
      } catch (Throwable t) {
         msg("§cLoad failed: " + t.getClass().getSimpleName() + " " + String.valueOf(t.getMessage()));
         this.targets.clear();
      }
   }

   @Override
   public void run2() {
      this.targets.clear();
   }

   private void load() throws Exception {
      File dir = new File(class310.runDirectory, "schematics");
      if (!dir.isDirectory()) {
         msg("§cNo schematics/ folder at " + dir.getPath());
         return;
      }
      File chosen = null;
      String want = this.file.getValue() == null ? "" : this.file.getValue().trim();
      File[] files = dir.listFiles((d, n) -> n.toLowerCase().endsWith(".litematic"));
      if (files == null || files.length == 0) {
         msg("§cNo .litematic files in schematics/");
         return;
      }
      if (!want.isEmpty()) {
         for (File f : files) {
            if (f.getName().equalsIgnoreCase(want) || f.getName().equalsIgnoreCase(want + ".litematic")) {
               chosen = f;
               break;
            }
         }
         if (chosen == null) {
            msg("§cNo match for \"" + want + "\"; using newest.");
         }
      }
      if (chosen == null) {
         chosen = files[0];
         for (File f : files) {
            if (f.lastModified() > chosen.lastModified()) {
               chosen = f;
            }
         }
      }
      this.loadedName = chosen.getName();

      Path path = chosen.toPath();
      NbtCompound root = NbtIo.readCompressed(path, NbtSizeTracker.ofUnlimitedBytes());
      NbtCompound regions = root.getCompoundOrEmpty("Regions");
      if (regions.getKeys().isEmpty()) {
         msg("§cNo Regions in " + this.loadedName);
         return;
      }

      BlockPos anchor = class310.player.getBlockPos();
      var blockLookup = class310.world.createCommandRegistryWrapper(RegistryKeys.BLOCK);

      for (String regionName : regions.getKeys()) {
         NbtCompound region = regions.getCompoundOrEmpty(regionName);
         NbtCompound size = region.getCompoundOrEmpty("Size");
         int sx = size.getInt("x", 0);
         int sy = size.getInt("y", 0);
         int sz = size.getInt("z", 0);
         int ax = Math.abs(sx);
         int ay = Math.abs(sy);
         int az = Math.abs(sz);
         NbtCompound posC = region.getCompoundOrEmpty("Position");
         int originX = posC.getInt("x", 0);
         int originY = posC.getInt("y", 0);
         int originZ = posC.getInt("z", 0);
         // litematica size can be negative (extends in -dir); normalise the corner
         int baseX = Math.min(originX, originX + sx + (sx < 0 ? 1 : -1));
         int baseY = Math.min(originY, originY + sy + (sy < 0 ? 1 : -1));
         int baseZ = Math.min(originZ, originZ + sz + (sz < 0 ? 1 : -1));

         NbtList paletteTag = region.getListOrEmpty("BlockStatePalette");
         BlockState[] palette = new BlockState[paletteTag.size()];
         for (int i = 0; i < paletteTag.size(); i++) {
            try {
               palette[i] = NbtHelper.toBlockState(blockLookup, paletteTag.getCompoundOrEmpty(i));
            } catch (Throwable t) {
               palette[i] = null;
            }
         }

         long[] data = region.getLongArray("BlockStates").orElse(new long[0]);
         int bits = Math.max(2, 32 - Integer.numberOfLeadingZeros(Math.max(1, palette.length - 1)));
         long mask = (1L << bits) - 1L;
         int volume = ax * ay * az;

         for (int index = 0; index < volume; index++) {
            int stateId = getAt(data, index, bits, mask);
            if (stateId < 0 || stateId >= palette.length) {
               continue;
            }
            BlockState state = palette[stateId];
            if (state == null || state.isAir()) {
               continue;
            }
            // litematica index order: y-major, then z, then x
            int lx = index % ax;
            int lz = (index / ax) % az;
            int ly = index / (ax * az);
            BlockPos world = new BlockPos(anchor.getX() + baseX + lx, anchor.getY() + baseY + ly, anchor.getZ() + baseZ + lz);
            this.targets.add(new Target(world, state));
         }
      }

      this.total = this.targets.size();
      msg("§aLoaded §f" + this.loadedName + " §7(" + this.total + " blocks). Anchored at your feet.");
   }

   // Litematica's bit array packs entries that can straddle two longs.
   private static int getAt(long[] data, int index, int bits, long mask) {
      long bitIndex = (long) index * bits;
      int startLong = (int) (bitIndex >> 6);
      int endLong = (int) (((long) (index + 1) * bits - 1L) >> 6);
      int startOffset = (int) (bitIndex & 63L);
      if (startLong < 0 || startLong >= data.length) {
         return -1;
      }
      if (startLong == endLong) {
         return (int) (data[startLong] >>> startOffset & mask);
      }
      if (endLong >= data.length) {
         return -1;
      }
      int endOffset = 64 - startOffset;
      return (int) ((data[startLong] >>> startOffset | data[endLong] << endOffset) & mask);
   }

   @Override
   public void run3() {
      if (class310.player == null || class310.world == null || this.targets.isEmpty()) {
         return;
      }
      double reachSq = this.reach.getValue() * this.reach.getValue();
      Vec3d eye = class310.player.getEyePos();
      int budget = this.blocksPerTick.getValueInt();
      int done = 0;

      // walk targets; drop ones already satisfied, place reachable ones
      for (int i = 0; i < this.targets.size() && done < budget; i++) {
         Target t = this.targets.get(i);
         BlockState current = class310.world.getBlockState(t.pos);
         if (current.isOf(t.state.getBlock())) {
            continue; // already there (ignore exact property match for MVP)
         }
         if (!current.isAir() && !current.isReplaceable()) {
            continue; // something else occupies it
         }
         if (this.onlyReachable.getValue() && eye.squaredDistanceTo(Vec3d.ofCenter(t.pos)) > reachSq) {
            continue;
         }
         if (tryPlace(t)) {
            done++;
            this.placed++;
         }
      }
   }

   private boolean tryPlace(Target t) {
      int slot = findHotbarSlot(t.state.getBlock());
      if (slot < 0) {
         return false;
      }
      // find a solid neighbour to place against
      for (Direction dir : Direction.values()) {
         BlockPos against = t.pos.offset(dir);
         BlockState neighbour = class310.world.getBlockState(against);
         if (neighbour.isAir() || neighbour.isReplaceable()) {
            continue;
         }
         Direction face = dir.getOpposite();
         Vec3d hit = Vec3d.ofCenter(against).add(face.getOffsetX() * 0.5, face.getOffsetY() * 0.5, face.getOffsetZ() * 0.5);
         if (class310.player.getEyePos().squaredDistanceTo(hit) > this.reach.getValue() * this.reach.getValue()) {
            continue;
         }
         int prev = class310.player.getInventory().getSelectedSlot();
         class310.player.getInventory().setSelectedSlot(slot);
         BlockHitResult hr = new BlockHitResult(hit, face, against, false);
         class310.interactionManager.interactBlock(class310.player, Hand.MAIN_HAND, hr);
         if (this.swing.getValue()) {
            class310.player.swingHand(Hand.MAIN_HAND);
         }
         class310.player.getInventory().setSelectedSlot(prev);
         return true;
      }
      return false;
   }

   private int findHotbarSlot(Block block) {
      for (int i = 0; i < 9; i++) {
         ItemStack stack = class310.player.getInventory().getStack(i);
         if (!stack.isEmpty() && stack.getItem() instanceof BlockItem bi && bi.getBlock() == block) {
            return i;
         }
      }
      return -1;
   }

   @Override
   public void run4(DrawContext ctx, float tickDelta) {
      if (this.render.getValue() && class310.player != null && class310.world != null && !this.targets.isEmpty()) {
         int shown = 0;
         for (Target t : this.targets) {
            if (shown >= 64) {
               break;
            }
            BlockState cur = class310.world.getBlockState(t.pos);
            if (cur.isOf(t.state.getBlock())) {
               continue;
            }
            Box b = new Box(t.pos.getX(), t.pos.getY(), t.pos.getZ(), t.pos.getX() + 1.0, t.pos.getY() + 1.0, t.pos.getZ() + 1.0);
            ListUtils.run5(b, 0x6600E5FF, 1.0F, true);
            shown++;
         }
      }
      if (this.hud.getValue() && class310.textRenderer != null && this.total > 0) {
         int remaining = countRemaining();
         String s = "Schem  " + (this.total - remaining) + "/" + this.total;
         int w = class310.textRenderer.getWidth(s) + 16;
         ctx.fill(6, 188, 6 + w, 207, -535817448);
         ctx.fill(6, 188, 9, 207, -4523);
         ctx.drawText(class310.textRenderer, s, 16, 193, -4523, false);
      }
   }

   private int countRemaining() {
      int n = 0;
      for (Target t : this.targets) {
         if (!class310.world.getBlockState(t.pos).isOf(t.state.getBlock())) {
            n++;
         }
      }
      return n;
   }

   private static void msg(String s) {
      if (class310.player != null) {
         class310.player.sendMessage(Text.literal("§b[Schem] §r" + s), false);
      }
   }

   @Override
   public String getString3() {
      return this.total > 0 ? "§7" + (this.total - countRemaining()) + "/" + this.total : null;
   }

   private static final class Target {
      final BlockPos pos;
      final BlockState state;

      Target(BlockPos pos, BlockState state) {
         this.pos = pos;
         this.state = state;
      }
   }
}
