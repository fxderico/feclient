package dev.fede.util;

import java.util.ArrayDeque;
import java.util.Deque;

public final class CpsTracker {
   private static final Deque<Long> LEFT = new ArrayDeque<>();
   private static final Deque<Long> RIGHT = new ArrayDeque<>();

   private CpsTracker() {
   }

   public static void onClick(int button) {
      Deque<Long> deque = button == 0 ? LEFT : (button == 1 ? RIGHT : null);
      if (deque != null) {
         synchronized (deque) {
            deque.addLast(System.nanoTime());
         }
      }
   }

   public static int get(int button) {
      Deque<Long> deque = button == 0 ? LEFT : RIGHT;
      long cutoff = System.nanoTime() - 1000000000L;
      synchronized (deque) {
         while (!deque.isEmpty() && deque.peekFirst() < cutoff) {
            deque.pollFirst();
         }

         return deque.size();
      }
   }
}

