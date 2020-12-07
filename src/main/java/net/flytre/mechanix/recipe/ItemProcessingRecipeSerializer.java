package net.flytre.mechanix.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
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
        Ingredient ingredient = Ingredient.fromJson(jsonElement);
        int pressurizeTime = 200;
        if(JsonHelper.hasPrimitive(jsonObject,"time"))
            pressurizeTime = JsonHelper.getInt(jsonObject,"time");
        ItemStack itemStack = AlloyerRecipeSerializer.getItemStack(jsonObject,"result");
        return this.recipeFactory.create(id, ingredient, itemStack,pressurizeTime);
    }


    @Override
    public ItemProcessingRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient ingredient = Ingredient.fromPacket(buf);
        ItemStack itemStack = buf.readItemStack();
        int pressurizeTime = buf.readInt();
        return this.recipeFactory.create(id, ingredient, itemStack,pressurizeTime);
    }

    @Override
    public void write(PacketByteBuf buf, ItemProcessingRecipe recipe) {
        recipe.getInput().write(buf);
        buf.writeItemStack(recipe.getOutput());
        buf.writeInt(recipe.getCraftTime());
    }


    public interface RecipeFactory {
        ItemProcessingRecipe create(Identifier id, Ingredient input, ItemStack output, int craftTime);
    }
}
