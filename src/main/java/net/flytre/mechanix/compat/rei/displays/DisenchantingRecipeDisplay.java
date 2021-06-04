package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import net.flytre.mechanix.recipe.DisenchantingRecipe;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DisenchantingRecipeDisplay extends MechanixRecipeDisplay<DisenchantingRecipe> {

    private static final ItemStack ENCHANTED_BOOK = new ItemStack(Items.ENCHANTED_BOOK, 1);
    private static final ItemStack BOOK = new ItemStack(Items.BOOK, 1);

    static {
        ENCHANTED_BOOK.addEnchantment(Enchantments.UNBREAKING, 1);
    }

    private List<List<ItemStack>> sample;

    public DisenchantingRecipeDisplay(DisenchantingRecipe recipe) {
        super(recipe);
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        if(sample == null)
            sample = recipe.generateSample();
        List<EntryStack> list = sample.stream().map(i -> i.get(0)).map(EntryStack::create).collect(Collectors.toList());
        return Arrays.asList(list, Collections.singletonList(EntryStack.create(BOOK)));
    }

    @Override
    public List<List<EntryStack>> createOutputs() {
        if(sample == null)
            sample = recipe.generateSample();
        List<EntryStack> list1 = sample.stream().map(i -> i.get(1)).map(EntryStack::create).collect(Collectors.toList());
        List<EntryStack> list2 = sample.stream().map(i -> i.get(2)).map(EntryStack::create).collect(Collectors.toList());
        return Arrays.asList(list1, list2);
    }

    @Override
    public int createTime() {
        return recipe.getCraftTime();
    }
}
