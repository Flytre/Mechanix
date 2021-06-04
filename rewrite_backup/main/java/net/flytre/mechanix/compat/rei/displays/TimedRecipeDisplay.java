package net.flytre.mechanix.compat.rei.displays;

import net.flytre.flytre_lib.compat.rei.AbstractRecipeDisplay;
import net.minecraft.recipe.Recipe;

public abstract class TimedRecipeDisplay<T extends Recipe<?>> extends AbstractRecipeDisplay<T> {
    protected final int time;


    public TimedRecipeDisplay(T recipe) {
        super(recipe);
        this.time = createTime();
    }

    public int getTime() {
        return time;
    }


    public abstract int createTime();
}
