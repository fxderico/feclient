package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class SeedChunkFinder extends Module {
   private static final AtomicReference<SeedChunkFinder.State> atomicReference = new AtomicReference<>(SeedChunkFinder.State.IDLE);
   private static final AtomicReference<Long> atomicReference2 = new AtomicReference<>(Long.MIN_VALUE);
   private static final AtomicReference<String> atomicReference3 = new AtomicReference<>("idle");
   private final StringSetting seed = new StringSetting("Seed", "", 24);
   private final BooleanSetting autoCrack = new BooleanSetting("AutoCrack", true);
   private final NumberSetting minSamples = new NumberSetting("MinSamples", 20.0, 5.0, 200.0, 1.0);
   private final NumberSetting verifySamples = new NumberSetting("VerifySamples", 2.0, 1.0, 10.0, 1.0);
   private final BooleanSetting chatNotify = new BooleanSetting("ChatNotify", true);
   private final BooleanSetting oldHeuristic = new BooleanSetting("OldHeuristic", false);
   private final NumberSetting scanBelowY = new NumberSetting("ScanBelowY", 0.0, -64.0, 320.0, 1.0);
   private static final Set<Long> set = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private static final AtomicInteger atomicInteger = new AtomicInteger();
   private static final AtomicInteger atomicInteger2 = new AtomicInteger();
   private static final Set<Long> set2 = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private static boolean bool = false;
   private static volatile boolean bool2 = false;
   private static volatile int intVal = 0;
   private static volatile boolean bool3 = false;
   private volatile Thread thread;
   private volatile long longVal;
   private static final long longVal2 = 1200000L;
   private boolean bool4;

   public SeedChunkFinder() {
      super("SeedChunkFinder", "Auto-detects the server world seed from bedrock samples + hashedSeed verify", Category.DONUTSMP);
      this.run6(new Setting[]{this.seed, this.autoCrack, this.minSamples, this.verifySamples, this.chatNotify, this.oldHeuristic, this.scanBelowY});
   }

   @Override
   public void run() {
      bool2 = true;
      intVal = this.scanBelowY.getValueInt();
      bool3 = this.oldHeuristic.getValue();
      this.bool4 = false;
      run4();
      if (atomicReference.get() == SeedChunkFinder.State.IDLE || atomicReference.get() == SeedChunkFinder.State.FAILED) {
         this.run12();
         run11(SeedChunkFinder.State.SAMPLING, "waiting for bedrock samples");
      }

      String var1 = this.seed.getValue();
      if (!var1.isEmpty()) {
         this.run6(var1);
      }

      try {
         if (class310 != null && class310.world != null && class310.player != null) {
            int var2 = class310.player.getChunkPos().x;
            int var3 = class310.player.getChunkPos().z;
            int var4 = class310.options != null ? (Integer)class310.options.getViewDistance().getValue() : 8;

            for (int var5 = -var4; var5 <= var4; var5++) {
               for (int var6 = -var4; var6 <= var4; var6++) {
                  WorldChunk var7 = class310.world.getChunkManager().getWorldChunk(var2 + var5, var3 + var6, false);
                  if (var7 != null) {
                     run5(var7);
                  }
               }
            }
         }
      } catch (Throwable var8) {
      }
   }

   @Override
   public void run2() {
      bool2 = false;
      set2.clear();
      this.run10();
   }

   @Override
   public void run3() {
      intVal = this.scanBelowY.getValueInt();
      bool3 = this.oldHeuristic.getValue();
      String var1 = this.seed.getValue();
      if (!var1.isEmpty() && atomicReference.get() != SeedChunkFinder.State.CONFIRMED) {
         this.run6(var1);
      }

      if (this.autoCrack.getValue()
         && atomicReference.get() == SeedChunkFinder.State.SAMPLING
         && getInt2() >= this.minSamples.getValueInt()
         && this.thread == null) {
         this.run8();
      }

      if (atomicReference.get() == SeedChunkFinder.State.CRACKING && System.currentTimeMillis() - this.longVal > 1200000L) {
         this.run10();
         run11(SeedChunkFinder.State.FAILED, "timed out — sample more chunks or plug in a real reverser");
      }

      if (!this.bool4 && bool2) {
         this.bool4 = true;
         this.run13("§7[SeedChunkFinder] auto-detect armed — walk around (Nether preferred) to gather bedrock samples.");
      }
   }

   private static void run4() {
      if (!bool) {
         bool = true;
         ClientChunkEvents.CHUNK_LOAD.register(SeedChunkFinder::run16);
      }
   }

   private static void run5(WorldChunk var0) {
      int var1 = var0.getPos().x;
      int var2 = var0.getPos().z;
      int var3 = var0.getBottomY();
      ChunkSection[] var4 = var0.getSectionArray();
      int var5 = 0;
      boolean var6 = var5 == 0 || var5 == 1;
      int[] var7;
      if (var5 == 0) {
         var7 = new int[]{-64, -63, -62, -61, -60, -59};
      } else if (var5 == 1) {
         var7 = new int[]{0, 1, 2, 3, 4, 123, 124, 125, 126, 127};
      } else {
         var7 = new int[0];
      }

      boolean var8 = false;
      int var9 = intVal;

      for (int var10 = 0; var10 < var4.length; var10++) {
         ChunkSection var11 = var4[var10];
         if (var11 != null && !var11.isEmpty()) {
            int var12 = var3 + (var10 << 4);
            if (bool3 && !var8 && var12 < var9) {
               int var13 = Math.min(16, var9 - var12);

               label122:
               for (int var14 = 0; var14 < var13; var14++) {
                  for (int var15 = 0; var15 < 16; var15++) {
                     for (int var16 = 0; var16 < 16; var16++) {
                        BlockState var17;
                        try {
                           var17 = var11.getBlockState(var15, var14, var16);
                        } catch (Throwable var27) {
                           continue;
                        }

                        if (!var17.isAir()) {
                           set2.add(longOf(var1, var2));
                           var8 = true;
                           break label122;
                        }
                     }
                  }
               }
            }

            if (var6) {
               for (int var31 : var7) {
                  if (var31 >= var12 && var31 < var12 + 16) {
                     int var32 = var31 - var12;

                     for (int var18 = 0; var18 < 16; var18++) {
                        for (int var19 = 0; var19 < 16; var19++) {
                           BlockState var20;
                           try {
                              var20 = var11.getBlockState(var18, var32, var19);
                           } catch (Throwable var26) {
                              continue;
                           }

                           boolean var21 = var20.isOf(Blocks.BEDROCK);
                           int var22 = (var1 << 4) + var18;
                           int var23 = (var2 << 4) + var19;
                           long var24 = longOf2(var5, var22, var31, var23, var21);
                           if (set.add(var24)) {
                              (var5 == 0 ? atomicInteger : atomicInteger2).incrementAndGet();
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static int getInt_s() {
      try {
         if (class310 == null || class310.world == null) {
            return -1;
         }

         String var0 = class310.world.getRegistryKey().getValue().toString();
         if ("minecraft:overworld".equals(var0)) {
            return 0;
         }

         if ("minecraft:the_nether".equals(var0)) {
            return 1;
         }
      } catch (Throwable var1) {
      }

      return -1;
   }

   private static boolean check(long var0) {
      long var2 = getLong();
      if (var2 == 0L) {
         return false;
      } else {
         try {
            MessageDigest var4 = MessageDigest.getInstance("SHA-256");
            var4.update(ByteBuffer.allocate(8).putLong(var0).array());
            byte[] var5 = var4.digest();
            long var6 = ByteBuffer.wrap(var5, 0, 8).getLong();
            return var6 == var2;
         } catch (Throwable var8) {
            return false;
         }
      }
   }

   private static long getLong() {
      try {
         if (class310 == null || class310.world == null) {
            return 0L;
         }

         BiomeAccess var0 = class310.world.getBiomeAccess();

         for (Field var4 : var0.getClass().getDeclaredFields()) {
            if (var4.getType() == long.class) {
               var4.setAccessible(true);
               return var4.getLong(var0);
            }
         }
      } catch (Throwable var5) {
      }

      return 0L;
   }

   private void run6(String var1) {
      long var2;
      try {
         var2 = Long.parseLong(var1.trim());
      } catch (NumberFormatException var5) {
         return;
      }

      if (check(var2)) {
         this.run7(var2);
      }
   }

   private void run7(long var1) {
      if (atomicReference.get() != SeedChunkFinder.State.CONFIRMED || atomicReference2.get() != var1) {
         long var3 = getLong();
         if (var3 != 0L && !check(var1)) {
            run11(SeedChunkFinder.State.FAILED, "candidate " + var1 + " failed hashedSeed check");
         } else {
            atomicReference2.set(var1);
            run11(SeedChunkFinder.State.CONFIRMED, "seed = " + var1);

            try {
               this.seed.setValue(Long.toString(var1));
            } catch (Throwable var6) {
            }

            if (this.chatNotify.getValue()) {
               this.run13("§a[SeedChunkFinder] §fSeed: §a" + var1 + " §7(verified via hashedSeed)");
            }
         }
      }
   }

   private void run8() {
      run11(SeedChunkFinder.State.CRACKING, "reducing " + getInt2() + " bedrock samples…");
      this.longVal = System.currentTimeMillis();
      Thread var1 = new Thread(this::run9, "CodeEngine-SeedChunkFinder-Cracker");
      var1.setDaemon(true);
      var1.setPriority(1);
      this.thread = var1;
      var1.start();
   }

   private void run9() {
      try {
         try {
            if (this.isEnabled2()) {
               run11(SeedChunkFinder.State.FAILED, "server flat-bedrock detected — reversal impossible");
               return;
            }

            long var1 = System.currentTimeMillis() + 1200000L;

            while (System.currentTimeMillis() < var1 && atomicReference.get() == SeedChunkFinder.State.CRACKING && bool2) {
               Thread.sleep(1000L);
               if (atomicReference.get() == SeedChunkFinder.State.CONFIRMED) {
                  return;
               }
            }

            if (atomicReference.get() == SeedChunkFinder.State.CRACKING) {
               run11(SeedChunkFinder.State.FAILED, "no reverser wired in — port MiranCZ/BedrockSeedCracker (see javadoc)");
            }

            return;
         } catch (InterruptedException var7) {
            Thread.currentThread().interrupt();
         } catch (Throwable var8) {
            run11(SeedChunkFinder.State.FAILED, "reverser threw: " + var8.getClass().getSimpleName());
         }
      } finally {
         this.thread = null;
      }
   }

   private boolean isEnabled2() {
      int var1 = 0;
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      for (long var6 : set) {
         int var8 = (int)(var6 >>> 62 & 1L);
         if (var8 == 0) {
            int var9 = (int)(var6 >> 32 & 4095L) - 128;
            boolean var10 = (var6 >>> 63 & 1L) == 1L;
            if (var9 == -64) {
               var2++;
               if (var10) {
                  var1++;
               }
            }

            if (var9 == -63) {
               var4++;
               if (var10) {
                  var3++;
               }
            }
         }
      }

      return var2 >= 32 && var4 >= 32 && var1 == var2 && var3 == 0;
   }

   private void run10() {
      Thread var1 = this.thread;
      if (var1 != null) {
         var1.interrupt();
         this.thread = null;
      }
   }

   private static void run11(SeedChunkFinder.State var0, String var1) {
      atomicReference.set(var0);
      atomicReference3.set(var1);
   }

   private void run12() {
      set.clear();
      atomicInteger.set(0);
      atomicInteger2.set(0);
      set2.clear();
      atomicReference2.set(Long.MIN_VALUE);
   }

   private static int getInt2() {
      return atomicInteger.get() + atomicInteger2.get();
   }

   private void run13(String var1) {
      try {
         if (class310 == null || class310.inGameHud == null || class310.inGameHud.getChatHud() == null) {
            return;
         }

         class310.inGameHud.getChatHud().addMessage(Text.literal(var1));
      } catch (Throwable var3) {
      }
   }

   public void run14(DrawContext var1, float var2) {
      if (class310 != null && class310.world != null && class310.player != null) {
         this.run15(var1);
         if (this.oldHeuristic.getValue() && !set2.isEmpty()) {
            double var4 = this.scanBelowY.getValue();

            for (long var8 : new HashSet<>(set2)) {
               int var10 = (int)(var8 >> 32);
               int var11 = (int)var8;
               double var12 = var10 * 16.0;
               double var14 = var11 * 16.0;
               double var16 = var12 + 16.0;
               double var18 = var14 + 16.0;

               try {
                  ListUtils.run13(var12, var4, var14, var16, var18, 1717093631);
               } catch (Throwable var21) {
               }
            }
         }
      }
   }

   private void run15(DrawContext var1) {
      TextRenderer var2 = class310.textRenderer;
      if (var2 != null) {
         SeedChunkFinder.State var3 = atomicReference.get();
         String var4;
         int var5;
         switch (var3) {
            case SAMPLING:
               var4 = "Seed: sampling " + getInt2() + "/" + this.minSamples.getValueInt();
               var5 = -8875;
               break;
            case CRACKING:
               var4 = "Seed: cracking...";
               var5 = -11141121;
               break;
            case CONFIRMED:
               Object var6 = atomicReference2.get();
               var4 = "Seed: " + (var6 != null && (Long)var6 != Long.MIN_VALUE ? var6 : "?") + " ✓";
               var5 = -11141291;
               break;
            case FAILED:
               var4 = "Seed: failed - " + atomicReference3.get();
               var5 = -43691;
               break;
            default:
               var4 = "Seed: idle";
               var5 = -5592406;
         }

         int var18 = var1.getScaledWindowWidth();
         int var7 = var2.getWidth(var4);
         int var10 = var7 + 8;
         Objects.requireNonNull(var2);
         int var12 = var18 - var10 - 4;
         int var14 = var12 + var10;

         try {
            var1.fill(var12, 4, var14, 19, -1342177280);
            var1.fill(var12, 4, var14, 5, -14671840);
            var1.fill(var12, 18, var14, 19, -14671840);
            var1.fill(var12, 4, var12 + 1, 19, -14671840);
            var1.fill(var14 - 1, 4, var14, 19, -14671840);
            var1.drawTextWithShadow(var2, var4, var12 + 4, 7, var5);
         } catch (Throwable var17) {
         }
      }
   }

   private static long longOf(int var0, int var1) {
      return (long)var0 << 32 | var1 & 4294967295L;
   }

   private static long longOf2(int var0, int var1, int var2, int var3, boolean var4) {
      long var5 = (var1 + 131072 & 262143L) << 44;
      long var7 = (var2 + 128 & 4095L) << 32;
      long var9 = (var3 + 131072 & 262143L) << 14;
      long var11 = (long)(var0 & 1) << 62;
      long var13 = (long)(var4 ? 1 : 0) << 63;
      return var13 | var11 | var5 | var7 | var9;
   }

   @Override
   public String getString3() {
      SeedChunkFinder.State var1 = atomicReference.get();
      if (var1 != SeedChunkFinder.State.CONFIRMED) {
         if (var1 == SeedChunkFinder.State.SAMPLING) {
            return "§e" + getInt2() + "/" + this.minSamples.getValueInt();
         } else if (var1 == SeedChunkFinder.State.CRACKING) {
            return "§bcracking";
         } else {
            return var1 == SeedChunkFinder.State.FAILED ? "§cfailed" : null;
         }
      } else {
         Long var2 = atomicReference2.get();
         return var2 != null && var2 != Long.MIN_VALUE ? "§anull" : null;
      }
   }

   private static void run16(ClientWorld var0, WorldChunk var1) {
      if (bool2 && var1 != null) {
         try {
            run5(var1);
         } catch (Throwable var3) {
         }
      }
   }

   private static enum State {
      IDLE,
      SAMPLING,
      CRACKING,
      CONFIRMED,
      FAILED;

      private static final SeedChunkFinder.State[] seedChunkFinderStateArray = getSeedChunkFinderStateArray();

      private static SeedChunkFinder.State[] getSeedChunkFinderStateArray() {
         return new SeedChunkFinder.State[]{IDLE, SAMPLING, CRACKING, CONFIRMED, FAILED};
      }
   }
}

