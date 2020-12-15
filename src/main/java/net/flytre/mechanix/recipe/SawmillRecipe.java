package net.flytre.mechanix.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.block.sawmill.SawmillEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class SawmillRecipe implements MechanixRecipe<SawmillEntity> {

    private final Identifier id;
    private final Ingredient input;
    private final OutputProvider output;
    private final OutputProvider secondary;
    private final double secondaryChance;

    public SawmillRecipe(Identifier id, Ingredient input, OutputProvider output, OutputProvider secondary, double secondaryChance) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.secondary = secondary;
        this.secondaryChance = secondaryChance;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.SAWMILL_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.SAWMILL_RECIPE;
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public ItemStack getOutput() {
        return output.getStack();
    }

    public OutputProvider[] getOutputs() {
        return new OutputProvider[]{output,secondary};
    }

    public ItemStack getSecondary() {
        return secondary.getStack();
    }

    public double getSecondaryChance() {
        return secondaryChance;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(MachineRegistry.SAWMILL.getBlock());
    }

    @Override
    public boolean matches(SawmillEntity inv, World world) {

        if(cancelLoad())
            return false;

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

    public ItemStack craft(SawmillEntity inv) {
        return this.getOutput().copy();
    }

    @Override
    public boolean cancelLoad() {
        return getOutput().isEmpty() || getInput().isEmpty();
    }
}
