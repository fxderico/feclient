package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.util.Colors;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class CustomAccessoriesModule extends Module {
   public final ColorSetting color = this.addSetting(new ColorSetting("Color", "Base tint for every accessory", -49508));
   public final BooleanSetting rainbow = this.addSetting(new BooleanSetting("Rainbow", "Cycle every accessory through the rainbow", false));
   public final SliderSetting glow = this.addSetting(new SliderSetting("Glow", "Soft outer-halo intensity", 70.0, 0.0, 100.0, 5.0, "%"));
   public final BooleanSetting firstPerson = this.addSetting(new BooleanSetting("First Person", "Also show your accessories in first-person view", false));
   public final BooleanSetting cape = this.addSetting(new BooleanSetting("Cape", "A flowing cloth cape down your back", true));
   public final ModeSetting capeStyle = this.addSetting(new ModeSetting("Cape Style", "Cape look", "FE Logo", "FE Logo", "Wave", "Grid", "Solid"));
   public final BooleanSetting capePhysics = this.addSetting(new BooleanSetting("Cape Physics", "Sway & billow with your movement", true));
   public final BooleanSetting trail = this.addSetting(new BooleanSetting("Trail", "A glowing trail left behind as you move", true));
   public final ModeSetting trailStyle = this.addSetting(new ModeSetting("Trail Style", "Trail look", "Ribbon", "Ribbon", "Sparkle", "Echo"));
   public final SliderSetting trailLength = this.addSetting(new SliderSetting("Trail Length", "How long the trail lingers", 1.2, 0.2, 4.0, 0.1, "s"));
   public final BooleanSetting aura = this.addSetting(new BooleanSetting("Aura", "An orbiting aura around your feet", false));
   public final ModeSetting auraStyle = this.addSetting(new ModeSetting("Aura Style", "Aura look", "Orbit", "Orbit", "Ring"));
   public final BooleanSetting crown = this.addSetting(new BooleanSetting("Crown", "A floating, spinning FE crown above your head", false));
   private final Deque<CustomAccessoriesModule.TrailNode> trailNodes = new ArrayDeque<>();
   private static final int MAX_TRAIL = 256;

   public CustomAccessoriesModule() {
      super("CustomAccessories", "Client-side cosmetics — cape, trail, aura & crown", Category.ADDONS);
      this.capeStyle.visibleWhen(this.cape::get);
      this.capePhysics.visibleWhen(this.cape::get);
      this.trailStyle.visibleWhen(this.trail::get);
      this.trailLength.visibleWhen(this.trail::get);
      this.auraStyle.visibleWhen(this.aura::get);
   }

   public Deque<CustomAccessoriesModule.TrailNode> trailNodes() {
      return this.trailNodes;
   }

   @Override
   public void onTick() {
      if (!this.trail.get()) {
         if (!this.trailNodes.isEmpty()) {
            this.trailNodes.clear();
         }
      } else {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         if (player != null) {
            double x = player.getX();
            double y = player.getY() + player.getHeight() * 0.5;
            double z = player.getZ();
            this.trailNodes.addLast(new TrailNode(x, y, z, System.nanoTime()));
            long cutoff = System.nanoTime() - (long)(Math.max(0.2F, this.trailLength.getFloat()) * 1.E9);

            while (!this.trailNodes.isEmpty() && this.trailNodes.peekFirst().nanos < cutoff) {
               this.trailNodes.removeFirst();
            }

            while (this.trailNodes.size() > 256) {
               this.trailNodes.removeFirst();
            }
         }
      }
   }

   @Override
   protected void onDisable() {
      this.clear();
   }

   public void clear() {
      this.trailNodes.clear();
   }

   public int currentRgb() {
      if (this.rainbow.get()) {
         float hue = (float)(System.currentTimeMillis() % 4000L) / 4000.0F * 360.0F;
         return Colors.hsvToRgb(hue, 0.8F, 1.0F) & 16777215;
      } else {
         return this.color.get() & 16777215;
      }
   }

   public float glowStrength() {
      return this.glow.getFloat() / 100.0F;
   }

   public final class TrailNode {
      public double doubleVal;
      public double doubleVal2;
      public double doubleVal3;
      public long nanos;

      TrailNode(double x, double y, double z, long nanos) {
         this.doubleVal = x;
         this.doubleVal2 = y;
         this.doubleVal3 = z;
         this.nanos = nanos;
      }

      public float ageSeconds(long now) {
         return (float)(now - this.nanos) / 1.E9F;
      }
   }
}


