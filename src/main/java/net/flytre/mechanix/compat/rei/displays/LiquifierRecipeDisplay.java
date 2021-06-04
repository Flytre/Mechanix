package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.recipe.ItemFluidProcessingRecipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LiquifierRecipeDisplay extends MechanixRecipeDisplay<ItemFluidProcessingRecipe<DoubleInventory>> {
    public LiquifierRecipeDisplay(ItemFluidProcessingRecipe<DoubleInventory> recipe) {
        super(recipe);
    }

    @Override
    public List<List<EntryStack>> createOutputs() {
        return Collections.singletonList(Collections.singletonList(EntryStack.create(recipe.getFluidOutputs()[0].getFluid(), recipe.getFluidOutputs()[0].getBuckets())));
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        return CollectionUtils.map(Collections.singletonList(recipe.getInputs()[0]), ing -> EntryStack.ofItemStacks(Arrays.asList(ing.getMatchingStacksClient())));
    }

    @Override
    public int createTime() {
        return 120;
    }
}
