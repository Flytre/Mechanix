package net.flytre.mechanix.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.block.foundry.FoundryBlockEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FoundryRecipe implements MechanixRecipe<FoundryBlockEntity> {

    private final Identifier id;
    private final FluidStack input;
    private final OutputProvider output;

    public FoundryRecipe(Identifier id, FluidStack input, OutputProvider output) {
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

        if(cancelLoad())
            return false;

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


    public OutputProvider getOutputProvider() {
        return output;
    }

    @Override
    public ItemStack getOutput() {
        return output.getStack().copy();
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

    @Override
    public boolean cancelLoad() {
        return getOutput().isEmpty();
    }
}
