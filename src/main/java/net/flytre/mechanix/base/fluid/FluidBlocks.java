package net.flytre.mechanix.base.fluid;

import net.minecraft.block.FluidBlock;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public class FluidBlocks {
    public static final ArrayList<FluidBlock> fluidBlocks = new ArrayList<>();

    static {
        Registry.BLOCK.forEach(i -> {
            if(i instanceof FluidBlock)
                fluidBlocks.add((FluidBlock) i);
        });
    }
}
