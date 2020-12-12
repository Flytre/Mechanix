package net.flytre.mechanix.recipe;

import com.google.gson.JsonObject;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class DistillerRecipeSerializer implements RecipeSerializer<DistillerRecipe> {

    private final RecipeFactory recipeFactory;

    public DistillerRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public DistillerRecipe read(Identifier id, JsonObject json) {
        FluidStack stack = FluidStack.fromJson(json.getAsJsonObject("input"));
        FluidStack result = FluidStack.fromJson(json.getAsJsonObject("result"));
        return recipeFactory.create(id,stack,result);
    }

    @Override
    public DistillerRecipe read(Identifier id, PacketByteBuf buf) {
        FluidStack input = FluidStack.fromPacket(buf);
        FluidStack output = FluidStack.fromPacket(buf);
        return recipeFactory.create(id,input,output);
    }

    @Override
    public void write(PacketByteBuf buf, DistillerRecipe recipe) {
        recipe.getInput().toPacket(buf);
        recipe.fluidOutput().toPacket(buf);
    }

    public interface RecipeFactory {
        DistillerRecipe create(Identifier id, FluidStack input, FluidStack output);
    }
}
