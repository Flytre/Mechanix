package net.flytre.mechanix.recipe;

import com.google.gson.JsonObject;
import net.flytre.flytre_lib.common.recipe.OutputProvider;
import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.flytre.flytre_lib.common.recipe.RecipeUtils;
import net.flytre.flytre_lib.common.util.PacketUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.Arrays;

public class FakeTransmutingSerializer implements RecipeSerializer<FakeTransmutingRecipe> {

    private final RecipeFactory recipeFactory;

    public FakeTransmutingSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public FakeTransmutingRecipe read(Identifier id, JsonObject jsonObject) {
        OutputProvider[] result = RecipeUtils.getOutputProviders(jsonObject, "items", "item");
        int craftTime = JsonHelper.hasPrimitive(jsonObject, "time") ? JsonHelper.getInt(jsonObject, "time") : 200;
        QuantifiedIngredient[] upgrades = RecipeUtils.getQuantifiedIngredients(jsonObject, "upgrades", "upgrade", true);
        return recipeFactory.create(id, result, craftTime, upgrades);
    }

    @Override
    public FakeTransmutingRecipe read(Identifier id, PacketByteBuf buf) {
        OutputProvider[] output = PacketUtils.listFromPacket(buf, OutputProvider::fromPacket).toArray(new OutputProvider[0]);
        int craftTime = buf.readInt();
        QuantifiedIngredient[] upgrades = PacketUtils.listFromPacket(buf, QuantifiedIngredient::fromPacket).toArray(new QuantifiedIngredient[0]);
        return recipeFactory.create(id, output, craftTime, upgrades);
    }

    @Override
    public void write(PacketByteBuf buf, FakeTransmutingRecipe recipe) {
        PacketUtils.toPacket(buf, Arrays.asList(recipe.getOutputs()), OutputProvider::toPacket);
        buf.writeInt(recipe.getCraftTime());
        PacketUtils.toPacket(buf, Arrays.asList(recipe.getUpgrades()), QuantifiedIngredient::toPacket);
    }

    public interface RecipeFactory {
        FakeTransmutingRecipe create(Identifier id, OutputProvider[] outputs, int craftTime, QuantifiedIngredient[] upgrades);
    }

}
