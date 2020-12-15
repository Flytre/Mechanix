package net.flytre.mechanix.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    private static final ShapedRecipe EMPTY = new ShapedRecipe(new Identifier("mechanix:dummy"),"dummy",5,5, DefaultedList.ofSize(1,Ingredient.EMPTY), ItemStack.EMPTY);

    @ModifyVariable(method = "apply", at = @At(value = "INVOKE_ASSIGN", ordinal = 0, target = "Lnet/minecraft/recipe/RecipeManager;deserialize(Lnet/minecraft/util/Identifier;Lcom/google/gson/JsonObject;)Lnet/minecraft/recipe/Recipe;"))
    public Recipe<?> cancelLoad(Recipe<?> recipe) {
        return recipe;
    }
}
