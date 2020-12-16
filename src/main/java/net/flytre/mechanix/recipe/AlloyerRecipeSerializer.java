package net.flytre.mechanix.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.api.recipe.QuantifiedIngredient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.HashSet;
import java.util.Set;

public class AlloyerRecipeSerializer implements RecipeSerializer<AlloyingRecipe> {

    private final RecipeFactory recipeFactory;

    public AlloyerRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public AlloyingRecipe read(Identifier id, JsonObject jsonObject) {
        HashSet<QuantifiedIngredient> ingredients = new HashSet<>();
        JsonArray array = JsonHelper.getArray(jsonObject,"ingredients");
        if(array.size() > 3) {
            throw new RuntimeException("Alloyer: " + id + " has too many ingredients. May only have 3.");
        }
        for(int i = 0; i < array.size(); i++) {
            ingredients.add(QuantifiedIngredient.fromJson(array.get(i)));
        }

        OutputProvider result = OutputProvider.fromJson(jsonObject.get("result"));
        return this.recipeFactory.create(id, ingredients, result);
    }

    @Override
    public AlloyingRecipe read(Identifier id, PacketByteBuf buf) {
        HashSet<QuantifiedIngredient> ingredients = new HashSet<>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++) {
            QuantifiedIngredient ingredient = QuantifiedIngredient.fromPacket(buf);
            if(!ingredient.isEmpty())
                ingredients.add(ingredient);
        }
        OutputProvider outputProvider = OutputProvider.fromPacket(buf);
        return this.recipeFactory.create(id, ingredients, outputProvider);
    }

    @Override
    public void write(PacketByteBuf buf, AlloyingRecipe recipe) {
        buf.writeInt(recipe.getInputs().size());
        for(QuantifiedIngredient ingredient : recipe.getInputs()) {
            ingredient.toPacket(buf);
        }
        recipe.getOutputProvider().toPacket(buf);
    }

    public interface RecipeFactory {
        AlloyingRecipe create(Identifier id, Set<QuantifiedIngredient> ingredients, OutputProvider output);
    }
}
