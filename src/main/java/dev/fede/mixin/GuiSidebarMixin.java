package dev.fede.mixin;

import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import dev.fede.module.impl.FakeStatsModule;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InGameHud.class})
public class GuiSidebarMixin {
   @Inject(
      method = {"method_1757"},
      at = {@At("HEAD")}
   )
   private void FeClient$beginSidebar(DrawContext guiGraphics, ScoreboardObjective objective, CallbackInfo ci) {
      FakeStatsModule fs = fakeStats();
      if (fs != null) {
         fs.beginSidebar();
      }
   }

   @Redirect(
      method = {"method_1757"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_327;method_27525(Lnet/minecraft/class_5348;)I",
         ordinal = 1
      )
   )
   private int FeClient$lineWidth(TextRenderer font, StringVisitable text) {
      FakeStatsModule fs = fakeStats();
      if (fs != null && text instanceof Text component) {
         try {
            return font.getWidth(fs.rewriteForWidth(component));
         } catch (Exception var6) {
         }
      }

      return font.getWidth(text);
   }

   @ModifyArg(
      method = {"method_1757"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_332;method_51439(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;IIIZ)V",
         ordinal = 1
      ),
      index = 1
   )
   private Text FeClient$lineText(Text text) {
      FakeStatsModule fs = fakeStats();
      if (fs != null) {
         try {
            return fs.rewriteForDraw(text);
         } catch (Exception var4) {
         }
      }

      return text;
   }

   private static FakeStatsModule fakeStats() {
      ModuleManager m = FeClient.modules();
      return m != null && m.fakeStats != null && m.fakeStats.isEnabled() ? m.fakeStats : null;
   }
}



