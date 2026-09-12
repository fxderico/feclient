package dev.fede.nyx.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public final class AutoTunnelUtil3 {
   private static final MinecraftClient class310 = MinecraftClient.getInstance();

   private AutoTunnelUtil3() {
   }

   private static ClientPlayNetworkHandler getclass634() {
      return class310.getNetworkHandler();
   }

   public static void run(float var0, float var1, boolean var2) {
      ClientPlayNetworkHandler var3 = getclass634();
      if (var3 != null) {
         var3.sendPacket(new LookAndOnGround(var0, var1, var2, false));
      }
   }

   public static void run2(Vec3d var0, boolean var1) {
      ClientPlayNetworkHandler var2 = getclass634();
      if (var2 != null && var0 != null) {
         var2.sendPacket(new PositionAndOnGround(var0.x, var0.y, var0.z, var1, false));
      }
   }

   public static void run3(Hand var0) {
      ClientPlayNetworkHandler var1 = getclass634();
      if (var1 != null && var0 != null) {
         var1.sendPacket(new HandSwingC2SPacket(var0));
      }
   }
}

