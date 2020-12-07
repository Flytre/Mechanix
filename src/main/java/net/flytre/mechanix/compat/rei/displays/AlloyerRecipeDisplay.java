package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.block.alloyer.AlloyingRecipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AlloyerRecipeDisplay extends AbstractRecipeDisplay<AlloyingRecipe> {

    public AlloyerRecipeDisplay(AlloyingRecipe recipe) {
        super(recipe);
    }

    @Override
    public List<EntryStack> createOutputs() {
        return Collections.singletonList(EntryStack.create(recipe.getOutput()));
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        return CollectionUtils.map(recipe.getInputs(), ing -> EntryStack.ofItemStacks(Arrays.asList(ing.getMatchingStacksClient())));
    }

    @Override
    public int createTime() {
        return 120;
    }
}
