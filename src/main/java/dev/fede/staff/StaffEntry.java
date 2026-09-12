package dev.fede.staff;

public final class StaffEntry {
   private String name;
   private String rankLabel;
   private int color;
   private boolean vanished;
   private int latency;
   private int priority;

   public StaffEntry(String name, String rankLabel, int color, boolean vanished, int latency, int priority) {
      this.name = name;
      this.rankLabel = rankLabel;
      this.color = color;
      this.vanished = vanished;
      this.latency = latency;
      this.priority = priority;
   }

   public boolean hasColor() {
      return (this.color & 16777215) != 0;
   }

   public String name() {
      return this.name;
   }

   public String rankLabel() {
      return this.rankLabel;
   }

   public int color() {
      return this.color;
   }

   public boolean vanished() {
      return this.vanished;
   }

   public int latency() {
      return this.latency;
   }

   public int priority() {
      return this.priority;
   }
}

