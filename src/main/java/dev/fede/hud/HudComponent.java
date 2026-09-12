package dev.fede.hud;

import dev.fede.render.nanovg.NVGRenderer;
import java.util.function.BooleanSupplier;

public abstract class HudComponent {
   public static final float MIN_SCALE = 0.5F;
   public static final float MAX_SCALE = 2.0F;
   private String id;
   private BooleanSupplier visibility;
   private float defaultFx;
   private float defaultFy;
   private float scale = 1.0F;

   protected HudComponent(String id, float defaultFx, float defaultFy, BooleanSupplier visibility) {
      this.id = id;
      this.defaultFx = defaultFx;
      this.defaultFy = defaultFy;
      this.visibility = visibility;
   }

   public String getId() {
      return this.id;
   }

   public float getFx() {
      return this.defaultFx;
   }

   public float getFy() {
      return this.defaultFy;
   }

   public void setPosition(float fx, float fy) {
      this.defaultFx = Math.clamp(fx, 0.0F, 1.0F);
      this.defaultFy = Math.clamp(fy, 0.0F, 1.0F);
   }

   public float getScale() {
      return this.scale;
   }

   public void setScale(float scale) {
      this.scale = Math.clamp(scale, 0.5F, 2.0F);
   }

   public final boolean visible() {
      return this.visibility.getAsBoolean();
   }

   public abstract float measureWidth(NVGRenderer var1);

   public abstract float measureHeight(NVGRenderer var1);

   public abstract void render(NVGRenderer var1, float var2, float var3, float var4, float var5);

   public boolean onEditClick(float localX, float localY) {
      return false;
   }

   public boolean rightAnchored() {
      return this.defaultFx > 0.5F;
   }
}

