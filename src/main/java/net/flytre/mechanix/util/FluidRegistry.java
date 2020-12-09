package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.flytre.mechanix.fluid.MoltenGold;
import net.flytre.mechanix.fluid.MoltenIron;
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

    public static FlowableFluid STILL_MOLTEN_PERLIUM;
    public static FlowableFluid FLOWING_MOLTEN_PERLIUM;
    public static Item MOLTEN_PERLIUM_BUCKET;
    public static Block MOLTEN_PERLIUM_BLOCK;

    public static FlowableFluid STILL_MOLTEN_GOLD;
    public static FlowableFluid FLOWING_MOLTEN_GOLD;
    public static Item MOLTEN_GOLD_BUCKET;
    public static Block MOLTEN_GOLD_BLOCK;

    public static FlowableFluid STILL_MOLTEN_IRON;
    public static FlowableFluid FLOWING_MOLTEN_IRON;
    public static Item MOLTEN_IRON_BUCKET;
    public static Block MOLTEN_IRON_BLOCK;


    public static void init() {
        STILL_MOLTEN_PERLIUM = Registry.register(Registry.FLUID, new Identifier("mechanix", "molten_perlium"), new MoltenPerlium.Still());
        FLOWING_MOLTEN_PERLIUM = Registry.register(Registry.FLUID, new Identifier("mechanix", "flowing_molten_perlium"), new MoltenPerlium.Flowing());
        MOLTEN_PERLIUM_BUCKET = Registry.register(Registry.ITEM, new Identifier("mechanix", "molten_perlium_bucket"), new BucketItem(STILL_MOLTEN_PERLIUM, new Item.Settings().recipeRemainder(Items.BUCKET).group(MiscRegistry.TAB).maxCount(1)));
        MOLTEN_PERLIUM_BLOCK = Registry.register(Registry.BLOCK, new Identifier("mechanix", "molten_perlium"), new FluidBlock(STILL_MOLTEN_PERLIUM, FabricBlockSettings.copy(Blocks.LAVA)){});

        STILL_MOLTEN_GOLD = Registry.register(Registry.FLUID, new Identifier("mechanix", "molten_gold"), new MoltenGold.Still());
        FLOWING_MOLTEN_GOLD = Registry.register(Registry.FLUID, new Identifier("mechanix", "flowing_molten_gold"), new MoltenGold.Flowing());
        MOLTEN_GOLD_BUCKET = Registry.register(Registry.ITEM, new Identifier("mechanix", "molten_gold_bucket"), new BucketItem(STILL_MOLTEN_GOLD, new Item.Settings().recipeRemainder(Items.BUCKET).group(MiscRegistry.TAB).maxCount(1)));
        MOLTEN_GOLD_BLOCK = Registry.register(Registry.BLOCK, new Identifier("mechanix", "molten_gold"), new FluidBlock(STILL_MOLTEN_GOLD, FabricBlockSettings.copy(Blocks.LAVA)){});

        STILL_MOLTEN_IRON = Registry.register(Registry.FLUID, new Identifier("mechanix", "molten_iron"), new MoltenIron.Still());
        FLOWING_MOLTEN_IRON = Registry.register(Registry.FLUID, new Identifier("mechanix", "flowing_molten_iron"), new MoltenIron.Flowing());
        MOLTEN_IRON_BUCKET = Registry.register(Registry.ITEM, new Identifier("mechanix", "molten_iron_bucket"), new BucketItem(STILL_MOLTEN_IRON, new Item.Settings().recipeRemainder(Items.BUCKET).group(MiscRegistry.TAB).maxCount(1)));
        MOLTEN_IRON_BLOCK = Registry.register(Registry.BLOCK, new Identifier("mechanix", "molten_iron"), new FluidBlock(STILL_MOLTEN_IRON, FabricBlockSettings.copy(Blocks.LAVA)){});
    }
    
    public static void registerFluid() {
        
    }
}
