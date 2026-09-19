package dev.fede.module.impl;

import dev.fede.FeClient;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.settings.StringSetting;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SpawnerProtectModule extends Module {
   public final SliderSetting targetStackCount = this.addSetting(
      new SliderSetting("Stacks To Deposit", "How many full stacks of spawners to mine before stashing.", 3.0, 1.0, 30.0, 1.0)
   );
   public final SliderSetting scanRange = this.addSetting(
      new SliderSetting("Trigger Range", "Horizontal distance a stranger triggers the routine.", 64.0, 16.0, 128.0, 1.0, "m")
   );
   public final SliderSetting rotationSpeed = this.addSetting(
      new SliderSetting("Rotation Speed", "Degrees per tick the look direction is nudged toward its target.", 15.0, 1.0, 30.0, 1.0)
   );
   public final BooleanSetting detectBlockUpdates = this.addSetting(
      new BooleanSetting("Detect Block Updates", "Detects distant players by their out-of-render block break/place packets.", true)
   );
   public final StringSetting whitelist = this.addSetting(new StringSetting("Whitelist", "Comma-separated names to ignore.", "", 256, "Steve, Alex"));
   public final SliderSetting breakRange = this.addSetting(
      new SliderSetting("Break Range", "How close a spawner must be before it is broken.", 5.5, 1.0, 8.0, 0.5, "m")
   );
   public final BooleanSetting doubleCheck = this.addSetting(
      new BooleanSetting("Double Check", "Requires two distant breaks within the window before triggering.", true)
   );
   public final SliderSetting doubleCheckWindow = this.addSetting(
      new SliderSetting("Double Check Window", "Time window for the double check.", 60.0, 1.0, 600.0, 1.0, "s")
   );
   public final StringSetting webhookUrl = this.addSetting(
      new StringSetting("Webhook URL", "Optional Discord webhook for alerts.", "", 256, "https://discord.com/api/webhooks/...")
   );
   private final MinecraftClient mc = MinecraftClient.getInstance();
   private SpawnerProtectModule.State currentState = SpawnerProtectModule.State.WAITING_FOR_STRANGER;
   private BlockPos targetBlock = null;
   private BlockPos targetChest = null;
   private int chestTick = 0;
   private int breakCooldown = 0;
   private int lagWaitTicks = 0;
   private boolean strangerDetected = false;
   private final Set<Integer> verifiedPlayers = new HashSet<>();
   private int blockBreakCounter = 0;
   private long lastBreakTimestamp = 0L;
   private int shopSequence = 0;
   private int shopTick = 0;
   private int miningTicks = 0;

   public SpawnerProtectModule() {
      super("SpawnerProtect", "Auto-salvages your spawners when a stranger approaches, then logs out.", Category.DONUT);
      this.doubleCheckWindow.visibleWhen(this.doubleCheck::get);
   }

   @Override
   protected void onEnable() {
      this.resetModule();
   }

   @Override
   protected void onDisable() {
      this.stopMovement();
      this.updateSneak(false);
   }

   private void resetModule() {
      this.currentState = SpawnerProtectModule.State.WAITING_FOR_STRANGER;
      this.strangerDetected = false;
      this.lagWaitTicks = 0;
      this.verifiedPlayers.clear();
      this.blockBreakCounter = 0;
      this.lastBreakTimestamp = 0L;
      this.resetState();
   }

   private void resetState() {
      this.targetChest = null;
      this.targetBlock = null;
      this.chestTick = 0;
      this.breakCooldown = 0;
      this.miningTicks = 0;
   }

   public boolean isTriggered() {
      return this.strangerDetected;
   }

   public String phase() {
      return this.currentState.name();
   }

   public boolean detectBlockUpdatesEnabled() {
      return this.detectBlockUpdates.get();
   }

   public void onBlockDestructionPacket(int breakerId, BlockPos pos) {
      ClientPlayerEntity player = this.mc.player;
      ClientWorld level = this.mc.world;
      if (!this.strangerDetected && player != null && level != null && !this.isNearSpawn()) {
         if (breakerId != player.getId()) {
            Entity breaker = level.getEntityById(breakerId);
            if (breaker instanceof PlayerEntity p) {
               if (this.isWhitelisted(p.getName().getString())) {
                  return;
               }
            } else if (breaker == null) {
               for (PlayerEntity px : level.getPlayers()) {
                  if (px.getEyePos().distanceTo(Vec3d.ofCenter(pos)) < 8.0 && this.isWhitelisted(px.getName().getString())) {
                     return;
                  }
               }
            }

            double dx = player.getX() - pos.getX();
            double dz = player.getZ() - pos.getZ();
            double horizontalDist = Math.sqrt(dx * dx + dz * dz);
            if (horizontalDist <= this.scanRange.get()) {
               this.strangerDetected = true;
               this.currentState = SpawnerProtectModule.State.WORKING;
               String name = breaker != null ? breaker.getName().getString() : "Invisible/Anti-ESP";
               this.warn("\ud83d\udea8 PACKET DETECT: " + name + " started breaking! (Horizontal: " + (int)horizontalDist + "m)");
               this.sendWebhook(
                  "\ud83d\udea8 **PACKET DETECT:** `" + name + "` started breaking! (Horizontal: " + (int)horizontalDist + "m, Y: " + pos.getY() + ")."
               );
            }
         }
      }
   }

   public void onServerBlockUpdate(BlockPos pos, BlockState newState, boolean multi) {
      ClientPlayerEntity player = this.mc.player;
      ClientWorld level = this.mc.world;
      if (!this.strangerDetected && player != null && level != null && !this.isNearSpawn()) {
         for (PlayerEntity p : level.getPlayers()) {
            if (p.getEyePos().distanceTo(Vec3d.ofCenter(pos)) < 8.0 && this.isWhitelisted(p.getName().getString())) {
               return;
            }
         }

         if (pos.getY() > -64 && newState.isAir()) {
            BlockState oldState = level.getBlockState(pos);
            if (!oldState.isAir() && !(oldState.getBlock() instanceof ShulkerBoxBlock)) {
               double distToPlayer = player.getEyePos().distanceTo(Vec3d.ofCenter(pos));
               if (!(distToPlayer < 6.0)) {
                  double dx = player.getX() - pos.getX();
                  double dz = player.getZ() - pos.getZ();
                  double horizontalDist = Math.sqrt(dx * dx + dz * dz);
                  if (!(horizontalDist > this.scanRange.get())) {
                     if (this.doubleCheck.get()) {
                        long now = System.currentTimeMillis();
                        if (now - this.lastBreakTimestamp > this.doubleCheckWindow.getInt() * 1000L) {
                           this.blockBreakCounter = 0;
                        }

                        this.blockBreakCounter++;
                        this.lastBreakTimestamp = now;
                        if (this.blockBreakCounter < 2) {
                           return;
                        }
                     }

                     this.strangerDetected = true;
                     this.currentState = SpawnerProtectModule.State.WORKING;
                     String reason = multi ? "multi-block break" : "block break";
                     this.warn("\ud83d\udea8 REMOTE DETECT: a block broke nearby! (Horizontal: " + (int)horizontalDist + "m, Y: " + pos.getY() + ")");
                     this.sendWebhook(
                        "\ud83d\udea8 **REMOTE DETECT ("
                           + reason
                           + "):** an invisible or far-away player broke a block! (Horizontal: "
                           + (int)horizontalDist
                           + "m, Y: "
                           + pos.getY()
                           + ")"
                     );
                  }
               }
            }
         }
      }
   }

   @Override
   public void onTick() {
      ClientPlayerEntity player = this.mc.player;
      ClientWorld level = this.mc.world;
      if (player != null && level != null) {
         if (this.isNearSpawn()) {
            if (this.strangerDetected) {
               this.resetModule();
            }
         } else {
            if (!this.strangerDetected) {
               for (PlayerEntity p : level.getPlayers()) {
                  if (p != player && !this.isWhitelisted(p.getName().getString())) {
                     double dx = player.getX() - p.getX();
                     double dz = player.getZ() - p.getZ();
                     double horizontalDist = Math.sqrt(dx * dx + dz * dz);
                     if (!(horizontalDist > this.scanRange.get())) {
                        boolean verified = this.verifiedPlayers.contains(p.getId());
                        if (!verified && p.isUsingItem()) {
                           verified = true;
                           this.verifiedPlayers.add(p.getId());
                        }

                        if (verified) {
                           this.strangerDetected = true;
                           this.currentState = SpawnerProtectModule.State.WORKING;
                           this.warn("⚠ PLAYER SPOTTED (verified): " + p.getName().getString() + " (Horizontal: " + (int)horizontalDist + "m)");
                           this.sendWebhook(
                              "⚠ **PLAYER SPOTTED (verified):** `"
                                 + p.getName().getString()
                                 + "` (Horizontal: "
                                 + (int)horizontalDist
                                 + "m, Y: "
                                 + p.getBlockY()
                                 + ")!"
                           );
                           break;
                        }
                     }
                  }
               }
            }

            if (!this.strangerDetected) {
               for (Entity entity : level.getEntities()) {
                  if (entity.getType() == EntityType.ENDER_PEARL
                     && !(
                        entity instanceof ProjectileEntity proj
                           && proj.getOwner() instanceof PlayerEntity owner
                           && this.isWhitelisted(owner.getName().getString())
                     )) {
                     double dx = player.getX() - entity.getX();
                     double dz = player.getZ() - entity.getZ();
                     double horizontalDist = Math.sqrt(dx * dx + dz * dz);
                     if (horizontalDist <= this.scanRange.get()) {
                        this.strangerDetected = true;
                        this.currentState = SpawnerProtectModule.State.WORKING;
                        this.warn("⚠ ENDER PEARL SPOTTED! (Horizontal: " + (int)horizontalDist + "m)");
                        this.sendWebhook("⚠ **ENDER PEARL SPOTTED!** Someone threw a pearl. (Horizontal: " + (int)horizontalDist + "m)!");
                        break;
                     }
                  }
               }
            }

            if (this.currentState != SpawnerProtectModule.State.WAITING_FOR_STRANGER) {
               if (this.currentState != SpawnerProtectModule.State.OPENING_CHEST
                  && this.currentState != SpawnerProtectModule.State.DEPOSITING_ITEMS
                  && this.currentState != SpawnerProtectModule.State.BUYING_ECHEST) {
                  this.updateSneak(true);
               }

               this.handleRotation();
               switch (this.currentState) {
                  case WORKING:
                     this.handleWorking();
                     break;
                  case GOING_TO_CHEST:
                     this.handleGoingToChest();
                     break;
                  case OPENING_CHEST:
                     this.handleOpeningChest();
                     break;
                  case DEPOSITING_ITEMS:
                     this.handleDepositing();
                     break;
                  case FINAL_EXIT:
                     this.handleFinalExit();
                     break;
                  case BUYING_ECHEST:
                     this.handleBuyingEChest();
                     break;
                  case PLACING_ECHEST:
                     this.handlePlacingEChest();
               }
            }
         }
      }
   }

   private void handleRotation() {
      ClientPlayerEntity player = this.mc.player;
      ClientWorld level = this.mc.world;
      if (player != null && level != null) {
         Vec3d targetPos = null;
         if (this.currentState == SpawnerProtectModule.State.WORKING) {
            ItemEntity dropped = this.findDroppedSpawner();
            if (dropped != null) {
               targetPos = dropped.getEntityPos();
            } else {
               if (this.targetBlock == null
                  || level.getBlockState(this.targetBlock).getBlock() != Blocks.SPAWNER
                  || player.getBlockPos().getSquaredDistance(this.targetBlock) > 256.0) {
                  this.targetBlock = this.findRandomBlock(Blocks.SPAWNER, 16);
                  this.miningTicks = 0;
               }

               if (this.targetBlock != null) {
                  targetPos = Vec3d.ofCenter(this.targetBlock);
               }
            }
         } else if ((this.currentState == SpawnerProtectModule.State.GOING_TO_CHEST || this.currentState == SpawnerProtectModule.State.OPENING_CHEST)
            && this.targetChest != null) {
            targetPos = Vec3d.ofCenter(this.targetChest);
         }

         if (targetPos != null) {
            this.smoothLook(targetPos);
         }
      }
   }

   private void smoothLook(Vec3d target) {
      ClientPlayerEntity player = this.mc.player;
      if (player != null) {
         Vec3d eyes = player.getEyePos();
         double dx = target.x - eyes.x;
         double dy = target.y - eyes.y;
         double dz = target.z - eyes.z;
         double dist = Math.sqrt(dx * dx + dz * dz);
         float targetYaw = (float)Math.toDegrees(Math.atan2(-dx, dz));
         float targetPitch = (float)(-Math.toDegrees(Math.atan2(dy, dist)));
         float step = this.rotationSpeed.getFloat();
         player.setYaw(player.getYaw() + MathHelper.clamp(MathHelper.wrapDegrees(targetYaw - player.getYaw()), -step, step));
         player.setPitch(player.getPitch() + MathHelper.clamp(MathHelper.wrapDegrees(targetPitch - player.getPitch()), -step, step));
      }
   }

   private void handleWorking() {
      ClientPlayerEntity player = this.mc.player;
      ClientWorld level = this.mc.world;
      if (player != null && level != null) {
         if (player.currentScreenHandler != player.playerScreenHandler) {
            player.closeHandledScreen();
         }

         ItemEntity dropped = this.findDroppedSpawner();
         if (dropped != null) {
            this.lagWaitTicks = 0;
            this.stopBreaking();
            this.mc.options.forwardKey.setPressed(true);
         } else if (this.getSpawnerCount() >= this.targetStackCount.getInt() * 64) {
            this.goToChest();
         } else {
            if (this.targetBlock == null
               || level.getBlockState(this.targetBlock).getBlock() != Blocks.SPAWNER
               || player.getBlockPos().getSquaredDistance(this.targetBlock) > 256.0) {
               this.targetBlock = this.findRandomBlock(Blocks.SPAWNER, 16);
               this.miningTicks = 0;
            }

            if (this.targetBlock == null) {
               this.stopMovement();
               if (this.lagWaitTicks < 40) {
                  this.lagWaitTicks++;
               } else if (this.getSpawnerCount() > 0) {
                  this.goToChest();
               } else {
                  this.currentState = SpawnerProtectModule.State.FINAL_EXIT;
                  this.lagWaitTicks = 0;
               }
            } else {
               this.lagWaitTicks = 0;
               double dist = player.getEyePos().distanceTo(Vec3d.ofCenter(this.targetBlock));
               if (dist <= this.breakRange.get()) {
                  this.mc.options.forwardKey.setPressed(false);
                  this.miningTicks++;
                  if (this.miningTicks > 60) {
                     this.mc
                        .interactionManager
                        .interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(this.targetBlock), Direction.UP, this.targetBlock, false));
                     player.swingHand(Hand.MAIN_HAND);
                     this.miningTicks = 0;
                  }

                  if (this.breakCooldown <= 0) {
                     this.mc.interactionManager.updateBlockBreakingProgress(this.targetBlock, Direction.UP);
                     player.swingHand(Hand.MAIN_HAND);
                     this.mc.options.attackKey.setPressed(true);
                     this.breakCooldown = 6;
                  } else {
                     this.breakCooldown--;
                  }
               } else {
                  this.stopBreaking();
                  this.mc.options.forwardKey.setPressed(true);
               }
            }
         }
      }
   }

   private void goToChest() {
      this.stopMovement();
      this.targetChest = this.findNearestBlock(Blocks.ENDER_CHEST, 4);
      if (this.targetChest != null) {
         this.currentState = SpawnerProtectModule.State.GOING_TO_CHEST;
      } else if (this.getEnderChestCount() > 0) {
         this.currentState = SpawnerProtectModule.State.PLACING_ECHEST;
         this.shopTick = 0;
      } else {
         this.currentState = SpawnerProtectModule.State.BUYING_ECHEST;
         this.shopSequence = 0;
         this.shopTick = 0;
      }
   }

   private void handleGoingToChest() {
      ClientPlayerEntity player = this.mc.player;
      if (player != null) {
         if (this.targetChest == null) {
            this.targetChest = this.findNearestBlock(Blocks.ENDER_CHEST, 4);
         }

         if (this.targetChest == null) {
            this.currentState = SpawnerProtectModule.State.WORKING;
         } else {
            this.mc.options.forwardKey.setPressed(true);
            if (player.getBlockPos().isWithinDistance(this.targetChest, 4.0)) {
               this.stopMovement();
               this.currentState = SpawnerProtectModule.State.OPENING_CHEST;
            }
         }
      }
   }

   private void handleOpeningChest() {
      ClientPlayerEntity player = this.mc.player;
      if (player != null) {
         this.updateSneak(false);
         if (this.chestTick % 12 == 0 && this.targetChest != null) {
            this.mc
               .interactionManager
               .interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(this.targetChest), Direction.UP, this.targetChest, false));
         }

         this.chestTick++;
         if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
            this.chestTick = 0;
            this.currentState = SpawnerProtectModule.State.DEPOSITING_ITEMS;
            this.lagWaitTicks = 0;
         }
      }
   }

   private void handleBuyingEChest() {
      ClientPlayerEntity player = this.mc.player;
      if (player != null) {
         this.shopTick++;
         if (this.shopSequence > 0 && this.mc.currentScreen == null && this.shopTick > 60) {
            this.say("Shop screen closed, retrying...");
            this.shopSequence = 0;
            this.shopTick = 0;
         } else if (this.shopTick >= 30) {
            switch (this.shopSequence) {
               case 0:
                  this.say("Opening shop: /shop");
                  if (this.mc.getNetworkHandler() != null) {
                     this.mc.getNetworkHandler().sendChatCommand("shop");
                  }

                  this.shopSequence = 1;
                  this.shopTick = 0;
                  break;
               case 1:
                  if (this.mc.currentScreen != null && this.screenTitle().contains("SHOP")) {
                     this.say("Shop: selecting the End category...");
                     this.clickSlot(11, SlotActionType.PICKUP);
                     this.shopSequence = 2;
                     this.shopTick = 0;
                  }
                  break;
               case 2:
                  if (this.mc.currentScreen != null && this.screenTitle().contains("END")) {
                     this.say("Shop: selecting Ender Chest...");
                     this.clickSlot(9, SlotActionType.PICKUP);
                     this.shopSequence = 3;
                     this.shopTick = 0;
                  }
                  break;
               case 3:
                  if (this.mc.currentScreen != null && this.screenTitle().contains("ENDER CHEST")) {
                     this.say("Shop: confirming purchase...");
                     this.clickSlot(25, SlotActionType.PICKUP);
                     this.shopSequence = 4;
                     this.shopTick = 0;
                  }
                  break;
               case 4:
                  if (this.getEnderChestCount() > 0) {
                     this.say("Ender Chest purchased.");
                     player.closeHandledScreen();
                     this.currentState = SpawnerProtectModule.State.PLACING_ECHEST;
                     this.shopTick = 0;
                  } else if (this.shopTick > 100) {
                     this.say("Purchase failed (timeout).");
                     player.closeHandledScreen();
                     this.shopSequence = 0;
                     this.shopTick = 0;
                  }
            }
         }
      }
   }

   private void handlePlacingEChest() {
      ClientPlayerEntity player = this.mc.player;
      ClientWorld level = this.mc.world;
      if (player != null && level != null) {
         this.shopTick++;
         if (this.shopTick >= 10) {
            int slot = -1;

            for (int i = 0; i < 9; i++) {
               if (player.getInventory().getStack(i).getItem() == Blocks.ENDER_CHEST.asItem()) {
                  slot = i;
                  break;
               }
            }

            if (slot == -1) {
               for (int ix = 9; ix < 36; ix++) {
                  if (player.getInventory().getStack(ix).getItem() == Blocks.ENDER_CHEST.asItem()) {
                     this.mc.interactionManager.clickSlot(player.currentScreenHandler.syncId, ix, 0, SlotActionType.QUICK_MOVE, player);
                     this.shopTick = 0;
                     return;
                  }
               }

               this.currentState = SpawnerProtectModule.State.BUYING_ECHEST;
               this.shopSequence = 0;
            } else {
               player.getInventory().setSelectedSlot(slot);
               BlockPos p = player.getBlockPos();
               BlockPos placePos = null;

               for (Direction d : Direction.values()) {
                  if (d != Direction.UP && d != Direction.DOWN) {
                     BlockPos bp = p.offset(d);
                     if (level.getBlockState(bp).isReplaceable()) {
                        placePos = bp;
                        break;
                     }
                  }
               }

               if (placePos != null) {
                  this.mc.interactionManager.interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(placePos), Direction.UP, placePos, false));
                  player.swingHand(Hand.MAIN_HAND);
                  this.targetChest = placePos;
                  this.currentState = SpawnerProtectModule.State.GOING_TO_CHEST;
               } else {
                  this.warn("No spot to place the Ender Chest!");
                  this.currentState = SpawnerProtectModule.State.FINAL_EXIT;
                  this.lagWaitTicks = 0;
               }
            }
         }
      }
   }

   private void handleDepositing() {
      ClientPlayerEntity player = this.mc.player;
      if (player != null) {
         if (this.lagWaitTicks < 15) {
            this.lagWaitTicks++;
         } else if (player.currentScreenHandler instanceof GenericContainerScreenHandler handler) {
            int var8 = handler.slots.size() - 36;
            boolean hasSpace = false;

            for (int invStart = 0; invStart < var8; invStart++) {
               ItemStack stack = handler.getSlot(invStart).getStack();
               if (stack.isEmpty() || stack.getItem() == Blocks.SPAWNER.asItem() && stack.getCount() < stack.getMaxCount()) {
                  hasSpace = true;
                  break;
               }
            }

            int invStartx = var8;

            for (int i = 0; i < 36; i++) {
               int slotId = invStartx + i;
               if (handler.getSlot(slotId).getStack().getItem() == Blocks.SPAWNER.asItem()) {
                  if (!hasSpace) {
                     this.currentState = SpawnerProtectModule.State.FINAL_EXIT;
                     this.lagWaitTicks = 0;
                     return;
                  }

                  this.mc.interactionManager.clickSlot(handler.syncId, slotId, 0, SlotActionType.QUICK_MOVE, player);
                  return;
               }
            }

            player.closeHandledScreen();
            if (this.findNearestBlock(Blocks.SPAWNER, 16) != null) {
               this.currentState = SpawnerProtectModule.State.WORKING;
            } else {
               this.currentState = SpawnerProtectModule.State.FINAL_EXIT;
               this.lagWaitTicks = 0;
            }
         }
      }
   }

   private void handleFinalExit() {
      if (this.lagWaitTicks == 0) {
         this.sendWebhook("\ud83c\udfc1 **DONE.** Waiting for items to save...");
         this.say("Task done — waiting 2s for the server to save, then disconnecting...");
      }

      this.lagWaitTicks++;
      if (this.lagWaitTicks > 40) {
         ClientPlayNetworkHandler conn = this.mc.getNetworkHandler();
         if (conn != null) {
            conn.getConnection().disconnect(Text.literal("[SpawnerProtect] Task complete, safe disconnect."));
         }

         this.resetModule();
      }
   }

   private void updateSneak(boolean sneak) {
      ClientPlayerEntity player = this.mc.player;
      if (player != null) {
         player.setSneaking(sneak);
      }

      this.mc.options.sneakKey.setPressed(sneak);
   }

   private boolean isNearSpawn() {
      ClientPlayerEntity player = this.mc.player;
      return player != null && Math.abs(player.getX()) < 100.0 && Math.abs(player.getZ()) < 100.0;
   }

   private boolean isWhitelisted(String name) {
      if (name != null && !name.isEmpty()) {
         for (String n : this.whitelist.get().split(",")) {
            if (n.trim().equalsIgnoreCase(name)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private String screenTitle() {
      return this.mc.currentScreen == null ? "" : this.mc.currentScreen.getTitle().getString().toUpperCase(Locale.ROOT);
   }

   private void clickSlot(int slot, SlotActionType type) {
      ClientPlayerEntity player = this.mc.player;
      if (player != null) {
         this.mc.interactionManager.clickSlot(player.currentScreenHandler.syncId, slot, 0, type, player);
      }
   }

   private BlockPos findNearestBlock(Block block, int r) {
      ClientPlayerEntity player = this.mc.player;
      ClientWorld level = this.mc.world;
      if (player != null && level != null) {
         BlockPos p = player.getBlockPos();
         BlockPos nearest = null;
         double minDist = Double.MAX_VALUE;

         for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
               for (int z = -r; z <= r; z++) {
                  BlockPos bp = p.add(x, y, z);
                  if (level.getBlockState(bp).getBlock() == block) {
                     double d = p.getSquaredDistance(bp);
                     if (d < minDist) {
                        minDist = d;
                        nearest = bp;
                     }
                  }
               }
            }
         }

         return nearest;
      } else {
         return null;
      }
   }

   private BlockPos findRandomBlock(Block block, int r) {
      ClientPlayerEntity player = this.mc.player;
      ClientWorld level = this.mc.world;
      if (player != null && level != null) {
         BlockPos p = player.getBlockPos();
         List<BlockPos> list = new ArrayList<>();
         double maxDistSq = (double)r * r;

         for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
               for (int z = -r; z <= r; z++) {
                  BlockPos bp = p.add(x, y, z);
                  if (p.getSquaredDistance(bp) <= maxDistSq && level.getBlockState(bp).getBlock() == block) {
                     list.add(bp);
                  }
               }
            }
         }

         return list.isEmpty() ? null : list.get(new Random().nextInt(list.size()));
      } else {
         return null;
      }
   }

   private int getSpawnerCount() {
      return this.countItem(Blocks.SPAWNER.asItem(), 36);
   }

   private int getEnderChestCount() {
      return this.countItem(Blocks.ENDER_CHEST.asItem(), 45);
   }

   private int countItem(Item item, int slots) {
      ClientPlayerEntity player = this.mc.player;
      if (player == null) {
         return 0;
      } else {
         int count = 0;

         for (int i = 0; i < slots; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == item) {
               count += stack.getCount();
            }
         }

         return count;
      }
   }

   private ItemEntity findDroppedSpawner() {
      ClientPlayerEntity player = this.mc.player;
      ClientWorld level = this.mc.world;
      if (player != null && level != null) {
         ItemEntity best = null;
         double bestDist = Double.MAX_VALUE;

         for (Entity e : level.getEntities()) {
            if (e instanceof ItemEntity item && item.getStack().getItem() == Blocks.SPAWNER.asItem()) {
               double d = player.distanceTo(item);
               if (d < 16.0 && d < bestDist) {
                  bestDist = d;
                  best = item;
               }
            }
         }

         return best;
      } else {
         return null;
      }
   }

   private void stopBreaking() {
      this.mc.options.attackKey.setPressed(false);
      this.breakCooldown = 0;
   }

   private void stopMovement() {
      this.stopBreaking();
      this.mc.options.forwardKey.setPressed(false);
   }

   private void warn(String msg) {
      ClientPlayerEntity player = this.mc.player;
      if (player != null) {
         player.sendMessage(Text.literal("§c[SpawnerProtect] §fnull"), false);
      }

      try {
         FeClient.notifications().pushInfo("SpawnerProtect · " + msg.replaceAll("§.", ""));
      } catch (Exception var4) {
      }
   }

   private void say(String msg) {
      ClientPlayerEntity player = this.mc.player;
      if (player != null) {
         player.sendMessage(Text.literal("§7[SpawnerProtect] null"), false);
      }
   }

   private void sendWebhook(String message) {
      // was hardcoded to "" — meaning this never fired regardless of what you
      // typed into the Webhook URL setting. reads the real setting now.
      String urlStr = this.webhookUrl.get().trim();
      if (!urlStr.isEmpty()) {
         String json = "{\"content\": \"" + message.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
         new Thread(() -> {
            try {
               HttpURLConnection conn = (HttpURLConnection)URI.create(urlStr).toURL().openConnection();
               conn.setRequestMethod("POST");
               conn.setRequestProperty("Content-Type", "application/json");
               conn.setDoOutput(true);

               try (OutputStream os = conn.getOutputStream()) {
                  os.write(json.getBytes(StandardCharsets.UTF_8));
               }

               conn.getInputStream().close();
            } catch (Exception var8) {
            }
         }, "SpawnerProtect-Webhook").start();
      }
   }

   enum State {
      WAITING_FOR_STRANGER,
      WORKING,
      GOING_TO_CHEST,
      OPENING_CHEST,
      DEPOSITING_ITEMS,
      FINAL_EXIT,
      BUYING_ECHEST,
      PLACING_ECHEST;

      private static SpawnerProtectModule.State[] $values() {
         return new SpawnerProtectModule.State[]{
            WAITING_FOR_STRANGER, WORKING, GOING_TO_CHEST, OPENING_CHEST, DEPOSITING_ITEMS, FINAL_EXIT, BUYING_ECHEST, PLACING_ECHEST
         };
      }
   }
}



