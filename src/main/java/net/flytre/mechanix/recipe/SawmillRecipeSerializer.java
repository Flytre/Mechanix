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

public class SawmillRecipeSerializer implements RecipeSerializer<SawmillRecipe> {


    private final RecipeFactory recipeFactory;

    public SawmillRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public SawmillRecipe read(Identifier id, JsonObject json) {
        JsonElement jsonElement = JsonHelper.hasArray(json, "ingredient") ? JsonHelper.getArray(json, "ingredient") : JsonHelper.getObject(json, "ingredient");
        Ingredient ingredient = RecipeUtils.fromJson(jsonElement);
        OutputProvider result = OutputProvider.fromJson(json.get("result"));
        OutputProvider secondary = OutputProvider.fromJson(json.get("secondary"));
        double chance = JsonHelper.hasPrimitive(json,"secondary_chance") ? JsonHelper.getFloat(json,"secondary_chance") : 0;
        return recipeFactory.create(id,ingredient,result,secondary,chance);
    }

    @Override
    public SawmillRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient ingredient = Ingredient.fromPacket(buf);
        OutputProvider result = OutputProvider.fromPacket(buf);
        OutputProvider secondary = OutputProvider.fromPacket(buf);
        double chance = buf.readDouble();
        return recipeFactory.create(id,ingredient,result,secondary,chance);
    }

    @Override
    public void write(PacketByteBuf buf, SawmillRecipe recipe) {
        recipe.getInput().write(buf);
        for(OutputProvider outputProvider : recipe.getOutputs()) {
            outputProvider.toPacket(buf);
        }
        buf.writeDouble(recipe.getSecondaryChance());
    }

    public interface RecipeFactory {
        SawmillRecipe create(Identifier id, Ingredient input, OutputProvider output, OutputProvider secondary, double secondaryChance);
    }
}
