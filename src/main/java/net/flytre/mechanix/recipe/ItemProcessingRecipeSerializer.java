package net.flytre.mechanix.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.api.recipe.RecipeUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class ItemProcessingRecipeSerializer implements RecipeSerializer<ItemProcessingRecipe> {

    private final RecipeFactory recipeFactory;

    public ItemProcessingRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public ItemProcessingRecipe read(Identifier id, JsonObject jsonObject) {
        JsonElement jsonElement = JsonHelper.hasArray(jsonObject, "ingredient") ? JsonHelper.getArray(jsonObject, "ingredient") : JsonHelper.getObject(jsonObject, "ingredient");
        Ingredient ingredient = RecipeUtils.fromJson(jsonElement);
        int pressurizeTime = 200;
        if(JsonHelper.hasPrimitive(jsonObject,"time"))
            pressurizeTime = JsonHelper.getInt(jsonObject,"time");
       OutputProvider outputProvider = OutputProvider.fromJson(jsonObject.get("result"));
        return this.recipeFactory.create(id, ingredient, outputProvider,pressurizeTime);
    }


    @Override
    public ItemProcessingRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient ingredient = Ingredient.fromPacket(buf);
        OutputProvider outputProvider = OutputProvider.fromPacket(buf);
        int pressurizeTime = buf.readInt();
        return this.recipeFactory.create(id, ingredient, outputProvider,pressurizeTime);
    }

    @Override
    public void write(PacketByteBuf buf, ItemProcessingRecipe recipe) {
        recipe.getInput().write(buf);
        recipe.getOutputProvider().toPacket(buf);
        buf.writeInt(recipe.getCraftTime());
    }


    public interface RecipeFactory {
        ItemProcessingRecipe create(Identifier id, Ingredient input, OutputProvider output, int craftTime);
    }
}
