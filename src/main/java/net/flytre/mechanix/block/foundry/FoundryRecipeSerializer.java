package net.flytre.mechanix.block.foundry;

import com.google.gson.JsonObject;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.block.alloyer.AlloyerRecipeSerializer;
import net.minecraft.item.ItemStack;
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
        ItemStack itemStack = AlloyerRecipeSerializer.getItemStack(json,"result");
        return recipeFactory.create(id,stack,itemStack);
    }

    @Override
    public FoundryRecipe read(Identifier id, PacketByteBuf buf) {
        FluidStack fluidStack = FluidStack.fromPacket(buf);
        ItemStack itemStack = buf.readItemStack();
        return this.recipeFactory.create(id,fluidStack,itemStack);
    }

    @Override
    public void write(PacketByteBuf buf, FoundryRecipe recipe) {
        recipe.getInput().toPacket(buf);
        buf.writeItemStack(recipe.getOutput());
    }

    public interface RecipeFactory {
        FoundryRecipe create(Identifier id, FluidStack input, ItemStack output);
    }
}
