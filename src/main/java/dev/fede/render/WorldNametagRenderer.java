package dev.fede.render;

import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import dev.fede.module.Modules;
import dev.fede.module.impl.NameProtectModule;
import dev.fede.module.impl.NameTagsModule;
import dev.fede.module.impl.SpawnerNametagsModule;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.theme.Theme;
import dev.fede.util.Colors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2fStack;

public final class WorldNametagRenderer {
   private static final int SPAWNER_ACCENT = -22733;
   private static final int MAX_TAGS = 80;

   private WorldNametagRenderer() {
   }

   public static void render(NVGRenderer vg) {
      if (WorldProjection.isValid()) {
         ModuleManager modules = FeClient.modules();
         if (modules != null) {
            MinecraftClient mc = MinecraftClient.getInstance();
            ClientWorld level = mc.world;
            ClientPlayerEntity self = mc.player;
            if (level != null && self != null) {
               NameTagsModule names = modules.nameTags;
               SpawnerNametagsModule spawners = modules.spawnerNametags;
               boolean doNames = names != null && names.isEnabled();
               boolean doSpawners = spawners != null && spawners.isEnabled() && spawners.nametag.get();
               if (doNames || doSpawners) {
                  float pt = WorldProjection.partialTick();
                  Theme theme = FeClient.themes().current();
                  Modules.HudModule hud = modules.hud;
                  int accent = hud != null && !hud.themeSync.get() ? hud.listColor.get() : theme.accent();
                  List<WorldNametagRenderer.Tag> tags = new ArrayList<>();
                  if (doNames) {
                     double range = names.range.get();
                     double rangeSq = range * range;
                     float nameScale = names.scale.getFloat();
                     float nameOpacity = (float)(names.opacity.get() / 100.0);
                     if (names.players.get()) {
                        NameProtectModule protect = modules.nameProtect;
                        boolean protecting = protect != null && protect.isEnabled();
                        boolean showSelf = names.self.get() && !mc.options.getPerspective().isFirstPerson();

                        for (AbstractClientPlayerEntity player : level.getPlayers()) {
                           boolean isSelf = player == self;
                           if ((!isSelf || showSelf) && !player.isSpectator() && player.isAlive()) {
                              double distSq = self.squaredDistanceTo(player);
                              if (isSelf || !(distSq > rangeSq)) {
                                 String shown = player.getGameProfile().name();
                                 if (protecting) {
                                    String replaced = protect.replacementForDisplay(shown);
                                    if (replaced != null) {
                                       shown = replaced;
                                    }
                                 }

                                 float healthFrac = -1.0F;
                                 if (names.health.get()) {
                                    float max = player.getMaxHealth();
                                    if (max > 0.0F) {
                                       healthFrac = MathHelper.clamp(player.getHealth() / max, 0.0F, 1.0F);
                                    }
                                 }

                                 String suffix = !isSelf && names.distance.get() ? (int)Math.sqrt(distSq) + "m" : null;
                                 if (!shown.isEmpty() || suffix != null || !(healthFrac < 0.0F)) {
                                    tags.add(entityTag(player, pt, Math.sqrt(distSq), shown, suffix, accent, healthFrac, nameScale, nameOpacity));
                                 }
                              }
                           }
                        }
                     }

                     if (names.items.get()) {
                        for (Entity entity : level.getEntities()) {
                           if (entity instanceof ItemEntity item && item.isAlive()) {
                              double distSq = self.squaredDistanceTo(item);
                              if (!(distSq > rangeSq)) {
                                 ItemStack stack = item.getStack();
                                 if (!stack.isEmpty()) {
                                    String suffix = itemSuffix(
                                       stack.getCount(), names.itemAmount.get(), names.distance.get() ? (int)Math.sqrt(distSq) + "m" : null
                                    );
                                    tags.add(entityTag(item, pt, Math.sqrt(distSq), stack.getName().getString(), suffix, accent, -1.0F, nameScale, nameOpacity));
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (doSpawners) {
                     boolean showDist = spawners.distance.get();
                     float spawnerOpacity = (float)(spawners.opacity.get() / 100.0);

                     for (BlockPos pos : spawners.scan.get()) {
                        double cx = pos.getX() + 0.5;
                        double cy = pos.getY() + 1.35;
                        double cz = pos.getZ() + 0.5;
                        double distSq = self.squaredDistanceTo(cx, pos.getY() + 0.5, cz);
                        String suffix = showDist ? (int)Math.sqrt(distSq) + "m" : null;
                        tags.add(new Tag(Math.sqrt(distSq), cx, cy, cz, spawnerName(level, pos), suffix, -22733, -1.0F, 1.0F, spawnerOpacity));
                     }
                  }

                  if (!tags.isEmpty()) {
                     tags.sort(Comparator.comparingDouble(WorldNametagRenderer.Tag::dist));
                     int count = Math.min(tags.size(), 80);

                     for (int i = count - 1; i >= 0; i--) {
                        WorldNametagRenderer.Tag tag = tags.get(i);
                        float[] screen = WorldProjection.project(tag.doubleVal, tag.doubleVal2, tag.doubleVal3);
                        if (screen != null) {
                           drawTag(vg, theme, screen[0], screen[1], tag);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static WorldNametagRenderer.Tag entityTag(
      Entity e, float pt, double dist, String name, String suffix, int accent, float healthFrac, float scale, float opacity
   ) {
      double x = MathHelper.lerp(pt, e.lastRenderX, e.getX());
      double y = MathHelper.lerp(pt, e.lastRenderY, e.getY()) + e.getHeight() + 0.5;
      double z = MathHelper.lerp(pt, e.lastRenderZ, e.getZ());
      return new Tag(dist, x, y, z, name, suffix, accent, healthFrac, scale, opacity);
   }

   private static String itemSuffix(int amount, boolean showAmount, String distance) {
      StringBuilder sb = new StringBuilder();
      if (showAmount && amount > 1) {
         sb.append('x').append(amount);
      }

      if (distance != null) {
         if (sb.length() > 0) {
            sb.append("  ");
         }

         sb.append(distance);
      }

      return sb.length() == 0 ? null : sb.toString();
   }

   private static String spawnerName(ClientWorld level, BlockPos pos) {
      try {
         BlockEntity be = level.getBlockEntity(pos);
         if (be instanceof MobSpawnerBlockEntity spawner) {
            Entity display = spawner.getLogic().getRenderedEntity(level, pos);
            return display != null ? display.getType().getName().getString() + " Spawner" : "Spawner";
         }

         if (be instanceof TrialSpawnerBlockEntity trial) {
            Entity display = trial.getSpawner().getData().setDisplayEntity(trial.getSpawner(), level, trial.getSpawnerState());
            return display != null ? "Trial: " + display.getType().getName().getString() : "Trial Spawner";
         }
      } catch (Exception var5) {
      }

      return "Spawner";
   }

   private static float pillHeight(float s, boolean hasHealth) {
      float textRowH = 12.5F * s + 3.5F * s * 2.0F;
      return textRowH + (hasHealth ? 3.0F * s + 3.5F * s : 0.0F);
   }

   public static void renderEquipment(DrawContext gg) {
      if (WorldProjection.isValid()) {
         ModuleManager modules = FeClient.modules();
         if (modules != null) {
            NameTagsModule names = modules.nameTags;
            if (names != null && names.isEnabled() && names.players.get()) {
               boolean showArmor = names.armor.get();
               boolean showHand = names.heldItem.get();
               if (showArmor || showHand) {
                  MinecraftClient mc = MinecraftClient.getInstance();
                  if (mc.currentScreen == null && !mc.options.hudHidden) {
                     ClientWorld level = mc.world;
                     ClientPlayerEntity self = mc.player;
                     if (level != null && self != null) {
                        float pt = WorldProjection.partialTick();
                        double range = names.range.get();
                        double rangeSq = range * range;
                        float s = names.scale.getFloat();
                        float pillH = pillHeight(s, names.health.get());
                        float uiScale = OverlayRenderer.uiScale();
                        double guiScale = mc.getWindow().getScaleFactor();
                        boolean showSelf = names.self.get() && !mc.options.getPerspective().isFirstPerson();
                        List<ItemStack> row = new ArrayList<>();

                        for (AbstractClientPlayerEntity player : level.getPlayers()) {
                           boolean isSelf = player == self;
                           if ((!isSelf || showSelf) && !player.isSpectator() && player.isAlive() && (isSelf || !(self.squaredDistanceTo(player) > rangeSq))) {
                              row.clear();
                              if (showArmor) {
                                 addItem(row, player.getEquippedStack(EquipmentSlot.HEAD));
                                 addItem(row, player.getEquippedStack(EquipmentSlot.CHEST));
                                 addItem(row, player.getEquippedStack(EquipmentSlot.LEGS));
                                 addItem(row, player.getEquippedStack(EquipmentSlot.FEET));
                              }

                              if (showHand) {
                                 addItem(row, player.getMainHandStack());
                                 addItem(row, player.getEquippedStack(EquipmentSlot.OFFHAND));
                              }

                              if (!row.isEmpty()) {
                                 double wx = MathHelper.lerp(pt, player.lastRenderX, player.getX());
                                 double wy = MathHelper.lerp(pt, player.lastRenderY, player.getY()) + player.getHeight() + 0.5;
                                 double wz = MathHelper.lerp(pt, player.lastRenderZ, player.getZ());
                                 float[] px = WorldProjection.projectRaw(wx, wy, wz);
                                 if (px != null) {
                                    float guiX = (float)(px[0] / guiScale);
                                    float rowBottomY = (float)((px[1] - pillH * uiScale) / guiScale) - 3.0F;
                                    float icon = 11.0F * s;
                                    float step = icon + 1.5F;
                                    float totalW = row.size() * icon + (row.size() - 1) * 1.5F;
                                    float startX = guiX - totalW / 2.0F;
                                    float rowTopY = rowBottomY - icon;
                                    Matrix3x2fStack pose = gg.getMatrices();

                                    for (int i = 0; i < row.size(); i++) {
                                       ItemStack stack = row.get(i);
                                       pose.pushMatrix();
                                       pose.translate(startX + i * step, rowTopY);
                                       pose.scale(icon / 16.0F, icon / 16.0F);
                                       gg.drawItem(player, stack, 0, 0, 0);
                                       gg.drawStackOverlay(mc.textRenderer, stack, 0, 0);
                                       pose.popMatrix();
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void addItem(List<ItemStack> list, ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         list.add(stack);
      }
   }

   private static void drawTag(NVGRenderer vg, Theme theme, float uiX, float uiY, WorldNametagRenderer.Tag tag) {
      float s = tag.scale;
      int accentBright = Colors.lighten(tag.accent, 0.35F);
      float fontMain = 12.5F * s;
      float fontSub = 10.0F * s;
      float padX = 6.0F * s;
      float padY = 3.5F * s;
      float gap = 5.0F * s;
      boolean hasHealth = tag.healthFrac >= 0.0F;
      float barH = 3.0F * s;
      float nameW = vg.textWidth(tag.name, fontMain);
      float suffixW = tag.suffix != null ? gap + vg.textWidth(tag.suffix, fontSub) : 0.0F;
      float w = nameW + suffixW + padX * 2.0F;
      float textRowH = fontMain + padY * 2.0F;
      float h = textRowH + (hasHealth ? barH + padY : 0.0F);
      float x = uiX - w / 2.0F;
      float y = uiY - h;
      float radius = Math.min(6.0F * s, h / 2.0F);
      boolean fade = tag.opacity < 0.999F;
      if (fade) {
         vg.save();
         vg.alpha(tag.opacity);
      }

      vg.glow(x, y, w, h, radius, 4.0F, Colors.withAlpha(tag.accent, 0.12F));
      vg.rectGradient(x, y, w, h, radius, theme.background(), theme.backgroundTo(), true);
      float cy = y + padY + fontMain / 2.0F;
      float tx = x + padX;
      tx += vg.textGradient(tag.name, tx, cy, fontMain, accentBright, tag.accent);
      if (tag.suffix != null) {
         tx += gap;
         vg.text(tag.suffix, tx, cy, fontSub, theme.textMuted());
      }

      if (hasHealth) {
         float barY = y + textRowH;
         float barX = x + padX;
         float barW = w - padX * 2.0F;
         vg.rect(barX, barY, barW, barH, barH / 2.0F, Colors.withAlpha(-16777216, 0.55F));
         int hpColor = Colors.lerp(-2080450, -11671924, tag.healthFrac);
         vg.rect(barX, barY, Math.max(barH, barW * tag.healthFrac), barH, barH / 2.0F, hpColor);
      }

      if (fade) {
         vg.restore();
      }
   }

   final static class Tag {
      private double dist;
      private double doubleVal;
      private double doubleVal2;
      private double doubleVal3;
      private String name;
      private String suffix;
      private int accent;
      private float healthFrac;
      private float scale;
      private float opacity;

      private Tag(double dist, double wx, double wy, double wz, String name, String suffix, int accent, float healthFrac, float scale, float opacity) {
         this.dist = dist;
         this.doubleVal = wx;
         this.doubleVal2 = wy;
         this.doubleVal3 = wz;
         this.name = name;
         this.suffix = suffix;
         this.accent = accent;
         this.healthFrac = healthFrac;
         this.scale = scale;
         this.opacity = opacity;
      }

      public double dist() {
         return this.dist;
      }

      public double getDouble() {
         return this.doubleVal;
      }

      public double getDouble2() {
         return this.doubleVal2;
      }

      public double getDouble3() {
         return this.doubleVal3;
      }

      public String name() {
         return this.name;
      }

      public String suffix() {
         return this.suffix;
      }

      public int accent() {
         return this.accent;
      }

      public float healthFrac() {
         return this.healthFrac;
      }

      public float scale() {
         return this.scale;
      }

      public float opacity() {
         return this.opacity;
      }
   }
}



