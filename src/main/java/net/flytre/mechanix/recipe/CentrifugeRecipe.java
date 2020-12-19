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

public class CentrifugeRecipe extends ItemSeparationRecipe<Inventory> {

    public CentrifugeRecipe(Identifier id, QuantifiedIngredient input, OutputProvider[] outputs, int craftTime) {
        super(id, input, outputs, craftTime);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CENTRIFUGE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.CENTRIFUGE_RECIPE;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(MachineRegistry.CENTRIFUGE.getBlock());
    }
}
