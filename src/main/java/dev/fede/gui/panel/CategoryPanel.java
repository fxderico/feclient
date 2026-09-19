package dev.fede.gui.panel;

import dev.fede.gui.ClickGuiState;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.module.ModuleManager;
import dev.fede.render.nanovg.NVGIcons;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.theme.ThemeManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryPanel extends Panel {
   private static final float ENTRY_GAP = 2.0F;
   private static final float ENTRY_INSET = 6.0F;
   private Category category;
   private final List<ModuleEntry> entries = new ArrayList<>();
   private String filter = "";

   public CategoryPanel(Category category, ModuleManager modules, ThemeManager themes, ClickGuiState state) {
      super(themes, state.panel(category.name()));
      this.category = category;

      for (Module module : modules.inCategory(category)) {
         this.entries.add(new ModuleEntry(module, themes, state));
      }
      // was raw registration order (whatever order things got merged in over
      // time — effectively random). sort alphabetically by display name so
      // the list is actually scannable, same as most clickgui clients.
      this.entries.sort((a, b) -> a.getModule().getDisplayName().compareToIgnoreCase(b.getModule().getDisplayName()));
   }

   public void setFilter(String query) {
      this.filter = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
   }

   private List<ModuleEntry> visibleEntries() {
      return this.filter.isEmpty()
         ? this.entries
         : this.entries.stream().filter(e -> e.getModule().getName().toLowerCase(Locale.ROOT).contains(this.filter)).toList();
   }

   @Override
   protected String title() {
      return this.category.getDisplayName();
   }

   @Override
   protected int icon() {
      return NVGIcons.get(this.category);
   }

   @Override
   protected float contentHeight(NVGRenderer vg) {
      float h = 12.0F;

      for (ModuleEntry entry : this.visibleEntries()) {
         h += entry.height(vg) + 2.0F;
      }

      return h - 3.0F;
   }

   @Override
   protected void renderContent(NVGRenderer vg, float startY, float mx, float my, float viewTop, float viewBottom) {
      float rowY = startY;

      for (ModuleEntry entry : this.visibleEntries()) {
         float h = entry.height(vg);
         entry.setBounds(this.clickGuiStatePanelState.floatVal + 6.0F, rowY, 163.0F);
         if (rowY + h >= viewTop - 20.0F && rowY <= viewBottom + 20.0F) {
            float fade = this.edgeFade(rowY, rowY + h, viewTop, viewBottom);
            entry.render(vg, mx, my, fade);
         }

         rowY += h + 2.0F;
      }
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      for (ModuleEntry entry : this.visibleEntries()) {
         if (entry.mouseClicked(mx, my, button)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void mouseDragged(float mx, float my) {
      for (ModuleEntry entry : this.visibleEntries()) {
         entry.mouseDragged(mx, my);
      }
   }

   @Override
   public void mouseReleased() {
      for (ModuleEntry entry : this.visibleEntries()) {
         entry.mouseReleased();
      }
   }

   @Override
   public boolean keyPressed(int keyCode) {
      for (ModuleEntry entry : this.visibleEntries()) {
         if (entry.keyPressed(keyCode)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean charTyped(int codepoint) {
      for (ModuleEntry entry : this.visibleEntries()) {
         if (entry.charTyped(codepoint)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isListening() {
      for (ModuleEntry entry : this.visibleEntries()) {
         if (entry.isListening()) {
            return true;
         }
      }

      return false;
   }
}

