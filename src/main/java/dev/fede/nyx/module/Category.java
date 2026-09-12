package dev.fede.nyx.module;

public enum Category {
   COMBAT("Combat"),
   MOVEMENT("Movement"),
   RENDER("Render"),
   PLAYER("Player"),
   WORLD("World"),
   CLIENT("Client"),
   ADDONS("Addons"),
   CONFIG("Config"),
   DONUTSMP("DonutSMP");

   private final String string;
   private static final Category[] categoryArray = getCategoryArray();

   private Category(String displayName) {
      this.string = displayName;
   }

   public String getString() {
      return this.string;
   }

   private static Category[] getCategoryArray() {
      return new Category[]{COMBAT, MOVEMENT, RENDER, PLAYER, WORLD, CLIENT, ADDONS, CONFIG, DONUTSMP};
   }
}

