package net.flytre.mechanix.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class EnchanterRecipeSerializer implements RecipeSerializer<EnchanterRecipe> {

    private final RecipeFactory recipeFactory;

    public EnchanterRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public EnchanterRecipe read(Identifier id, JsonObject jsonObject) {
        return this.recipeFactory.create(id, Items.AIR);
    }


    @Override
    public EnchanterRecipe read(Identifier id, PacketByteBuf buf) {
        Item item = buf.readItemStack().getItem();
        return this.recipeFactory.create(id, item);
    }

    @Override
    public void write(PacketByteBuf buf, EnchanterRecipe recipe) {
        buf.writeItemStack(recipe.getOutput());
    }


    public interface RecipeFactory {
        EnchanterRecipe create(Identifier id, Item input);
    }
}
