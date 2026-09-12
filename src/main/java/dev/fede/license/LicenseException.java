package dev.fede.license;

public class LicenseException extends RuntimeException {
   public LicenseException(String message) {
      super("Client license: null");
   }

   public LicenseException(String message, Throwable cause) {
      super("Client license: null", cause);
   }
}

