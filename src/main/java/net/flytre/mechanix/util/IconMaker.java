package net.flytre.mechanix.util;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

@FunctionalInterface
public interface IconMaker<T extends Block> {

    public BlockItem create(T block);
}
