package dev.fede.nyx.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.RenderSetup.OutlineMode;
import net.minecraft.util.Identifier;

public final class Sampler0 {
   private static final ConcurrentHashMap<Identifier, RenderLayer> concurrentHashMap = new ConcurrentHashMap<>();
   private static volatile boolean bool = false;

   private Sampler0() {
   }

   public static boolean isEnabled() {
      return Sampler1.renderPipeline != null;
   }

   public static RenderLayer class1921Of(Identifier var0) {
      if (var0 == null) {
         return null;
      } else {
         RenderPipeline var1 = Sampler1.renderPipeline;
         return var1 == null ? null : concurrentHashMap.computeIfAbsent(var0, Sampler0::class1921Of2);
      }
   }

   private static RenderLayer class1921Of2(Identifier var0) {
      RenderPipeline var1 = Sampler1.renderPipeline;
      if (var1 == null) {
         return null;
      } else {
         try {
            RenderSetup var2 = RenderSetup.builder(var1)
               .texture("Sampler0", var0)
               .useLightmap()
               .useOverlay()
               .crumbling()
               .translucent()
               .outlineMode(OutlineMode.NONE)
               .build();
            String var3 = "codeengine_chams_" + var0.getNamespace() + "_" + var0.getPath().replace('/', '_');
            return RenderLayer.of(var3, var2);
         } catch (Throwable var4) {
            if (!bool) {
               bool = true;
               System.err
                  .println(
                     "[ChamsRenderPipeline] Failed to build Chams RenderLayer for "
                        + var0
                        + " ("
                        + var4.getClass().getSimpleName()
                        + ": "
                        + var4.getMessage()
                        + ")"
                  );
               var4.printStackTrace();
            }

            return null;
         }
      }
   }
}

