package net.flytre.mechanix.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;

public interface MechanixRecipe<C extends Inventory> extends Recipe<C> {

    boolean cancelLoad();

    default int getCraftTime() {
        return 120;
    }
}
