package net.flytre.mechanix.block.fluid_pipe;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public class FluidPipeResult {
    private final BlockPos pos;
    private final double max;
    private final Direction direction;
    private final ArrayList<BlockPos> path;


    public FluidPipeResult(BlockPos pos, double max, Direction direction,ArrayList<BlockPos> path) {
        this.pos = pos;
        this.max = max;
        this.direction = direction;
        this.path = path;
    }

    public BlockPos getDestination() {
        return pos;
    }

    public double getAmount() {
        return max;
    }

    public Direction getDirection() {
        return direction;
    }

    public ArrayList<BlockPos> getPath() {
        return path;
    }

}
