package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.recipe.EnchanterRecipe;
import net.flytre.mechanix.util.FluidRegistry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnchanterRecipeDisplay extends AbstractRecipeDisplay<EnchanterRecipe> {

    public EnchanterRecipeDisplay(EnchanterRecipe recipe) {
        super(recipe);
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        List<List<EntryStack>> list = new ArrayList<>();
        list.addAll(CollectionUtils.map(Collections.singletonList(new FluidStack(FluidRegistry.LIQUID_XP.getStill(), 600)), ing -> Collections.singletonList(EntryStack.create(ing.getFluid(), ing.getAmount()))));
        list.addAll(CollectionUtils.map(Collections.singletonList(new ItemStack(recipe.getInput(),1)), ing -> EntryStack.ofItemStacks(Collections.singletonList(ing))));
        return list;
    }

    @Override
    public List<EntryStack> createOutputs() {

        return Collections.singletonList(EntryStack.create(recipe.getOutput()));
    }

    @Override
    public int createTime() {
        return recipe.getCraftTime();
    }
}
