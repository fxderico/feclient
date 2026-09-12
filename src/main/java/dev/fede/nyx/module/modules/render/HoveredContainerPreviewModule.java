package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.storage.Chest;
import dev.fede.nyx.storage.Chest$KindUtils;
import dev.fede.nyx.storage.ContainerSnapshotMixinEntry;
import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HoveredContainerPreviewModule extends Module {
   private static final int intVal = 6;
   private static final int intVal2 = 18;
   private static final int intVal3 = 4;
   private static final int intVal4 = 16;
   private static final int intVal5 = -535752431;
   private static final int intVal6 = -12976364;
   private static final int intVal7 = -3355444;
   private static final int intVal8 = -7829368;
   private final NumberSetting maxSlots = new NumberSetting("MaxSlots", 27.0, 1.0, 54.0, 1.0);
   private final BooleanSetting showAge = new BooleanSetting("ShowAge", true);
   private final ModeSetting position = new ModeSetting("Position", "NearCursor", "NearCursor", "TopRight", "BottomCenter");
   private final NumberSetting maxHoverDistance = new NumberSetting("MaxHoverDistance", 6.0, 1.0, 32.0, 1.0);
   private volatile Chest chest;

   public HoveredContainerPreviewModule() {
      super("HoveredContainerPreview", "Shows last-seen inventory of the chest under your crosshair", Category.RENDER);
      this.run6(new Setting[]{this.maxSlots, this.showAge, this.position, this.maxHoverDistance});
   }

   @Override
   public void run2() {
      this.chest = null;
   }

   @Override
   public void run() {
      this.chest = this.getChest();
   }

   private Chest getChest() {
      if (class310.world != null && class310.player != null) {
         HitResult var1 = class310.crosshairTarget;
         if (var1 instanceof BlockHitResult var2 && var1.getType() == Type.BLOCK) {
            BlockPos var3 = var2.getBlockPos();
            if (var3 == null) {
               return null;
            } else {
               double var4 = Vec3d.ofCenter(var3).squaredDistanceTo(class310.player.getEntityPos());
               double var6 = this.maxHoverDistance.getValue();
               if (var4 > var6 * var6) {
                  return null;
               } else {
                  for (Chest var9 : Chest$KindUtils.all()) {
                     if (var9.pos().equals(var3)) {
                        return var9.hasSnapshot() ? var9 : null;
                     }
                  }

                  return null;
               }
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      Chest var3 = this.chest;
      if (var3 != null) {
         List var4 = var3.snapshotOrEmpty();
         if (!var4.isEmpty()) {
            int var5 = Math.min(var4.size(), this.maxSlots.getValueInt());
            int var6 = (var5 + 6 - 1) / 6;
            int var8 = var6 * 18;
            TextRenderer var9 = class310.textRenderer;
            String var10 = var3.kind().prettyName();
            String var11;
            byte var12;
            if (this.showAge.getValue() && var3.snapshotAtMs() > 0L) {
               var11 = stringOf(System.currentTimeMillis() - var3.snapshotAtMs());
               var12 = 9;
               if (var11 != null) {
                  int var36;
                  int var70;
                  label467: {
                     int var30;
                     int var31;
                     int var44;
                     label328: {
                        int var54;
                        int var89;
                        label453: {
                           int var32;
                           int var33;
                           label326: {
                              String var53 = null;
                              label325: {
                                 byte var29 = 10;
                                 int var165 = var9.getWidth(var10);
                                 if (var11 == null) {
                                    var30 = Math.max(108, Math.max(var165, 0)) + 8;
                                    var31 = 11 + var8 + var29 + 8;
                                    var32 = var1.getScaledWindowWidth();
                                    var33 = var1.getScaledWindowHeight();
                                    var53 = this.position.getMode();
                                    byte var67 = -1;
                                    switch (var53.hashCode()) {
                                       case -913702425:
                                          break label325;
                                       case 1946229376:
                                          break;
                                       default:
                                          break label326;
                                    }
                                 } else {
                                    var30 = Math.max(108, Math.max(var165, var9.getWidth(var11))) + 8;
                                    var31 = 11 + var8 + var29 + 8;
                                    var32 = var1.getScaledWindowWidth();
                                    var33 = var1.getScaledWindowHeight();
                                    var53 = this.position.getMode();
                                    byte var68 = -1;
                                    switch (var53.hashCode()) {
                                       case -913702425:
                                          break label325;
                                       case 1946229376:
                                          break;
                                       default:
                                          break label326;
                                    }
                                 }

                                 if (var53.equals("BottomCenter")) {
                                    boolean var69 = true;
                                    var36 = (var32 - var30) / 2;
                                    var44 = var33 - var31 - 30;
                                    var1.fill(var36, var44, var36 + var30, var44 + var31, -535752431);
                                    var1.fill(var36, var44, var36 + var30, var44 + 1, -12976364);
                                    var1.fill(var36, var44 + var31 - 1, var36 + var30, var44 + var31, -12976364);
                                    var1.fill(var36, var44, var36 + 1, var44 + var31, -12976364);
                                    var1.fill(var36 + var30 - 1, var44, var36 + var30, var44 + var31, -12976364);
                                    var1.drawTextWithShadow(var9, var10, var36 + 4, var44 + 4, -3355444);
                                    var54 = var36 + (var30 - 108) / 2;
                                    var70 = var44 + 4 + var12 + 2;
                                    var89 = 0;
                                    if (var89 >= var5) {
                                       break label467;
                                    }
                                    break label453;
                                 }
                                 break label326;
                              }

                              if (var53.equals("TopRight")) {
                                 byte var74 = 0;
                                 var36 = var32 - var30 - 4;
                                 byte var47 = 4;
                                 var1.fill(var36, var47, var36 + var30, var47 + var31, -535752431);
                                 var1.fill(var36, var47, var36 + var30, 5, -12976364);
                                 var1.fill(var36, var47 + var31 - 1, var36 + var30, var47 + var31, -12976364);
                                 var1.fill(var36, var47, var36 + 1, var47 + var31, -12976364);
                                 var1.fill(var36 + var30 - 1, var47, var36 + var30, var47 + var31, -12976364);
                                 var1.drawTextWithShadow(var9, var10, var36 + 4, 8, -3355444);
                                 var54 = var36 + (var30 - 108) / 2;
                                 var74 = 19;
                                 var89 = 0;
                                 if (var89 < var5) {
                                    do {
                                       ContainerSnapshotMixinEntry var109 = (ContainerSnapshotMixinEntry)var4.get(var89);
                                       if (var109 != null) {
                                          ItemStack var120 = var109.toStack();
                                          if (!var120.isEmpty()) {
                                             int var131 = var89 % 6;
                                             int var142 = var89 / 6;
                                             int var153 = var54 + var131 * 18 + 1;
                                             int var164 = var74 + var142 * 18 + 1;
                                             var1.drawItem(var120, var153, var164);
                                             var1.drawStackOverlay(var9, var120, var153, var164);
                                             var89++;
                                             continue;
                                          }
                                       }

                                       var89++;
                                    } while (var89 < var5);
                                 }

                                 if (var11 != null) {
                                    var89 = var74 + var8 + 1;
                                    var1.drawTextWithShadow(var9, var11, var36 + 4, var89, -7829368);
                                    return;
                                 }

                                 return;
                              }
                           }

                           label312: {
                              label311: {
                                 var36 = (int)(class310.mouse.getX() * var32 / class310.getWindow().getWidth()) + 16;
                                 var44 = (int)(class310.mouse.getY() * var33 / class310.getWindow().getHeight()) + 16;
                                 if (var36 + var30 > var32) {
                                    var36 = var32 - var30 - 2;
                                    if (var44 + var31 > var33) {
                                       break label311;
                                    }
                                 } else if (var44 + var31 > var33) {
                                    break label311;
                                 }

                                 if (var36 < 2) {
                                    break label328;
                                 }
                                 break label312;
                              }

                              var44 = var33 - var31 - 2;
                              if (var36 < 2) {
                                 break label328;
                              }
                           }

                           if (var44 < 2) {
                              byte var45 = 2;
                              var1.fill(var36, var45, var36 + var30, var45 + var31, -535752431);
                              var1.fill(var36, var45, var36 + var30, 3, -12976364);
                              var1.fill(var36, var45 + var31 - 1, var36 + var30, var45 + var31, -12976364);
                              var1.fill(var36, var45, var36 + 1, var45 + var31, -12976364);
                              var1.fill(var36 + var30 - 1, var45, var36 + var30, var45 + var31, -12976364);
                              var1.drawTextWithShadow(var9, var10, var36 + 4, 6, -3355444);
                              var54 = var36 + (var30 - 108) / 2;
                              byte var71 = 17;
                              var89 = 0;
                              if (var89 < var5) {
                                 do {
                                    ContainerSnapshotMixinEntry var106 = (ContainerSnapshotMixinEntry)var4.get(var89);
                                    if (var106 != null) {
                                       ItemStack var117 = var106.toStack();
                                       if (!var117.isEmpty()) {
                                          int var128 = var89 % 6;
                                          int var139 = var89 / 6;
                                          int var150 = var54 + var128 * 18 + 1;
                                          int var161 = var71 + var139 * 18 + 1;
                                          var1.drawItem(var117, var150, var161);
                                          var1.drawStackOverlay(var9, var117, var150, var161);
                                          var89++;
                                          continue;
                                       }
                                    }

                                    var89++;
                                 } while (var89 < var5);
                              }

                              if (var11 != null) {
                                 var89 = var71 + var8 + 1;
                                 var1.drawTextWithShadow(var9, var11, var36 + 4, var89, -7829368);
                                 return;
                              }

                              return;
                           }

                           var1.fill(var36, var44, var36 + var30, var44 + var31, -535752431);
                           var1.fill(var36, var44, var36 + var30, var44 + 1, -12976364);
                           var1.fill(var36, var44 + var31 - 1, var36 + var30, var44 + var31, -12976364);
                           var1.fill(var36, var44, var36 + 1, var44 + var31, -12976364);
                           var1.fill(var36 + var30 - 1, var44, var36 + var30, var44 + var31, -12976364);
                           var1.drawTextWithShadow(var9, var10, var36 + 4, var44 + 4, -3355444);
                           var54 = var36 + (var30 - 108) / 2;
                           var70 = var44 + 4 + var12 + 2;
                           var89 = 0;
                           if (var89 >= var5) {
                              break label467;
                           }
                        }

                        while (true) {
                           label297: {
                              ContainerSnapshotMixinEntry var105 = (ContainerSnapshotMixinEntry)var4.get(var89);
                              if (var105 != null) {
                                 ItemStack var116 = var105.toStack();
                                 if (!var116.isEmpty()) {
                                    int var127 = var89 % 6;
                                    int var138 = var89 / 6;
                                    int var149 = var54 + var127 * 18 + 1;
                                    int var160 = var70 + var138 * 18 + 1;
                                    var1.drawItem(var116, var149, var160);
                                    var1.drawStackOverlay(var9, var116, var149, var160);
                                    var89++;
                                    break label297;
                                 }
                              }

                              var89++;
                           }

                           if (var89 >= var5) {
                              break label467;
                           }
                        }
                     }

                     byte var37 = 2;
                     if (var44 < 2) {
                        byte var46 = 2;
                        var1.fill(var37, var46, var37 + var30, var46 + var31, -535752431);
                        var1.fill(var37, var46, var37 + var30, 3, -12976364);
                        var1.fill(var37, var46 + var31 - 1, var37 + var30, var46 + var31, -12976364);
                        var1.fill(var37, var46, 3, var46 + var31, -12976364);
                        var1.fill(var37 + var30 - 1, var46, var37 + var30, var46 + var31, -12976364);
                        var1.drawTextWithShadow(var9, var10, 6, 6, -3355444);
                        int var57 = var37 + (var30 - 108) / 2;
                        byte var73 = 17;
                        int var95 = 0;
                        if (var95 < var5) {
                           do {
                              ContainerSnapshotMixinEntry var108 = (ContainerSnapshotMixinEntry)var4.get(var95);
                              if (var108 != null) {
                                 ItemStack var119 = var108.toStack();
                                 if (!var119.isEmpty()) {
                                    int var130 = var95 % 6;
                                    int var141 = var95 / 6;
                                    int var152 = var57 + var130 * 18 + 1;
                                    int var163 = var73 + var141 * 18 + 1;
                                    var1.drawItem(var119, var152, var163);
                                    var1.drawStackOverlay(var9, var119, var152, var163);
                                    var95++;
                                    continue;
                                 }
                              }

                              var95++;
                           } while (var95 < var5);
                        }

                        if (var11 != null) {
                           var95 = var73 + var8 + 1;
                           var1.drawTextWithShadow(var9, var11, 6, var95, -7829368);
                           return;
                        }

                        return;
                     }

                     var1.fill(var37, var44, var37 + var30, var44 + var31, -535752431);
                     var1.fill(var37, var44, var37 + var30, var44 + 1, -12976364);
                     var1.fill(var37, var44 + var31 - 1, var37 + var30, var44 + var31, -12976364);
                     var1.fill(var37, var44, 3, var44 + var31, -12976364);
                     var1.fill(var37 + var30 - 1, var44, var37 + var30, var44 + var31, -12976364);
                     var1.drawTextWithShadow(var9, var10, 6, var44 + 4, -3355444);
                     int var56 = var37 + (var30 - 108) / 2;
                     var70 = var44 + 4 + var12 + 2;
                     int var93 = 0;
                     if (var93 < var5) {
                        do {
                           ContainerSnapshotMixinEntry var107 = (ContainerSnapshotMixinEntry)var4.get(var93);
                           if (var107 != null) {
                              ItemStack var118 = var107.toStack();
                              if (!var118.isEmpty()) {
                                 int var129 = var93 % 6;
                                 int var140 = var93 / 6;
                                 int var151 = var56 + var129 * 18 + 1;
                                 int var162 = var70 + var140 * 18 + 1;
                                 var1.drawItem(var118, var151, var162);
                                 var1.drawStackOverlay(var9, var118, var151, var162);
                                 var93++;
                                 continue;
                              }
                           }

                           var93++;
                        } while (var93 < var5);
                     }

                     if (var11 != null) {
                        var93 = var70 + var8 + 1;
                        var1.drawTextWithShadow(var9, var11, 6, var93, -7829368);
                        return;
                     }

                     return;
                  }

                  if (var11 != null) {
                     int var90 = var70 + var8 + 1;
                     var1.drawTextWithShadow(var9, var11, var36 + 4, var90, -7829368);
                     return;
                  }

                  return;
               }
            } else {
               var11 = null;
               var12 = 9;
            }

            int var18;
            int var61;
            label457: {
               label471: {
                  int var14;
                  int var15;
                  int var39;
                  label438: {
                     int var22;
                     int var48;
                     label459: {
                        int var16;
                        int var17;
                        label436: {
                           String var20 = null;
                           label435: {
                              byte var13 = 0;
                              int var10001 = var9.getWidth(var10);
                              if (var11 == null) {
                                 var14 = Math.max(108, Math.max(var10001, 0)) + 8;
                                 var15 = 11 + var8 + var13 + 8;
                                 var16 = var1.getScaledWindowWidth();
                                 var17 = var1.getScaledWindowHeight();

                                 var20 = this.position.getMode();
                                 byte var21 = -1;
                                 switch (var20.hashCode()) {
                                    case -913702425:
                                       break label435;
                                    case 1946229376:
                                       break;
                                    default:
                                       break label436;
                                 }
                              } else {
                                 var14 = Math.max(108, Math.max(var10001, var9.getWidth(var11))) + 8;
                                 var15 = 11 + var8 + var13 + 8;
                                 var16 = var1.getScaledWindowWidth();
                                 var17 = var1.getScaledWindowHeight();
                                 var20 = this.position.getMode();
                                 byte var59 = -1;
                                 switch (var20.hashCode()) {
                                    case -913702425:
                                       break label435;
                                    case 1946229376:
                                       break;
                                    default:
                                       break label436;
                                 }
                              }

                              if (var20.equals("BottomCenter")) {
                                 boolean var60 = true;
                                 var18 = (var16 - var14) / 2;
                                 var39 = var17 - var15 - 30;
                                 var1.fill(var18, var39, var18 + var14, var39 + var15, -535752431);
                                 var1.fill(var18, var39, var18 + var14, var39 + 1, -12976364);
                                 var1.fill(var18, var39 + var15 - 1, var18 + var14, var39 + var15, -12976364);
                                 var1.fill(var18, var39, var18 + 1, var39 + var15, -12976364);
                                 var1.fill(var18 + var14 - 1, var39, var18 + var14, var39 + var15, -12976364);
                                 var1.drawTextWithShadow(var9, var10, var18 + 4, var39 + 4, -3355444);
                                 var48 = var18 + (var14 - 108) / 2;
                                 var61 = var39 + 4 + var12 + 2;
                                 var22 = 0;
                                 if (var22 >= var5) {
                                    if (var11 != null) {
                                       var22 = var61 + var8 + 1;
                                       var1.drawTextWithShadow(var9, var11, var18 + 4, var22, -7829368);
                                       return;
                                    }

                                    return;
                                 }

                                 ContainerSnapshotMixinEntry var23 = (ContainerSnapshotMixinEntry)var4.get(var22);
                                 if (var23 != null) {
                                    ItemStack var24 = var23.toStack();
                                    if (!var24.isEmpty()) {
                                       int var25 = 0;
                                       int var26 = 0;
                                       int var27 = var48 + 1;
                                       int var28 = var61 + 1;
                                       var1.drawItem(var24, var27, var28);
                                       var1.drawStackOverlay(var9, var24, var27, var28);
                                       if (++var22 >= var5) {
                                          break label457;
                                       }

                                       while (true) {
                                          var23 = (ContainerSnapshotMixinEntry)var4.get(var22);
                                          if (var23 != null) {
                                             var24 = var23.toStack();
                                             if (!var24.isEmpty()) {
                                                var25 = var22 % 6;
                                                var26 = var22 / 6;
                                                var27 = var48 + var25 * 18 + 1;
                                                var28 = var61 + var26 * 18 + 1;
                                                var1.drawItem(var24, var27, var28);
                                                var1.drawStackOverlay(var9, var24, var27, var28);
                                                if (++var22 >= var5) {
                                                   break label471;
                                                }
                                                break label459;
                                             }
                                          }

                                          if (++var22 >= var5) {
                                             break label457;
                                          }
                                       }
                                    }
                                 }

                                 if (++var22 >= var5) {
                                    break label471;
                                 }
                                 break label459;
                              }
                              break label436;
                           }

                           if (var20.equals("TopRight")) {
                              byte var65 = 0;
                              var18 = var16 - var14 - 4;
                              byte var42 = 4;
                              var1.fill(var18, var42, var18 + var14, var42 + var15, -535752431);
                              var1.fill(var18, var42, var18 + var14, 5, -12976364);
                              var1.fill(var18, var42 + var15 - 1, var18 + var14, var42 + var15, -12976364);
                              var1.fill(var18, var42, var18 + 1, var42 + var15, -12976364);
                              var1.fill(var18 + var14 - 1, var42, var18 + var14, var42 + var15, -12976364);
                              var1.drawTextWithShadow(var9, var10, var18 + 4, 8, -3355444);
                              var48 = var18 + (var14 - 108) / 2;
                              var65 = 19;
                              var22 = 0;
                              if (var22 < var5) {
                                 do {
                                    ContainerSnapshotMixinEntry var104 = (ContainerSnapshotMixinEntry)var4.get(var22);
                                    if (var104 != null) {
                                       ItemStack var115 = var104.toStack();
                                       if (!var115.isEmpty()) {
                                          int var126 = var22 % 6;
                                          int var137 = var22 / 6;
                                          int var148 = var48 + var126 * 18 + 1;
                                          int var159 = var65 + var137 * 18 + 1;
                                          var1.drawItem(var115, var148, var159);
                                          var1.drawStackOverlay(var9, var115, var148, var159);
                                          var22++;
                                          continue;
                                       }
                                    }

                                    var22++;
                                 } while (var22 < var5);
                              }

                              if (var11 != null) {
                                 var22 = var65 + var8 + 1;
                                 var1.drawTextWithShadow(var9, var11, var18 + 4, var22, -7829368);
                                 return;
                              }

                              return;
                           }
                        }

                        label401: {
                           label400: {
                              var18 = (int)(class310.mouse.getX() * var16 / class310.getWindow().getWidth()) + 16;
                              var39 = (int)(class310.mouse.getY() * var17 / class310.getWindow().getHeight()) + 16;
                              if (var18 + var14 > var16) {
                                 var18 = var16 - var14 - 2;
                                 if (var39 + var15 > var17) {
                                    break label400;
                                 }
                              } else if (var39 + var15 > var17) {
                                 break label400;
                              }

                              if (var18 < 2) {
                                 break label438;
                              }
                              break label401;
                           }

                           var39 = var17 - var15 - 2;
                           if (var18 < 2) {
                              break label438;
                           }
                        }

                        if (var39 < 2) {
                           byte var40 = 2;
                           var1.fill(var18, var40, var18 + var14, var40 + var15, -535752431);
                           var1.fill(var18, var40, var18 + var14, 3, -12976364);
                           var1.fill(var18, var40 + var15 - 1, var18 + var14, var40 + var15, -12976364);
                           var1.fill(var18, var40, var18 + 1, var40 + var15, -12976364);
                           var1.fill(var18 + var14 - 1, var40, var18 + var14, var40 + var15, -12976364);
                           var1.drawTextWithShadow(var9, var10, var18 + 4, 6, -3355444);
                           var48 = var18 + (var14 - 108) / 2;
                           byte var62 = 17;
                           var22 = 0;
                           if (var22 < var5) {
                              do {
                                 ContainerSnapshotMixinEntry var101 = (ContainerSnapshotMixinEntry)var4.get(var22);
                                 if (var101 != null) {
                                    ItemStack var112 = var101.toStack();
                                    if (!var112.isEmpty()) {
                                       int var123 = var22 % 6;
                                       int var134 = var22 / 6;
                                       int var145 = var48 + var123 * 18 + 1;
                                       int var156 = var62 + var134 * 18 + 1;
                                       var1.drawItem(var112, var145, var156);
                                       var1.drawStackOverlay(var9, var112, var145, var156);
                                       var22++;
                                       continue;
                                    }
                                 }

                                 var22++;
                              } while (var22 < var5);
                           }

                           if (var11 != null) {
                              var22 = var62 + var8 + 1;
                              var1.drawTextWithShadow(var9, var11, var18 + 4, var22, -7829368);
                              return;
                           }

                           return;
                        }

                        var1.fill(var18, var39, var18 + var14, var39 + var15, -535752431);
                        var1.fill(var18, var39, var18 + var14, var39 + 1, -12976364);
                        var1.fill(var18, var39 + var15 - 1, var18 + var14, var39 + var15, -12976364);
                        var1.fill(var18, var39, var18 + 1, var39 + var15, -12976364);
                        var1.fill(var18 + var14 - 1, var39, var18 + var14, var39 + var15, -12976364);
                        var1.drawTextWithShadow(var9, var10, var18 + 4, var39 + 4, -3355444);
                        var48 = var18 + (var14 - 108) / 2;
                        var61 = var39 + 4 + var12 + 2;
                        var22 = 0;
                        if (var22 >= var5) {
                           break label471;
                        }
                     }

                     while (true) {
                        ContainerSnapshotMixinEntry var100 = (ContainerSnapshotMixinEntry)var4.get(var22);
                        if (var100 == null) {
                           var22++;
                        } else {
                           ItemStack var111 = var100.toStack();
                           if (var111.isEmpty()) {
                              var22++;
                           } else {
                              int var122 = var22 % 6;
                              int var133 = var22 / 6;
                              int var144 = var48 + var122 * 18 + 1;
                              int var155 = var61 + var133 * 18 + 1;
                              var1.drawItem(var111, var144, var155);
                              var1.drawStackOverlay(var9, var111, var144, var155);
                              var22++;
                           }
                        }

                        if (var22 >= var5) {
                           break label471;
                        }
                     }
                  }

                  byte var34 = 2;
                  if (var39 < 2) {
                     byte var41 = 2;
                     var1.fill(var34, var41, var34 + var14, var41 + var15, -535752431);
                     var1.fill(var34, var41, var34 + var14, 3, -12976364);
                     var1.fill(var34, var41 + var15 - 1, var34 + var14, var41 + var15, -12976364);
                     var1.fill(var34, var41, 3, var41 + var15, -12976364);
                     var1.fill(var34 + var14 - 1, var41, var34 + var14, var41 + var15, -12976364);
                     var1.drawTextWithShadow(var9, var10, 6, 6, -3355444);
                     int var51 = var34 + (var14 - 108) / 2;
                     byte var64 = 17;
                     int var85 = 0;
                     if (var85 < var5) {
                        do {
                           ContainerSnapshotMixinEntry var103 = (ContainerSnapshotMixinEntry)var4.get(var85);
                           if (var103 != null) {
                              ItemStack var114 = var103.toStack();
                              if (!var114.isEmpty()) {
                                 int var125 = var85 % 6;
                                 int var136 = var85 / 6;
                                 int var147 = var51 + var125 * 18 + 1;
                                 int var158 = var64 + var136 * 18 + 1;
                                 var1.drawItem(var114, var147, var158);
                                 var1.drawStackOverlay(var9, var114, var147, var158);
                                 var85++;
                                 continue;
                              }
                           }

                           var85++;
                        } while (var85 < var5);
                     }

                     if (var11 != null) {
                        var85 = var64 + var8 + 1;
                        var1.drawTextWithShadow(var9, var11, 6, var85, -7829368);
                        return;
                     }

                     return;
                  }

                  var1.fill(var34, var39, var34 + var14, var39 + var15, -535752431);
                  var1.fill(var34, var39, var34 + var14, var39 + 1, -12976364);
                  var1.fill(var34, var39 + var15 - 1, var34 + var14, var39 + var15, -12976364);
                  var1.fill(var34, var39, 3, var39 + var15, -12976364);
                  var1.fill(var34 + var14 - 1, var39, var34 + var14, var39 + var15, -12976364);
                  var1.drawTextWithShadow(var9, var10, 6, var39 + 4, -3355444);
                  int var50 = var34 + (var14 - 108) / 2;
                  var61 = var39 + 4 + var12 + 2;
                  int var83 = 0;
                  if (var83 < var5) {
                     do {
                        ContainerSnapshotMixinEntry var102 = (ContainerSnapshotMixinEntry)var4.get(var83);
                        if (var102 != null) {
                           ItemStack var113 = var102.toStack();
                           if (!var113.isEmpty()) {
                              int var124 = var83 % 6;
                              int var135 = var83 / 6;
                              int var146 = var50 + var124 * 18 + 1;
                              int var157 = var61 + var135 * 18 + 1;
                              var1.drawItem(var113, var146, var157);
                              var1.drawStackOverlay(var9, var113, var146, var157);
                              var83++;
                              continue;
                           }
                        }

                        var83++;
                     } while (var83 < var5);
                  }

                  if (var11 != null) {
                     var83 = var61 + var8 + 1;
                     var1.drawTextWithShadow(var9, var11, 6, var83, -7829368);
                     return;
                  }

                  return;
               }

               if (var11 != null) {
                  int var80 = var61 + var8 + 1;
                  var1.drawTextWithShadow(var9, var11, var18 + 4, var80, -7829368);
                  return;
               }

               return;
            }

            if (var11 != null) {
               int var78 = var61 + var8 + 1;
               var1.drawTextWithShadow(var9, var11, var18 + 4, var78, -7829368);
            }
         }
      }
   }

   private static String stringOf(long var0) {
      if (var0 < 0L) {
         var0 = 0L;
      }

      long var2 = var0 / 1000L;
      if (var2 < 5L) {
         return "just now";
      } else if (var2 < 60L) {
         return var2 + "s ago";
      } else {
         long var4 = var2 / 60L;
         if (var4 < 60L) {
            return var4 + "m ago";
         } else {
            long var6 = var4 / 60L;
            if (var6 < 24L) {
               return var6 + "h ago";
            } else {
               long var8 = var6 / 24L;
               return var8 + "d ago";
            }
         }
      }
   }
}

