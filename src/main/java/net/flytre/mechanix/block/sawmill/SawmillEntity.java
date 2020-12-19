package net.flytre.mechanix.block.sawmill;

import net.flytre.mechanix.api.energy.MachineEntity;
import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.recipe.SawmillRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SawmillEntity extends MachineEntity<Inventory, SawmillRecipe> {

    public SawmillEntity() {
        super(MachineRegistry.SAWMILL.getEntityType(), DefaultedList.ofSize(3, ItemStack.EMPTY),
                (World world, Inventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.SAWMILL_RECIPE, inventory, world).orElse(null)
                , 40);
    }

    @Override
    protected boolean canAcceptRecipeOutput(@Nullable SawmillRecipe recipe) {
        if (recipe == null)
            return false;
        return getStack(1).isEmpty() || EasyInventory.canMergeItems(getStack(1), recipe.craft(this));
    }

    @Override
    protected void craft(SawmillRecipe recipe) {
        ItemStack result = recipe.craft(this);
        ItemStack secondary = recipe.getOutputProvider(1).getStack().copy();
        if (this.getStack(1).isEmpty()) {
            this.setStack(1, result);
        } else {
            if (EasyInventory.canMergeItems(getStack(1), result)) {
                this.getStack(1).increment(result.getCount());
            }
        }
        if (Math.random() < recipe.getOutputProvider(1).getChance()) {
            if (this.getStack(2).isEmpty()) {
                this.setStack(2, secondary);
            } else if (EasyInventory.canMergeItems(getStack(2), secondary)) {
                this.getStack(2).increment(secondary.getCount());
            }
        }
        getStack(0).decrement(recipe.getInput().getQuantity());
    }


    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot == 0;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0 && super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot > 0 && super.canExtract(slot, stack, dir);
    }


    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.sawmill");
    }


    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new SawmillScreenHandler(syncId, inv, this, this.getProperties());
    }
}
