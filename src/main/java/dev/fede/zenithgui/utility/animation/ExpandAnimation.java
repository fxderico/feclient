package dev.fede.zenithgui.utility.animation;

public class ExpandAnimation {
    private float currentHeight = 0.0F;
    private float targetHeight = 0.0F;
    private float startHeight = 0.0F;
    private long startTime = 0L;
    private long duration = 400L;
    private boolean animating = false;
    private boolean expanding = true;

    public void startExpand(float fromHeight, float toHeight, long customDuration) {
        this.startHeight = fromHeight;
        this.targetHeight = toHeight;
        this.startTime = System.currentTimeMillis();
        this.duration = customDuration > 0L ? customDuration : 400L;
        this.animating = true;
        this.expanding = true;
    }

    public void startCollapse(float fromHeight, float toHeight, long customDuration) {
        this.startHeight = fromHeight;
        this.targetHeight = toHeight;
        this.startTime = System.currentTimeMillis();
        this.duration = customDuration > 0L ? customDuration : 400L;
        this.animating = true;
        this.expanding = false;
    }

    public float getCurrentHeight() {
        if (!this.animating) {
            return this.targetHeight;
        } else {
            long elapsed = System.currentTimeMillis() - this.startTime;
            float progress = Math.min(1.0F, (float) elapsed / (float) this.duration);
            progress = (float) Math.sin((double) progress * Math.PI / 2.0);
            float height = this.startHeight + (this.targetHeight - this.startHeight) * progress;
            if (progress >= 1.0F) {
                this.animating = false;
                height = this.targetHeight;
            }
            return height;
        }
    }

    public float getTargetHeight() {
        return this.targetHeight;
    }

    public boolean isAnimating() {
        return this.animating;
    }
}
