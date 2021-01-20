package net.flytre.mechanix.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.flytre.mechanix.recipe.DisenchanterRecipe;
import net.flytre.mechanix.recipe.EnchanterRecipe;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Shadow private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes;
    private static final ShapedRecipe EMPTY = new ShapedRecipe(new Identifier("mechanix:dummy"),"dummy",5,5, DefaultedList.ofSize(1,Ingredient.EMPTY), ItemStack.EMPTY);

//    @ModifyVariable(method = "apply", at = @At(value = "INVOKE_ASSIGN", ordinal = 0, target = "Lnet/minecraft/recipe/RecipeManager;deserialize(Lnet/minecraft/util/Identifier;Lcom/google/gson/JsonObject;)Lnet/minecraft/recipe/Recipe;"))
//    public Recipe<?> cancelLoad(Recipe<?> recipe) {
//        return recipe;
//    }

    @Inject(method="apply", at = @At("RETURN"))
    public void insertEnchantingRecipes(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        List<EnchanterRecipe> recipes = EnchanterRecipe.getRecipes();
        Map<Identifier, Recipe<?>> enchMap = new HashMap<>();
        for (EnchanterRecipe recipe : recipes) {
            enchMap.put(recipe.getId(), recipe);
        }

        List<DisenchanterRecipe> disenchanterRecipes = DisenchanterRecipe.getRecipes();
        Map<Identifier, Recipe<?>> disenchantMap = new HashMap<>();
        for (DisenchanterRecipe recipe : disenchanterRecipes) {
            disenchantMap.put(recipe.getId(), recipe);
        }

        Map<RecipeType<?>, Map<Identifier, Recipe<?>>> old = this.recipes;
        Map<RecipeType<?>, Map<Identifier, Recipe<?>>> copy = new HashMap<>(old);
        copy.put(RecipeRegistry.ENCHANTING_RECIPE, enchMap); // add a new entry
        copy.put(RecipeRegistry.DISENCHANTING_RECIPE, disenchantMap); // add a new entry
        this.recipes = ImmutableMap.copyOf(copy);
    }
}
