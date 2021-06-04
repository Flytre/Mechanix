package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.flytre_lib.common.recipe.OutputProvider;
import net.flytre.flytre_lib.compat.rei.AbstractRecipeDisplay;
import net.flytre.mechanix.recipe.AlloyingRecipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AlloyerRecipeDisplay extends TimedRecipeDisplay<AlloyingRecipe> {

    public AlloyerRecipeDisplay(AlloyingRecipe recipe) {
        super(recipe);
    }

    @Override
    public List<List<EntryStack>> createOutputs() {
        return Collections.singletonList(Collections.singletonList(EntryStack.create(recipe.getOutput())));
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
