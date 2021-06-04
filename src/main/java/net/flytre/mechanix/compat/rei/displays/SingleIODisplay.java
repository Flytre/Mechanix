package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.recipe.ItemProcessingRecipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SingleIODisplay extends MechanixRecipeDisplay<ItemProcessingRecipe<?>> {
    public SingleIODisplay(ItemProcessingRecipe recipe) {
        super(recipe);
    }

    @Override
    public List<List<EntryStack>> createOutputs() {
        return Collections.singletonList(Collections.singletonList(EntryStack.create(recipe.getOutput())));
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        return CollectionUtils.map(Collections.singletonList(recipe.getInputs()[0]), ing -> EntryStack.ofItemStacks(Arrays.asList(ing.getMatchingStacksClient())));
    }

    @Override
    public int createTime() {
        return recipe.getCraftTime();
    }
}
