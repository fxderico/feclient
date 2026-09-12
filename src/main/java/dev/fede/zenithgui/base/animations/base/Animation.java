package dev.fede.zenithgui.base.animations.base;

// [lombok removed]

public class Animation {
    private long duration;
    private float value;
    private Easing easing;
    private long startTime;
    private float startValue;
    private float targetValue;
    private boolean done;
    private boolean direction;

    public Animation(long duration, float initialValue, Easing easing) {
        this.duration = duration;
        this.easing = easing;
        this.value = initialValue;
        this.startValue = initialValue;
        this.targetValue = initialValue;
        this.done = true;
    }

    public Animation(long duration, Easing easing) {
        this(duration, 0.0F, easing);
    }

    public void update(boolean bool) {
        this.update(bool ? 1.0F : 0.0F);
    }

    public float update(float newValue) {
        long currentTime = System.currentTimeMillis();
        if (newValue != this.targetValue) {
            this.startValue = this.value;
            this.targetValue = newValue;
            this.startTime = currentTime;
            this.done = false;
        }

        long elapsed = currentTime - this.startTime;
        if (elapsed >= this.duration) {
            this.value = this.targetValue;
            this.done = true;
            return this.value;
        } else {
            float progress = (float)elapsed / (float)this.duration;
            float easedProgress = this.easing.ease(progress, 0.0F, 1.0F, 1.0F);
            this.value = this.startValue + (this.targetValue - this.startValue) * easedProgress;
            return this.value;
        }
    }

    public void setValue(float newValue) {
        this.value = newValue;
        this.startValue = newValue;
        this.targetValue = newValue;
        this.done = true;
    }

    public void reset(float initialValue) {
        this.value = initialValue;
        this.startValue = initialValue;
        this.targetValue = initialValue;
        this.done = true;
    }

    public void reset() {
        this.reset(0.0F);
    }

    public void animateTo(float newTarget) {
        if (newTarget != this.targetValue) {
            this.startValue = this.value;
            this.targetValue = newTarget;
            this.startTime = System.currentTimeMillis();
            this.done = false;
        }
    }

    public float update() {
        return this.update(this.targetValue);
    }

        public long getDuration() {
        return this.duration;
    }

        public float getValue() {
        return this.value;
    }

        public Easing getEasing() {
        return this.easing;
    }

        public long getStartTime() {
        return this.startTime;
    }

        public float getStartValue() {
        return this.startValue;
    }

        public float getTargetValue() {
        return this.targetValue;
    }

        public boolean isDone() {
        return this.done;
    }

        public boolean isDirection() {
        return this.direction;
    }

        public void setDuration(long duration) {
        this.duration = duration;
    }

        public void setEasing(Easing easing) {
        this.easing = easing;
    }

        public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

        public void setStartValue(float startValue) {
        this.startValue = startValue;
    }

        public void setTargetValue(float targetValue) {
        this.targetValue = targetValue;
    }

        public void setDone(boolean done) {
        this.done = done;
    }

        public void setDirection(boolean direction) {
        this.direction = direction;
    }
}
