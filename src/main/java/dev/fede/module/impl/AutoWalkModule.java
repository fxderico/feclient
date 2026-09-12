package dev.fede.module.impl;

import dev.fede.mixin.KeyMappingAccessor;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;

public class AutoWalkModule extends Module {
   public final ModeSetting mode = this.addSetting(new ModeSetting("Mode", "Walking mode.", "Simple", "Simple", "Smart"));
   public final ModeSetting direction = this.addSetting(
      new ModeSetting("Direction", "The direction to walk in Simple mode.", "Forwards", "Forwards", "Backwards", "Left", "Right")
   );
   public final BooleanSetting disableOnInput = this.addSetting(new BooleanSetting("Disable On Input", "Disable the module on manual movement input.", false));
   public final BooleanSetting disableOnY = this.addSetting(new BooleanSetting("Disable On Y Change", "Disable the module if you move vertically.", false));
   public final BooleanSetting waitForChunks = this.addSetting(new BooleanSetting("No Unloaded Chunks", "Do not walk into unloaded chunks.", true));

   public AutoWalkModule() {
      super("AutoWalk", "Automatically walks forward.", Category.MISC);
      this.direction.visibleWhen(() -> this.mode.check("Simple"));
      this.disableOnY.visibleWhen(() -> this.mode.check("Simple"));
      this.waitForChunks.visibleWhen(() -> this.mode.check("Simple"));
   }

   @Override
   protected void onDisable() {
      this.release(MinecraftClient.getInstance());
   }

   @Override
   public void onTick() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null && mc.world != null) {
         this.release(mc);
         if (this.mode.check("Smart")) {
            mc.options.forwardKey.setPressed(true);
            mc.options.sprintKey.setPressed(true);
            if (mc.player.horizontalCollision && mc.player.isOnGround()) {
               mc.options.jumpKey.setPressed(true);
            }
         } else if (this.disableOnY.get() && mc.player.lastY != mc.player.getY()) {
            this.toggle();
         } else if (!this.waitForChunks.get() || this.chunkAheadLoaded(mc)) {
            String var2 = this.direction.get();
            switch (var2.hashCode()) {
               case -934227760:
                  if (var2.equals("Backwards")) {
                     mc.options.backKey.setPressed(true);
                  }
                  break;
               case 2364455:
                  if (var2.equals("Left")) {
                     mc.options.leftKey.setPressed(true);
                  }
                  break;
               case 78959100:
                  if (var2.equals("Right")) {
                     mc.options.rightKey.setPressed(true);
                  }
                  break;
               case 547957358:
                  if (var2.equals("Forwards")) {
                     mc.options.forwardKey.setPressed(true);
                  }
            }
         }
      } else {
         this.release(mc);
      }
   }

   @Override
   public boolean onKeyPress(int keyCode) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (this.disableOnInput.get() && mc.currentScreen == null && this.isMovementKey(mc, keyCode)) {
         this.toggle();
      }

      return false;
   }

   private boolean chunkAheadLoaded(MinecraftClient mc) {
      double dx;
      double dz;
      label22: {
         double yawRad = Math.toRadians(mc.player.getYaw());
         double fx = -Math.sin(yawRad);
         double fz = Math.cos(yawRad);
         String bx = this.direction.get();
         byte bz = -1;
         switch (bx.hashCode()) {
            case -934227760:
               if (bx.equals("Backwards")) {
                  dx = -fx;
                  dz = -fz;
                  break label22;
               }
               break;
            case 2364455:
               if (bx.equals("Left")) {
                  dx = -fz;
                  dz = fx;
                  break label22;
               }
               break;
            case 78959100:
               if (bx.equals("Right")) {
                  dx = fz;
                  dz = -fx;
                  break label22;
               }
         }

         dx = fx;
         dz = fz;
      }

      int bx = (int)Math.floor(mc.player.getX() + dx * 2.0);
      int bz = (int)Math.floor(mc.player.getZ() + dz * 2.0);
      return mc.world.getChunkManager().isChunkLoaded(bx >> 4, bz >> 4);
   }

   private void release(MinecraftClient mc) {
      if (mc.options != null) {
         mc.options.forwardKey.setPressed(false);
         mc.options.backKey.setPressed(false);
         mc.options.leftKey.setPressed(false);
         mc.options.rightKey.setPressed(false);
         mc.options.jumpKey.setPressed(false);
         mc.options.sprintKey.setPressed(false);
      }
   }

   private boolean isMovementKey(MinecraftClient mc, int keyCode) {
      GameOptions o = mc.options;
      return matches(o.forwardKey, keyCode)
         || matches(o.backKey, keyCode)
         || matches(o.leftKey, keyCode)
         || matches(o.rightKey, keyCode)
         || matches(o.jumpKey, keyCode)
         || matches(o.sneakKey, keyCode);
   }

   private static boolean matches(KeyBinding km, int keyCode) {
      Key key = ((KeyMappingAccessor)km).FeClient$getKey();
      return key.getCategory() == Type.KEYSYM && key.getCode() == keyCode;
   }
}

