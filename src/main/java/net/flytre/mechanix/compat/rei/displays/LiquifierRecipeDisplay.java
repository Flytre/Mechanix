package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.block.liquifier.LiquifierRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LiquifierRecipeDisplay extends AbstractRecipeDisplay<LiquifierRecipe> {
    public LiquifierRecipeDisplay(LiquifierRecipe recipe) {
        super(recipe);
    }

    @Override
    public List<EntryStack> createOutputs() {
        List<EntryStack> result = new ArrayList<>();
        result.add(EntryStack.create(recipe.fluidOutput().getFluid(),recipe.fluidOutput().getAmount()));
        return result;
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        return CollectionUtils.map(Collections.singletonList(recipe.getInput()),ing -> EntryStack.ofItemStacks(Arrays.asList(ing.getMatchingStacksClient())));
    }

    @Override
    public int createTime() {
        return 120;
    }
}
