package net.flytre.mechanix.compat.rei;

import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;


public class ReiUtils {

    public static Identifier getId(RecipeType<?> type) {
        if(type == RecipeRegistry.ALLOYING_RECIPE)
            return new Identifier("mechanix:alloying");
        if(type == RecipeRegistry.FOUNDRY_RECIPE)
            return new Identifier("mechanix:casting");
        if(type == RecipeRegistry.LIQUIFIER_RECIPE)
            return new Identifier("mechanix:liquifying");
        if(type == RecipeRegistry.PRESSURIZER_RECIPE)
            return new Identifier("mechanix:compressing");
        if(type == RecipeRegistry.CRUSHER_RECIPE)
            return new Identifier("mechanix:crushing");
        if (type == RecipeRegistry.DISTILLER_RECIPE)
            return new Identifier("mechanix:distilling");
        if (type == RecipeRegistry.SAWMILL_RECIPE)
            return new Identifier("mechanix:sawing");
        if (type == RecipeRegistry.CENTRIFUGE_RECIPE)
            return new Identifier("mechanix:centrifuging");
        if (type == RecipeRegistry.HYDROPONATOR_RECIPE)
            return new Identifier("mechanix:hydroponics");
        if (type == RecipeRegistry.ENCHANTING_RECIPE)
            return new Identifier("mechanix:enchanting");
        if (type == RecipeRegistry.DISENCHANTING_RECIPE)
            return new Identifier("mechanix:unenchanting");
        throw new RuntimeException(new Exception("Unknown identifier for recipe type: " + type + ". Declare in ReiUtils.java"));
    }
}
