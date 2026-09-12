package dev.fede.nyx.module.modules.client;

public enum HOVER {
   TOGGLE_ON,
   TOGGLE_OFF,
   HOVER,
   DRAG_START,
   DRAG_END,
   PANEL_OPEN;

   private static final HOVER[] clickSoundsTypeArray = getClickSoundsTypeArray();

   private static HOVER[] getClickSoundsTypeArray() {
      return new HOVER[]{TOGGLE_ON, TOGGLE_OFF, HOVER, DRAG_START, DRAG_END, PANEL_OPEN};
   }
}

