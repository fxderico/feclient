package dev.fede.gui;

import dev.fede.mixin.AbstractContainerScreenAccessor;
import dev.fede.module.impl.GambleRiggerModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;

public class GamblePanel {
   private static final int PAD = 8;
   private static final int CELL = 22;
   private static final int GAP = 4;
   private static final int TITLE_H = 12;
   private static final int BTN_H = 16;
   private static final int RESET_H = 12;
   private static final int PANEL_FILL = -3750202;
   private static final int PANEL_HOVER = -2697514;
   private static final int BTN_DISABLED = -5263441;
   private static final int HL_LIGHT = -1;
   private static final int HL_DARK = -11184811;
   private static final int SLOT_FILL = -7631989;
   private static final int SLOT_DARK = -13158601;
   private static final int OUTLINE = -16777216;
   private static final int LABEL = -12566464;
   private static final int TEXT_ON_BTN = -1;
   private static final int TEXT_HOVER = -96;
   private static final int TEXT_MUTED = -6250336;
   private final HandledScreen<?> screen;
   private AbstractContainerScreenAccessor access;
   private GambleRiggerModule mod;

   public GamblePanel(HandledScreen<?> screen, GambleRiggerModule mod) {
      this.screen = screen;
      this.access = (AbstractContainerScreenAccessor)screen;
      this.mod = mod;
   }

   private int gridW() {
      return 74;
   }

   private int panelW() {
      return this.gridW() + 16;
   }

   private int panelX() {
      int right = this.access.getLeftPos() + this.access.getImageWidth() + 6;
      if (right + this.panelW() > this.screen.width) {
         int left = this.access.getLeftPos() - this.panelW() - 6;
         if (left >= 2) {
            return left;
         }
      }

      return right;
   }

   private int panelY() {
      return this.access.getTopPos();
   }

   private int gridTop() {
      return this.panelY() + 8 + 12 + 4;
   }

   private int gridH() {
      return 74;
   }

   private int restoreTop() {
      return this.gridTop() + this.gridH() + 6;
   }

   private int resetTop() {
      return this.restoreTop() + 16 + 3;
   }

   private int statusTop() {
      return this.resetTop() + 12 + 5;
   }

   private int panelH() {
      return this.statusTop() + 16 + 7 - this.panelY();
   }

   private int cellX(int i) {
      return this.panelX() + 8 + i % 3 * 26;
   }

   private int cellY(int i) {
      return this.gridTop() + i / 3 * 26;
   }

   private int controlX() {
      return this.panelX() + 8;
   }

   private static boolean in(double mx, double my, int x, int y, int w, int h) {
      return mx >= x && mx < x + w && my >= y && my < y + h;
   }

   public void render(DrawContext g, int mx, int my) {
      if (this.mod.isEnabled()) {
         TextRenderer font = MinecraftClient.getInstance().textRenderer;
         int px = this.panelX();
         int py = this.panelY();
         int pw = this.panelW();
         int ph = this.panelH();
         g.fill(px - 1, py - 1, px + pw + 1, py + ph + 1, -16777216);
         raised(g, px, py, pw, ph, -3750202);
         this.centered(g, font, Text.literal("Gamble Rigger"), px + pw / 2, py + 8, -12566464, false);
         boolean pickable = this.mod.canExtract();

         for (int i = 0; i < 9; i++) {
            this.drawCell(g, font, i, mx, my, pickable);
         }

         this.drawButton(g, font, this.controlX(), this.restoreTop(), this.gridW(), 16, "Restore", this.mod.canRestore(), mx, my);
         boolean canReset = this.mod.phase() != GambleRiggerModule.Phase.IDLE || this.mod.keepSlot() >= 0;
         this.drawButton(g, font, this.controlX(), this.resetTop(), this.gridW(), 12, "Reset", canReset, mx, my);

         String status = switch (this.mod.phase()) {
            case EXTRACTING -> "Taking... " + this.mod.queued();
            case EXTRACTED -> "Kept #" + (this.mod.keepSlot() + 1);
            case RESTORING -> "Restoring... " + this.mod.queued();
            default -> "Pick a slot";
         };
         this.centered(g, font, Text.literal(status), px + pw / 2, this.statusTop(), -12566464, false);
      }
   }

   private void drawCell(DrawContext g, TextRenderer font, int i, int mx, int my, boolean pickable) {
      int x = this.cellX(i);
      int y = this.cellY(i);
      boolean selected = this.mod.keepSlot() == i && this.mod.phase() != GambleRiggerModule.Phase.IDLE;
      boolean hovered = pickable && in(mx, my, x, y, 22, 22);
      Text num = Text.literal(Integer.toString(i + 1));
      int tx = x + 11;
      int ty = y + 7;
      if (selected) {
         sunken(g, x, y, 22, 22, -7631989);
         this.centered(g, font, num, tx, ty, -1, true);
      } else {
         raised(g, x, y, 22, 22, hovered ? -2697514 : -3750202);
         int text = !pickable ? -6250336 : (hovered ? -96 : -1);
         this.centered(g, font, num, tx, ty, text, true);
      }
   }

   private void drawButton(DrawContext g, TextRenderer font, int x, int y, int w, int h, String label, boolean enabled, int mx, int my) {
      boolean hovered = enabled && in(mx, my, x, y, w, h);
      int fill = !enabled ? -5263441 : (hovered ? -2697514 : -3750202);
      raised(g, x, y, w, h, fill);
      int text = !enabled ? -6250336 : (hovered ? -96 : -1);
      this.centered(g, font, Text.literal(label), x + w / 2, y + (h - 8) / 2, text, true);
   }

   private static void raised(DrawContext g, int x, int y, int w, int h, int fill) {
      g.fill(x, y, x + w, y + h, fill);
      g.fill(x, y, x + w, y + 1, -1);
      g.fill(x, y, x + 1, y + h, -1);
      g.fill(x, y + h - 1, x + w, y + h, -11184811);
      g.fill(x + w - 1, y, x + w, y + h, -11184811);
   }

   private static void sunken(DrawContext g, int x, int y, int w, int h, int fill) {
      g.fill(x, y, x + w, y + h, fill);
      g.fill(x, y, x + w, y + 1, -13158601);
      g.fill(x, y, x + 1, y + h, -13158601);
      g.fill(x, y + h - 1, x + w, y + h, -1);
      g.fill(x + w - 1, y, x + w, y + h, -1);
   }

   private void centered(DrawContext g, TextRenderer font, Text text, int cx, int y, int color, boolean shadow) {
      g.drawText(font, text, cx - font.getWidth(text) / 2, y, color, shadow);
   }

   public boolean handleClick(double mx, double my, int button) {
      if (!this.mod.isEnabled()) {
         return false;
      } else {
         int px = this.panelX();
         int py = this.panelY();
         if (!in(mx, my, px, py, this.panelW(), this.panelH())) {
            return false;
         } else {
            if (button == 0) {
               if (this.mod.canExtract()) {
                  for (int i = 0; i < 9; i++) {
                     if (in(mx, my, this.cellX(i), this.cellY(i), 22, 22)) {
                        this.mod.requestKeep(i);
                        return true;
                     }
                  }
               }

               if (this.mod.canRestore() && in(mx, my, this.controlX(), this.restoreTop(), this.gridW(), 16)) {
                  this.mod.requestRestore();
                  return true;
               }

               if (in(mx, my, this.controlX(), this.resetTop(), this.gridW(), 12)) {
                  this.mod.reset();
                  return true;
               }
            }

            return true;
         }
      }
   }
}

