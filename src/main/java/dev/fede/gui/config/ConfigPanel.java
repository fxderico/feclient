package dev.fede.gui.config;

import dev.fede.FeClient;
import dev.fede.config.ConfigStore;
import dev.fede.render.anim.Animation;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.theme.Theme;
import dev.fede.util.Colors;
import dev.fede.util.UiSounds;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public class ConfigPanel {
   private static final float CARD_W = 600.0F;
   private static final float HEADER_H = 64.0F;
   private static final float FOOTER_H = 38.0F;
   private static final float ROW_H = 60.0F;
   private static final float ROW_GAP = 8.0F;
   private static final float PAD = 18.0F;
   private static final float BTN_INSET = 16.0F;
   private static final int DANGER = -45730;
   private static final int DANGER_BRIGHT = -37252;
   private final Animation openAnim = new Animation(160.0F, 0.0F);
   private boolean open;
   private float cardX;
   private float cardY;
   private float cardH;
   private final List<ConfigPanel.Hit> hits = new ArrayList<>();
   private float closeX;
   private float closeY;
   private float closeSize;
   private int renamingSlot = -1;
   private final StringBuilder renameBuffer = new StringBuilder();
   private ConfigPanel.Confirm pendingConfirm;
   private float confirmOkX;
   private float confirmOkY;
   private float confirmOkW;
   private float confirmOkH;
   private float confirmCancelX;
   private float confirmCancelY;
   private float confirmCancelW;
   private float confirmCancelH;
   private float confirmCardX;
   private float confirmCardY;
   private float confirmCardW;
   private float confirmCardH;

   public boolean isOpen() {
      return this.open;
   }

   public void open() {
      this.open = true;
      this.openAnim.setTarget(1.0F);
   }

   public void close() {
      this.open = false;
      this.openAnim.setTarget(0.0F);
      this.cancelRename();
      this.pendingConfirm = null;
   }

   public boolean isListening() {
      return this.open && this.renamingSlot >= 0;
   }

   private ConfigStore store() {
      return FeClient.configStore();
   }

   public void render(NVGRenderer vg, float mx, float my, float uiWidth, float uiHeight) {
      float t = this.openAnim.value();
      if (!(t <= 0.002F) || this.open) {
         Theme theme = FeClient.themes().current();
         vg.rect(0.0F, 0.0F, uiWidth, uiHeight, 0.0F, Colors.withAlpha(-16316918, 0.55F * t));
         vg.save();
         vg.alpha(t);
         float scale = 0.97F + 0.03F * t;
         vg.translate(uiWidth / 2.0F, uiHeight / 2.0F);
         vg.scale(scale);
         vg.translate(-uiWidth / 2.0F, -uiHeight / 2.0F);
         ConfigStore store = this.store();
         this.cardH = 434.0F;
         this.cardX = (uiWidth - 600.0F) / 2.0F;
         this.cardY = (uiHeight - this.cardH) / 2.0F;
         vg.glow(this.cardX, this.cardY, 600.0F, this.cardH, 18.0F, 22.0F, Colors.withAlpha(-16777216, 0.45F));
         vg.rectVaryingGradient(this.cardX, this.cardY, 600.0F, this.cardH, 18.0F, 18.0F, 18.0F, 18.0F, theme.background(), theme.backgroundTo());
         vg.rectOutline(this.cardX, this.cardY, 600.0F, this.cardH, 18.0F, 1.2F, Colors.withAlpha(theme.accent(), 0.3F));
         this.renderHeader(vg, theme, mx, my);
         this.hits.clear();
         float rowY = this.cardY + 64.0F;

         for (int i = 0; i < 5; i++) {
            this.renderSlot(vg, theme, store.slot(i), this.cardX + 18.0F, rowY, 564.0F, mx, my, store.activeIndex() == i);
            rowY += 68.0F;
         }

         this.renderFooter(vg, theme);
         if (this.pendingConfirm != null) {
            this.renderConfirm(vg, theme, mx, my, uiWidth, uiHeight);
         }

         vg.restore();
      }
   }

   private void renderHeader(NVGRenderer vg, Theme theme, float mx, float my) {
      float gx = this.cardX + 18.0F;
      float gy = this.cardY + 22.0F;
      vg.rect(gx + 4.0F, gy + 3.0F, 15.0F, 12.0F, 3.0F, Colors.withAlpha(theme.accent(), 0.45F));
      vg.rect(gx, gy, 15.0F, 12.0F, 3.0F, theme.accentBright());
      vg.text("Configs", gx + 28.0F, this.cardY + 26.0F, 19.0F, theme.textPrimary());
      vg.text("Save, activate & share your full client setup", gx + 28.0F, this.cardY + 44.0F, 12.0F, theme.textMuted());
      this.closeSize = 16.0F;
      this.closeX = this.cardX + 600.0F - 18.0F - this.closeSize;
      this.closeY = this.cardY + 20.0F;
      boolean hover = mx >= this.closeX - 4.0F
         && mx <= this.closeX + this.closeSize + 4.0F
         && my >= this.closeY - 4.0F
         && my <= this.closeY + this.closeSize + 4.0F;
      if (hover) {
         vg.rect(this.closeX - 5.0F, this.closeY - 5.0F, this.closeSize + 10.0F, this.closeSize + 10.0F, 6.0F, Colors.withAlpha(theme.accent(), 0.18F));
      }

      vg.cross(this.closeX, this.closeY, this.closeSize, 1.8F, hover ? theme.accentBright() : theme.textMuted());
      vg.rect(this.cardX + 18.0F, this.cardY + 64.0F - 2.0F, 564.0F, 1.0F, 0.5F, Colors.withAlpha(theme.accent(), 0.2F));
   }

   private void renderSlot(NVGRenderer vg, Theme theme, ConfigStore.Slot slot, float x, float y, float w, float mx, float my, boolean active) {
      boolean rowHover = mx >= x && mx <= x + w && my >= y && my <= y + 60.0F;
      int fill = active ? theme.moduleActiveFill() : Colors.withAlpha(-16777216, rowHover ? 0.32F : 0.22F);
      vg.rect(x, y, w, 60.0F, 12.0F, fill);
      if (active) {
         vg.rect(x, y + 10.0F, 3.0F, 40.0F, 1.5F, theme.accentBright());
         vg.rectOutline(x, y, w, 60.0F, 12.0F, 1.1F, Colors.withAlpha(theme.accent(), 0.5F));
      }

      float dotX = x + 16.0F;
      float dotY = y + 30.0F;
      if (active) {
         vg.circleGlow(dotX, dotY, 4.0F, 5.0F, theme.accent());
         vg.circle(dotX, dotY, 4.0F, theme.accentBright());
      } else if (slot.filled()) {
         vg.circle(dotX, dotY, 3.5F, theme.statusEnabled());
      } else {
         vg.circleOutline(dotX, dotY, 3.5F, 1.2F, theme.statusDisabled());
      }

      float nameX = x + 32.0F;
      if (this.renamingSlot == slot.index()) {
         this.renderRenameField(vg, theme, nameX, y + 12.0F, 190.0F);
      } else {
         vg.textTruncated(slot.name(), nameX, y + 22.0F, 15.0F, theme.textPrimary(), 200.0F);
         if (active) {
            float nameW = vg.textWidth(slot.name(), 15.0F);
            this.drawTag(vg, theme, nameX + Math.min(nameW, 200.0F) + 8.0F, y + 22.0F, "ACTIVE");
         }
      }

      String status = this.renamingSlot == slot.index()
         ? "Enter to confirm · Esc to cancel"
         : (slot.filled() ? "Saved · " + relativeTime(slot.savedAt()) : "Empty slot");
      vg.text(status, nameX, y + 40.0F, 11.5F, theme.textMuted());
      this.layoutButtons(vg, theme, slot, x + w - 16.0F, y, mx, my);
   }

   private void renderRenameField(NVGRenderer vg, Theme theme, float x, float y, float w) {
      vg.rect(x, y, w, 20.0F, 20.0F / 2.0F, Colors.withAlpha(-16777216, 0.5F));
      vg.rectOutline(x, y, w, 20.0F, 20.0F / 2.0F, 1.2F, Colors.withAlpha(theme.accentBright(), 0.9F));
      float tx = x + 8.0F;
      float ty = y + 20.0F / 2.0F;
      float tw = vg.text(this.renameBuffer.toString(), tx, ty, 12.5F, theme.textPrimary());
      if (System.nanoTime() / 400000000L % 2L == 0L) {
         vg.rect(tx + tw + 1.5F, ty - 6.0F, 1.4F, 12.0F, 0.7F, theme.accentBright());
      }
   }

   private void drawTag(NVGRenderer vg, Theme theme, float x, float y, String label) {
      float tw = vg.textWidth(label, 9.5F);
      vg.rect(x, y - 7.0F, tw + 12.0F, 14.0F, 7.0F, Colors.withAlpha(theme.accent(), 0.22F));
      vg.text(label, x + 6.0F, y, 9.5F, theme.accentBright());
   }

   private void layoutButtons(NVGRenderer vg, Theme theme, ConfigStore.Slot slot, float rowRight, float rowY, float mx, float my) {
      final class Spec {
         private ConfigPanel.Action action;
         private String label;
         private boolean primary;
         private boolean danger;

         Spec(ConfigPanel.Action action, String label, boolean primary, boolean danger) {
            this.action = action;
            this.label = label;
            this.primary = primary;
            this.danger = danger;
         }

         public ConfigPanel.Action action() {
            return this.action;
         }

         public String label() {
            return this.label;
         }

         public boolean primary() {
            return this.primary;
         }

         public boolean danger() {
            return this.danger;
         }
      }

      List<Spec> specs = new ArrayList<>();
      if (slot.filled()) {
         specs.add(new Spec(ConfigPanel.Action.ACTIVATE, "Activate", true, false));
         specs.add(new Spec(ConfigPanel.Action.SAVE, "Save", false, false));
         specs.add(new Spec(ConfigPanel.Action.RENAME, "Rename", false, false));
         specs.add(new Spec(ConfigPanel.Action.EXPORT, "Export", false, false));
         specs.add(new Spec(ConfigPanel.Action.IMPORT, "Import", false, false));
         specs.add(new Spec(ConfigPanel.Action.DELETE, "Delete", false, true));
      } else {
         specs.add(new Spec(ConfigPanel.Action.SAVE, "Save", true, false));
         specs.add(new Spec(ConfigPanel.Action.IMPORT, "Import", false, false));
      }

      float total = 0.0F;
      float[] widths = new float[specs.size()];

      for (int i = 0; i < specs.size(); i++) {
         widths[i] = vg.textWidth(specs.get(i).label(), 12.0F) + 11.0F * 2.0F;
         total += widths[i] + (i > 0 ? 6.0F : 0.0F);
      }

      float bx = rowRight - total;
      float by = rowY + (60.0F - 26.0F) / 2.0F;

      for (int i = 0; i < specs.size(); i++) {
         Spec spec = specs.get(i);
         float bw = widths[i];
         boolean hover = mx >= bx && mx <= bx + bw && my >= by && my <= by + 26.0F;
         this.drawButton(vg, theme, bx, by, bw, 26.0F, spec.label(), 12.0F, spec.primary(), hover, spec.danger());
         this.hits.add(new Hit(spec.action(), slot.index(), bx, by, bw, 26.0F, spec.primary()));
         bx += bw + 6.0F;
      }
   }

   private void drawButton(
      NVGRenderer vg, Theme theme, float x, float y, float w, float h, String label, float font, boolean primary, boolean hover, boolean danger
   ) {
      int accent = danger ? -45730 : theme.accent();
      int accentBright = danger ? -37252 : theme.accentBright();
      if (primary) {
         int top = hover ? Colors.lighten(accentBright, 0.1F) : accentBright;
         vg.rectGradient(x, y, w, h, h / 2.0F, top, accent, true);
         if (hover) {
            vg.glow(x, y, w, h, h / 2.0F, 5.0F, Colors.withAlpha(accent, 0.35F));
         }

         vg.text(label, x + (w - vg.textWidth(label, font)) / 2.0F, y + h / 2.0F, font, -15593706);
      } else {
         vg.rect(x, y, w, h, h / 2.0F, Colors.withAlpha(-1, hover ? 0.12F : 0.06F));
         vg.rectOutline(x, y, w, h, h / 2.0F, 1.0F, Colors.withAlpha(hover ? accentBright : accent, hover ? 0.7F : 0.28F));
         int rest = danger ? Colors.withAlpha(accent, 0.85F) : theme.textMuted();
         vg.text(label, x + (w - vg.textWidth(label, font)) / 2.0F, y + h / 2.0F, font, hover ? (danger ? accentBright : theme.textPrimary()) : rest);
      }
   }

   private void renderFooter(NVGRenderer vg, Theme theme) {
      float y = this.cardY + this.cardH - 19.0F;
      vg.text("Export copies to your clipboard & a file · Import pastes it", this.cardX + 18.0F, y, 11.0F, theme.textMuted());
      vg.text("v1", this.cardX + 600.0F - 18.0F - vg.textWidth("v1", 11.0F), y, 11.0F, theme.textDisabled());
   }

   private void renderConfirm(NVGRenderer vg, Theme theme, float mx, float my, float uiWidth, float uiHeight) {
      vg.rect(this.cardX, this.cardY, 600.0F, this.cardH, 18.0F, Colors.withAlpha(-16316918, 0.55F));
      this.confirmCardW = 380.0F;
      this.confirmCardH = 148.0F;
      this.confirmCardX = (uiWidth - this.confirmCardW) / 2.0F;
      this.confirmCardY = (uiHeight - this.confirmCardH) / 2.0F;
      boolean danger = this.pendingConfirm.action() == ConfigPanel.Action.DELETE;
      vg.glow(this.confirmCardX, this.confirmCardY, this.confirmCardW, this.confirmCardH, 16.0F, 20.0F, Colors.withAlpha(-16777216, 0.5F));
      vg.rectVaryingGradient(
         this.confirmCardX, this.confirmCardY, this.confirmCardW, this.confirmCardH, 16.0F, 16.0F, 16.0F, 16.0F, theme.headerTop(), theme.background()
      );
      vg.rectOutline(
         this.confirmCardX, this.confirmCardY, this.confirmCardW, this.confirmCardH, 16.0F, 1.2F, Colors.withAlpha(danger ? -45730 : theme.accent(), 0.45F)
      );
      vg.text(this.pendingConfirm.title(), this.confirmCardX + 22.0F, this.confirmCardY + 34.0F, 16.0F, theme.textPrimary());
      vg.text(this.pendingConfirm.body(), this.confirmCardX + 22.0F, this.confirmCardY + 58.0F, 12.0F, theme.textMuted());
      float by = this.confirmCardY + this.confirmCardH - 30.0F - 20.0F;
      this.confirmOkW = 118.0F;
      this.confirmCancelW = 92.0F;
      this.confirmOkH = 30.0F;
      this.confirmCancelH = 30.0F;
      this.confirmOkX = this.confirmCardX + this.confirmCardW - 22.0F - this.confirmOkW;
      this.confirmOkY = by;
      this.confirmCancelX = this.confirmOkX - 10.0F - this.confirmCancelW;
      this.confirmCancelY = by;
      boolean okHover = mx >= this.confirmOkX && mx <= this.confirmOkX + this.confirmOkW && my >= by && my <= by + 30.0F;
      boolean cancelHover = mx >= this.confirmCancelX && mx <= this.confirmCancelX + this.confirmCancelW && my >= by && my <= by + 30.0F;
      this.drawButton(vg, theme, this.confirmCancelX, this.confirmCancelY, this.confirmCancelW, this.confirmCancelH, "Cancel", 12.5F, false, cancelHover, false);
      this.drawButton(vg, theme, this.confirmOkX, this.confirmOkY, this.confirmOkW, this.confirmOkH, this.confirmLabel(), 12.5F, true, okHover, danger);
   }

   private String confirmLabel() {
      return switch (this.pendingConfirm.action()) {
         case IMPORT -> "Import";
         case DELETE -> "Delete";
         default -> "Overwrite";
      };
   }

   public boolean mouseClicked(float mx, float my, int button) {
      if (!this.open) {
         return false;
      } else {
         if (this.renamingSlot >= 0) {
            this.commitRename();
         }

         if (this.pendingConfirm != null) {
            if (this.hit(mx, my, this.confirmOkX, this.confirmOkY, this.confirmOkW, this.confirmOkH)) {
               this.runConfirm();
            } else if (this.hit(mx, my, this.confirmCancelX, this.confirmCancelY, this.confirmCancelW, this.confirmCancelH)
               || !this.hit(mx, my, this.confirmCardX, this.confirmCardY, this.confirmCardW, this.confirmCardH)) {
               this.pendingConfirm = null;
               UiSounds.select();
            }

            return true;
         } else if (mx >= this.closeX - 5.0F
            && mx <= this.closeX + this.closeSize + 5.0F
            && my >= this.closeY - 5.0F
            && my <= this.closeY + this.closeSize + 5.0F) {
            this.close();
            UiSounds.guiClose();
            return true;
         } else {
            for (ConfigPanel.Hit h : this.hits) {
               if (h.contains(mx, my)) {
                  this.dispatch(h.action, h.slot);
                  return true;
               }
            }

            if (mx < this.cardX || mx > this.cardX + 600.0F || my < this.cardY || my > this.cardY + this.cardH) {
               this.close();
               UiSounds.guiClose();
            }

            return true;
         }
      }
   }

   public boolean keyPressed(int keyCode) {
      if (!this.open) {
         return false;
      } else if (this.renamingSlot >= 0) {
         switch (keyCode) {
            case 256:
               this.cancelRename();
               break;
            case 257:
            case 335:
               this.commitRename();
               break;
            case 259:
               if (this.renameBuffer.length() > 0) {
                  this.renameBuffer.deleteCharAt(this.renameBuffer.length() - 1);
               }
         }

         return true;
      } else if (this.pendingConfirm != null) {
         switch (keyCode) {
            case 256:
               this.pendingConfirm = null;
               UiSounds.select();
               break;
            case 257:
            case 335:
               this.runConfirm();
         }

         return true;
      } else if (keyCode == 256) {
         this.close();
         UiSounds.guiClose();
         return true;
      } else {
         return true;
      }
   }

   public boolean charTyped(int codepoint) {
      if (this.open && this.renamingSlot >= 0) {
         if (this.renameBuffer.length() >= 24) {
            return true;
         } else {
            char c = (char)codepoint;
            if (c >= ' ' && c < 127) {
               this.renameBuffer.append(c);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private void dispatch(ConfigPanel.Action action, int slot) {
      ConfigStore store = this.store();
      switch (action) {
         case ACTIVATE:
            this.doActivate(slot);
            break;
         case SAVE:
            if (store.slot(slot).filled()) {
               this.pendingConfirm = new Confirm(
                  ConfigPanel.Action.SAVE,
                  slot,
                  "Overwrite \"" + store.slot(slot).name() + "\"?",
                  "This replaces the config saved in slot " + (slot + 1) + ".",
                  null
               );
               UiSounds.select();
            } else {
               this.doSave(slot);
            }
            break;
         case RENAME:
            this.beginRename(slot);
            break;
         case EXPORT:
            this.doExport(slot);
            break;
         case IMPORT:
            String clip = this.readClipboard();
            if (clip == null || clip.isBlank()) {
               this.toast("Nothing on the clipboard to import");
               UiSounds.select();
               return;
            }

            if (store.slot(slot).filled()) {
               this.pendingConfirm = new Confirm(
                  ConfigPanel.Action.IMPORT,
                  slot,
                  "Import over \"" + store.slot(slot).name() + "\"?",
                  "This replaces slot " + (slot + 1) + " with the pasted config.",
                  clip
               );
               UiSounds.select();
            } else {
               this.doImport(slot, clip);
            }
            break;
         case DELETE:
            this.pendingConfirm = new Confirm(
               ConfigPanel.Action.DELETE, slot, "Delete \"" + store.slot(slot).name() + "\"?", "This permanently removes slot " + (slot + 1) + ".", null
            );
            UiSounds.select();
      }
   }

   private void runConfirm() {
      ConfigPanel.Confirm c = this.pendingConfirm;
      this.pendingConfirm = null;
      if (c != null) {
         if (c.action() == ConfigPanel.Action.SAVE) {
            this.doSave(c.slot());
         } else if (c.action() == ConfigPanel.Action.IMPORT) {
            this.doImport(c.slot(), c.payload());
         } else if (c.action() == ConfigPanel.Action.DELETE) {
            this.doDelete(c.slot());
         }
      }
   }

   private void doDelete(int slot) {
      String name = this.store().slot(slot).name();
      if (this.store().delete(slot)) {
         this.toast("Deleted \"null\"");
         UiSounds.select();
      } else {
         this.toast("Couldn't delete the config");
      }
   }

   private void doSave(int slot) {
      if (this.store().save(slot)) {
         this.toast("Saved to \"" + this.store().slot(slot).name() + "\"");
         UiSounds.toggle(true);
      } else {
         this.toast("Couldn't save the config");
      }
   }

   private void doActivate(int slot) {
      if (this.store().activate(slot)) {
         this.toast("Activated \"" + this.store().slot(slot).name() + "\"");
         UiSounds.toggle(true);
      } else {
         this.toast("That slot is empty");
      }
   }

   private void doExport(int slot) {
      String json = this.store().export(slot);
      if (json == null) {
         this.toast("That slot is empty");
      } else {
         this.setClipboard(json);
         this.toast("Copied \"" + this.store().slot(slot).name() + "\" to clipboard");
         UiSounds.select();
      }
   }

   private void doImport(int slot, String payload) {
      ConfigStore.ImportResult result = this.store().importInto(slot, payload);
      this.toast(result.message());
      UiSounds.toggle(true);
   }

   private void beginRename(int slot) {
      this.renamingSlot = slot;
      this.renameBuffer.setLength(0);
      this.renameBuffer.append(this.store().slot(slot).name());
      UiSounds.select();
   }

   private void commitRename() {
      if (this.renamingSlot >= 0) {
         this.store().rename(this.renamingSlot, this.renameBuffer.toString());
         this.renamingSlot = -1;
      }
   }

   private void cancelRename() {
      this.renamingSlot = -1;
   }

   private boolean hit(float mx, float my, float x, float y, float w, float h) {
      return mx >= x && mx <= x + w && my >= y && my <= y + h;
   }

   private void toast(String message) {
      if (FeClient.notifications() != null) {
         FeClient.notifications().pushInfo(message);
      }
   }

   private String readClipboard() {
      try {
         return MinecraftClient.getInstance().keyboard.getClipboard();
      } catch (Exception var2) {
         return null;
      }
   }

   private void setClipboard(String text) {
      try {
         MinecraftClient.getInstance().keyboard.setClipboard(text);
      } catch (Exception var3) {
      }
   }

   private static String relativeTime(long savedAt) {
      if (savedAt <= 0L) {
         return "just now";
      } else {
         long diff = System.currentTimeMillis() - savedAt;
         if (diff < 60000L) {
            return "just now";
         } else {
            long minutes = diff / 60000L;
            if (minutes < 60L) {
               return minutes + "m ago";
            } else {
               long hours = minutes / 60L;
               return hours < 24L ? hours + "h ago" : hours / 24L + "d ago";
            }
         }
      }
   }

   public void debugBeginRename(int slot) {
      this.open();
      this.beginRename(slot);
   }

   public void debugConfirmOverwrite(int slot) {
      this.open();
      this.pendingConfirm = new Confirm(
         ConfigPanel.Action.SAVE,
         slot,
         "Overwrite \"" + this.store().slot(slot).name() + "\"?",
         "This replaces the config saved in slot " + (slot + 1) + ".",
         null
      );
   }

   public void debugConfirmDelete(int slot) {
      this.open();
      this.pendingConfirm = new Confirm(
         ConfigPanel.Action.DELETE, slot, "Delete \"" + this.store().slot(slot).name() + "\"?", "This permanently removes slot " + (slot + 1) + ".", null
      );
   }

   enum Action {
      ACTIVATE,
      SAVE,
      RENAME,
      EXPORT,
      IMPORT,
      DELETE;

      private static ConfigPanel.Action[] $values() {
         return new ConfigPanel.Action[]{ACTIVATE, SAVE, RENAME, EXPORT, IMPORT, DELETE};
      }
   }

   final class Confirm {
      private ConfigPanel.Action action;
      private int slot;
      private String title;
      private String body;
      private String payload;

      private Confirm(ConfigPanel.Action action, int slot, String title, String body, String payload) {
         this.action = action;
         this.slot = slot;
         this.title = title;
         this.body = body;
         this.payload = payload;
      }

      public ConfigPanel.Action action() {
         return this.action;
      }

      public int slot() {
         return this.slot;
      }

      public String title() {
         return this.title;
      }

      public String body() {
         return this.body;
      }

      public String payload() {
         return this.payload;
      }
   }

   final class Hit {
      final ConfigPanel.Action action;
      final int slot;
      final float floatVal;
      final float floatVal2;
      final float floatVal3;
      final float floatVal4;
      final boolean primary;

      Hit(ConfigPanel.Action action, int slot, float x, float y, float w, float h, boolean primary) {
         this.action = action;
         this.slot = slot;
         this.floatVal = x;
         this.floatVal2 = y;
         this.floatVal3 = w;
         this.floatVal4 = h;
         this.primary = primary;
      }

      boolean contains(float mx, float my) {
         return mx >= this.floatVal && mx <= this.floatVal + this.floatVal3 && my >= this.floatVal2 && my <= this.floatVal2 + this.floatVal4;
      }
   }
}



