package net.flytre.mechanix.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.api.recipe.QuantifiedIngredient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class ItemSeperationRecipeSerializer implements RecipeSerializer<ItemSeparationRecipe> {


    private final RecipeFactory recipeFactory;

    public ItemSeperationRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public ItemSeparationRecipe read(Identifier id, JsonObject json) {
        JsonElement jsonElement = JsonHelper.hasArray(json, "ingredient") ? JsonHelper.getArray(json, "ingredient") : JsonHelper.getObject(json, "ingredient");
        QuantifiedIngredient ingredient = QuantifiedIngredient.fromJson(jsonElement);
        OutputProvider[] result;
        if(JsonHelper.hasArray(json, "results")) {
            JsonArray array = JsonHelper.getArray(json, "results");
            result = new OutputProvider[array.size()];
            for(int i = 0; i < array.size(); i++)
                result[i] = OutputProvider.fromJson(array.get(i));
        } else {
            result = new OutputProvider[]{OutputProvider.fromJson(json.get("results"))};
        }
        int craftTime = 200;
        if(JsonHelper.hasPrimitive(json,"time"))
            craftTime = JsonHelper.getInt(json,"time");
        return recipeFactory.create(id,ingredient,result,craftTime);
    }

    @Override
    public ItemSeparationRecipe read(Identifier id, PacketByteBuf buf) {
        QuantifiedIngredient ingredient = QuantifiedIngredient.fromPacket(buf);
        int i = buf.readInt();
        OutputProvider[] outputs = new OutputProvider[i];
        for(int k = 0; k < i; k++) {
            outputs[k] = OutputProvider.fromPacket(buf);
        }
        int craftTime = buf.readInt();
        return recipeFactory.create(id,ingredient,outputs,craftTime);
    }

    @Override
    public void write(PacketByteBuf buf, ItemSeparationRecipe recipe) {
        recipe.getInput().toPacket(buf);
        buf.writeInt(recipe.getOutputProviders().length);
        for(OutputProvider outputProvider : recipe.getOutputProviders()) {
            outputProvider.toPacket(buf);
        }
        buf.writeInt(recipe.getCraftTime());
    }

    public interface RecipeFactory {
        ItemSeparationRecipe create(Identifier id, QuantifiedIngredient input, OutputProvider[] outputs, int craftTime);
    }
}
