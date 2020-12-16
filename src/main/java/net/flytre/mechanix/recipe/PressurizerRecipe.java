package net.flytre.mechanix.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.api.recipe.QuantifiedIngredient;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class PressurizerRecipe extends ItemProcessingRecipe {
    public PressurizerRecipe(Identifier id, QuantifiedIngredient input, OutputProvider output, int craftTime) {
        super(id, input, output, craftTime);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(MachineRegistry.PRESSURIZER.getBlock());
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
