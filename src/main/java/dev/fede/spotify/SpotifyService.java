package dev.fede.spotify;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.fede.FeClient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;

public final class SpotifyService {
   private final Path artPath = FabricLoader.getInstance().getConfigDir().resolve("feclient_art.img");
   private volatile SpotifyState state = SpotifyState.INACTIVE;
   private Process process;
   private BufferedWriter commandWriter;
   private volatile boolean stopped;
   private int restarts;

   public SpotifyState state() {
      return this.state;
   }

   public Path artPath() {
      return this.artPath;
   }

   public void start() {
      if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
         FeClient.LOGGER.info("SpotifyHUD bridge disabled (not Windows)");
      } else {
         Thread thread = new Thread(this::runBridge, "feclient-spotify-bridge");
         thread.setDaemon(true);
         thread.start();
      }
   }

   public synchronized void stop() {
      this.stopped = true;
      if (this.process != null) {
         this.process.destroy();
      }
   }

   private void runBridge() {
      while (!this.stopped && this.restarts < 4) {
         try {
            Path script = this.extractScript();
            ProcessBuilder builder = new ProcessBuilder(
               "powershell.exe", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-File", script.toString(), this.artPath.toString()
            );
            builder.redirectErrorStream(false);
            synchronized (this) {
               if (this.stopped) {
                  return;
               }

               this.process = builder.start();
               this.commandWriter = new BufferedWriter(new OutputStreamWriter(this.process.getOutputStream(), StandardCharsets.UTF_8));
            }

            String line;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.process.getInputStream(), StandardCharsets.UTF_8))) {
               while ((line = reader.readLine()) != null) {
                  this.parseLine(line.trim());
               }
            }

            this.process.waitFor();
         } catch (Exception var9) {
            FeClient.LOGGER.warn("Spotify bridge died: {}", var9.toString());
         }

         this.state = SpotifyState.INACTIVE;
         this.restarts++;
      }
   }

   private Path extractScript() throws Exception {
      Path dir = FabricLoader.getInstance().getConfigDir();
      Files.createDirectories(dir);
      Path script = dir.resolve("feclient_smtc_bridge.ps1");

      try (InputStream in = SpotifyService.class.getClassLoader().getResourceAsStream("assets/feclient/spotify/smtc_bridge.ps1")) {
         if (in == null) {
            throw new IllegalStateException("bridge script missing from mod resources");
         }

         Files.write(script, in.readAllBytes());
      }

      return script;
   }

   private void parseLine(String line) {
      if (!line.isEmpty() && line.startsWith("{")) {
         try {
            JsonObject json = JsonParser.parseString(line).getAsJsonObject();
            if (!json.has("active") || !json.get("active").getAsBoolean()) {
               this.state = SpotifyState.INACTIVE;
               return;
            }

            this.state = new SpotifyState(
               true,
               json.get("title").getAsString(),
               json.get("artist").getAsString(),
               json.get("posMs").getAsLong(),
               json.get("durMs").getAsLong(),
               json.get("playing").getAsBoolean(),
               json.has("canSeek") && json.get("canSeek").getAsBoolean(),
               json.has("artV") ? json.get("artV").getAsInt() : 0,
               json.has("vol") ? json.get("vol").getAsInt() : -1,
               System.nanoTime()
            );
         } catch (Exception var3) {
         }
      }
   }

   private synchronized void send(String command) {
      if (this.commandWriter != null) {
         try {
            this.commandWriter.write(command);
            this.commandWriter.newLine();
            this.commandWriter.flush();
         } catch (Exception var3) {
            FeClient.LOGGER.warn("Spotify bridge command failed: {}", var3.toString());
         }
      }
   }

   public void next() {
      this.send("NEXT");
   }

   public void previous() {
      this.send("PREV");
   }

   public void togglePlay() {
      this.send("PLAYPAUSE");
   }

   public void seekTo(long ms) {
      this.send("SEEK " + Math.max(0L, ms));
      SpotifyState s = this.state;
      if (s.active()) {
         this.state = new SpotifyState(true, s.title(), s.artist(), ms, s.durMs(), s.playing(), s.canSeek(), s.artVersion(), s.volume(), System.nanoTime());
      }
   }

   public void setVolume(int pct) {
      int v = Math.clamp((long)pct, 0, 100);
      this.send("VOLUME " + v);
      SpotifyState s = this.state;
      if (s.active()) {
         this.state = new SpotifyState(true, s.title(), s.artist(), s.posMs(), s.durMs(), s.playing(), s.canSeek(), s.artVersion(), v, s.receivedNanos());
      }
   }
}



