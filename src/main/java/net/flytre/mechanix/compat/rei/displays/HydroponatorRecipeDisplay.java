package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.recipe.HydroponatorRecipe;
import net.flytre.mechanix.recipe.ItemFluidProcessingRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HydroponatorRecipeDisplay extends MechanixRecipeDisplay<ItemFluidProcessingRecipe<DoubleInventory>> {
    public HydroponatorRecipeDisplay(ItemFluidProcessingRecipe<DoubleInventory> recipe) {
        super(recipe);
    }


    @Override
    public List<List<EntryStack>> createOutputs() {
        return CollectionUtils.map(recipe.getOutputs(), i -> Collections.singletonList(EntryStack.create(i.getStack())));
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        List<List<EntryStack>> list = new ArrayList<>();
        list.add(Collections.singletonList(EntryStack.create(HydroponatorRecipe.WATER.getFluid(), HydroponatorRecipe.WATER.getBuckets())));
        list.addAll(CollectionUtils.map(recipe.getInputs(), ing -> EntryStack.ofItemStacks(Arrays.asList(ing.getMatchingStacksClient()))));
        return list;
    }

    @Override
    public int createTime() {
        return 40;
    }
}
