package net.flytre.mechanix.recipe;

import net.flytre.flytre_lib.common.recipe.OutputProvider;
import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class AlloyingRecipe extends ItemProcessingRecipe<Inventory> {


    public AlloyingRecipe(Identifier id, QuantifiedIngredient[] inputs, OutputProvider[] outputs, int craftTime) {
        super(id, inputs, outputs, craftTime);
    }


    @Override
    public ItemStack getRecipeKindIcon() {
        return MachineRegistry.ALLOYER.getBlock().asItem().getDefaultStack();
    }

    @Override
    public boolean areSlotsExact() {
        return false;
    }

    @Override
    public int getSetLength() {
        return 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.ALLOYING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.ALLOYING_RECIPE;
    }
}
