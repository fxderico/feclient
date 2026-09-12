package dev.fede.spotify;

public final class SpotifyState {
   private boolean active;
   private String title;
   private String artist;
   private long posMs;
   private long durMs;
   private boolean playing;
   private boolean canSeek;
   private int artVersion;
   private int volume;
   private long receivedNanos;
   public static final SpotifyState INACTIVE = new SpotifyState(false, "", "", 0L, 0L, false, false, 0, -1, 0L);

   public SpotifyState(
      boolean active, String title, String artist, long posMs, long durMs, boolean playing, boolean canSeek, int artVersion, int volume, long receivedNanos
   ) {
      this.active = active;
      this.title = title;
      this.artist = artist;
      this.posMs = posMs;
      this.durMs = durMs;
      this.playing = playing;
      this.canSeek = canSeek;
      this.artVersion = artVersion;
      this.volume = volume;
      this.receivedNanos = receivedNanos;
   }

   public long livePosMs() {
      if (this.playing && this.durMs > 0L) {
         long elapsed = (System.nanoTime() - this.receivedNanos) / 1000000L;
         return Math.min(this.posMs + elapsed, this.durMs);
      } else {
         return this.posMs;
      }
   }

   public boolean active() {
      return this.active;
   }

   public String title() {
      return this.title;
   }

   public String artist() {
      return this.artist;
   }

   public long posMs() {
      return this.posMs;
   }

   public long durMs() {
      return this.durMs;
   }

   public boolean playing() {
      return this.playing;
   }

   public boolean canSeek() {
      return this.canSeek;
   }

   public int artVersion() {
      return this.artVersion;
   }

   public int volume() {
      return this.volume;
   }

   public long receivedNanos() {
      return this.receivedNanos;
   }
}

