package net.flytre.mechanix.recipe;

import com.google.gson.JsonObject;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.recipe.OutputProvider;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class FoundryRecipeSerializer implements RecipeSerializer<FoundryRecipe> {

    private final RecipeFactory recipeFactory;

    public FoundryRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }


    @Override
    public FoundryRecipe read(Identifier id, JsonObject json) {
        FluidStack stack = FluidStack.fromJson(json.getAsJsonObject("input"));
        OutputProvider result = OutputProvider.fromJson(json.get("result"));
        return recipeFactory.create(id,stack,result);
    }

    @Override
    public FoundryRecipe read(Identifier id, PacketByteBuf buf) {
        FluidStack fluidStack = FluidStack.fromPacket(buf);
        OutputProvider result = OutputProvider.fromPacket(buf);
        return this.recipeFactory.create(id,fluidStack,result);
    }

    @Override
    public void write(PacketByteBuf buf, FoundryRecipe recipe) {
        recipe.getInput().toPacket(buf);
        recipe.getOutputProvider().toPacket(buf);
    }

    public interface RecipeFactory {
        FoundryRecipe create(Identifier id, FluidStack input, OutputProvider output);
    }
}
