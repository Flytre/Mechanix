package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.flytre_lib.compat.rei.AbstractRecipeDisplay;
import net.flytre.mechanix.recipe.ItemProcessingRecipe;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SingleIODisplay extends TimedRecipeDisplay<ItemProcessingRecipe<?>> {
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
