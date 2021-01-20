package net.flytre.mechanix.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class DisenchanterRecipeSerializer implements RecipeSerializer<DisenchanterRecipe> {

    private final RecipeFactory recipeFactory;

    public DisenchanterRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public DisenchanterRecipe read(Identifier id, JsonObject jsonObject) {
        return this.recipeFactory.create(id, Items.AIR);
    }


    @Override
    public DisenchanterRecipe read(Identifier id, PacketByteBuf buf) {
        Item item = buf.readItemStack().getItem();
        return this.recipeFactory.create(id, item);
    }

    @Override
    public void write(PacketByteBuf buf, DisenchanterRecipe recipe) {
        buf.writeItemStack(recipe.getOutput());
    }


    public interface RecipeFactory {
        DisenchanterRecipe create(Identifier id, Item input);
    }
}
