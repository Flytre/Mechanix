package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.recipe.ItemSeparationRecipe;
import net.minecraft.inventory.Inventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleOutputDisplay extends AbstractRecipeDisplay<ItemSeparationRecipe<Inventory>> {

    public MultipleOutputDisplay(ItemSeparationRecipe<Inventory> recipe) {
        super(recipe);
    }

    @Override
    public List<EntryStack> createOutputs() {
        return Arrays.stream(recipe.getOutputProviders()).map(i -> EntryStack.create(i.getStack())).collect(Collectors.toList());
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        return CollectionUtils.map(Collections.singletonList(recipe.getInput()),ing -> EntryStack.ofItemStacks(Arrays.asList(ing.getMatchingStacksClient())));
    }

    public double getChance() {
        return recipe.getOutputProvider(1).getChance();
    }

    @Override
    public int createTime() {
        return recipe.getCraftTime();
    }
}
