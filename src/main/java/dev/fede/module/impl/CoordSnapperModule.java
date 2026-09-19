package dev.fede.module.impl;

import dev.fede.FeClient;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.KeybindSetting;
import dev.fede.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;

public class CoordSnapperModule extends Module {
   public final ModeSetting format = this.addSetting(new ModeSetting("Format", "Clipboard format.", "X Y Z", "X Y Z", "JSON", "Command"));
   public final ModeSetting target = this.addSetting(new ModeSetting("Target", "Which coordinates to copy.", "Looked-at Block", "Looked-at Block", "Player"));
   public final KeybindSetting copyKey = this.addSetting(new KeybindSetting("Copy Key", "Press to copy the coordinates.", -1));
   public final BooleanSetting notify = this.addSetting(new BooleanSetting("Notify", "Show a confirmation when copied.", true));
   private String lastCopied = "";

   public CoordSnapperModule() {
      super("CoordSnapper", "Copies looked-at coordinates with one key.", Category.MISC);
   }

   public String lastCopied() {
      return this.lastCopied;
   }

   @Override
   public boolean onKeyPress(int keyCode) {
      if (!this.copyKey.matches(keyCode)) {
         return false;
      } else {
         this.snap();
         return true;
      }
   }

   private void snap() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null) {
         BlockPos pos = this.resolvePos(mc);
         if (pos == null) {
            if (this.notify.get()) {
               FeClient.notifications().pushInfo("CoordSnapper · no block in view");
            }
         } else {
            String var10000;
            label31: {
               String var4 = this.format.get();
               switch (var4.hashCode()) {
                  case -1679919317:
                     if (var4.equals("Command")) {
                        var10000 = "/tp " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
                        break label31;
                     }
                     break;
                  case 2286824:
                     if (var4.equals("JSON")) {
                        var10000 = "{\"x\": " + pos.getX() + ", \"y\": " + pos.getY() + ", \"z\": " + pos.getZ() + "}";
                        break label31;
                     }
               }

               var10000 = pos.getX() + " " + pos.getY() + " " + pos.getZ();
            }

            String text = var10000;
            mc.keyboard.setClipboard(text);
            this.lastCopied = text;
            if (this.notify.get()) {
               FeClient.notifications().pushInfo("Copied · null");
            }
         }
      }
   }

   private BlockPos resolvePos(MinecraftClient mc) {
      if (this.target.check("Player")) {
         return mc.player.getBlockPos();
      } else {
         HitResult hit = mc.crosshairTarget;
         return hit instanceof BlockHitResult block && hit.getType() == Type.BLOCK ? block.getBlockPos() : null;
      }
   }
}



