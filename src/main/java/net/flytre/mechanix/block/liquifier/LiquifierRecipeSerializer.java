package net.flytre.mechanix.block.liquifier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LiquifierRecipeSerializer implements RecipeSerializer<LiquifierRecipe> {


    private final RecipeFactory recipeFactory;

    public LiquifierRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public LiquifierRecipe read(Identifier id, JsonObject jsonObject) {
        JsonElement jsonElement = JsonHelper.hasArray(jsonObject, "ingredient") ? JsonHelper.getArray(jsonObject, "ingredient") : JsonHelper.getObject(jsonObject, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(jsonElement);
        FluidStack stack = FluidStack.fromJson(jsonObject.getAsJsonObject("result"));
        return this.recipeFactory.create(id,ingredient,stack);
    }

    @Override
    public LiquifierRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient ingredient = Ingredient.fromPacket(buf);
        FluidStack stack = FluidStack.fromPacket(buf);
        return this.recipeFactory.create(id,ingredient,stack);
    }

    @Override
    public void write(PacketByteBuf buf, LiquifierRecipe recipe) {
        recipe.getInput().write(buf);
        recipe.fluidOutput().toPacket(buf);
    }

    public interface RecipeFactory {
        LiquifierRecipe create(Identifier id, Ingredient input, FluidStack output);
    }
}
