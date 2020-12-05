package net.flytre.mechanix.block.alloyer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Set;

public class AlloyerRecipeSerializer implements RecipeSerializer<AlloyingRecipe> {

    private final RecipeFactory recipeFactory;

    public AlloyerRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public AlloyingRecipe read(Identifier id, JsonObject jsonObject) {
        HashSet<Ingredient> ingredients = new HashSet<>();
        JsonArray array = JsonHelper.getArray(jsonObject,"ingredients");
        if(array.size() > 3) {
            System.out.println("Alloyer: " + id + " has too many ingredients. May only have 3.");
        }
        for(int i = 0; i < array.size(); i++) {
            ingredients.add(Ingredient.fromJson(array.get(i)));
        }

        ItemStack itemStack = getItemStack(jsonObject,"result");
        return this.recipeFactory.create(id, ingredients, itemStack);
    }


    public static ItemStack getItemStack(JsonObject object, String key) {
        ItemStack itemStack;
        if(JsonHelper.hasString(object,key)) {
            String string2 = JsonHelper.getString(object, key);
            Identifier identifier2 = new Identifier(string2);
            itemStack = new ItemStack(Registry.ITEM.getOrEmpty(identifier2).orElseThrow(() -> new IllegalStateException("Item: " + string2 + " does not exist")));

        } else {
            itemStack = getItemStack(object.getAsJsonObject(key));
        }
        return itemStack;
    }

    @Override
    public AlloyingRecipe read(Identifier id, PacketByteBuf buf) {
        HashSet<Ingredient> ingredients = new HashSet<>();
        for(int i = 0; i < 3; i++) {
            Ingredient ingredient = Ingredient.fromPacket(buf);
            if(ingredient != Ingredient.EMPTY)
                ingredients.add(ingredient);
        }
        ItemStack itemStack = buf.readItemStack();
        return this.recipeFactory.create(id, ingredients, itemStack);
    }

    @Override
    public void write(PacketByteBuf buf, AlloyingRecipe recipe) {
        for(Ingredient ingredient : recipe.getInputs()) {
            ingredient.write(buf);
        }
        buf.writeItemStack(recipe.getOutput().copy());
    }

    public interface RecipeFactory {
        AlloyingRecipe create(Identifier id, Set<Ingredient> ingredients, ItemStack output);
    }


    public static ItemStack getItemStack(JsonObject json) {
        String string = JsonHelper.getString(json, "item");
        Item item = Registry.ITEM.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + string + "'"));
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = JsonHelper.getInt(json, "count", 1);
            return new ItemStack(item, i);
        }
    }
}
