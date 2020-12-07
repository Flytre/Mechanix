package net.flytre.mechanix.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.block.liquifier.LiquifierBlockEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class LiquifierRecipe implements Recipe<LiquifierBlockEntity> {

    private final Ingredient input;
    private final FluidStack output;
    private final Identifier id;


    public LiquifierRecipe(Identifier id, Ingredient input, FluidStack output) {
        this.input = input;
        this.output = output;
        this.id = id;
    }

    public Ingredient getInput() {
        return input;
    }


    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(MachineRegistry.LIQUIFIER.getBlock());
    }


    @Override
    public boolean matches(LiquifierBlockEntity inv, World world) {
        return input.test(inv.getStack(0));
    }

    @Override
    public ItemStack craft(LiquifierBlockEntity inv) {
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
        return RecipeRegistry.LIQUIFIER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.LIQUIFIER_RECIPE;
    }
}
