package net.flytre.mechanix.compat.rei.categories;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.flytre.flytre_lib.compat.rei.ArrowWidget;
import net.flytre.mechanix.compat.rei.displays.MechanixRecipeDisplay;
import net.flytre.mechanix.recipe.ItemProcessingRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SawmillRecipeCategory extends MechanixCustomCategory<ItemProcessingRecipe<Inventory>> {
    public SawmillRecipeCategory(RecipeType<ItemProcessingRecipe<Inventory>> recipeType) {
        super(recipeType);
    }


    @Override
    public @NotNull EntryStack getLogo() {
        return EntryStack.create(MachineRegistry.SAWMILL.getBlock());
    }

    @NotNull
    public String getCategoryName() {
        return I18n.translate("recipe.mechanix.sawing");
    }

    @Override
    public @NotNull List<Widget> setupDisplay(MechanixRecipeDisplay<ItemProcessingRecipe<Inventory>> recipeDisplay, Rectangle bounds) {
        List<Widget> widgets = super.setupDisplay(recipeDisplay, bounds);
        int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height - 20;
        widgets.add(Widgets.createSlot(new Point(x + w / 4 - 9, y + h / 2 - 9)).entries(getInput(recipeDisplay, 0)).markInput());
        widgets.add(Widgets.createSlot(new Point(x + 3 * w / 4 - 9, y + h / 2 - 12)).entries(getOutput(recipeDisplay, 0)).markOutput());
        widgets.add(Widgets.createSlot(new Point(x + 3 * w / 4 - 9, y + 3 * h / 4 - 9)).entries(getOutput(recipeDisplay, 1)).markOutput());
        ArrowWidget arrow = new ArrowWidget(new Rectangle(x + w / 2 - 12, y + h / 2 - 9, 24, 7));
        arrow.setAnimationDuration(recipeDisplay.getTime() * 20);
        widgets.add(arrow);
        if (!recipeDisplay.getRecipe().getOutputs()[1].getStack().isEmpty())
            widgets.add(Widgets.createLabel(new Point(x + w / 2 + 13, y + 3 * h / 4 - 5), Text.of(String.format("%.0f", recipeDisplay.getRecipe().getOutputs()[1].getChance() * 100) + "%")));
        return widgets;
    }


}
