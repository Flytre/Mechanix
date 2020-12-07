package net.flytre.mechanix.compat.rei.categories;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.gui.entries.RecipeEntry;
import me.shedaniel.rei.gui.entries.SimpleRecipeEntry;
import net.flytre.mechanix.compat.rei.MechanixPlugin;
import net.flytre.mechanix.compat.rei.ReiUtils;
import net.flytre.mechanix.compat.rei.displays.AbstractRecipeDisplay;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public class AbstractCustomCategory<R extends Recipe<?>>  implements RecipeCategory<AbstractRecipeDisplay<R>> {
    private final RecipeType<R> recipeType;

    public AbstractCustomCategory(RecipeType<R> recipeType) {
        this.recipeType = recipeType;
    }


    @Override
    public Identifier getIdentifier() {
        return ReiUtils.getId(recipeType);
    }

    @Override
    public String getCategoryName() {
        return I18n.translate( ReiUtils.getId(recipeType).toString());
    }

    @Override
    public EntryStack getLogo() {
        return EntryStack.create(MechanixPlugin.iconMap.getOrDefault(recipeType, () -> Items.BARRIER));
    }

    @Override
    public RecipeEntry getSimpleRenderer(AbstractRecipeDisplay<R> recipe) {
        return SimpleRecipeEntry.create(Collections.singletonList(recipe.getInputEntries().get(0)), recipe.getOutputEntries());
    }

    public List<EntryStack> getInput(AbstractRecipeDisplay<R> recipeDisplay, int index) {
        List<List<EntryStack>> inputs = recipeDisplay.getInputEntries();
        return inputs.size() > index ? inputs.get(index) : Collections.emptyList();
    }
    public List<EntryStack> getOutput(AbstractRecipeDisplay<R> recipeDisplay, int index) {
        List<EntryStack> outputs = recipeDisplay.getOutputEntries();
        return outputs.size() > index ? Collections.singletonList(outputs.get(index)) : Collections.emptyList();
    }


}
