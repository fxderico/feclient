package dev.fede.zenithgui.utility.math;

public class TimerUtils {
    private long lastTime = 0L;

    public TimerUtils() {
        this.reset();
    }

    public void reset() {
        this.lastTime = System.currentTimeMillis();
    }

    public long getPassedTime() {
        return System.currentTimeMillis() - this.lastTime;
    }
}
