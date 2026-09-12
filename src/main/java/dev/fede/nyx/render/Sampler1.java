package dev.fede.nyx.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;

final class Sampler1 {
   static final RenderPipeline renderPipeline;

   private Sampler1() {
   }

   static {
      RenderPipeline var0 = null;

      try {
         var0 = RenderPipeline.builder(new Snippet[]{RenderPipelines.ENTITY_SNIPPET})
            .withLocation(Identifier.of("codeengine", "pipeline/chams_entity_no_depth"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("PER_FACE_LIGHTING")
            .withSampler("Sampler1")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .build();
      } catch (Throwable var2) {
         System.err
            .println(
               "[ChamsRenderPipeline] Pipeline init FAILED ("
                  + var2.getClass().getSimpleName()
                  + ": "
                  + var2.getMessage()
                  + ") — Chams will fall back to vanilla depth-tested rendering."
            );
         var2.printStackTrace();
      }

      renderPipeline = var0;
   }
}

