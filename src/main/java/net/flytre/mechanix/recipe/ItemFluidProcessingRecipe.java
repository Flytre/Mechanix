package net.flytre.mechanix.recipe;

import net.flytre.flytre_lib.common.recipe.OutputProvider;
import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.flytre.mechanix.api.fluid.FluidRecipeUtils;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class ItemFluidProcessingRecipe<T extends DoubleInventory> extends ItemProcessingRecipe<T> {

    private final FluidStack[] fluidInputs;
    private final FluidStack[] fluidOutputs;

    public ItemFluidProcessingRecipe(Identifier id, QuantifiedIngredient[] inputs, OutputProvider[] outputs, FluidStack[] fluidInputs, FluidStack[] fluidOutputs, int craftTime, QuantifiedIngredient[] upgrades) {
        super(id, inputs, outputs, craftTime, upgrades);
        this.fluidInputs = fluidInputs;
        this.fluidOutputs = fluidOutputs;
    }

    public FluidStack[] getFluidInputs() {
        return fluidInputs;
    }

    public FluidStack[] getFluidOutputs() {
        return fluidOutputs;
    }

    @Override
    public boolean matches(T inv, World world) {
        if (!super.matches(inv, world))
            return false;

        for (int i = 0; i < fluidInputs.length; i++)
            if (!fluidInputs[i].test(inv.getFluidStack(i)))
                return false;

        return true;

    }

    @Override
    public ItemStack craft(T inv) {
        FluidRecipeUtils.craftOutput(inv, fluidOutputStartSlot(), fluidOutputStartSlot() + fluidOutputs.length, fluidOutputs);
        fluidDec(inv);
        return super.craft(inv);
    }


    private void fluidDec(T inv) {
        for (int i = 0; i < fluidInputs.length; i++) {
            inv.getFluidStack(i).decrement(fluidInputs[i].getAmount());
        }
    }


    @Override
    public boolean canAcceptRecipeOutput(T inv) {
        return super.canAcceptRecipeOutput(inv) && FluidRecipeUtils.matches(inv, fluidOutputStartSlot(), fluidOutputStartSlot() + fluidOutputs.length, fluidOutputs);
    }

    /**
     * The index of the first fluid output slot
     */
    public int fluidOutputStartSlot() {
        return fluidInputs.length;
    }

}
