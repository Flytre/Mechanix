package net.flytre.mechanix.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.flytre.mechanix.Mechanix;
import net.flytre.mechanix.recipe.*;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * When updating: Any method that uses recipes should trigger the function at head;
 */

@Mixin(value = RecipeManager.class, priority = 989)
public class RecipeManagerMixin implements ReloadTracker {

    @Shadow
    private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes;

    @Unique
    private boolean tweaked = false;

    @Unique
    private long lastReloadTime = -1L;


    @Inject(method = "apply", at = @At("HEAD"))
    public void mechanix$wasTweaked(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        tweaked = true;
        lastReloadTime = System.currentTimeMillis();
    }

    /*
     Yes this is sketch but its the only way to look elegant, have KubeJS support, and also load properly 100% of the time
     Basically we just tweak the recipes the first time they're accessed instead of in the apply method
        @Inject(method = "apply", at = @At("RETURN"))
        public void addMechanixRecipes(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
            doRecipeThings();
        }
    */

    private void doRecipeThings() {
        tweakRecipes();
        cancelRecipes();
        validateUpgrades();
    }


    private void tweakRecipes() {
        Map<Identifier, Recipe<?>> enchMap = new HashMap<>();
        for (EnchantingRecipe recipe : EnchantingRecipe.getRecipes())
            enchMap.put(recipe.getId(), recipe);

        Map<Identifier, Recipe<?>> disMap = new HashMap<>();
        for (DisenchantingRecipe recipe : DisenchantingRecipe.getRecipes())
            disMap.put(recipe.getId(), recipe);

        Map<Identifier, Recipe<?>> transmuting = new HashMap<>(this.recipes.get(RecipeRegistry.TRANSMUTING_RECIPE_FAKE));
        Map<Identifier, Recipe<?>> transmutingRecipeMap = new HashMap<>();

        for (Recipe<?> recipe : transmuting.values())
            for (ItemProcessingRecipe<Inventory> r : ((FakeTransmutingRecipe) recipe).generateRecipes())
                transmutingRecipeMap.put(r.getId(), r);

        Map<RecipeType<?>, Map<Identifier, Recipe<?>>> old = this.recipes;
        Map<RecipeType<?>, Map<Identifier, Recipe<?>>> copy = new HashMap<>(old);
        copy.put(RecipeRegistry.ENCHANTING_RECIPE, enchMap); // add a new entry
        copy.put(RecipeRegistry.DISENCHANTING_RECIPE, disMap); // add a new entry
        copy.put(RecipeRegistry.TRANSMUTING_RECIPE, transmutingRecipeMap);
        this.recipes = ImmutableMap.copyOf(copy);
        this.tweaked = false;
        Mechanix.LOGGER.info("Added enchanting recipes, disenchanting recipes, and transmutation recipes.");
    }

    private void cancelRecipes() {
        int ct = 0;
        Map<RecipeType<?>, Map<Identifier, Recipe<?>>> copy = new HashMap<>(this.recipes);
        for (RecipeType<?> type : this.recipes.keySet()) {
            Map<Identifier, Recipe<?>> modified = new HashMap<>();
            Map<Identifier, Recipe<?>> current = recipes.get(type);
            for (Map.Entry<Identifier, Recipe<?>> entry : current.entrySet()) {
                boolean valid = !(entry.getValue() instanceof MechanixRecipe) || !((MechanixRecipe<?>) entry.getValue()).cancelLoad();
                if (valid)
                    modified.put(entry.getKey(), entry.getValue());
                else
                    ct += 1;
            }
            copy.put(type, modified);
        }
        this.recipes = ImmutableMap.copyOf(copy);
        Mechanix.LOGGER.info("Removed " + ct + " recipes.");

    }


    private void validateUpgrades() {
        for (RecipeType<?> type : this.recipes.keySet()) {
            if (!(type instanceof MechanixRecipeType))
                continue;
            ((MechanixRecipeType<?>) type).reset();
            Map<Identifier, Recipe<?>> current = recipes.get(type);
            for (Map.Entry<Identifier, Recipe<?>> entry : current.entrySet()) {
                boolean valid = entry.getValue() instanceof MechanixRecipe && !((MechanixRecipe<?>) entry.getValue()).cancelLoad();
                if (valid && ((MechanixRecipe<?>) entry.getValue()).getUpgrades().length > 0) {
                    for (QuantifiedIngredient q : ((MechanixRecipe<?>) entry.getValue()).getUpgrades()) {
                        ((MechanixRecipeType<?>) type).addValidCondition(q);
                    }
                }
            }
        }
    }


    @Inject(method = "getAllOfType", at = @At("HEAD"))
    public <C extends Inventory, T extends Recipe<C>> void mechanix$getAllofType(RecipeType<T> type, CallbackInfoReturnable<Map<Identifier, Recipe<C>>> cir) {
        if (tweaked)
            doRecipeThings();
    }

    @Inject(method = "get", at = @At("HEAD"))
    public void mechanix$get(Identifier id, CallbackInfoReturnable<Optional<? extends Recipe<?>>> cir) {
        if (tweaked)
            doRecipeThings();
    }

    @Inject(method = "values", at = @At("HEAD"))
    public void mechanix$values(CallbackInfoReturnable<Collection<Recipe<?>>> cir) {
        if (tweaked)
            doRecipeThings();
    }

    @Inject(method = "keys", at = @At("HEAD"))
    public void mechanix$keys(CallbackInfoReturnable<Collection<Recipe<?>>> cir) {
        if (tweaked)
            doRecipeThings();
    }

    @Override
    public long lastReloadTime() {
        return lastReloadTime;
    }
}
