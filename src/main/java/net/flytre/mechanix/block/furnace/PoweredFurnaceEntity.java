package net.flytre.mechanix.block.furnace;

import net.flytre.mechanix.api.machine.GenericMachineEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class PoweredFurnaceEntity extends GenericMachineEntity<SmeltingRecipe> {

    public PoweredFurnaceEntity() {
        super(MachineRegistry.POWERED_FURNACE.getEntityType(), null, DefaultedList.ofSize(2, ItemStack.EMPTY), 20);
        setRecipeSupplier(() -> world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this, world).orElse(null));
    }

    @Override
    protected boolean matches(SmeltingRecipe recipe) {
        return recipe.matches(this, this.world);
    }

    @Override
    protected void craft(SmeltingRecipe recipe) {
        if (recipe != null && this.canAcceptRecipeOutput(recipe)) {
            ItemStack inputStack = this.items.get(0);
            ItemStack recipeOutput = recipe.getOutput();
            ItemStack currentOutputStack = this.items.get(1);
            if (currentOutputStack.isEmpty()) {
                this.items.set(1, recipeOutput.copy());
            } else if (currentOutputStack.getItem() == recipeOutput.getItem()) {
                currentOutputStack.increment(1);
            }


            inputStack.decrement(1);
        }
    }

    @Override
    protected boolean canAcceptRecipeOutput(SmeltingRecipe recipe) {
        if (!this.items.get(0).isEmpty() && recipe != null) {
            ItemStack recipeOutput = recipe.getOutput();
            if (recipeOutput.isEmpty()) {
                return false;
            } else {
                ItemStack currentOutputStack = this.items.get(1);
                if (currentOutputStack.isEmpty()) {
                    return true;
                } else if (!currentOutputStack.isItemEqualIgnoreDamage(recipeOutput)) {
                    return false;
                } else if (currentOutputStack.getCount() < this.getMaxCountPerStack() && currentOutputStack.getCount() < currentOutputStack.getMaxCount()) {
                    return true;
                } else {
                    return currentOutputStack.getCount() < recipeOutput.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        //input mode, input slot, & valid item
        return slot == 0 && super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        //output mode & output slot
        return slot == 1 && super.canExtract(slot, stack, dir);
    }


    @Override
    public Set<Item> validUpgrades() {
        return new HashSet<>();
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.furnace");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new PoweredFurnaceHandler(syncId, inv, this, getDelegate());
    }
}
