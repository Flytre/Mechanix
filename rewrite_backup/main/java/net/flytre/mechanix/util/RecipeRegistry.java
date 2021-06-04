package net.flytre.mechanix.util;

import net.flytre.flytre_lib.common.recipe.OutputProvider;
import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.flytre.mechanix.recipe.AlloyingRecipe;
import net.flytre.mechanix.recipe.ItemProcessingRecipe;
import net.flytre.mechanix.recipe.ItemProcessingRecipeSerializer;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RecipeRegistry {


    public static ItemProcessingRecipeSerializer<Inventory, AlloyingRecipe> ALLOYING_SERIALIZER;
    public static RecipeType<AlloyingRecipe> ALLOYING_RECIPE;
    public static ItemProcessingRecipeSerializer<Inventory, ItemProcessingRecipe<Inventory>> CRUSHER_SERIALIZER;
    public static RecipeType<ItemProcessingRecipe<Inventory>> CRUSHER_RECIPE;

    public static void init() {
        ALLOYING_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:alloying"), new RecipeType<AlloyingRecipe>() {
            public String toString() {
                return "mechanix:alloying";
            }
        });
        ALLOYING_SERIALIZER = RecipeSerializer.register("mechanix:alloying", new ItemProcessingRecipeSerializer<>(AlloyingRecipe::new));

        CRUSHER_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:crushing"), new RecipeType<ItemProcessingRecipe<Inventory>>() {
            public String toString() {
                return "mechanix:crushing";
            }
        });
        CRUSHER_SERIALIZER = RecipeSerializer.register("mechanix:crushing", new ItemProcessingRecipeSerializer<>((Identifier id, QuantifiedIngredient[] inputs, OutputProvider[] outputs, int craftTime) -> new ItemProcessingRecipe<Inventory>(id, inputs, outputs, craftTime) {

            @Override
            public RecipeSerializer<?> getSerializer() {
                return RecipeRegistry.CRUSHER_SERIALIZER;
            }

            @Override
            public RecipeType<?> getType() {
                return RecipeRegistry.CRUSHER_RECIPE;
            }
        }));

    }
}
