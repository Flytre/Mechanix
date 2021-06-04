package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.recipe.ItemProcessingRecipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemRecipeDisplay<T extends ItemProcessingRecipe<?>> extends MechanixRecipeDisplay<T> {
    public ItemRecipeDisplay(T recipe) {
        super(recipe);
    }

    @Override
    public int createTime() {
        return recipe.getCraftTime();
    }

    @Override
    public List<List<EntryStack>> createOutputs() {
        return CollectionUtils.map(recipe.getOutputs(), ing -> Collections.singletonList(EntryStack.create(ing.getStack())));
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        return CollectionUtils.map(recipe.getInputs(), ing -> EntryStack.ofItemStacks(Arrays.asList(ing.getMatchingStacksClient())));
    }
}
