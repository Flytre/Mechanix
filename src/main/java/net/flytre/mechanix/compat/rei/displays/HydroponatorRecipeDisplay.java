package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.recipe.HydroponatorRecipe;
import net.minecraft.fluid.Fluids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HydroponatorRecipeDisplay extends AbstractRecipeDisplay<HydroponatorRecipe> {

    public HydroponatorRecipeDisplay(HydroponatorRecipe recipe) {
        super(recipe);
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        List<List<EntryStack>> list = new ArrayList<>();
        list.addAll(CollectionUtils.map(Collections.singletonList(new FluidStack(Fluids.WATER, 500)), ing -> Collections.singletonList(EntryStack.create(ing.getFluid(), ing.getAmount()))));
        list.addAll(CollectionUtils.map(Arrays.asList(recipe.getInput(),recipe.getFertilizer()), ing -> EntryStack.ofItemStacks(Arrays.asList(ing.getMatchingStacksClient()))));
        return list;
    }

    @Override
    public List<EntryStack> createOutputs() {
        return Arrays.stream(recipe.getOutputProviders()).map(i -> EntryStack.create(i.getStack())).collect(Collectors.toList());
    }

    public double getChance() {
        return recipe.getOutputProvider(1).getChance();
    }

    @Override
    public int createTime() {
        return recipe.getCraftTime();
    }
}
