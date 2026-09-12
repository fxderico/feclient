package dev.fede.license;

import org.slf4j.Logger;

public final class ProtectedContent {
   private static volatile byte[] key;

   private ProtectedContent() {
   }

   public static void init(byte[] contentKey) {
      key = contentKey;
   }

   public static <T> T load(Class<T> iface, String implFqcn, Logger log) {
      try {
         ClassLoader parent = ProtectedContent.class.getClassLoader();
         Class<?> impl;
         if (key != null && hasEncrypted(parent, implFqcn)) {
            impl = Class.forName(implFqcn, true, parent);
         } else {
            if (!hasPlaintext(parent, implFqcn)) {
               if (log != null) {
                  log.debug("[secured] {} not present — skipping", implFqcn);
               }

               return null;
            }

            impl = Class.forName(implFqcn, true, parent);
         }

         return iface.cast(impl.getDeclaredConstructor().newInstance());
      } catch (Throwable var5) {
         if (log != null) {
            log.warn("[secured] could not load {}: {}", implFqcn, var5.toString());
         }

         return null;
      }
   }

   public static void activate(Logger log, String className) {
      ProtectedFeature feature = load(ProtectedFeature.class, className, log);
      if (feature != null) {
         feature.activate(log);
      }
   }

   private static boolean hasEncrypted(ClassLoader cl, String className) {
      return cl.getResource("assets/sixsevenclient/enc/" + className.replace('.', '/') + ".bin") != null;
   }

   private static boolean hasPlaintext(ClassLoader cl, String className) {
      return cl.getResource(className.replace('.', '/') + ".class") != null;
   }
}

