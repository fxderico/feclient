package dev.fede.render.nanovg;

import org.lwjgl.opengl.GL33C;

public final class GlStateSnapshot {
   private int program;
   private int vao;
   private int arrayBuffer;
   private int uniformBuffer;
   private int uniformBufferBase0;
   private int activeTexture;
   private int texture2D;
   private int sampler0;
   private int blendSrcRgb;
   private int blendDstRgb;
   private int blendSrcAlpha;
   private int blendDstAlpha;
   private int blendEqRgb;
   private int blendEqAlpha;
   private int cullFaceMode;
   private int frontFace;
   private int depthFunc;
   private int stencilFunc;
   private int stencilRef;
   private int stencilValueMask;
   private int stencilWriteMask;
   private int stencilFail;
   private int stencilPassDepthFail;
   private int stencilPassDepthPass;
   private boolean blend;
   private boolean cullFace;
   private boolean depthTest;
   private boolean scissorTest;
   private boolean stencilTest;
   private boolean depthMask;
   private final int[] viewport = new int[4];
   private final int[] scissorBox = new int[4];
   private final boolean[] colorMask = new boolean[4];
   private int unpackAlignment;
   private int unpackRowLength;
   private int unpackSkipPixels;
   private int unpackSkipRows;

   private GlStateSnapshot() {
   }

   public static GlStateSnapshot capture() {
      GlStateSnapshot s = new GlStateSnapshot();
      s.program = GL33C.glGetInteger(35725);
      s.vao = GL33C.glGetInteger(34229);
      s.arrayBuffer = GL33C.glGetInteger(34964);
      s.uniformBuffer = GL33C.glGetInteger(35368);
      s.uniformBufferBase0 = GL33C.glGetIntegeri(35368, 0);
      s.activeTexture = GL33C.glGetInteger(34016);
      GL33C.glActiveTexture(33984);
      s.texture2D = GL33C.glGetInteger(32873);
      s.sampler0 = GL33C.glGetInteger(35097);
      GL33C.glBindSampler(0, 0);
      GL33C.glActiveTexture(s.activeTexture);
      s.blend = GL33C.glIsEnabled(3042);
      s.blendSrcRgb = GL33C.glGetInteger(32969);
      s.blendDstRgb = GL33C.glGetInteger(32968);
      s.blendSrcAlpha = GL33C.glGetInteger(32971);
      s.blendDstAlpha = GL33C.glGetInteger(32970);
      s.blendEqRgb = GL33C.glGetInteger(32777);
      s.blendEqAlpha = GL33C.glGetInteger(34877);
      s.cullFace = GL33C.glIsEnabled(2884);
      s.cullFaceMode = GL33C.glGetInteger(2885);
      s.frontFace = GL33C.glGetInteger(2886);
      s.depthTest = GL33C.glIsEnabled(2929);
      s.depthFunc = GL33C.glGetInteger(2932);
      s.depthMask = GL33C.glGetBoolean(2930);
      s.scissorTest = GL33C.glIsEnabled(3089);
      s.stencilTest = GL33C.glIsEnabled(2960);
      s.stencilFunc = GL33C.glGetInteger(2962);
      s.stencilRef = GL33C.glGetInteger(2967);
      s.stencilValueMask = GL33C.glGetInteger(2963);
      s.stencilWriteMask = GL33C.glGetInteger(2968);
      s.stencilFail = GL33C.glGetInteger(2964);
      s.stencilPassDepthFail = GL33C.glGetInteger(2965);
      s.stencilPassDepthPass = GL33C.glGetInteger(2966);
      GL33C.glGetIntegerv(2978, s.viewport);
      GL33C.glGetIntegerv(3088, s.scissorBox);
      int[] mask = new int[4];
      GL33C.glGetIntegerv(3107, mask);

      for (int i = 0; i < 4; i++) {
         s.colorMask[i] = mask[i] != 0;
      }

      s.unpackAlignment = GL33C.glGetInteger(3317);
      s.unpackRowLength = GL33C.glGetInteger(3314);
      s.unpackSkipPixels = GL33C.glGetInteger(3316);
      s.unpackSkipRows = GL33C.glGetInteger(3315);
      return s;
   }

   public void restore() {
      GL33C.glUseProgram(this.program);
      GL33C.glBindVertexArray(this.vao);
      GL33C.glBindBuffer(34962, this.arrayBuffer);
      GL33C.glBindBufferBase(35345, 0, this.uniformBufferBase0);
      GL33C.glBindBuffer(35345, this.uniformBuffer);
      GL33C.glActiveTexture(33984);
      GL33C.glBindTexture(3553, this.texture2D);
      GL33C.glBindSampler(0, this.sampler0);
      GL33C.glActiveTexture(this.activeTexture);
      setEnabled(3042, this.blend);
      GL33C.glBlendFuncSeparate(this.blendSrcRgb, this.blendDstRgb, this.blendSrcAlpha, this.blendDstAlpha);
      GL33C.glBlendEquationSeparate(this.blendEqRgb, this.blendEqAlpha);
      setEnabled(2884, this.cullFace);
      GL33C.glCullFace(this.cullFaceMode);
      GL33C.glFrontFace(this.frontFace);
      setEnabled(2929, this.depthTest);
      GL33C.glDepthFunc(this.depthFunc);
      GL33C.glDepthMask(this.depthMask);
      setEnabled(3089, this.scissorTest);
      setEnabled(2960, this.stencilTest);
      GL33C.glStencilFunc(this.stencilFunc, this.stencilRef, this.stencilValueMask);
      GL33C.glStencilMask(this.stencilWriteMask);
      GL33C.glStencilOp(this.stencilFail, this.stencilPassDepthFail, this.stencilPassDepthPass);
      GL33C.glViewport(this.viewport[0], this.viewport[1], this.viewport[2], this.viewport[3]);
      GL33C.glScissor(this.scissorBox[0], this.scissorBox[1], this.scissorBox[2], this.scissorBox[3]);
      GL33C.glColorMask(this.colorMask[0], this.colorMask[1], this.colorMask[2], this.colorMask[3]);
      GL33C.glPixelStorei(3317, this.unpackAlignment);
      GL33C.glPixelStorei(3314, this.unpackRowLength);
      GL33C.glPixelStorei(3316, this.unpackSkipPixels);
      GL33C.glPixelStorei(3315, this.unpackSkipRows);
   }

   private static void setEnabled(int cap, boolean enabled) {
      if (enabled) {
         GL33C.glEnable(cap);
      } else {
         GL33C.glDisable(cap);
      }
   }
}

