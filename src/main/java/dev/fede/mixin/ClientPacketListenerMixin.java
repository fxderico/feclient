package dev.fede.mixin;

import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import dev.fede.module.impl.BlockEntityEspModule;
import dev.fede.module.impl.SpawnerProtectModule;
import dev.fede.util.TpsTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayNetworkHandler.class})
public class ClientPacketListenerMixin {
   @Inject(
      method = {"method_11079"},
      at = {@At("HEAD")}
   )
   private void FeClient$trackTps(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
      TpsTracker.onTimePacket();
   }

   @Inject(
      method = {"method_45730"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void FeClient$fakeCommands(String command, CallbackInfo ci) {
      ModuleManager modules = FeClient.modules();
      if (modules != null) {
         try {
            if (modules.fakePay != null && modules.fakePay.tryIntercept(command)) {
               ci.cancel();
               return;
            }

            if (modules.fakeStats != null && modules.fakeStats.tryInterceptBalance(command)) {
               ci.cancel();
            }
         } catch (Exception var5) {
         }
      }
   }

   @Inject(
      method = {"method_11128"},
      at = {@At("TAIL")}
   )
   private void FeClient$blockEntityChunk(ChunkDataS2CPacket packet, CallbackInfo ci) {
      BlockEntityEspModule module = module();
      if (module != null && module.isEnabled() && module.chunkPacketsEnabled()) {
         try {
            packet.getChunkData().getBlockEntities(packet.getChunkX(), packet.getChunkZ()).accept((pos, type, tag) -> module.run(pos, type));
         } catch (Exception var5) {
         }
      }
   }

   @Inject(
      method = {"method_11094"},
      at = {@At("TAIL")}
   )
   private void FeClient$blockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo ci) {
      BlockEntityEspModule module = module();
      if (module != null && module.isEnabled() && module.beUpdatePacketsEnabled()) {
         try {
            module.run(packet.getPos(), packet.getBlockEntityType());
         } catch (Exception var5) {
         }
      }
   }

   private static BlockEntityEspModule module() {
      ModuleManager modules = FeClient.modules();
      return modules != null ? modules.blockEntityEsp : null;
   }

   private static SpawnerProtectModule spawnerProtect() {
      ModuleManager modules = FeClient.modules();
      return modules != null ? modules.spawnerProtect : null;
   }

   @Inject(
      method = {"method_11116"},
      at = {@At("HEAD")}
   )
   private void FeClient$spawnerProtectDestruction(BlockBreakingProgressS2CPacket packet, CallbackInfo ci) {
      SpawnerProtectModule sp = spawnerProtect();
      if (sp != null && sp.isEnabled() && MinecraftClient.getInstance().isOnThread()) {
         try {
            sp.onBlockDestructionPacket(packet.getEntityId(), packet.getPos());
         } catch (Exception var5) {
         }
      }
   }

   @Inject(
      method = {"method_11136"},
      at = {@At("HEAD")}
   )
   private void FeClient$spawnerProtectBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo ci) {
      SpawnerProtectModule sp = spawnerProtect();
      if (sp != null && sp.isEnabled() && sp.detectBlockUpdatesEnabled() && MinecraftClient.getInstance().isOnThread()) {
         try {
            sp.onServerBlockUpdate(packet.getPos(), packet.getState(), false);
         } catch (Exception var5) {
         }
      }
   }

   @Inject(
      method = {"method_11100"},
      at = {@At("HEAD")}
   )
   private void FeClient$spawnerProtectSectionUpdate(ChunkDeltaUpdateS2CPacket packet, CallbackInfo ci) {
      SpawnerProtectModule sp = spawnerProtect();
      if (sp != null && sp.isEnabled() && sp.detectBlockUpdatesEnabled() && MinecraftClient.getInstance().isOnThread()) {
         try {
            packet.visitUpdates((pos, state) -> sp.onServerBlockUpdate(pos, state, true));
         } catch (Exception var5) {
         }
      }
   }
}



