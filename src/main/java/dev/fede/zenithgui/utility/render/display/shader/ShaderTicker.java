package dev.fede.zenithgui.utility.render.display.shader;

import dev.fede.zenithgui.utility.math.Timer;

public class ShaderTicker {
    private final Timer timer = new Timer();
    private long passedTime = 0L;

    public ShaderTicker() {
        this.timer.reset();
    }

    public void reset() {
        this.passedTime = 0L;
        this.timer.reset();
    }

    public void update(float speed) {
        this.passedTime = this.passedTime + (long)((float)this.timer.getElapsedTime() * speed);
        this.timer.reset();
    }

    public long getPassedTime() {
        return this.passedTime;
    }
}
