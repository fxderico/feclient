package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.KeybindSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.util.BlockHelper;
import dev.fede.util.InventoryHelper;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class AutoCrystalModule extends Module {
   public final KeybindSetting activateKey = this.addSetting(new KeybindSetting("Activate Key", "Hold to place + break (default RMB)", 1));
   public final SliderSetting breakDelay = this.addSetting(new SliderSetting("Break Delay", "Ticks between crystal breaks", 1.0, 0.0, 20.0, 1.0, "t"));
   public final SliderSetting placeDelay = this.addSetting(new SliderSetting("Place Delay", "Ticks between crystal places", 1.0, 0.0, 20.0, 1.0, "t"));
   public final BooleanSetting autoObsidian = this.addSetting(new BooleanSetting("Auto Obsidian", "Lay an obsidian base when you're not aiming at one", true));
   public final SliderSetting obsidianDelay = this.addSetting(
      new SliderSetting("Obsidian Delay", "Ticks between obsidian placements", 2.0, 0.0, 20.0, 1.0, "t")
   );
   public final SliderSetting range = this.addSetting(
      new SliderSetting("Break Range", "Max distance to a crystal you'll break (vanilla reach ~3)", 3.0, 1.0, 6.0, 0.5, "m")
   );
   public final BooleanSetting switchBack = this.addSetting(new BooleanSetting("Switch Back", "Return to your original hotbar slot when released", true));
   private int breakCooldown;
   private int placeCooldown;
   private int obsidianCooldown;
   private int savedSlot = -1;
   private int lastBrokenId = -1;
   private boolean wasActive;

   public AutoCrystalModule() {
      super("Auto Crystal", "Hold RMB to auto place + break crystals, laying an obsidian base when needed", Category.COMBAT);
   }

   @Override
   protected void onEnable() {
      this.resetAll();
   }

   @Override
   protected void onDisable() {
      this.restoreSlot();
      this.resetAll();
   }

   @Override
   public void onTick() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null && mc.interactionManager != null && mc.world != null && mc.currentScreen == null && this.isTriggerHeld(mc)) {
         this.wasActive = true;
         this.tickCooldowns();
         if (this.activateKey.get() == 1) {
            mc.options.useKey.setPressed(false);
         }

         HitResult hitResult = mc.crosshairTarget;
         if (hitResult instanceof EntityHitResult ehr && ehr.getEntity() instanceof EndCrystalEntity crystal) {
            if (this.tryBreak(mc, crystal)) {
               this.lastBrokenId = crystal.getId();
            }
         } else {
            if (hitResult instanceof BlockHitResult hit && hit.getType() == Type.BLOCK) {
               BlockPos clicked = hit.getBlockPos();
               if (isCrystalBase(mc.world, clicked)) {
                  this.serviceBase(mc, hit, clicked);
               } else if (this.autoObsidian.get()) {
                  this.layObsidian(mc, hit, clicked);
               }
            }
         }
      } else {
         this.endActiveHold();
      }
   }

   private void serviceBase(MinecraftClient mc, BlockHitResult hit, BlockPos base) {
      EndCrystalEntity crystal = crystalOn(mc.world, base);
      if (crystal != null) {
         if (this.tryBreak(mc, crystal)) {
            this.lastBrokenId = crystal.getId();
         }
      } else {
         this.lastBrokenId = -1;
         if (this.placeCooldown == 0 && canFitCrystal(mc.world, base)) {
            Hand hand = this.equip(mc, Items.END_CRYSTAL);
            if (hand != null) {
               BlockHelper.interactBlock(hit, hand, true);
               this.placeCooldown = this.placeDelay.getInt();
            }
         }
      }
   }

   private void layObsidian(MinecraftClient mc, BlockHitResult hit, BlockPos clicked) {
      if (this.obsidianCooldown == 0) {
         BlockPos target = mc.world.getBlockState(clicked).isReplaceable() ? clicked : clicked.offset(hit.getSide());
         if (mc.world.getBlockState(target).isReplaceable()) {
            if (canFitCrystal(mc.world, target)) {
               Hand hand = this.equip(mc, Items.OBSIDIAN);
               if (hand != null) {
                  BlockHelper.interactBlock(hit, hand, true);
                  this.obsidianCooldown = Math.max(1, this.obsidianDelay.getInt());
               }
            }
         }
      }
   }

   private boolean tryBreak(MinecraftClient mc, EndCrystalEntity crystal) {
      if (this.breakCooldown != 0) {
         return false;
      } else if (crystal.getId() == this.lastBrokenId) {
         return false;
      } else if (mc.player.getEyePos().distanceTo(crystal.getEntityPos()) > this.range.get()) {
         return false;
      } else {
         mc.interactionManager.attackEntity(mc.player, crystal);
         mc.player.swingHand(Hand.MAIN_HAND);
         this.breakCooldown = this.breakDelay.getInt();
         return true;
      }
   }

   @Nullable
   private Hand equip(MinecraftClient mc, Item item) {
      ClientPlayerEntity player = mc.player;
      if (player.getMainHandStack().isOf(item)) {
         return Hand.MAIN_HAND;
      } else if (player.getOffHandStack().isOf(item)) {
         return Hand.OFF_HAND;
      } else {
         int slot = InventoryHelper.getHotbarSlot(item);
         if (slot < 0) {
            return null;
         } else {
            if (this.savedSlot < 0) {
               this.savedSlot = player.getInventory().getSelectedSlot();
            }

            InventoryHelper.selectHotbarSlot(slot);
            return Hand.MAIN_HAND;
         }
      }
   }

   private static boolean isCrystalBase(World level, BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      return state.isOf(Blocks.OBSIDIAN) || state.isOf(Blocks.BEDROCK);
   }

   @Nullable
   private static EndCrystalEntity crystalOn(World level, BlockPos base) {
      BlockPos up = base.up();
      Box region = new Box(up.getX() + 0.125, up.getY() - 0.1, up.getZ() + 0.125, up.getX() + 0.875, up.getY() + 2.5, up.getZ() + 0.875);
      List<EndCrystalEntity> found = level.getEntitiesByClass(EndCrystalEntity.class, region, e -> true);
      return found.isEmpty() ? null : found.get(0);
   }

   private static boolean canFitCrystal(World level, BlockPos base) {
      BlockPos up = base.up();
      if (!level.getBlockState(up).isAir()) {
         return false;
      } else {
         Box box = new Box(up.getX(), up.getY(), up.getZ(), up.getX() + 1.0, up.getY() + 2.0, up.getZ() + 1.0);
         return level.getNonSpectatingEntities(Entity.class, box).isEmpty();
      }
   }

   private void tickCooldowns() {
      if (this.breakCooldown > 0) {
         this.breakCooldown--;
      }

      if (this.placeCooldown > 0) {
         this.placeCooldown--;
      }

      if (this.obsidianCooldown > 0) {
         this.obsidianCooldown--;
      }
   }

   private boolean isTriggerHeld(MinecraftClient mc) {
      int key = this.activateKey.get();
      if (key == -1) {
         return false;
      } else {
         return key <= 7 ? GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), key) == 1 : InputUtil.isKeyPressed(mc.getWindow(), key);
      }
   }

   private void endActiveHold() {
      if (this.wasActive) {
         this.restoreSlot();
         this.breakCooldown = 0;
         this.placeCooldown = 0;
         this.obsidianCooldown = 0;
         this.lastBrokenId = -1;
         this.wasActive = false;
      }
   }

   private void restoreSlot() {
      if (this.switchBack.get() && this.savedSlot >= 0) {
         InventoryHelper.selectHotbarSlot(this.savedSlot);
      }

      this.savedSlot = -1;
   }

   private void resetAll() {
      this.breakCooldown = 0;
      this.placeCooldown = 0;
      this.obsidianCooldown = 0;
      this.savedSlot = -1;
      this.lastBrokenId = -1;
      this.wasActive = false;
   }
}

