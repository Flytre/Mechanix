package net.flytre.mechanix.block.foundry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FoundryRecipe implements Recipe<FoundryBlockEntity> {

    private final Identifier id;
    private final FluidStack input;
    private final ItemStack output;

    public FoundryRecipe(Identifier id, FluidStack input, ItemStack output) {
        this.id = id;
        this.input = input;
        this.output = output;
    }

    public FluidStack getInput() {
        return input;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(MachineRegistry.FOUNDRY.getBlock());
    }


    @Override
    public boolean matches(FoundryBlockEntity inv, World world) {
        FluidStack toTest = inv.getFluidStack(0);
        return toTest.getFluid() == input.getFluid() && toTest.getAmount() >= input.getAmount();
    }

    @Override
    public ItemStack craft(FoundryBlockEntity inv) {
        return this.getOutput();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return output.copy();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.FOUNDRY_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.FOUNDRY_RECIPE;
    }
}
