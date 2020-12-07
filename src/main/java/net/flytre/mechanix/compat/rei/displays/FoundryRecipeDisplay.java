package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.recipe.FoundryRecipe;

import java.util.Collections;
import java.util.List;

public class FoundryRecipeDisplay extends AbstractRecipeDisplay<FoundryRecipe> {
    public FoundryRecipeDisplay(FoundryRecipe recipe) {
        super(recipe);
    }

    @Override
    public List<EntryStack> createOutputs() {
        return Collections.singletonList(EntryStack.create(recipe.getOutput()));
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        return CollectionUtils.map(Collections.singletonList(recipe.getInput()), ing -> Collections.singletonList(EntryStack.create(ing.getFluid(), ing.getAmount())));
    }

    @Override
    public int createTime() {
        return 120;
    }
}
