package net.flytre.mechanix.recipe;

import net.flytre.flytre_lib.common.recipe.OutputProvider;
import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.flytre.flytre_lib.common.recipe.RecipeUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.*;

public abstract class ItemProcessingRecipe<T extends Inventory> implements MechanixRecipe<T> {

    private final Identifier id;
    private final QuantifiedIngredient[] inputs;
    private final OutputProvider[] outputs;
    private final int craftTime;

    public ItemProcessingRecipe(Identifier id, QuantifiedIngredient[] inputs, OutputProvider[] outputs, int craftTime) {
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
        this.craftTime = craftTime;
    }

    /**
     * Leave as-is if you want exact slot matching (i.e. Hydroponator)
     * Override if input slots are flexible (i.e. Alloyer)
     *
     * @return
     */
    public boolean areSlotsExact() {
        return true;
    }


    public int getCraftTime() {
        return craftTime;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public QuantifiedIngredient[] getInputs() {
        return inputs;
    }


    @Override
    public ItemStack getOutput() {
        if (outputs.length > 1)
            throw new AssertionError("Cannot call getOutput() on a multi-output recipe");
        return outputs.length == 0 ? ItemStack.EMPTY : outputs[0].getStack();
    }


    @Override
    public boolean matches(T inv, World world) {
        return !cancelLoad() && areSlotsExact() ? exactMatch(inv, world) : setMatch(inv, world);
    }

    private boolean exactMatch(T inv, World world) {

        for (int i = 0; i < inputs.length; i++)
            if (!inputs[i].test(inv.getStack(i)))
                return false;

        return true;
    }

    /**
     * Override to set the number of slots to check in a recipe. For example, in an Alloyer this would be set to 3
     * to check the 1st 3 slots for matches even in a recipe with just 2 inputs, i.e. for bronze
     *
     * @return the number of slots to check
     */
    public int getSetLength() {
        return inputs.length;
    }

    private boolean setMatch(T inv, World world) {
        Set<Integer> used = new HashSet<>();
        for (QuantifiedIngredient ingredient : inputs) {
            boolean matched = false;
            for (int i = 0; i < getSetLength(); i++) {
                if (!used.contains(i) && ingredient.test(inv.getStack(i))) {
                    matched = true;
                    used.add(i);
                    break;
                }
            }
            if (!matched)
                return false;
        }
        for (int i = 0; i < getSetLength(); i++) {
            if (!used.contains(i) && !inv.getStack(i).isEmpty())
                return false;
        }

        return true;
    }


    /**
     * For inexact slot matching, which slots are actually used
     */
    private Map<Integer, Integer> getUsedStacks(T inv) {
        Map<Integer, Integer> checked = new HashMap<>();
        for (QuantifiedIngredient ingredient : inputs) {
            for (int i = 0; i < getSetLength(); i++)
                if (!checked.containsKey(i) && ingredient.test(inv.getStack(i)))
                    checked.put(i, ingredient.getQuantity());
        }
        return checked;
    }

    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> list = DefaultedList.ofSize(areSlotsExact() ? getSetLength() : inputs.length, Ingredient.EMPTY);
        int index = 0;
        for (QuantifiedIngredient i : inputs) {
            list.set(index++, i.getIngredient());
        }
        return list;
    }

    @Override
    public ItemStack craft(T inv) {
        RecipeUtils.craftOutput(inv, outputSlotStart(), outputSlotStart() + outputs.length, outputs);
        if (areSlotsExact())
            exactInputDec(inv);
        else
            setInputDec(inv);
        return ItemStack.EMPTY;
    }


    @Override
    public boolean canAcceptRecipeOutput(T inv) {
        return RecipeUtils.matches(inv, outputSlotStart(), outputSlotStart() + outputs.length, outputs);
    }

    /**
     * The index of the first output slot
     */
    public int outputSlotStart() {
        return !areSlotsExact() ? getSetLength() : inputs.length;
    }

    private void exactInputDec(T inv) {
        for (int i = 0; i < inputs.length; i++) {
            inv.getStack(i).decrement(inputs[i].getQuantity());
        }
    }

    private void setInputDec(T inv) {
        Map<Integer, Integer> used = getUsedStacks(inv);
        for (int i : used.keySet()) {
            inv.getStack(i).decrement(used.get(i));
        }
    }


    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    public OutputProvider[] getOutputs() {
        return outputs;
    }

    @Override
    public boolean cancelLoad() {
        return Arrays.stream(outputs).map(OutputProvider::getStack).anyMatch(ItemStack::isEmpty) || Arrays.stream(inputs).anyMatch(QuantifiedIngredient::isEmpty);
    }
}
