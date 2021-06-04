package net.flytre.mechanix.recipe;

import com.google.gson.JsonObject;
import net.flytre.flytre_lib.common.recipe.OutputProvider;
import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.flytre.flytre_lib.common.recipe.RecipeUtils;
import net.flytre.flytre_lib.common.util.PacketUtils;
import net.flytre.mechanix.api.fluid.FluidRecipeUtils;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.Arrays;

public class ItemFluidProcessingRecipeSerializer<R extends DoubleInventory, T extends ItemFluidProcessingRecipe<R>> implements RecipeSerializer<T> {

    private final RecipeFactory<R, T> recipeFactory;

    public ItemFluidProcessingRecipeSerializer(RecipeFactory<R, T> recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    public static <K> K[] nullableGet(JsonGrabber<K> grabber, JsonObject object, String pl, String sg, K[] defaultVal) {
        if (!object.has(pl) && !object.has(sg))
            return defaultVal;
        return grabber.get(object, pl, sg);
    }

    public T read(Identifier id, JsonObject jsonObject) {
        QuantifiedIngredient[] ingredients = nullableGet(RecipeUtils::getQuantifiedIngredients, jsonObject, "ingredients", "ingredient", new QuantifiedIngredient[0]);
        OutputProvider[] result = nullableGet(RecipeUtils::getOutputProviders, jsonObject, "results", "result", new OutputProvider[0]);
        FluidStack[] fluidInputs = nullableGet(FluidRecipeUtils::getFluidStacks, jsonObject, "fluid_ingredients", "fluid_ingredient", new FluidStack[0]);
        FluidStack[] fluidOutputs = nullableGet(FluidRecipeUtils::getFluidStacks, jsonObject, "fluid_results", "fluid_result", new FluidStack[0]);
        int craftTime = JsonHelper.hasPrimitive(jsonObject, "time") ? JsonHelper.getInt(jsonObject, "time") : 200;
        QuantifiedIngredient[] upgrades = RecipeUtils.getQuantifiedIngredients(jsonObject, "upgrades", "upgrade", true);
        return recipeFactory.create(id, ingredients, result, fluidInputs, fluidOutputs, craftTime, upgrades);
    }

    @Override
    public T read(Identifier id, PacketByteBuf buf) {
        QuantifiedIngredient[] ingredients = PacketUtils.listFromPacket(buf, QuantifiedIngredient::fromPacket).toArray(new QuantifiedIngredient[0]);
        OutputProvider[] output = PacketUtils.listFromPacket(buf, OutputProvider::fromPacket).toArray(new OutputProvider[0]);
        int craftTime = buf.readInt();
        FluidStack[] fluidInputs = PacketUtils.listFromPacket(buf, FluidStack::fromPacket).toArray(new FluidStack[0]);
        FluidStack[] fluidOutputs = PacketUtils.listFromPacket(buf, FluidStack::fromPacket).toArray(new FluidStack[0]);
        QuantifiedIngredient[] upgrades = PacketUtils.listFromPacket(buf, QuantifiedIngredient::fromPacket).toArray(new QuantifiedIngredient[0]);
        return recipeFactory.create(id, ingredients, output, fluidInputs, fluidOutputs, craftTime, upgrades);
    }

    @Override
    public void write(PacketByteBuf buf, T recipe) {
        PacketUtils.toPacket(buf, Arrays.asList(recipe.getInputs()), QuantifiedIngredient::toPacket);
        PacketUtils.toPacket(buf, Arrays.asList(recipe.getOutputs()), OutputProvider::toPacket);
        buf.writeInt(recipe.getCraftTime());
        PacketUtils.toPacket(buf, Arrays.asList(recipe.getFluidInputs()), FluidStack::toPacket);
        PacketUtils.toPacket(buf, Arrays.asList(recipe.getFluidOutputs()), FluidStack::toPacket);
        PacketUtils.toPacket(buf, Arrays.asList(recipe.getUpgrades()), QuantifiedIngredient::toPacket);
    }


    public interface RecipeFactory<R extends DoubleInventory, T extends ItemFluidProcessingRecipe<R>> {
        T create(Identifier id, QuantifiedIngredient[] inputs, OutputProvider[] outputs, FluidStack[] fluidInputs, FluidStack[] fluidOutputs, int craftTime, QuantifiedIngredient[] upgrades);
    }


    @FunctionalInterface
    private interface JsonGrabber<T> {
        T[] get(JsonObject obj, String pl, String sg);
    }
}
