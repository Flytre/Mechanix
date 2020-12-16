package net.flytre.mechanix.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.api.recipe.QuantifiedIngredient;
import net.flytre.mechanix.block.alloyer.AlloyerBlockEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AlloyingRecipe implements MechanixRecipe<AlloyerBlockEntity> {

    private final Set<QuantifiedIngredient> inputs;
    private final OutputProvider output;
    private final Identifier id;



    public AlloyingRecipe(Identifier id, Set<QuantifiedIngredient> ingredients, OutputProvider output) {
        this.id = id;
        this.inputs = ingredients;
        this.output = output;
    }

    public Set<QuantifiedIngredient> getInputs() {
        return inputs;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(MachineRegistry.ALLOYER.getBlock());
    }



    @Override
    public boolean matches(AlloyerBlockEntity inv, World world) {

        if(cancelLoad())
            return false;

        HashSet<Integer> checked = new HashSet<>();
        for(QuantifiedIngredient ingredient : inputs) {
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

    public HashMap<Integer, Integer> getUsedStacks(AlloyerBlockEntity inv) {
        HashMap<Integer, Integer> checked = new HashMap<>();
        for(QuantifiedIngredient ingredient : inputs) {
            for(int i = 0; i < 3; i++)
                if(!checked.containsKey(i) && ingredient.test(inv.getStack(i)))
                    checked.put(i,ingredient.getQuantity());
        }
        return checked;
    }

    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> list = DefaultedList.ofSize(3,Ingredient.EMPTY);
        int index = 0;
        for(QuantifiedIngredient i : inputs) {
            list.set(index++,i.getIngredient());
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

    public OutputProvider getOutputProvider() {
        return output;
    }

    @Override
    public ItemStack getOutput() {
        return output.getStack();
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

    @Override
    public boolean cancelLoad() {
        return getOutput().isEmpty() || inputs.stream().anyMatch(QuantifiedIngredient::isEmpty);
    }
}
