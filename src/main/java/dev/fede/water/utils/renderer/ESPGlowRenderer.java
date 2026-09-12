package dev.fede.water.utils.renderer;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public final class ESPGlowRenderer {
   private static int fboGeometry = -1;
   private static int texGeometry = -1;
   private static int fboBlurH = -1;
   private static int texBlurH = -1;
   private static int fboBlurV = -1;
   private static int texBlurV = -1;
   private static int progBlurH = -1;
   private static int progBlurV = -1;
   private static int progComposite = -1;
   private static int lastW = -1;
   private static int lastH = -1;
   private static boolean active = false;
   private static int savedFbo = 0;
   private static boolean shadersOk = false;
   public static float glowStrength = 2.0F;

   private ESPGlowRenderer() {
   }

   public static void begin() {
      if (!active) {
         MinecraftClient var0 = MinecraftClient.getInstance();
         int var1 = var0.getFramebuffer().textureWidth;
         int var2 = var0.getFramebuffer().textureHeight;

         try {
            ensureResources(var1, var2);
         } catch (Exception var4) {
            System.err.println("[ESPGlowRenderer] Shader init failed: " + var4.getMessage());
            shadersOk = false;
            return;
         }

         if (shadersOk) {
            savedFbo = GL11.glGetInteger(36006);
            GL30.glBindFramebuffer(36160, fboGeometry);
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glClear(16384);
            active = true;
         }
      }
   }

   public static void end() {
      if (active && shadersOk) {
         active = false;
         int var0 = lastW;
         int var1 = lastH;
         boolean var2 = GL11.glIsEnabled(3042);
         boolean var3 = GL11.glIsEnabled(2929);
         boolean var4 = GL11.glGetBoolean(2930);
         int var5 = GL11.glGetInteger(32969);
         int var6 = GL11.glGetInteger(32968);
         GL11.glDisable(2929);
         GL11.glDepthMask(false);
         GL30.glBindFramebuffer(36160, fboBlurH);
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glClear(16384);
         GL11.glDisable(3042);
         GL20.glUseProgram(progBlurH);
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, texGeometry);
         GL20.glUniform1i(GL20.glGetUniformLocation(progBlurH, "Sampler0"), 0);
         GL20.glUniform2f(GL20.glGetUniformLocation(progBlurH, "uResolution"), var0, var1);
         GL11.glDrawArrays(4, 0, 6);
         GL30.glBindFramebuffer(36160, fboBlurV);
         GL11.glClear(16384);
         GL20.glUseProgram(progBlurV);
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, texBlurH);
         GL20.glUniform1i(GL20.glGetUniformLocation(progBlurV, "Sampler0"), 0);
         GL20.glUniform2f(GL20.glGetUniformLocation(progBlurV, "uResolution"), var0, var1);
         GL11.glDrawArrays(4, 0, 6);
         GL30.glBindFramebuffer(36160, savedFbo);
         GL11.glEnable(3042);
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
         GL20.glUseProgram(progComposite);
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, texGeometry);
         GL20.glUniform1i(GL20.glGetUniformLocation(progComposite, "Sampler0"), 0);
         GL13.glActiveTexture(33985);
         GL11.glBindTexture(3553, texBlurV);
         GL20.glUniform1i(GL20.glGetUniformLocation(progComposite, "Sampler1"), 1);
         GL20.glUniform1f(GL20.glGetUniformLocation(progComposite, "uGlowStrength"), glowStrength);
         GL11.glDrawArrays(4, 0, 6);
         GL20.glUseProgram(0);
         GL13.glActiveTexture(33985);
         GL11.glBindTexture(3553, 0);
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, 0);
         GL14.glBlendFuncSeparate(var5, var6, var5, var6);
         if (!var2) {
            GL11.glDisable(3042);
         }

         if (var3) {
            GL11.glEnable(2929);
         }

         GL11.glDepthMask(var4);
      }
   }

   public static boolean isActive() {
      return active;
   }

   private static void ensureResources(int var0, int var1) {
      if (var0 != lastW || var1 != lastH || fboGeometry == -1 || !shadersOk) {
         lastW = var0;
         lastH = var1;
         deleteResources();
         texGeometry = createTexture(var0, var1);
         texBlurH = createTexture(var0, var1);
         texBlurV = createTexture(var0, var1);
         fboGeometry = createFbo(texGeometry);
         fboBlurH = createFbo(texBlurH);
         fboBlurV = createFbo(texBlurV);
         String var2 = loadShader("esp_glow_vertex.vsh");
         progBlurH = compileProgram(var2, loadShader("esp_blur_h_fragment.fsh"));
         progBlurV = compileProgram(var2, loadShader("esp_blur_v_fragment.fsh"));
         progComposite = compileProgram(var2, loadShader("esp_composite_fragment.fsh"));
         shadersOk = true;
      }
   }

   private static int createTexture(int var0, int var1) {
      int var2 = GL11.glGenTextures();
      GL11.glBindTexture(3553, var2);
      GL11.glTexImage2D(3553, 0, 6408, var0, var1, 0, 6408, 5121, (ByteBuffer)null);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10242, 33071);
      GL11.glTexParameteri(3553, 10243, 33071);
      return var2;
   }

   private static int createFbo(int var0) {
      int var1 = GL30.glGenFramebuffers();
      GL30.glBindFramebuffer(36160, var1);
      GL30.glFramebufferTexture2D(36160, 36064, 3553, var0, 0);
      GL30.glBindFramebuffer(36160, 0);
      return var1;
   }

   private static void deleteResources() {
      if (fboGeometry != -1) {
         GL30.glDeleteFramebuffers(fboGeometry);
         fboGeometry = -1;
      }

      if (fboBlurH != -1) {
         GL30.glDeleteFramebuffers(fboBlurH);
         fboBlurH = -1;
      }

      if (fboBlurV != -1) {
         GL30.glDeleteFramebuffers(fboBlurV);
         fboBlurV = -1;
      }

      if (texGeometry != -1) {
         GL11.glDeleteTextures(texGeometry);
         texGeometry = -1;
      }

      if (texBlurH != -1) {
         GL11.glDeleteTextures(texBlurH);
         texBlurH = -1;
      }

      if (texBlurV != -1) {
         GL11.glDeleteTextures(texBlurV);
         texBlurV = -1;
      }

      shadersOk = false;
   }

   private static String loadShader(String var0) {
      try {
         String var2;
         try (InputStream var1 = ESPGlowRenderer.class.getResourceAsStream("/assets/water/shaders/" + var0)) {
            if (var1 == null) {
               throw new RuntimeException("Not found: " + var0);
            }

            var2 = new String(var1.readAllBytes(), StandardCharsets.UTF_8);
         }

         return var2;
      } catch (Exception var6) {
         throw new RuntimeException("Load failed: " + var0, var6);
      }
   }

   private static int compileProgram(String var0, String var1) {
      int var2 = GL20.glCreateShader(35633);
      GL20.glShaderSource(var2, var0);
      GL20.glCompileShader(var2);
      if (GL20.glGetShaderi(var2, 35713) == 0) {
         throw new RuntimeException("VSH: " + GL20.glGetShaderInfoLog(var2));
      } else {
         int var3 = GL20.glCreateShader(35632);
         GL20.glShaderSource(var3, var1);
         GL20.glCompileShader(var3);
         if (GL20.glGetShaderi(var3, 35713) == 0) {
            throw new RuntimeException("FSH: " + GL20.glGetShaderInfoLog(var3));
         } else {
            int var4 = GL20.glCreateProgram();
            GL20.glAttachShader(var4, var2);
            GL20.glAttachShader(var4, var3);
            GL20.glLinkProgram(var4);
            GL20.glDeleteShader(var2);
            GL20.glDeleteShader(var3);
            return var4;
         }
      }
   }
}

