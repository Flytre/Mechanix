package net.flytre.mechanix.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.block.distiller.DistillerEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DistillerRecipe implements MechanixRecipe<DistillerEntity> {

    private final Identifier id;
    private final FluidStack input;
    private final FluidStack output;

    public DistillerRecipe(Identifier id, FluidStack input, FluidStack output) {
        this.id = id;
        this.input = input;
        this.output = output;
    }

    public FluidStack getInput() {
        return input;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(MachineRegistry.DISTILLER.getBlock());
    }

    @Override
    public boolean matches(DistillerEntity inv, World world) {

        if(cancelLoad())
            return false;

        FluidStack toTest = inv.getFluidStack(0);
        FluidStack water = inv.getFluidStack(1);
        boolean bl = toTest.getFluid() == input.getFluid() && toTest.getAmount() >= input.getAmount();
        bl &= water.getFluid() == Fluids.WATER && water.getAmount() >= 500;
        return bl;
    }

    @Override
    public ItemStack craft(DistillerEntity inv) {
        return this.getOutput().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(output.getFluid().getBucketItem());
    }

    public FluidStack fluidOutput() {
        return output.copy();
    }


    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.DISTILLER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.DISTILLER_RECIPE;
    }

    @Override
    public boolean cancelLoad() {
        return false;
    }
}
