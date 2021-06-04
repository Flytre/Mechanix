package net.flytre.mechanix.api.machine;

import net.flytre.mechanix.recipe.MechanixRecipe;
import net.flytre.mechanix.util.ItemRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;


/**
 * Abstract class that takes care of redundancies in processing machines, so its very, very easy to add them!
 */
public abstract class MachineEntity<T extends Inventory, R extends MechanixRecipe<T>> extends GenericMachineEntity<R> {

    public MachineEntity(BlockEntityType<?> type, RecipeType<?> recipeType, DefaultedList<ItemStack> items, BiFunction<World, T, R> recipeSupplier, int costPerTick) {
        super(type, recipeType, items, costPerTick);
        setRecipeSupplier(() -> recipeSupplier.apply(world, (T) this));
    }

    @Override
    protected int calcCraftTime() {
        return recipe != null ? recipe.getCraftTime() : 200;
    }


    @Override
    protected boolean matches(R recipe) {
        return recipe.matches((T) this, this.world);
    }

    @Override
    protected boolean canAcceptRecipeOutput(R recipe) {
        return recipe.canAcceptRecipeOutput((T) this);
    }


    @Override
    protected void craft(R recipe) {
        recipe.craft((T) this);
    }
}
