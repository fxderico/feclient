package dev.fede.mixin.sodium;

import dev.fede.module.impl.XRayModule;
import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// remap = false: refmap-based remapping only exists for vanilla Minecraft
// members (Fabric Loader supplies that mapping data for every mod
// uniformly). Sodium is a third-party mod with no such mapping database.
@Mixin(value = AbstractBlockRenderContext.class, remap = false)
public abstract class SodiumXRayCullMixin {

   @Shadow
   protected BlockState state;
   @Shadow
   protected BlockPos pos;

   // NOT shadowed: Sodium's own "level" field is typed to whatever its
   // internal BlockView-family interface is (its distributed jar shows
   // net.minecraft.class_1920), but our own compiled code, once run through
   // Loom's remapJar pass, ends up expecting net.minecraft.class_1922 for
   // the Yarn type BlockView — a straight-up mismatch, which is why the
   // vanilla mixins never needed this workaround but this one did:
   // "@Shadow field level was not located ... No refMap loaded." plain
   // reflection sidesteps the whole descriptor-matching problem — we only
   // need one method off it (getBlockState(BlockPos)), which every
   // BlockView-family interface carries regardless of which exact one
   // Sodium's field is statically typed as.
   private static Field LEVEL_FIELD;

   // real crash, seen live: this used to be one shared static Method field,
   // resolved once against whichever levelHolder class hit it first (real
   // terrain rendering: Sodium's LevelSlice) — then reused for every later
   // call regardless of type. AbstractBlockRenderContext is ALSO used for
   // non-terrain single-block rendering (item icons etc), where "level" is
   // a different, unrelated BlockView implementation (class_12073 in the
   // crash log). invoking a Method resolved against one class on an
   // instance of an unrelated one throws IllegalArgumentException — which
   // isn't a ReflectiveOperationException, so the old catch block didn't
   // even catch it. keyed cache fixes the mismatch; broader catch below is
   // a second line of defense against this whole class of surprise.
   private static final Map<Class<?>, Method> GET_BLOCK_STATE_CACHE = new ConcurrentHashMap<>();

   private static Field levelField() {
      if (LEVEL_FIELD == null) {
         try {
            LEVEL_FIELD = AbstractBlockRenderContext.class.getDeclaredField("level");
            LEVEL_FIELD.setAccessible(true);
         } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("feclient: sodium's AbstractBlockRenderContext.level field is gone", e);
         }
      }
      return LEVEL_FIELD;
   }

   private static Method getBlockStateMethod(Object levelHolder) {
      return GET_BLOCK_STATE_CACHE.computeIfAbsent(levelHolder.getClass(), cls -> {
         // find by signature (BlockPos -> BlockState) instead of guessing an
         // intermediary method name — every BlockView-family interface has
         // exactly one method shaped like this (it's the core method of the
         // whole hierarchy), so it's an unambiguous match regardless of
         // which exact interface/mapping this particular level object uses.
         for (Method m : cls.getMethods()) {
            if (m.getReturnType() == BlockState.class
                  && m.getParameterCount() == 1
                  && m.getParameterTypes()[0] == BlockPos.class) {
               return m;
            }
         }
         throw new IllegalStateException("feclient: couldn't find getBlockState(BlockPos) on " + cls);
      });
   }

   @Inject(method = "shouldDrawSide(Lnet/minecraft/util/math/Direction;)Z", at = @At("RETURN"), cancellable = true)
   private void fe$sodiumXrayCull(Direction direction, CallbackInfoReturnable<Boolean> cir) {
      if (!XRayModule.ACTIVE || this.state == null || this.pos == null) {
         return;
      }

      boolean selfVisible = XRayModule.isVisible(this.state.getBlock());
      if (!selfVisible) {
         cir.setReturnValue(false);
         return;
      }

      try {
         Object levelHolder = levelField().get(this);
         if (levelHolder == null) return;
         Method getBlockState = getBlockStateMethod(levelHolder);
         BlockState adjacentState = (BlockState) getBlockState.invoke(levelHolder, this.pos.offset(direction));
         if (!XRayModule.isVisible(adjacentState.getBlock())) {
            cir.setReturnValue(true);
         }
      } catch (Exception e) {
         // never let a reflection surprise crash rendering — worst case xray
         // just doesn't force-draw this one face, sodium's own result stands
      }
   }
}
