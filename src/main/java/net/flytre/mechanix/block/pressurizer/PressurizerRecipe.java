package net.flytre.mechanix.block.pressurizer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class PressurizerRecipe implements Recipe<PressurizerBlockEntity> {
    private final Identifier id;
    private final Ingredient input;
    private final ItemStack output;
    private final int craftTime;

    public PressurizerRecipe(Identifier id, Ingredient input, ItemStack output, int craftTime) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.craftTime = craftTime;
    }

    public int getCraftTime() {
        return craftTime;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(MachineRegistry.PRESSURIZER.getBlock());
    }

    @Override
    public boolean matches(PressurizerBlockEntity inv, World world) {
        return input.test(inv.getStack(0));
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        return DefaultedList.ofSize(1,input);
    }
    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.PRESSURIZER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.PRESSURIZER_RECIPE;
    }



    public ItemStack craft(PressurizerBlockEntity inv) {
        return this.getOutput().copy();
    }

}
