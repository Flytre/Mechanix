package net.flytre.mechanix.recipe;

import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class CrusherRecipe extends ItemProcessingRecipe {
    public CrusherRecipe(Identifier id, Ingredient input, ItemStack output, int craftTime) {
        super(id, input, output, craftTime);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CRUSHER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.CRUSHER_RECIPE;
    }
}
