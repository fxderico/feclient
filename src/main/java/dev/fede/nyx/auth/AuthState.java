package dev.fede.nyx.auth;

import dev.fede.nyx.util.AntiDebugUtil;

public final class AuthState {
   public static volatile AuthState.State authStateState = AuthState.State.LOCKED;
   public static volatile String string = "";
   public static volatile int intVal = -1;
   public static volatile boolean bool = false;
   public static volatile boolean bool2 = false;
   private static final Object object = new Object();

   private AuthState() {
   }

   public static boolean isEnabled() {
      return true;
   }

   public static void run2() {
      try {
         try {
            AuthService.run2();
         } catch (Throwable var10) {
         }

         authStateState = AuthState.State.LOCKED;
         AuthGate.run2();

         try {
            Integrity.run();
         } catch (Throwable var9) {
         }

         try {
            AntiDebug.run();
         } catch (Throwable var8) {
         }

         try {
            AntiTamper.run();
         } catch (Throwable var7) {
         }
      } finally {
         bool2 = true;
      }
   }

   public static void run(String var0) {
      if (!bool) {
         if (var0 == null) {
            var0 = "";
         }

         String var1 = var0.trim();
         if (var1.isEmpty()) {
            authStateState = AuthState.State.authStateState;
            string = "Enter a license key";
         } else {
            bool = true;
            authStateState = AuthState.State.LOADING;
            string = "";
            Thread var2 = new Thread(() -> {}, "CodeEngine-Auth-Login");
            var2.setDaemon(true);
            var2.start();
         }
      }
   }

   public static void run3() {
      AuthService.run2();
      authStateState = AuthState.State.LOCKED;
      string = "";
      intVal = -1;
      AuthGate.run2();
   }

   private static void run4(String var0, AuthService.Inner2 var1) {
      if (var1.bool) {
         authStateState = AuthState.State.AUTHENTICATED;
         intVal = var1.intVal;
         string = "";
         AuthGate.run(AuthGate.longOf(var1.string2, AuthService.getString()));
         AuthService.run3(var0);
      } else if (var1.bool2) {
         authStateState = AuthState.State.authStateState3;
         string = "Cannot reach license server";
         AuthGate.run2();
      } else {
         AuthService.run2();
         AuthGate.run2();
         intVal = -1;
         String var2 = var1.string == null ? "" : var1.string.toLowerCase();
         if (var2.contains(AntiDebugUtil.stringOf(new byte[]{76, -6, 43, 55}))) {
            authStateState = AuthState.State.authStateState2;
            string = AntiDebugUtil.stringOf(
               new byte[]{
                  108,
                  -38,
                  11,
                  23,
                  -80,
                  -28,
                  -25,
                  -36,
                  -47,
                  36,
                  -18,
                  -88,
                  -64,
                  -31,
                  -124,
                  39,
                  -64,
                  -35,
                  -98,
                  42,
                  -93,
                  -100,
                  80,
                  -20,
                  -119,
                  -107,
                  40,
                  -44,
                  -83,
                  95,
                  114,
                  -73,
                  -16,
                  2,
                  -126,
                  82,
                  -48,
                  13,
                  -121,
                  -23,
                  122,
                  64,
                  -120,
                  -50,
                  102,
                  -43,
                  -26,
                  -22,
                  -43,
                  -66,
                  58,
                  74,
                  78,
                  -68
               }
            );
         } else if (var2.contains("expire")) {
            authStateState = AuthState.State.EXPIRED;
            string = "License expired";
         } else if (var2.contains(AntiDebugUtil.stringOf(new byte[]{77, -29, 52, 50, -4, -32, -22})) || var2.contains("not_found") || var2.contains("unknown")) {
            authStateState = AuthState.State.authStateState;
            string = AntiDebugUtil.stringOf(new byte[]{109, -29, 52, 50, -4, -32, -22, -113, -48, 44, -7, -82, -58, -78, 3, -121, 63, -104, -117});
         } else if (!var2.isEmpty()) {
            authStateState = AuthState.State.authStateState;
            string = "Rejected: " + var1.string;
         } else {
            authStateState = AuthState.State.authStateState;
            string = "License rejected";
         }
      }
   }

   private static void run5(String var0) {
      synchronized (object) {
         try {
            AuthService.Inner2 var2 = AuthService.authServicebOf(var0);
            run4(var0, var2);
         } catch (Throwable var8) {
            authStateState = AuthState.State.authStateState3;
            string = "Unexpected error: " + var8.getClass().getSimpleName();
         } finally {
            bool = false;
         }
      }
   }

   public static enum State {
      LOCKED,
      LOADING,
      AUTHENTICATED,
      authStateState,
      authStateState2,
      EXPIRED,
      authStateState3;

      private static final AuthState.State[] authStateStateArray = getAuthStateStateArray();

      private static AuthState.State[] getAuthStateStateArray() {
         return new AuthState.State[]{LOCKED, LOADING, AUTHENTICATED, authStateState, authStateState2, EXPIRED, authStateState3};
      }
   }
}
