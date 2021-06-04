package net.flytre.mechanix.recipe;

import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

import java.util.*;
import java.util.function.Predicate;

public abstract class MechanixRecipeType<T extends Recipe<?>> implements RecipeType<T> {

    private final List<Predicate<ItemStack>> upgradeConditions;

    public MechanixRecipeType() {
        upgradeConditions = new ArrayList<>();
    }

    public boolean test(ItemStack stack) {
        return upgradeConditions.stream().anyMatch(i -> i.test(stack));
    }


    public void reset() {
        upgradeConditions.clear();
    }

    public void addValidCondition(QuantifiedIngredient ingredient) {
        this.upgradeConditions.add(ingredient);
    }

    public abstract String toString();
}
