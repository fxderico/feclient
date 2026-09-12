package dev.fede.license;

import org.slf4j.Logger;

public interface ProtectedFeature {
   String name();

   void activate(Logger var1);
}

