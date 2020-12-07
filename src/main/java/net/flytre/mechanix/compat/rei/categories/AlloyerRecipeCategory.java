package net.flytre.mechanix.compat.rei.categories;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.flytre.mechanix.block.alloyer.AlloyingRecipe;
import net.flytre.mechanix.compat.rei.ArrowWidget;
import net.flytre.mechanix.compat.rei.displays.AbstractRecipeDisplay;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.recipe.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AlloyerRecipeCategory extends AbstractCustomCategory<AlloyingRecipe> {
    public AlloyerRecipeCategory(RecipeType<AlloyingRecipe> recipeType) {
        super(recipeType);
    }

    @NotNull
    public String getCategoryName() {
        return I18n.translate("recipe.mechanix.alloying");
    }

    @Override
    public @NotNull List<Widget> setupDisplay(AbstractRecipeDisplay<AlloyingRecipe> recipeDisplay, Rectangle bounds) {
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        int x = bounds.x;
        int y = bounds.y;
        int w = bounds.width;
        int h = bounds.height;
        widgets.add(Widgets.createSlot(new Point(x + w/4 - 9, y + h/4)).entries(getInput(recipeDisplay, 0)).markInput());
        widgets.add(Widgets.createSlot(new Point(x + w/2 - 9, y + h/4)).entries(getInput(recipeDisplay, 1)).markInput());
        widgets.add(Widgets.createSlot(new Point(x + 3*w/4 - 9, y + h/4)).entries(getInput(recipeDisplay, 2)).markInput());
        widgets.add(Widgets.createSlot(new Point(x + w/2 - 9, y + 2*h/3)).entries(getOutput(recipeDisplay, 0)).markOutput());
        widgets.add(ArrowWidget.create(new Point(x + w/4 - 9, y + 2*h/3),true));
        return widgets;
    }
}
