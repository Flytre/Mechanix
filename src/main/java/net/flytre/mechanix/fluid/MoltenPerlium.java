package net.flytre.mechanix.fluid;

import net.flytre.mechanix.util.FluidRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class MoltenPerlium extends AbstractMetallicFluid {


    @Override
    public Fluid getStill()
    {
        return FluidRegistry.STILL_MOLTEN_PERLIUM;
    }

    @Override
    public Fluid getFlowing()
    {
        return FluidRegistry.FLOWING_MOLTEN_PERLIUM;
    }

    @Override
    public Item getBucketItem()
    {
        return FluidRegistry.MOLTEN_PERLIUM_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState)
    {
        return FluidRegistry.MOLTEN_PERLIUM_BLOCK.getDefaultState().with(Properties.LEVEL_15, method_15741(fluidState));
    }

    public static class Flowing extends MoltenPerlium
    {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder)
        {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState)
        {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState)
        {
            return false;
        }
    }

    public static class Still extends MoltenPerlium
    {
        @Override
        public int getLevel(FluidState fluidState)
        {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState)
        {
            return true;
        }
    }
}
