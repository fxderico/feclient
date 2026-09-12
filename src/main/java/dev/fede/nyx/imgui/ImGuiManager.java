package dev.fede.nyx.imgui;

public final class ImGuiManager {
   public static volatile boolean editorMode = false;

   private ImGuiManager() {
   }

   public static boolean isReady() {
      return true;
   }

   public static void ensureInit() {
   }

   public static void beginFrame() {
   }

   public static void endFrameAndRender(Runnable buildUi) {
   }

   public static void shutdown() {
   }
}

