package net.flytre.mechanix.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class EnchanterRecipeSerializer<T extends MechanixRecipe<?>> implements RecipeSerializer<T> {


    private final RecipeFactory<T> recipeFactory;

    public EnchanterRecipeSerializer(RecipeFactory<T> recipeFactory) {
        this.recipeFactory = recipeFactory;
    }


    @Override
    public T read(Identifier id, JsonObject jsonObject) {
        throw new AssertionError("Enchanting Recipes cannot be created via Json");
    }


    @Override
    public T read(Identifier id, PacketByteBuf buf) {
        Item item = buf.readItemStack().getItem();
        int craftTime = buf.readInt();
        return this.recipeFactory.create(id, item, craftTime);
    }

    @Override
    public void write(PacketByteBuf buf, T recipe) {
        buf.writeItemStack(recipe.getOutput());
        buf.writeInt(recipe.getCraftTime());
    }


    public interface RecipeFactory<T extends MechanixRecipe<?>> {
        T create(Identifier id, Item input, int craftTime);
    }
}
