package net.flytre.mechanix.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.api.recipe.QuantifiedIngredient;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public abstract class ItemProcessingRecipe implements MechanixRecipe<Inventory> {
    private final Identifier id;
    private final QuantifiedIngredient input;
    private final OutputProvider output;
    private final int craftTime;

    public ItemProcessingRecipe(Identifier id, QuantifiedIngredient input, OutputProvider output, int craftTime) {
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

    public QuantifiedIngredient getInput() {
        return input;
    }

    @Override
    public ItemStack getOutput() {
        return output.getStack();
    }

    public OutputProvider getOutputProvider() {
        return output;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(MachineRegistry.PRESSURIZER.getBlock());
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
        return getOutput().isEmpty() || getInput().isEmpty();
    }
}
