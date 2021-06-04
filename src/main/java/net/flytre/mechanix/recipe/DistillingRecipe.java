package net.flytre.mechanix.recipe;

import net.flytre.flytre_lib.common.recipe.OutputProvider;
import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DistillingRecipe extends ItemFluidProcessingRecipe<DoubleInventory> {

    public static final FluidStack WATER = new FluidStack(Fluids.WATER, FluidStack.UNITS_PER_BUCKET / 2);

    public DistillingRecipe(Identifier id, QuantifiedIngredient[] inputs, OutputProvider[] outputs, FluidStack[] fluidInputs, FluidStack[] fluidOutputs, int craftTime, QuantifiedIngredient[] upgrades) {
        super(id, inputs, outputs, fluidInputs, fluidOutputs, craftTime, upgrades);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.DISTILLING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.DISTILLING_RECIPE;
    }

    @Override
    public int fluidOutputStartSlot() {
        return 2;
    }

    @Override
    public boolean matches(DoubleInventory inv, World world) {
        return super.matches(inv, world) && WATER.test(inv.getFluidStack(1));
    }

    @Override
    public ItemStack craft(DoubleInventory inv) {
        inv.getFluidStack(1).decrement(WATER.getAmount());
        return super.craft(inv);
    }
}
