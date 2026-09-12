package dev.fede.water.module;

public enum Category {
   field_a_1("COMBAT"),
   b("RENDER"),
   c("MISC"),
   d("DONUT"),
   e("CLIENT");

   private final String field_a_2;

   private Category(String name) {
      this.field_a_2 = name;
   }

   public String getName() {
      return this.field_a_2;
   }
}

