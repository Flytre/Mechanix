package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.recipe.EnchantingRecipe;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnchanterRecipeDisplay extends MechanixRecipeDisplay<EnchantingRecipe> {

    public EnchanterRecipeDisplay(EnchantingRecipe recipe) {
        super(recipe);
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        List<List<EntryStack>> list = new ArrayList<>();
        list.addAll(CollectionUtils.map(Collections.singletonList(EnchantingRecipe.EXP), ing -> Collections.singletonList(EntryStack.create(ing.getFluid(), ing.getBuckets()))));
        list.addAll(CollectionUtils.map(Collections.singletonList(new ItemStack(recipe.getInput(), 1)), ing -> EntryStack.ofItemStacks(Collections.singletonList(ing))));
        return list;
    }

    @Override
    public List<List<EntryStack>> createOutputs() {

        List<EntryStack> attempts = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            attempts.add(EntryStack.create(recipe.getOutput()));
        return Collections.singletonList(attempts);
    }

    @Override
    public int createTime() {
        return recipe.getCraftTime();
    }
}
