package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.recipe.ItemFluidProcessingRecipe;

import java.util.Collections;
import java.util.List;

public class FoundryRecipeDisplay extends MechanixRecipeDisplay<ItemFluidProcessingRecipe<DoubleInventory>> {
    public FoundryRecipeDisplay(ItemFluidProcessingRecipe<DoubleInventory> recipe) {
        super(recipe);
    }

    @Override
    public List<List<EntryStack>> createOutputs() {
        return Collections.singletonList(Collections.singletonList(EntryStack.create(recipe.getOutputs()[0].getStack())));
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        return Collections.singletonList(Collections.singletonList(EntryStack.create(recipe.getFluidInputs()[0].getFluid(), recipe.getFluidInputs()[0].getBuckets())));
    }

    @Override
    public int createTime() {
        return 120;
    }
}
