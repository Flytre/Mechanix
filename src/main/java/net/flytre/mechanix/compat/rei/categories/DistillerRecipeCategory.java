package net.flytre.mechanix.compat.rei.categories;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.flytre.mechanix.compat.rei.ArrowWidget;
import net.flytre.mechanix.compat.rei.displays.AbstractRecipeDisplay;
import net.flytre.mechanix.recipe.DistillerRecipe;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.recipe.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DistillerRecipeCategory extends AbstractCustomCategory<DistillerRecipe> {
    public DistillerRecipeCategory(RecipeType<DistillerRecipe> recipeType) {
        super(recipeType);
    }

    @NotNull
    public String getCategoryName() {
        return I18n.translate("recipe.mechanix.distilling");
    }


    @Override
    public @NotNull List<Widget> setupDisplay(AbstractRecipeDisplay<DistillerRecipe> recipeDisplay, Rectangle bounds) {
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        int x = bounds.x;
        int y = bounds.y;
        int w = bounds.width;
        int h = bounds.height;
        widgets.add(Widgets.createSlot(new Point(x + w / 6 - 9, y + h / 2 - 9)).entries(getInput(recipeDisplay, 0)).markInput());
        widgets.add(Widgets.createSlot(new Point(x + w / 3 - 9, y + h / 2 - 9)).entries(getInput(recipeDisplay, 1)).markInput());
        widgets.add(Widgets.createSlot(new Point(x + 3 * w / 4 - 9, y + h / 2 - 9)).entries(getOutput(recipeDisplay, 0)).markOutput());
        widgets.add(ArrowWidget.create(new Point(x + w / 2 - 12, y + h / 2 - 9), true, recipeDisplay.getTime()));
        return widgets;
    }
}
