package net.flytre.mechanix.compat.rei.categories;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.entries.RecipeEntry;
import me.shedaniel.rei.gui.entries.SimpleRecipeEntry;
import me.shedaniel.rei.gui.widget.Widget;
import net.flytre.mechanix.compat.rei.displays.MechanixRecipeDisplay;
import net.flytre.mechanix.recipe.MechanixRecipe;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class MechanixCustomCategory<R extends MechanixRecipe<?>> implements RecipeCategory<MechanixRecipeDisplay<R>> {
    private final RecipeType<R> recipeType;

    public MechanixCustomCategory(RecipeType<R> recipeType) {
        this.recipeType = recipeType;
    }


    @Override
    public @NotNull Identifier getIdentifier() {
        return Objects.requireNonNull(Registry.RECIPE_TYPE.getId(recipeType));
    }

    @Override
    public @NotNull String getCategoryName() {
        return I18n.translate(getIdentifier().toString());
    }

    @Override
    public abstract @NotNull EntryStack getLogo();

    public RecipeType<R> getRecipeType() {
        return recipeType;
    }

    @Override
    public @NotNull RecipeEntry getSimpleRenderer(MechanixRecipeDisplay<R> recipe) {
        return SimpleRecipeEntry.from(recipe.getInputEntries(), recipe.getResultingEntries());
    }

    public List<EntryStack> getInput(MechanixRecipeDisplay<R> recipeDisplay, int index) {
        List<List<EntryStack>> inputs = recipeDisplay.getInputEntries();
        return inputs.size() > index ? inputs.get(index) : Collections.emptyList();
    }


    public List<EntryStack> getOutput(MechanixRecipeDisplay<R> recipeDisplay, int index) {
        List<List<EntryStack>> outputs = recipeDisplay.getResultingEntries();
        return outputs.size() > index ? outputs.get(index) : Collections.emptyList();
    }


    public List<EntryStack> getUpgrade(MechanixRecipeDisplay<R> recipeDisplay, int index) {
        List<List<EntryStack>> upgrades = recipeDisplay.getUpgrades();
        return upgrades.size() > index ? upgrades.get(index) : Collections.emptyList();
    }

    @Override
    public int getDisplayHeight() {
        return RecipeCategory.super.getDisplayHeight() + 20;
    }

    @Override
    public @NotNull List<Widget> setupDisplay(MechanixRecipeDisplay<R> recipeDisplay, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height;
        for (int i = -2; i < 2; i++)
            widgets.add(Widgets.createSlot(new Point(x + w / 2.0 - 9 - 20 * (i + 0.5), y + h - 22)).entries(getUpgrade(recipeDisplay, 1 - i)).markInput());
        return widgets;
    }
}
