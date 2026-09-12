package dev.fede.nyx.module.modules.render;

import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.entity.Entity;

import net.minecraft.client.MinecraftClient;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.Matrix4fUtils;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix3x2fStack;

public class SpawnerESPModule extends Module {
   private final ColorSetting outline = new ColorSetting("Outline", -47958);
   private final NumberSetting lineWidth = new NumberSetting("Line Width", 1.5, 0.5, 5.0, 0.1);
   private final NumberSetting pulseHz = new NumberSetting("Pulse Hz", 1.0, 0.0, 4.0, 0.1);
   private final NumberSetting scanRadius = new NumberSetting("Scan Radius", 96.0, 16.0, 256.0, 8.0);
   private final BooleanSetting showMobPreview = new BooleanSetting("Show Mob Preview", true);
   private final NumberSetting labelScale = new NumberSetting("Label Scale", 1.0, 0.5, 2.0, 0.1);
   private static final int intVal = 16;
   private static final Map<Long, SpawnerESPModule.Inner2> map = new LinkedHashMap<>();
   private static final Deque<Long> deque = new ArrayDeque<>();
   private static final Set<Long> set = new HashSet<>();
   private static boolean bool = false;
   private static volatile boolean bool2 = false;

   public SpawnerESPModule() {
      super("SpawnerESP", "Highlights mob spawners with pulsing outlines", Category.RENDER);
      this.run6(new Setting[]{this.outline, this.lineWidth, this.pulseHz, this.scanRadius, this.showMobPreview, this.labelScale});
      this.labelScale.visibleWhen(this.showMobPreview::getValue);
   }

   @Override
   public void run() {
      bool2 = true;
      if (!bool) {
         bool = true;
         ClientChunkEvents.CHUNK_LOAD.register(SpawnerESPModule::run10);
         ClientChunkEvents.CHUNK_UNLOAD.register(SpawnerESPModule::run16);
      }

      if (class310.world != null && class310.player != null) {
         int var1 = this.scanRadius.getValueInt();
         int var2 = Math.max(1, var1 + 15 >> 4);
         int var3 = (int)Math.floor(class310.player.getX()) >> 4;
         int var4 = (int)Math.floor(class310.player.getZ()) >> 4;

         for (int var5 = -var2; var5 <= var2; var5++) {
            for (int var6 = -var2; var6 <= var2; var6++) {
               run6(var3 + var5, var4 + var6);
            }
         }
      }
   }

   @Override
   public void run2() {
      bool2 = false;
      deque.clear();
      set.clear();
      map.clear();
   }

   @Override
   public void run3() {
      if (class310.world != null && class310.player != null) {
         this.run7();
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null) {
         if (!map.isEmpty()) {
            Vec3d var3 = class310.player.getEntityPos();
            double var4 = this.scanRadius.getValue();
            double var6 = var4 * var4;
            float var8 = this.lineWidth.getValueFloat();
            int var9 = this.outline.getValue();
            int var10 = this.intOf(var9);
            boolean var11 = this.showMobPreview.getValue();
            TextRenderer var12 = class310.textRenderer;
            float var13 = this.labelScale.getValueFloat();

            for (Entry var15 : map.entrySet()) {
               SpawnerESPModule.Inner2 var16 = (SpawnerESPModule.Inner2)var15.getValue();
               BlockPos var17 = var16.class2338;
               double var18 = var17.getX() + 0.5;
               double var20 = var17.getY() + 0.5;
               double var22 = var17.getZ() + 0.5;
               double var24 = var18 - var3.x;
               double var26 = var20 - var3.y;
               double var28 = var22 - var3.z;
               if (!(var24 * var24 + var26 * var26 + var28 * var28 > var6)) {
                  ListUtils.run5(
                     new Box(var17.getX(), var17.getY(), var17.getZ(), var17.getX() + 1.0, var17.getY() + 1.0, var17.getZ() + 1.0), var10, var8, true
                  );
                  if (var11 && var12 != null) {
                     this.run5(var1, var12, var16, var17, var13);
                  }
               }
            }
         }
      }
   }

   private int intOf(int var1) {
      double var2 = this.pulseHz.getValue();
      if (var2 <= 0.0) {
         return var1;
      } else {
         double var4 = System.currentTimeMillis() % 100000L / 1000.0;
         double var6 = 0.7 + 0.3 * Math.sin(var4 * var2 * Math.PI * 2.0);
         int var8 = var1 >>> 24 & 0xFF;
         int var9 = Math.max(0, Math.min(255, (int)Math.round(var8 * var6)));
         return var9 << 24 | var1 & 16777215;
      }
   }

