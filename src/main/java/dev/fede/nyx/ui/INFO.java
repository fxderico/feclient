package dev.fede.nyx.ui;

public enum INFO {
   UNKNOWN(-12874753, -10773290, "\uf05a"),
   UNKNOWN_2(-12976364, -13315175, "\uf058"),
   UNKNOWN_3(-932849, -680437, "\uf071"),
   UNKNOWN_4(-1618884, -1096636, "\uf057");

   public final int intVal;
   public final int intVal2;
   public final String string;
   private static final INFO[] notificationTypeArray = getNotificationTypeArray();

   private INFO(int var3, int var4, String var5) {
      this.intVal = var3;
      this.intVal2 = var4;
      this.string = var5;
   }

   private static INFO[] getNotificationTypeArray() {
      return new INFO[]{UNKNOWN, UNKNOWN_2, UNKNOWN_3, UNKNOWN_4};
   }
}

