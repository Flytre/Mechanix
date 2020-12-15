package net.flytre.mechanix.mixin;

import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.stream.Stream;

@Mixin(Ingredient.class)
public interface IngredientMixin {

    @Invoker("ofEntries")
    static Ingredient createEntries(Stream<?> entries) {
        throw new AssertionError("Mixin Error : IngredientMixin");
    }
}
