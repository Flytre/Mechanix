package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class FluidType {
    private final FlowableFluid still;
    private final FlowableFluid flowing;
    private final Item bucket;
    private Block block;
    private String id;

    public FluidType(String id, Supplier<FlowableFluid> still, Supplier<FlowableFluid> flowing) {
        this.id = id;
        this.still = Registry.register(Registry.FLUID, new Identifier("mechanix", id),still.get());
        this.flowing = Registry.register(Registry.FLUID, new Identifier("mechanix", "flowing_" + id), flowing.get());
        this.bucket = Registry.register(Registry.ITEM, new Identifier("mechanix", id + "_bucket"), new BucketItem(this.still, new Item.Settings().recipeRemainder(Items.BUCKET).group(MiscRegistry.TAB).maxCount(1)));
    }

    public void setBlock() {
        this.block = Registry.register(Registry.BLOCK, new Identifier("mechanix", id), new FluidBlock(this.still, FabricBlockSettings.copy(Blocks.LAVA)){});
    }

    public FlowableFluid getStill() {
        return still;
    }

    public FlowableFluid getFlowing() {
        return flowing;
    }

    public Item getBucket() {
        return bucket;
    }

    public Block getBlock() {
        return block;
    }
}
