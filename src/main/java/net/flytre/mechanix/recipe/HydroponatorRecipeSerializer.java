package net.flytre.mechanix.recipe;

import com.google.gson.JsonObject;
import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.api.recipe.QuantifiedIngredient;
import net.flytre.mechanix.api.recipe.RecipeUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class HydroponatorRecipeSerializer implements RecipeSerializer<HydroponatorRecipe> {

    private final RecipeFactory recipeFactory;

    public HydroponatorRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public HydroponatorRecipe read(Identifier id, JsonObject json) {
        QuantifiedIngredient ingredient = QuantifiedIngredient.fromJson(json.get("ingredient"));
        QuantifiedIngredient fertilizer = QuantifiedIngredient.fromJson(json.get("fertilizer"));
        OutputProvider[] result = RecipeUtils.getOutputProviders(json, "results");
        int craftTime = 200;
        if (JsonHelper.hasPrimitive(json, "time"))
            craftTime = JsonHelper.getInt(json, "time");
        return recipeFactory.create(id, ingredient, fertilizer, result, craftTime);
    }

    @Override
    public HydroponatorRecipe read(Identifier id, PacketByteBuf buf) {
        QuantifiedIngredient ingredient = QuantifiedIngredient.fromPacket(buf);
        QuantifiedIngredient fertilizer = QuantifiedIngredient.fromPacket(buf);
        int i = buf.readInt();
        OutputProvider[] outputs = new OutputProvider[i];
        for (int k = 0; k < i; k++) {
            outputs[k] = OutputProvider.fromPacket(buf);
        }
        int craftTime = buf.readInt();
        return recipeFactory.create(id, ingredient, fertilizer, outputs, craftTime);
    }

    @Override
    public void write(PacketByteBuf buf, HydroponatorRecipe recipe) {
        recipe.getInput().toPacket(buf);
        recipe.getFertilizer().toPacket(buf);
        buf.writeInt(recipe.getOutputProviders().length);
        for (OutputProvider outputProvider : recipe.getOutputProviders()) {
            outputProvider.toPacket(buf);
        }
        buf.writeInt(recipe.getCraftTime());
    }

    public interface RecipeFactory {
        HydroponatorRecipe create(Identifier id, QuantifiedIngredient plant, QuantifiedIngredient fertilizer, OutputProvider[] outputs, int craftTime);
    }

}
