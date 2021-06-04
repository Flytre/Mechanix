package net.flytre.mechanix.compat.rei.categories;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.flytre.flytre_lib.compat.rei.AbstractCustomCategory;
import net.flytre.flytre_lib.compat.rei.AbstractRecipeDisplay;
import net.flytre.flytre_lib.compat.rei.ArrowWidget;
import net.flytre.mechanix.compat.rei.displays.ItemRecipeDisplay;
import net.flytre.mechanix.compat.rei.displays.MechanixRecipeDisplay;
import net.flytre.mechanix.recipe.AlloyingRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.recipe.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AlloyerRecipeCategory extends MechanixCustomCategory<AlloyingRecipe> {
    public AlloyerRecipeCategory(RecipeType<AlloyingRecipe> recipeType) {
        super(recipeType);
    }

    @NotNull
    public String getCategoryName() {
        return I18n.translate("recipe.mechanix.alloying");
    }

    @Override
    public @NotNull EntryStack getLogo() {
        return EntryStack.create(MachineRegistry.ALLOYER.getBlock());
    }

    @Override
    public @NotNull List<Widget> setupDisplay(MechanixRecipeDisplay<AlloyingRecipe> recipeDisplay, Rectangle bounds) {
        List<Widget> widgets = super.setupDisplay(recipeDisplay, bounds);
        int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height - 20;
        widgets.add(Widgets.createSlot(new Point(x + w / 4 - 9, y + h / 4)).entries(getInput(recipeDisplay, 0)).markInput());
        widgets.add(Widgets.createSlot(new Point(x + w / 2 - 9, y + h / 4)).entries(getInput(recipeDisplay, 1)).markInput());
        widgets.add(Widgets.createSlot(new Point(x + 3 * w / 4 - 9, y + h / 4)).entries(getInput(recipeDisplay, 2)).markInput());
        widgets.add(Widgets.createSlot(new Point(x + w / 2 - 9, y + 2 * h / 3)).entries(getOutput(recipeDisplay, 0)).markOutput());
        ArrowWidget arrow = new ArrowWidget(new Rectangle(x + w / 4 - 9, y + 2 * h / 3, 24, 7));
        arrow.setAnimationDuration(recipeDisplay.getTime() * 20);
        widgets.add(arrow);
        return widgets;
    }
}
