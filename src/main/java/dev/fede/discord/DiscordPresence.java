package dev.fede.discord;

import dev.fede.FeClient;

import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Minimal Discord Rich Presence over the local IPC pipe.
 *
 * We roll our own instead of using the old club.minnced/discord-rpc native lib
 * because that library predates Rich Presence buttons and cannot show them —
 * and a button linking to the releases page is the whole point here.
 *
 * Protocol: connect to \\.\pipe\discord-ipc-{0..9}, send a HANDSHAKE frame
 * (op 0) with the client_id, then SET_ACTIVITY frames (op 1). Each frame is
 * [op int32-LE][len int32-LE][utf8 json]. Everything runs on a daemon thread
 * and fails silently if Discord isn't running.
 */
public final class DiscordPresence {
   public static final String APP_ID = "1552059055596371998";
   private static final String RELEASES_URL = "https://github.com/fxderico/feclient/releases/latest";

   private static volatile RandomAccessFile pipe;
   private static volatile boolean running;
   private static volatile Thread worker;
   private static long startEpoch;

   private DiscordPresence() {
   }

   public static synchronized void start() {
      if (running) {
         return;
      }
      running = true;
      startEpoch = System.currentTimeMillis() / 1000L;
      worker = new Thread(DiscordPresence::run, "feclient-discord-rpc");
      worker.setDaemon(true);
      worker.start();
   }

   public static synchronized void stop() {
      running = false;
      RandomAccessFile p = pipe;
      pipe = null;
      if (p != null) {
         try {
            p.close();
         } catch (Throwable ignored) {
         }
      }
   }

   public static boolean isRunning() {
      return running;
   }

   private static void run() {
      try {
         if (!connect()) {
            running = false;
            return;
         }

         handshake();
         // keep the presence alive; Discord drops it if the pipe closes
         while (running) {
            updateActivity();
            try {
               Thread.sleep(15000L);
            } catch (InterruptedException e) {
               break;
            }
         }
      } catch (Throwable t) {
         FeClient.LOGGER.warn("[DiscordRPC] presence stopped: {}", t.toString());
      } finally {
         stop();
      }
   }

   private static boolean connect() {
      for (int i = 0; i < 10; i++) {
         try {
            // Windows named pipe; Discord opens ipc-0..9
            pipe = new RandomAccessFile("\\\\.\\pipe\\discord-ipc-" + i, "rw");
            return true;
         } catch (Throwable ignored) {
            // try the next slot
         }
      }
      FeClient.LOGGER.warn("[DiscordRPC] no discord-ipc pipe found (is Discord running?)");
      return false;
   }

   private static void handshake() throws Exception {
      String json = "{\"v\":1,\"client_id\":\"" + APP_ID + "\"}";
      writeFrame(0, json);
      readFrameQuietly(); // consume the READY event
   }

   private static void updateActivity() {
      try {
         long pid = ProcessHandle.current().pid();
         String nonce = UUID.randomUUID().toString();
         String activity = "{"
            + "\"cmd\":\"SET_ACTIVITY\",\"nonce\":\"" + nonce + "\",\"args\":{"
            + "\"pid\":" + pid + ","
            + "\"activity\":{"
            + "\"details\":\"exploiting with feclient\","
            + "\"assets\":{\"large_image\":\"logo\",\"large_text\":\"FeClient\"},"
            + "\"timestamps\":{\"start\":" + startEpoch + "},"
            + "\"buttons\":[{\"label\":\"Download\",\"url\":\"" + RELEASES_URL + "\"}]"
            + "}}}";
         writeFrame(1, activity);
         readFrameQuietly();
      } catch (Throwable t) {
         FeClient.LOGGER.warn("[DiscordRPC] activity update failed: {}", t.toString());
         running = false;
      }
   }

   private static synchronized void writeFrame(int op, String json) throws Exception {
      RandomAccessFile p = pipe;
      if (p == null) {
         throw new IllegalStateException("pipe closed");
      }
      byte[] data = json.getBytes(StandardCharsets.UTF_8);
      byte[] frame = new byte[8 + data.length];
      putLE(frame, 0, op);
      putLE(frame, 4, data.length);
      System.arraycopy(data, 0, frame, 8, data.length);
      p.write(frame);
   }

   private static void readFrameQuietly() {
      try {
         RandomAccessFile p = pipe;
         if (p == null) {
            return;
         }
         byte[] header = new byte[8];
         p.readFully(header);
         int len = getLE(header, 4);
         if (len > 0 && len < 1 << 20) {
            byte[] body = new byte[len];
            p.readFully(body);
         }
      } catch (Throwable ignored) {
      }
   }

   private static void putLE(byte[] b, int off, int v) {
      b[off] = (byte)(v & 0xFF);
      b[off + 1] = (byte)(v >>> 8 & 0xFF);
      b[off + 2] = (byte)(v >>> 16 & 0xFF);
      b[off + 3] = (byte)(v >>> 24 & 0xFF);
   }

   private static int getLE(byte[] b, int off) {
      return b[off] & 0xFF | (b[off + 1] & 0xFF) << 8 | (b[off + 2] & 0xFF) << 16 | (b[off + 3] & 0xFF) << 24;
   }
}
