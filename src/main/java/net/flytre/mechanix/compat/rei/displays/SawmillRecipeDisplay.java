package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.recipe.SawmillRecipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SawmillRecipeDisplay extends AbstractRecipeDisplay<SawmillRecipe> {

    public SawmillRecipeDisplay(SawmillRecipe recipe) {
        super(recipe);
    }

    @Override
    public List<EntryStack> createOutputs() {
        return Arrays.asList(EntryStack.create(recipe.getOutput()), EntryStack.create(recipe.getSecondary()));
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        return CollectionUtils.map(Collections.singletonList(recipe.getInput()),ing -> EntryStack.ofItemStacks(Arrays.asList(ing.getMatchingStacksClient())));
    }

    public double getChance() {
        return recipe.getSecondaryChance();
    }

    @Override
    public int createTime() {
        return 120;
    }
}
