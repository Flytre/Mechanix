package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.recipe.DistillingRecipe;
import net.flytre.mechanix.recipe.ItemFluidProcessingRecipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DistillerRecipeDisplay extends MechanixRecipeDisplay<ItemFluidProcessingRecipe<DoubleInventory>> {
    public DistillerRecipeDisplay(ItemFluidProcessingRecipe<DoubleInventory> recipe) {
        super(recipe);
    }


    @Override
    public List<List<EntryStack>> createOutputs() {
        return Collections.singletonList(Collections.singletonList(EntryStack.create(recipe.getFluidOutputs()[0].getFluid(), recipe.getFluidOutputs()[0].getBuckets())));
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        return CollectionUtils.map(Arrays.asList(recipe.getFluidInputs()[0], DistillingRecipe.WATER), ing -> Collections.singletonList(EntryStack.create(ing.getFluid(), ing.getBuckets())));

    }

    @Override
    public int createTime() {
        return 40;
    }
}
