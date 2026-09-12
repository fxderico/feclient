package dev.fede.render;

import dev.fede.FeClient;
import dev.fede.hud.HudDragController;
import dev.fede.hud.HudManager;
import dev.fede.module.impl.CustomCrosshairModule;
import dev.fede.module.impl.MotionBlurModule;
import dev.fede.notification.NotificationManager;
import dev.fede.render.nanovg.GlStateSnapshot;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.theme.Theme;
import dev.fede.util.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.texture.GlTexture;
import org.lwjgl.opengl.GL33C;

public final class OverlayRenderer {
   private static HudManager hudManager;
   private static NotificationManager notifications;
   private static int fbo = -1;
   private static boolean crashed;

   private OverlayRenderer() {
   }

   public static void init(HudManager hud, NotificationManager toasts) {
      hudManager = hud;
      notifications = toasts;
   }

   public static float uiScale() {
      MinecraftClient mc = MinecraftClient.getInstance();
      return Math.max(1.0F, mc.getWindow().getFramebufferHeight() / 1080.0F);
   }

   public static void render() {
      try {
         GlintTextureTinter.tick();
      } catch (Throwable var19) {
      }

      if (!crashed && hudManager != null) {
         MinecraftClient mc = MinecraftClient.getInstance();
         Framebuffer target = mc.getFramebuffer();
         if (target != null && target.getColorAttachment() instanceof GlTexture colorTexture) {
            boolean var22 = mc.currentScreen instanceof NvgDrawable;
            boolean chatEditing = mc.currentScreen instanceof ChatScreen;
            boolean drawHud = !mc.options.hudHidden && mc.world != null && !var22;
            boolean drawToasts = mc.world != null && !mc.options.hudHidden;
            MotionBlurModule mbModule = FeClient.modules() != null ? FeClient.modules().motionBlur : null;
            boolean motionBlur = mbModule != null && mbModule.isEnabled() && mc.world != null && !var22;
            if (var22 || drawHud || drawToasts || motionBlur) {
               try {
                  GlStateSnapshot state = GlStateSnapshot.capture();

                  try {
                     if (!bindOverlayFbo(colorTexture, target.textureWidth, target.textureHeight)) {
                        return;
                     }

                     NVGRenderer vg = NVGRenderer.get();
                     if (motionBlur) {
                        MotionBlurRenderer.render(vg, target.textureWidth, target.textureHeight, mbModule);
                     }

                     vg.setFontMode(FeClient.modules().clickGui.font.get());
                     if (vg.hasFont()) {
                        float scale = uiScale();
                        float uiWidth = target.textureWidth / scale;
                        float uiHeight = target.textureHeight / scale;
                        vg.beginFrame(target.textureWidth, target.textureHeight, 1.0F);
                        vg.save();
                        vg.scale(scale);
                        if (drawHud) {
                           if (chatEditing && HudDragController.isDragging()) {
                              HudDragController.updateDrag(vg);
                           }

                           CustomCrosshairModule crosshair = FeClient.modules().customCrosshair;
                           if (crosshair.isEnabled() && mc.currentScreen == null) {
                              crosshair.render(vg, uiWidth / 2.0F, uiHeight / 2.0F);
                           }

                           if (mc.currentScreen == null) {
                              WorldNametagRenderer.render(vg);
                           }

                           hudManager.render(vg, uiWidth, uiHeight);
                           if (chatEditing) {
                              renderChatEditOverlay(vg, uiWidth, uiHeight);
                           }
                        }

                        if (drawToasts) {
                           notifications.renderToasts(vg, uiWidth, uiHeight);
                        }

                        if (var22) {
                           ((NvgDrawable)mc.currentScreen).renderNvg(vg, uiMouseX(), uiMouseY(), uiWidth, uiHeight);
                        }

                        vg.restore();
                        vg.endFrame();
                        return;
                     }
                  } finally {
                     state.restore();
                  }
               } catch (Throwable var21) {
                  crashed = true;
                  FeClient.LOGGER.error("Client overlay renderer crashed; disabling overlay", var21);
               }
            }
         }
      }
   }

   private static void renderChatEditOverlay(NVGRenderer vg, float uiWidth, float uiHeight) {
      Theme theme = FeClient.themes().current();
      float mx = uiMouseX();
      float my = uiMouseY();
      vg.text(
         "Drag to move  ·  scroll to resize",
         (uiWidth - vg.textWidth("Drag to move  ·  scroll to resize", 14.0F)) / 2.0F,
         22.0F,
         14.0F,
         Colors.withAlpha(theme.textMuted(), 0.9F)
      );

      for (HudManager.Placement p : hudManager.layout(vg, uiWidth, uiHeight, true)) {
         boolean hidden = !p.component().visible();
         boolean active = p.contains(mx, my) || p.component() == HudDragController.draggedComponent();
         if (hidden) {
            vg.save();
            vg.alpha(0.35F);
            hudManager.renderPlacement(vg, p);
            vg.restore();
         }

         int outline = active ? theme.accentBright() : Colors.withAlpha(theme.accent(), 0.5F);
         vg.rectOutline(p.getFloat() - 3.0F, p.getFloat2() - 3.0F, p.getFloat3() + 6.0F, p.getFloat4() + 6.0F, 6.0F, active ? 1.6F : 1.0F, outline);
         vg.rect(p.getFloat() + p.getFloat3() - 2.0F, p.getFloat2() + p.getFloat4() - 2.0F, 6.0F, 6.0F, 2.0F, outline);
         if (active) {
            String pct = Math.round(p.component().getScale() * 100.0F) + "%";
            vg.text(pct, p.getFloat() + p.getFloat3() + 6.0F, p.getFloat2() + p.getFloat4() / 2.0F, 11.0F, theme.accentBright());
         }
      }
   }

   private static boolean bindOverlayFbo(GlTexture colorTexture, int width, int height) {
      if (fbo == -1) {
         fbo = GL33C.glGenFramebuffers();
      }

      GL33C.glBindFramebuffer(36160, fbo);
      GL33C.glFramebufferTexture2D(36160, 36064, 3553, colorTexture.getGlId(), 0);
      if (GL33C.glCheckFramebufferStatus(36160) != 36053) {
         return false;
      } else {
         GL33C.glViewport(0, 0, width, height);
         GL33C.glDisable(3089);
         return true;
      }
   }

   public static float uiMouseX() {
      MinecraftClient mc = MinecraftClient.getInstance();
      return (float)(mc.mouse.getX() * mc.getWindow().getFramebufferWidth() / Math.max(1, mc.getWindow().getWidth())) / uiScale();
   }

   public static float uiMouseY() {
      MinecraftClient mc = MinecraftClient.getInstance();
      return (float)(mc.mouse.getY() * mc.getWindow().getFramebufferHeight() / Math.max(1, mc.getWindow().getHeight())) / uiScale();
   }

   public static float guiToUi(double guiCoord) {
      MinecraftClient mc = MinecraftClient.getInstance();
      float pixels = (float)(guiCoord * mc.getWindow().getScaleFactor());
      return pixels / uiScale();
   }

   public static float uiWidth() {
      MinecraftClient mc = MinecraftClient.getInstance();
      return mc.getWindow().getFramebufferWidth() / uiScale();
   }

   public static float uiHeight() {
      MinecraftClient mc = MinecraftClient.getInstance();
      return mc.getWindow().getFramebufferHeight() / uiScale();
   }
}



