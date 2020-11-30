package net.flytre.mechanix.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class AbstractMetallicFluid extends FlowableFluid {


    @Override
    public boolean matchesType(Fluid fluid)
    {
        return fluid == getStill() || fluid == getFlowing();
    }


    @Override
    protected boolean isInfinite()
    {
        return false;
    }


    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state)
    {
        this.playExtinguishEvent(world, pos);

    }

    private void playExtinguishEvent(WorldAccess world, BlockPos pos) {
        world.syncWorldEvent(1501, pos, 0);
    }


    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction)
    {
        return false;
    }


    @Override
    protected int getFlowSpeed(WorldView worldView)
    {
        return 2;
    }


    @Override
    protected int getLevelDecreasePerBlock(WorldView worldView)
    {
        return 1;
    }

    @Override
    public int getTickRate(WorldView worldView)
    {
        return 5;
    }

    @Override
    protected float getBlastResistance()
    {
        return 100.0F;
    }


}
