package dev.fede.nyx.util;

import java.util.List;

final class InternalData2 {
   public List<String> list;

   private static final InternalData2 INSTANCE = new InternalData2();
   public static InternalData2 getINSTANCE() { return INSTANCE; }
   private InternalData2() {
   }
}

