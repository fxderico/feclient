package dev.fede.nyx.auth;

import java.util.concurrent.atomic.AtomicBoolean;

public final class AntiDebug {
   private static final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

   private AntiDebug() {
   }

   public static void run() {
      atomicBoolean.set(true);
   }

   public static void run2() {
   }

   public static boolean isEnabled8() {
      return atomicBoolean.get();
   }
}
