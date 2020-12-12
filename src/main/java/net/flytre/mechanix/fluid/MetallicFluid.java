package net.flytre.mechanix.fluid;

import net.flytre.mechanix.util.FluidType;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

import java.util.function.Supplier;

public abstract class MetallicFluid extends AbstractMetallicFluid {

    private final Supplier<FluidType> fluidType;

    public MetallicFluid(Supplier<FluidType> fluidType) {
        this.fluidType = fluidType;
    }

    @Override
    public Fluid getStill()
    {
        return fluidType.get().getStill();
    }

    @Override
    public Fluid getFlowing()
    {
        return fluidType.get().getFlowing();
    }

    @Override
    public Item getBucketItem()
    {
        return fluidType.get().getBucket();
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState)
    {
        return fluidType.get().getBlock().getDefaultState().with(Properties.LEVEL_15, method_15741(fluidState));
    }

    public static class Flowing extends MetallicFluid
    {
        public Flowing(Supplier<FluidType> test) {
            super(test);
        }

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

    public static class Still extends MetallicFluid
    {
        public Still(Supplier<FluidType> test) {
            super(test);
        }

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
