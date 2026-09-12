package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class PortalESP extends Module {
   private final BooleanSetting netherPortal = new BooleanSetting("NetherPortal", true);
   private final BooleanSetting endPortal = new BooleanSetting("EndPortal", true);
   private final BooleanSetting endGateway = new BooleanSetting("EndGateway", true);
   private final ColorSetting netherColor = new ColorSetting("Nether Color", -1062190849);
   private final ColorSetting endColor = new ColorSetting("End Color", -1071579200);
   private final ColorSetting gatewayColor = new ColorSetting("Gateway Color", -1057003777);
   private final NumberSetting radius = new NumberSetting("Radius", 96.0, 16.0, 256.0, 8.0);
   private final NumberSetting lineWidth = new NumberSetting("Line Width", 1.5, 0.5, 5.0, 0.1);
   private final ModeSetting style = new ModeSetting("Style", "PerBlock", "PerBlock", "MinimalBoundingBox");
   private static final int intVal = 16;
   private static final Map<Long, PortalESP.Kind> map = new HashMap<>();
   private static final Deque<Long> deque = new ArrayDeque<>();
   private static final Set<Long> set = new HashSet<>();
   private static boolean bool = false;
   private static volatile boolean bool2 = false;

   public PortalESP() {
      super("PortalESP", "Highlights nether/end portals and end gateways", Category.RENDER);
      this.run6(
         new Setting[]{
            this.netherPortal, this.endPortal, this.endGateway, this.netherColor, this.endColor, this.gatewayColor, this.radius, this.lineWidth, this.style
         }
      );
      this.netherColor.setVisible(this.netherPortal::getValue);
      this.endColor.setVisible(this.endPortal::getValue);
      this.gatewayColor.setVisible(this.endGateway::getValue);
   }

   @Override
   public void run() {
      bool2 = true;
      if (!bool) {
         bool = true;
         ClientChunkEvents.CHUNK_LOAD.register(PortalESP::run12);
         ClientChunkEvents.CHUNK_UNLOAD.register(PortalESP::run16);
      }

      if (class310.world != null && class310.player != null) {
         int var1 = this.radius.getValueInt();
         int var2 = Math.max(1, var1 + 15 >> 4);
         int var3 = (int)Math.floor(class310.player.getX()) >> 4;
         int var4 = (int)Math.floor(class310.player.getZ()) >> 4;

         for (int var5 = -var2; var5 <= var2; var5++) {
            for (int var6 = -var2; var6 <= var2; var6++) {
               run8(var3 + var5, var4 + var6);
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
         this.run9();
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null && !map.isEmpty()) {
         Vec3d var3 = class310.player.getEntityPos();
         double var4 = this.radius.getValue();
         double var6 = var4 * var4;
         float var8 = this.lineWidth.getValueFloat();
         boolean var9 = this.style.check("MinimalBoundingBox");
         int var10 = this.netherColor.getValue();
         int var11 = this.endColor.getValue();
         int var12 = this.gatewayColor.getValue();
         boolean var13 = this.netherPortal.getValue();
         boolean var14 = this.endPortal.getValue();
         boolean var15 = this.endGateway.getValue();
         if (var9) {
            this.run6(var3, var6, var8, var13, var14, var15, var10, var11, var12);
         } else {
            this.run5(var3, var6, var8, var13, var14, var15, var10, var11, var12);
         }
      }
   }

   private void run5(Vec3d var1, double var2, float var4, boolean var5, boolean var6, boolean var7, int var8, int var9, int var10) {
      for (Entry var12 : map.entrySet()) {
         PortalESP.Kind var13 = (PortalESP.Kind)var12.getValue();
         if (check(var13, var5, var6, var7)) {
            BlockPos var14 = BlockPos.fromLong((Long)var12.getKey());
            double var15 = var14.getX() + 0.5 - var1.x;
            double var17 = var14.getY() + 0.5 - var1.y;
            double var19 = var14.getZ() + 0.5 - var1.z;
            if (!(var15 * var15 + var17 * var17 + var19 * var19 > var2)) {
               ListUtils.run5(
                  new Box(var14.getX(), var14.getY(), var14.getZ(), var14.getX() + 1.0, var14.getY() + 1.0, var14.getZ() + 1.0),
                  intOf(var13, var8, var9, var10),
                  var4,
                  true
               );
            }
         }
      }
   }

   private void run6(Vec3d var1, double var2, float var4, boolean var5, boolean var6, boolean var7, int var8, int var9, int var10) {
      HashSet var11 = new HashSet(map.size() * 2);
      ArrayDeque var12 = new ArrayDeque();

      for (Entry var14 : map.entrySet()) {
         long var15 = (Long)var14.getKey();
         if (!var11.contains(var15)) {
            PortalESP.Kind var17 = (PortalESP.Kind)var14.getValue();
            if (!check(var17, var5, var6, var7)) {
               var11.add(var15);
            } else {
               var11.add(var15);
               var12.clear();
               var12.push(var15);
               int var18 = Integer.MAX_VALUE;
               int var19 = Integer.MAX_VALUE;
               int var20 = Integer.MAX_VALUE;
               int var21 = Integer.MIN_VALUE;
               int var22 = Integer.MIN_VALUE;
               int var23 = Integer.MIN_VALUE;

               while (!var12.isEmpty()) {
                  long var24 = (Long)var12.pop();
                  BlockPos var26 = BlockPos.fromLong(var24);
                  int var27 = var26.getX();
                  int var28 = var26.getY();
                  int var29 = var26.getZ();
                  if (var27 < var18) {
                     var18 = var27;
                  }

                  if (var28 < var19) {
                     var19 = var28;
                  }

                  if (var29 < var20) {
                     var20 = var29;
                  }

                  if (var27 > var21) {
                     var21 = var27;
                  }

                  if (var28 > var22) {
                     var22 = var28;
                  }

                  if (var29 > var23) {
                     var23 = var29;
                  }

                  run7(var27 + 1, var28, var29, var17, var11, var12);
                  run7(var27 - 1, var28, var29, var17, var11, var12);
                  run7(var27, var28 + 1, var29, var17, var11, var12);
                  run7(var27, var28 - 1, var29, var17, var11, var12);
                  run7(var27, var28, var29 + 1, var17, var11, var12);
                  run7(var27, var28, var29 - 1, var17, var11, var12);
               }

               double var36 = (var18 + var21 + 1) * 0.5;
               double var37 = (var19 + var22 + 1) * 0.5;
               double var38 = (var20 + var23 + 1) * 0.5;
               double var30 = var36 - var1.x;
               double var32 = var37 - var1.y;
               double var34 = var38 - var1.z;
               if (!(var30 * var30 + var32 * var32 + var34 * var34 > var2)) {
                  ListUtils.run5(new Box(var18, var19, var20, var21 + 1.0, var22 + 1.0, var23 + 1.0), intOf(var17, var8, var9, var10), var4, true);
               }
            }
         }
      }
   }

   private static void run7(int var0, int var1, int var2, PortalESP.Kind var3, Set<Long> var4, Deque<Long> var5) {
      long var6 = BlockPos.asLong(var0, var1, var2);
      if (!var4.contains(var6)) {
         PortalESP.Kind var8 = map.get(var6);
         if (var8 == var3) {
            var4.add(var6);
            var5.push(var6);
         }
      }
   }

   private static boolean check(PortalESP.Kind var0, boolean var1, boolean var2, boolean var3) {
      return switch (var0) {
         case NETHER -> var1;
         case END -> var2;
         case GATEWAY -> var3;
      };
   }

   private static int intOf(PortalESP.Kind var0, int var1, int var2, int var3) {
      return switch (var0) {
         case NETHER -> var1;
         case END -> var2;
         case GATEWAY -> var3;
      };
   }

   private static void run8(int var0, int var1) {
      long var2 = longOf(var0, var1);
      if (set.add(var2)) {
         deque.addLast(var2);
      }
   }

   private void run9() {
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
               run10(var6);
            }
         }
      }
   }

   private static void run10(WorldChunk var0) {
      int var1 = var0.getPos().x << 4;
      int var2 = var0.getPos().z << 4;
      int var3 = var0.getBottomY();

      ChunkSection[] var4;
      try {
         var4 = var0.getSectionArray();
      } catch (Throwable var17) {
         return;
      }

      for (int var5 = 0; var5 < var4.length; var5++) {
         ChunkSection var6 = var4[var5];
         if (var6 != null && !var6.isEmpty()) {
            boolean var7;
            try {
               var7 = var6.getBlockStateContainer().hasAny(PortalESP::check2);
            } catch (Throwable var18) {
               continue;
            }

            if (var7) {
               int var8 = var3 + (var5 << 4);

               for (int var9 = 0; var9 < 16; var9++) {
                  int var10 = var8 + var9;

                  for (int var11 = 0; var11 < 16; var11++) {
                     for (int var12 = 0; var12 < 16; var12++) {
                        BlockState var13;
                        try {
                           var13 = var6.getBlockState(var11, var9, var12);
                        } catch (Throwable var19) {
                           continue;
                        }

                        PortalESP.Kind var14 = portalESPKindOf(var13);
                        if (var14 != null) {
                           long var15 = BlockPos.asLong(var1 + var11, var10, var2 + var12);
                           map.put(var15, var14);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean check2(BlockState var0) {
      Block var1 = var0.getBlock();
      return var1 == Blocks.NETHER_PORTAL || var1 == Blocks.END_PORTAL || var1 == Blocks.END_GATEWAY;
   }

   private static PortalESP.Kind portalESPKindOf(BlockState var0) {
      Block var1 = var0.getBlock();
      if (var1 == Blocks.NETHER_PORTAL) {
         return PortalESP.Kind.NETHER;
      } else if (var1 == Blocks.END_PORTAL) {
         return PortalESP.Kind.END;
      } else {
         return var1 == Blocks.END_GATEWAY ? PortalESP.Kind.GATEWAY : null;
      }
   }

   private static void run11(int var0, int var1) {
      if (!map.isEmpty()) {
         ArrayList var2 = new ArrayList();

         for (long var4 : map.keySet()) {
            BlockPos var6 = BlockPos.fromLong(var4);
            if (var6.getX() >> 4 == var0 && var6.getZ() >> 4 == var1) {
               var2.add(var4);
            }
         }

         for (long var8 : (java.util.List<Long>)var2) {
            map.remove(var8);
         }
      }
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

   private static void run16(ClientWorld var0, WorldChunk var1) {
      if (var1 != null) {
         run11(var1.getPos().x, var1.getPos().z);
      }
   }

   private static void run12(ClientWorld var0, WorldChunk var1) {
      if (bool2 && var1 != null) {
         run8(var1.getPos().x, var1.getPos().z);
      }
   }

   private static enum Kind {
      NETHER,
      END,
      GATEWAY;

      private static final PortalESP.Kind[] portalESPKindArray = getPortalESPKindArray();

      private static PortalESP.Kind[] getPortalESPKindArray() {
         return new PortalESP.Kind[]{NETHER, END, GATEWAY};
      }
   }
}