   private void run5(DrawContext var1, TextRenderer var2, SpawnerESPModule.Inner2 var3, BlockPos var4, float var5) {
      Text var6 = var3.getclass2561();
      if (var6 != null) {
         Vec3d var7 = new Vec3d(var4.getX() + 0.5, var4.getY() + 1.15, var4.getZ() + 0.5);
         double[] var8 = Matrix4fUtils.doubleArrayOf(var7);
         if (var8 != null) {
            String var9 = var6.getString();
            int var10 = var2.getWidth(var9);
            Objects.requireNonNull(var2);
            Matrix3x2fStack var12 = var1.getMatrices();
            var12.pushMatrix();
            var12.translate((float)var8[0], (float)var8[1]);
            var12.scale(var5, var5);
            int var13 = -var10 / 2;
            var1.fill(var13 - 2, -5, var13 + var10 + 2, 5, -1342177280);
            var1.drawText(var2, var9, var13, -4, -1, false);
            var12.popMatrix();
         }
      }
   }

   private static void run6(int var0, int var1) {
      long var2 = longOf(var0, var1);
      if (set.add(var2)) {
         deque.addLast(var2);
      }
   }

   private void run7() {
      if (!deque.isEmpty()) {
         int var1 = Math.min(16, deque.size());

         for (int var2 = 0; var2 < var1; var2++) {
            Long var3 = deque.pollFirst();
            if (var3 == null) {
               break;
            }

            set.remove(var3);
            int var4 = intOf2(var3);
            int var5 = intOf3(var3);

            WorldChunk var6;
            try {
               var6 = class310.world.getChunkManager().getWorldChunk(var4, var5, false);
            } catch (Throwable var8) {
               var6 = null;
            }

            if (var6 != null) {
               run8(var6);
            }
         }
      }
   }

   private static void run8(WorldChunk var0) {
      try {
         for (Entry var2 : var0.getBlockEntities().entrySet()) {
            BlockEntity var3 = (BlockEntity)var2.getValue();
            BlockEntityType var4 = var3.getType();
            boolean var5 = var4 == BlockEntityType.MOB_SPAWNER || var4 == BlockEntityType.TRIAL_SPAWNER;
            if (var5) {
               BlockPos var6 = ((BlockPos)var2.getKey()).toImmutable();
               map.put(var6.asLong(), new SpawnerESPModule.Inner2(var6, var3));
            }
         }
      } catch (Throwable var7) {
      }
   }

   private static void run9(int var0, int var1) {
      map.entrySet().removeIf(_e -> false);
   }

   private static long longOf(int var0, int var1) {
      return (long)var0 << 32 | var1 & 4294967295L;
   }

   private static int intOf2(long var0) {
      return (int)(var0 >> 32);
   }

   private static int intOf3(long var0) {
      return (int)var0;
   }

   @Override
   public String getString3() {
      int var1 = map.size();
      return var1 > 0 ? "§7" + var1 : null;
   }

   private static boolean check2(int var0, int var1, Entry var2) {
      String class2338 = null;
      BlockPos var3 = (BlockPos)var2.getValue();
      return var3.getX() >> 4 == var0 && var3.getZ() >> 4 == var1;
   }

   private static void run16(ClientWorld var0, WorldChunk var1) {
      if (bool2) {
         run9(var1.getPos().x, var1.getPos().z);
      }
   }

   private static void run10(ClientWorld var0, WorldChunk var1) {
      if (bool2) {
         run6(var1.getPos().x, var1.getPos().z);
      }
   }

final static class Inner1 {
   static final MinecraftClient class310 = MinecraftClient.getInstance();

   private Inner1() {
   }
}

final static class Inner2 {
   final BlockPos class2338;
   final BlockEntity class2586;
   private boolean bool;
   private Text class2561;

   Inner2(BlockPos var1, BlockEntity var2) {
      this.class2338 = var1;
      this.class2586 = var2;
   }

   Text getclass2561() {
      if (this.bool) {
         return this.class2561;
      } else {
         this.bool = true;

         try {
            if (this.class2586 instanceof MobSpawnerBlockEntity var1 && SpawnerESPModule.Inner1.class310.world != null) {
               Entity var4 = var1.getLogic().getRenderedEntity(SpawnerESPModule.Inner1.class310.world, this.class2338);
               if (var4 != null) {
                  this.class2561 = var4.getType().getName();
               }
            } else if (this.class2586 instanceof TrialSpawnerBlockEntity) {
               this.class2561 = Text.literal("Trial Spawner");
            }
         } catch (Throwable var3) {
            this.class2561 = null;
         }

         if (this.class2561 == null) {
            this.class2561 = Text.literal(stringOf(this.class2586));
         }

         return this.class2561;
      }
   }

   private static String stringOf(BlockEntity var0) {
      return var0 instanceof TrialSpawnerBlockEntity ? "Trial Spawner" : "Spawner";
   }
}
}

