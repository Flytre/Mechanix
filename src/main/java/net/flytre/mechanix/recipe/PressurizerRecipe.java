package net.flytre.mechanix.recipe;

import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class PressurizerRecipe extends ItemProcessingRecipe {
    public PressurizerRecipe(Identifier id, Ingredient input, OutputProvider output, int craftTime) {
        super(id, input, output, craftTime);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.PRESSURIZER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.PRESSURIZER_RECIPE;
    }
}
