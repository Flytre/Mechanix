package net.flytre.mechanix.compat.rei.categories;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.flytre.mechanix.compat.rei.ArrowWidget;
import net.flytre.mechanix.compat.rei.displays.AbstractRecipeDisplay;
import net.flytre.mechanix.recipe.SawmillRecipe;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SawmillRecipeCategory extends AbstractCustomCategory<SawmillRecipe> {
    public SawmillRecipeCategory(RecipeType<SawmillRecipe> recipeType) {
        super(recipeType);
    }

    @NotNull
    public String getCategoryName() {
        return I18n.translate("recipe.mechanix.sawing");
    }

    @Override
    public @NotNull List<Widget> setupDisplay(AbstractRecipeDisplay<SawmillRecipe> recipeDisplay, Rectangle bounds) {
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        int x = bounds.x;
        int y = bounds.y;
        int w = bounds.width;
        int h = bounds.height;
        widgets.add(Widgets.createSlot(new Point(x + w / 4 - 9, y + h / 2 - 9)).entries(getInput(recipeDisplay, 0)).markInput());
        widgets.add(Widgets.createSlot(new Point(x + 3 * w / 4 - 9, y + h / 2 - 12)).entries(getOutput(recipeDisplay, 0)).markOutput());
        widgets.add(Widgets.createSlot(new Point(x + 3 * w / 4 - 9, y + 3 * h / 4 - 9)).entries(getOutput(recipeDisplay, 1)).markOutput());
        widgets.add(ArrowWidget.create(new Point(x + w / 2 - 12, y + h / 2 - 9), true, recipeDisplay.getTime()));

        if(!recipeDisplay.getRecipe().getOutputProvider(1).getStack().isEmpty())
            widgets.add(Widgets.createLabel(new Point(x + w / 2 + 13, y + 3 * h / 4 - 5), Text.of(String.format("%.0f",recipeDisplay.getRecipe().getOutputProvider(1).getChance()*100) + "%")));
        return widgets;
    }


}
