package net.flytre.mechanix.recipe;

import net.flytre.flytre_lib.common.recipe.OutputProvider;
import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FakeTransmutingRecipe extends ItemProcessingRecipe<Inventory> {

    public FakeTransmutingRecipe(Identifier id, OutputProvider[] items, int craftTime, QuantifiedIngredient[] upgrades) {
        super(id, new QuantifiedIngredient[0], items, craftTime, upgrades);
    }

    public List<ItemProcessingRecipe<Inventory>> generateRecipes() {
        List<ItemProcessingRecipe<Inventory>> recipes = new ArrayList<>();

        if (getOutputs().length < 2)
            return recipes;

        for (int i = 0; i < getOutputs().length - 1; i++) {
            recipes.add(genRecipe(getOutputs()[i], getOutputs()[i + 1]));
        }
        recipes.add(genRecipe(getOutputs()[getOutputs().length - 1], getOutputs()[0]));
        return recipes;
    }

    private ItemProcessingRecipe<Inventory> genRecipe(OutputProvider a, OutputProvider b) {
        Identifier itemId = Registry.ITEM.getId(a.getStack().getItem());
        Ingredient ingredient = Ingredient.ofStacks(Stream.of(a.getStack()));
        Identifier id = new Identifier("mechanix", "transmuting/" + itemId.getNamespace() + "/" + itemId.getPath());
        return new ItemProcessingRecipe<Inventory>(id, new QuantifiedIngredient[]{new QuantifiedIngredient(ingredient, a.getStack().getCount())}, new OutputProvider[]{b}, getCraftTime(), getUpgrades()) {

            @Override
            public RecipeSerializer<?> getSerializer() {
                return RecipeRegistry.TRANSMUTING_SERIALIZER;
            }

            @Override
            public RecipeType<?> getType() {
                return RecipeRegistry.TRANSMUTING_RECIPE;
            }
        };
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.TRANSMUTING_SERIALIZER_FAKE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.TRANSMUTING_RECIPE_FAKE;
    }
}
