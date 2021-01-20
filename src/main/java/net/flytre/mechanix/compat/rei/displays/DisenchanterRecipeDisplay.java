package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.recipe.DisenchanterRecipe;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisenchanterRecipeDisplay extends AbstractRecipeDisplay<DisenchanterRecipe> {

    private static ItemStack ENCHANTED_BOOK = new ItemStack(Items.ENCHANTED_BOOK, 1);

    static {
        ENCHANTED_BOOK.addEnchantment(Enchantments.UNBREAKING, 1);
    }

    public DisenchanterRecipeDisplay(DisenchanterRecipe recipe) {
        super(recipe);
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        List<List<EntryStack>> list = new ArrayList<>();
        list.addAll(CollectionUtils.map(Arrays.asList(recipe.getREIInput(), new ItemStack(Items.BOOK)), ing -> EntryStack.ofItemStacks(Collections.singletonList(ing))));
        return list;
    }

    @Override
    public List<EntryStack> createOutputs() {

        return Arrays.asList(EntryStack.create(recipe.getOutput()), EntryStack.create(ENCHANTED_BOOK));
    }

    @Override
    public int createTime() {
        return recipe.getCraftTime();
    }
}
