package net.flytre.mechanix.recipe;

import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

public interface MechanixRecipe<C extends Inventory> extends Recipe<C> {

    boolean cancelLoad();

    default int getCraftTime() {
        return 120;
    }

    /**
     * Whether the inventory is able to craft this recipe
     */
    boolean canAcceptRecipeOutput(C inv);


    /**
     * Craft means actually craft and remove inputs for a mechanix recipe and will be used as such
     */
    @Override
    ItemStack craft(C inv);


    /**
     * Required upgrades
     */
    default QuantifiedIngredient[] getUpgrades() {
        return new QuantifiedIngredient[]{};
    }
}
