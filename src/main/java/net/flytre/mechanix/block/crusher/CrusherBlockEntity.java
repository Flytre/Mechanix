package net.flytre.mechanix.block.crusher;

import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.recipe.CrusherRecipe;
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

public class CrusherBlockEntity extends MachineEntity<Inventory,CrusherRecipe> {

    public CrusherBlockEntity() {
        super(MachineRegistry.CRUSHER.getEntityType(), DefaultedList.ofSize(2, ItemStack.EMPTY),
                (World world, Inventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.CRUSHER_RECIPE, inventory, world).orElse(null)
                , 40);
    }


    @Override
    public void repeatTick() {

    }


    @Override
    protected void craft(CrusherRecipe recipe) {
        ItemStack result = recipe.craft(this);
        if (this.getStack(1).isEmpty()) {
            this.setStack(1, result);
        } else {
            if (EasyInventory.canMergeItems(getStack(1), result)) {
                this.getStack(1).increment(result.getCount());
            }
        }
        getStack(0).decrement(recipe.getInput().getQuantity());
    }

    @Override
    protected boolean canAcceptRecipeOutput(CrusherRecipe recipe) {
        if (recipe == null)
            return false;
        return getStack(1).isEmpty() || EasyInventory.canMergeItems(getStack(1), recipe.craft(this));
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0 && super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 1 && super.canExtract(slot, stack, dir);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.crusher");
    }


    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CrusherScreenHandler(syncId, inv, this, getProperties());
    }

}
