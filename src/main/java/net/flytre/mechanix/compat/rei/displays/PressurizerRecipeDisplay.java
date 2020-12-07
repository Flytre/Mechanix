package net.flytre.mechanix.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.mechanix.block.pressurizer.PressurizerRecipe;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PressurizerRecipeDisplay extends AbstractRecipeDisplay<PressurizerRecipe> {
    public PressurizerRecipeDisplay(PressurizerRecipe recipe) {
        super(recipe);
    }

    @Override
    public List<EntryStack> createOutputs() {
        return Collections.singletonList(EntryStack.create(recipe.getOutput()));
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        return CollectionUtils.map(Collections.singletonList(recipe.getInput()), ing -> EntryStack.ofItemStacks(Arrays.asList(ing.getMatchingStacksClient())));
    }

    @NotNull
    public String getCategoryName() {
        return I18n.translate("recipe.mechanix.compressing");
    }


    @Override
    public int createTime() {
        return recipe.getCraftTime();
    }
}
