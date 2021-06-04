package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.flytre_lib.compat.rei.AbstractRecipeDisplay;
import net.flytre.mechanix.recipe.MechanixRecipe;
import net.minecraft.recipe.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MechanixRecipeDisplay<T extends Recipe<?>> extends AbstractRecipeDisplay<T> {
    protected final int time;
    protected final List<List<EntryStack>> upgrades;


    public MechanixRecipeDisplay(T recipe) {
        super(recipe);
        this.time = createTime();
        this.upgrades = createUpgrades();
    }

    public int getTime() {
        return time;
    }


    public List<List<EntryStack>> getUpgrades() {
        return upgrades;
    }

    public abstract int createTime();

    public List<List<EntryStack>> createUpgrades() {
        if (recipe instanceof MechanixRecipe<?>)
            return CollectionUtils.map(((MechanixRecipe<?>) recipe).getUpgrades(), ing -> EntryStack.ofItemStacks(Arrays.asList(ing.getMatchingStacksClient())));
        return new ArrayList<>();
    }
}
