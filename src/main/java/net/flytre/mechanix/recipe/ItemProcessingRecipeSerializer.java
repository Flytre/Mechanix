package net.flytre.mechanix.recipe;

import com.google.gson.JsonObject;
import net.flytre.flytre_lib.common.recipe.OutputProvider;
import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.flytre.flytre_lib.common.recipe.RecipeUtils;
import net.flytre.flytre_lib.common.util.PacketUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.Arrays;

public class ItemProcessingRecipeSerializer<R extends Inventory, T extends ItemProcessingRecipe<R>> implements RecipeSerializer<T> {


    private final RecipeFactory<R, T> recipeFactory;

    public ItemProcessingRecipeSerializer(RecipeFactory<R, T> recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    public T read(Identifier id, JsonObject jsonObject) {
        QuantifiedIngredient[] ingredients = RecipeUtils.getQuantifiedIngredients(jsonObject, "ingredients", "ingredient");
        OutputProvider[] result = RecipeUtils.getOutputProviders(jsonObject, "results", "result");
        int craftTime = JsonHelper.hasPrimitive(jsonObject, "time") ? JsonHelper.getInt(jsonObject, "time") : 200;
        QuantifiedIngredient[] upgrades = RecipeUtils.getQuantifiedIngredients(jsonObject, "upgrades", "upgrade", true);
        return recipeFactory.create(id, ingredients, result, craftTime, upgrades);
    }

    @Override
    public T read(Identifier id, PacketByteBuf buf) {
        QuantifiedIngredient[] ingredients = PacketUtils.listFromPacket(buf, QuantifiedIngredient::fromPacket).toArray(new QuantifiedIngredient[0]);
        OutputProvider[] output = PacketUtils.listFromPacket(buf, OutputProvider::fromPacket).toArray(new OutputProvider[0]);
        int craftTime = buf.readInt();
        QuantifiedIngredient[] upgrades = PacketUtils.listFromPacket(buf, QuantifiedIngredient::fromPacket).toArray(new QuantifiedIngredient[0]);
        return recipeFactory.create(id, ingredients, output, craftTime, upgrades);
    }

    @Override
    public void write(PacketByteBuf buf, T recipe) {
        PacketUtils.toPacket(buf, Arrays.asList(recipe.getInputs()), QuantifiedIngredient::toPacket);
        PacketUtils.toPacket(buf, Arrays.asList(recipe.getOutputs()), OutputProvider::toPacket);
        buf.writeInt(recipe.getCraftTime());
        PacketUtils.toPacket(buf, Arrays.asList(recipe.getUpgrades()), QuantifiedIngredient::toPacket);
    }


    public interface RecipeFactory<R extends Inventory, T extends ItemProcessingRecipe<R>> {
        T create(Identifier id, QuantifiedIngredient[] inputs, OutputProvider[] outputs, int craftTime, QuantifiedIngredient[] upgrades);
    }
}
