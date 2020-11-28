package net.flytre.mechanix.util;

import net.minecraft.block.Block;

@FunctionalInterface
public interface BlockMaker<T extends Block> {

    T create();
}
