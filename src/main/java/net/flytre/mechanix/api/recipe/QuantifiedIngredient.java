package net.flytre.mechanix.api.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;

public class QuantifiedIngredient {

    private final Ingredient ingredient;
    private final int quantity;

    public QuantifiedIngredient(Ingredient ingredient, int quantity) {
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getQuantity() {
        return quantity;
    }

    public static QuantifiedIngredient fromJson(JsonElement json) {
        Ingredient ingredient = RecipeUtils.fromJson(json);
        int quantity = 1;
        if(json.isJsonObject() && JsonHelper.hasPrimitive((JsonObject)json,"count")) {
            quantity = JsonHelper.getInt((JsonObject)json,"count");
        }
        return new QuantifiedIngredient(ingredient,quantity);
    }
    public static QuantifiedIngredient fromPacket(PacketByteBuf buf) {
        Ingredient ingredient = Ingredient.fromPacket(buf);
        int quantity = buf.readInt();
        return new QuantifiedIngredient(ingredient,quantity);
    }

    public void toPacket(PacketByteBuf packet) {
        ingredient.write(packet);
        packet.writeInt(quantity);
    }

    public boolean isEmpty() {
        return ingredient.isEmpty() || quantity == 0;
    }

    public boolean test(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() >= quantity;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack[] getMatchingStacksClient() {
        ItemStack[] result = ingredient.getMatchingStacksClient();
        for(ItemStack stack : result) {
            stack.setCount(quantity);
        }
        return result;
    }
}
