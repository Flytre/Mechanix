package net.flytre.mechanix.util;

import net.flytre.mechanix.api.energy.EnergyDisplayItem;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface IconMaker<T extends Block> {

    IconMaker<Block> STANDARD = (block) -> new BlockItem(block, new Item.Settings().group(MiscRegistry.TAB));
    IconMaker<Block> ENERGY = (block) -> new EnergyDisplayItem(block, new Item.Settings().group(MiscRegistry.TAB));
    IconMaker<Block> FLUID = (block) -> new BlockItem(block, new Item.Settings().group(MiscRegistry.TAB)) {
        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            FluidInventory.toToolTip(stack, tooltip);
            super.appendTooltip(stack, world, tooltip, context);
        }
    };

    BlockItem create(T block);
}
