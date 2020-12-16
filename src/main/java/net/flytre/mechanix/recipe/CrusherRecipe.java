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

public class CrusherRecipe extends ItemProcessingRecipe {
    public CrusherRecipe(Identifier id, QuantifiedIngredient input, OutputProvider output, int craftTime) {
        super(id, input, output, craftTime);
    }


    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(MachineRegistry.CRUSHER.getBlock());
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
