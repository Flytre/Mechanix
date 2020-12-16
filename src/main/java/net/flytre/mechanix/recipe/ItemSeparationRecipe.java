package net.flytre.mechanix.recipe;

import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.api.recipe.QuantifiedIngredient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Arrays;

public abstract class ItemSeparationRecipe implements MechanixRecipe<Inventory> {

    private final Identifier id;
    private final QuantifiedIngredient input;
    private final OutputProvider[] outputs;
    private final int craftTime;

    public ItemSeparationRecipe(Identifier id, QuantifiedIngredient input, OutputProvider[] outputs, int craftTime) {
        this.id = id;
        this.input = input;
        this.outputs = outputs;
        this.craftTime = craftTime;
    }

    public int getCraftTime() {
        return craftTime;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public QuantifiedIngredient getInput() {
        return input;
    }


    @Override
    public ItemStack getOutput() {
        return outputs[0].getStack();
    }

    public OutputProvider[] getOutputProviders() {
        return outputs;
    }



    @Override
    public boolean matches(Inventory inv, World world) {

        if(cancelLoad())
            return false;

        return input.test(inv.getStack(0));
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        return DefaultedList.ofSize(1,input.getIngredient());
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }


    public ItemStack craft(Inventory inv) {
        return this.getOutput().copy();
    }


    @Override
    public boolean cancelLoad() {
        return Arrays.stream(getOutputProviders()).anyMatch(i -> i.getStack().isEmpty()) || getInput().isEmpty();
    }


}
