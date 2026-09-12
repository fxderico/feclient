package dev.fede.zenithgui.utility.render;

// [lombok removed]
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class RenderPosition {
    private BlockPos pos;
    private long startTime;

    public RenderPosition(BlockPos pos) {
        this.pos = pos;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RenderPosition ? ((RenderPosition)o).pos.equals(this.pos) : false;
    }

    public float get() {
        return 1.0F - this.toDelta$(this.startTime);
    }

    private float toDelta$(long start) {
        return MathHelper.clamp((float)this.toDelta(start) / 75.0F, 0.0F, 1.0F);
    }

    private long toDelta(long start) {
        return System.currentTimeMillis() - start;
    }

        public void setPos(BlockPos pos) {
        this.pos = pos;
    }

        public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

        public BlockPos getPos() {
        return this.pos;
    }

        public long getStartTime() {
        return this.startTime;
    }
}
