package net.flytre.mechanix.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.block.alloyer.AlloyerBlockEntity;
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

import java.util.HashSet;
import java.util.Set;

public class AlloyingRecipe implements Recipe<AlloyerBlockEntity> {

    private final Set<Ingredient> inputs;
    private final ItemStack output;
    private final Identifier id;


    public AlloyingRecipe(Identifier id, Set<Ingredient> ingredients, ItemStack output) {
        this.id = id;
        this.inputs = ingredients;
        this.output = output;
    }

    public Set<Ingredient> getInputs() {
        return inputs;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(MachineRegistry.ALLOYER.getBlock());
    }



    @Override
    public boolean matches(AlloyerBlockEntity inv, World world) {
        HashSet<Integer> checked = new HashSet<>();
        for(Ingredient ingredient : inputs) {
            boolean matched = false;
            for(int i = 0; i < 3; i++)
                if(!checked.contains(i) && ingredient.test(inv.getStack(i))) {
                    matched = true;
                    checked.add(i);
                    break;
                }
            if(!matched)
                return false;
        }
        return true;
    }

    public HashSet<Integer> getUsedStacks(AlloyerBlockEntity inv) {
        HashSet<Integer> checked = new HashSet<>();
        for(Ingredient ingredient : inputs) {
            for(int i = 0; i < 3; i++)
                if(!checked.contains(i) && ingredient.test(inv.getStack(i)))
                    checked.add(i);
        }
        return checked;
    }

    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> list = DefaultedList.ofSize(3,Ingredient.EMPTY);
        int index = 0;
        for(Ingredient i : inputs) {
            list.set(index++,i);
        }
        return list;
    }


    @Override
    public ItemStack craft(AlloyerBlockEntity inv) {
        return this.getOutput().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.ALLOYING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.ALLOYING_RECIPE;
    }
}
