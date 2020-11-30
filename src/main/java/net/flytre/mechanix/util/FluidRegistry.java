package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.flytre.mechanix.fluid.MoltenPerlium;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluidRegistry {

    public static FlowableFluid STILL_PERLIUM;
    public static FlowableFluid FLOWING_PERLIUM;
    public static Item PERLIUM_BUCKET;
    public static Block PERLIUM_BLOCK;


    public static void init() {
        STILL_PERLIUM = Registry.register(Registry.FLUID, new Identifier("mechanix", "molten_perlium_still"), new MoltenPerlium.Still());
        FLOWING_PERLIUM = Registry.register(Registry.FLUID, new Identifier("mechanix", "molten_perlium_flow"), new MoltenPerlium.Flowing());
        PERLIUM_BUCKET = Registry.register(Registry.ITEM, new Identifier("mechanix", "molten_perlium_bucket"), new BucketItem(STILL_PERLIUM, new Item.Settings().recipeRemainder(Items.BUCKET).group(MiscRegistry.TAB).maxCount(1)));
        PERLIUM_BLOCK = Registry.register(Registry.BLOCK, new Identifier("mechanix", "molten_perlium"), new FluidBlock(STILL_PERLIUM, FabricBlockSettings.copy(Blocks.LAVA)){});


    }
}
