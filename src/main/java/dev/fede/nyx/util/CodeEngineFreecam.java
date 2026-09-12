package dev.fede.nyx.util;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.Entity.RemovalReason;

public final class CodeEngineFreecam extends AbstractClientPlayerEntity {
   private static final int intVal = -464375311;

   public CodeEngineFreecam(ClientWorld var1) {
      super(var1, new GameProfile(UUID.randomUUID(), "CodeEngineFreecam"));
      this.setId(-464375311);
      this.setPose(EntityPose.SWIMMING);
      this.getAbilities().flying = true;
      this.getAbilities().allowFlying = true;
      this.noClip = false;
      this.setNoGravity(true);
      this.setInvisible(true);
      this.setSilent(true);
   }

   public void run() {
      if (this.getEntityWorld() instanceof ClientWorld var1) {
         var1.addEntity(this);
      }
   }

   public void run2() {
      if (this.getEntityWorld() instanceof ClientWorld var1) {
         var1.removeEntity(this.getId(), RemovalReason.DISCARDED);
      }
   }

   public boolean isEnabled8() {
      return false;
   }

   public boolean isEnabled() {
      return true;
   }

   public static void run4(Entity var0) {
      if (var0 instanceof CodeEngineFreecam var1) {
         var1.run2();
      }
   }
}

