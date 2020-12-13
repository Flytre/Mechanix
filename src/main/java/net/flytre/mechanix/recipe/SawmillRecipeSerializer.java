package net.flytre.mechanix.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
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
        Ingredient ingredient = Ingredient.fromJson(jsonElement);
        ItemStack result = AlloyerRecipeSerializer.getItemStack(json,"result");
        ItemStack secondary = AlloyerRecipeSerializer.getItemStack(json,"secondary");
        double chance = JsonHelper.hasPrimitive(json,"secondary_chance") ? JsonHelper.getFloat(json,"secondary_chance") : 0;
        return recipeFactory.create(id,ingredient,result,secondary,chance);
    }

    @Override
    public SawmillRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient ingredient = Ingredient.fromPacket(buf);
        ItemStack result = buf.readItemStack();
        ItemStack secondary = buf.readItemStack();
        double chance = buf.readDouble();
        return recipeFactory.create(id,ingredient,result,secondary,chance);
    }

    @Override
    public void write(PacketByteBuf buf, SawmillRecipe recipe) {
        recipe.getInput().write(buf);
        buf.writeItemStack(recipe.getOutput());
        buf.writeItemStack(recipe.getSecondary());
        buf.writeDouble(recipe.getSecondaryChance());
    }

    public interface RecipeFactory {
        SawmillRecipe create(Identifier id, Ingredient input, ItemStack output, ItemStack secondary, double secondaryChance);
    }
}
