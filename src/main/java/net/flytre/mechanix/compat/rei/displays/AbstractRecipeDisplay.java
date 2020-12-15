package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.flytre.mechanix.compat.rei.ReiUtils;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public abstract class AbstractRecipeDisplay<R extends Recipe<?>> implements RecipeDisplay {

    protected final R recipe;
    protected final List<List<EntryStack>> inputs;
    protected final List<EntryStack> outputs;
    protected final int time;

    public AbstractRecipeDisplay(R recipe) {
        this.recipe = recipe;
        inputs = createInputs();
        outputs = createOutputs();
        time = createTime();
    }

    public int getTime() {
        return time;
    }


    @Override
    public Optional<Identifier> getRecipeLocation() {
        return Optional.ofNullable(recipe).map(Recipe::getId);
    }

    @Override
    public List<List<EntryStack>> getInputEntries() {
        return inputs;
    }

    @Override
    public List<List<EntryStack>> getRequiredEntries() {
        return inputs;
    }

    @Override
    public List<EntryStack> getOutputEntries() {
        return outputs;
    }

    @Override
    public Identifier getRecipeCategory() {
        return ReiUtils.getId(recipe.getType());
    }

    public R getRecipe() {
        return recipe;
    }

    public abstract List<EntryStack> createOutputs();
    public abstract List<List<EntryStack>> createInputs();
    public abstract int createTime();

}
