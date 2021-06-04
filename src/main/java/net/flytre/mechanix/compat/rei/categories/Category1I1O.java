package net.flytre.mechanix.compat.rei.categories;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.flytre.flytre_lib.compat.rei.AbstractCustomCategory;
import net.flytre.flytre_lib.compat.rei.AbstractRecipeDisplay;
import net.flytre.flytre_lib.compat.rei.ArrowWidget;
import net.flytre.mechanix.compat.rei.displays.MechanixRecipeDisplay;
import net.flytre.mechanix.recipe.MechanixRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class Category1I1O<R extends MechanixRecipe<?>> extends MechanixCustomCategory<R> {

    public Category1I1O(RecipeType<R> recipeType) {
        super(recipeType);
    }

    @Override
    public @NotNull List<Widget> setupDisplay(MechanixRecipeDisplay<R> recipeDisplay, Rectangle bounds) {
        List<Widget> widgets = super.setupDisplay(recipeDisplay, bounds);
        int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height - 20;
        widgets.add(Widgets.createSlot(new Point(x + w / 4 - 9, y + h / 2 - 9)).entries(getInput(recipeDisplay, 0)).markInput());
        widgets.add(Widgets.createSlot(new Point(x + 3 * w / 4 - 9, y + h / 2 - 9)).entries(getOutput(recipeDisplay, 0)).markOutput());
        ArrowWidget arrow = new ArrowWidget(new Rectangle(x + w / 2 - 12, y + h / 2 - 9, 24, 7));
        arrow.setAnimationDuration(recipeDisplay.getTime() * 20);
        widgets.add(arrow);
        return widgets;
    }
}
