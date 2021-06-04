package net.flytre.mechanix.compat.rei;

import me.shedaniel.rei.api.DisplayVisibilityHandler;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.RecipeDisplay;
import net.flytre.flytre_lib.compat.rei.AbstractRecipeDisplay;
import net.flytre.mechanix.recipe.MechanixRecipe;
import net.minecraft.util.ActionResult;

public class MechanixRenderCanceller implements DisplayVisibilityHandler {


    @Override
    public float getPriority() {
        return 100.0f;
    }


    @Override
    public ActionResult handleDisplay(RecipeCategory<?> category, RecipeDisplay display) {
        if(!(display instanceof AbstractRecipeDisplay))
            return ActionResult.PASS;
        AbstractRecipeDisplay<?> disp = (AbstractRecipeDisplay<?>) display;
        if(disp.getRecipe() instanceof MechanixRecipe) {
            return ((MechanixRecipe<?>) disp.getRecipe()).cancelLoad() ? ActionResult.FAIL : ActionResult.SUCCESS;
        }
        return ActionResult.PASS;


    }
}
