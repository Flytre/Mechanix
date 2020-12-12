package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.recipe.DistillerRecipe;
import net.minecraft.fluid.Fluids;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DistillerRecipeDisplay extends AbstractRecipeDisplay<DistillerRecipe> {
    public DistillerRecipeDisplay(DistillerRecipe recipe) {
        super(recipe);
    }

    @Override
    public List<EntryStack> createOutputs() {
        return Collections.singletonList(EntryStack.create(recipe.fluidOutput().getFluid(),recipe.fluidOutput().getAmount()));
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        return CollectionUtils.map(Arrays.asList(recipe.getInput(), new FluidStack(Fluids.WATER,500)),ing -> Collections.singletonList(EntryStack.create(ing.getFluid(), ing.getAmount())));

    }

    @Override
    public int createTime() {
        return 40;
    }
}
