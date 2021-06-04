package net.flytre.mechanix.api.fluid;

import net.minecraft.block.FluidBlock;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

/**
 * Basically used to cache all blocks that have fluids (water , lava, molten perlium, etc)
 */
public class FluidBlocks {
    public static final List<FluidBlock> fluidBlocks = new ArrayList<>();

    static {
        Registry.BLOCK.forEach(i -> {
            if(i instanceof FluidBlock)
                fluidBlocks.add((FluidBlock) i);
        });
    }
}
