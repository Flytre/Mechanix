package net.flytre.mechanix.mixin;


import net.flytre.mechanix.recipe.MechanixRecipe;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {

    @Inject(method = "getGroupForRecipe", at = @At("HEAD"), cancellable = true)
    private static void mechanix$getRecipeGroup(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        if (recipe instanceof MechanixRecipe<?>)
            cir.setReturnValue(RecipeBookGroup.UNKNOWN);
    }
}
