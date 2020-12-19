package net.flytre.mechanix.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.api.recipe.QuantifiedIngredient;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class SawmillRecipe extends ItemSeparationRecipe<Inventory> {

    public SawmillRecipe(Identifier id, QuantifiedIngredient input, OutputProvider[] outputs, int craftTime) {
        super(id, input, outputs, craftTime);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.SAWMILL_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.SAWMILL_RECIPE;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(MachineRegistry.SAWMILL.getBlock());
    }
}
