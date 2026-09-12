package dev.fede.zenithgui.utility.math;

// [lombok removed]

public class Timer {
    private long millis;

    public Timer() {
        this.reset();
    }

    public boolean finished(float delay) {
        return (float)System.currentTimeMillis() - delay >= (float)this.millis;
    }

    public boolean finished(long delay) {
        return System.currentTimeMillis() - delay >= this.millis;
    }

    public void reset() {
        this.millis = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.millis;
    }

        public long getMillis() {
        return this.millis;
    }

        public void setMillis(long millis) {
        this.millis = millis;
    }
}
