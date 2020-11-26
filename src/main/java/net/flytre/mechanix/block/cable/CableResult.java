package net.flytre.mechanix.block.cable;

import net.minecraft.util.math.BlockPos;

public class CableResult {

    private final BlockPos pos;
    private final double max;

    public CableResult(BlockPos position, double max) {
        this.pos = position;
        this.max = max;
    }


    public BlockPos getPos() {
        return pos;
    }

    public double getMax() {
        return max;
    }
}
