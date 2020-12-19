package net.flytre.mechanix.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.api.recipe.QuantifiedIngredient;
import net.flytre.mechanix.api.recipe.RecipeUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class DefaultItemSeperationRecipeSerializer implements RecipeSerializer<ItemSeparationRecipe<Inventory>> {


    private final RecipeFactory recipeFactory;

    public DefaultItemSeperationRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public ItemSeparationRecipe<Inventory> read(Identifier id, JsonObject json) {
        JsonElement jsonElement = JsonHelper.hasArray(json, "ingredient") ? JsonHelper.getArray(json, "ingredient") : JsonHelper.getObject(json, "ingredient");
        QuantifiedIngredient ingredient = QuantifiedIngredient.fromJson(jsonElement);
        OutputProvider[] result = RecipeUtils.getOutputProviders(json,"results");
        int craftTime = 200;
        if(JsonHelper.hasPrimitive(json,"time"))
            craftTime = JsonHelper.getInt(json,"time");
        return recipeFactory.create(id,ingredient,result,craftTime);
    }

    @Override
    public ItemSeparationRecipe<Inventory> read(Identifier id, PacketByteBuf buf) {
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
        ItemSeparationRecipe<Inventory> create(Identifier id, QuantifiedIngredient input, OutputProvider[] outputs, int craftTime);
    }
}
