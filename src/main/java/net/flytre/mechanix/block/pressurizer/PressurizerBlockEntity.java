package net.flytre.mechanix.block.pressurizer;

import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.recipe.PressurizerRecipe;
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

public class PressurizerBlockEntity extends MachineEntity<Inventory, PressurizerRecipe> {


    public PressurizerBlockEntity() {
        super(MachineRegistry.PRESSURIZER.getEntityType(), DefaultedList.ofSize(2, ItemStack.EMPTY),
                (World world, Inventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.PRESSURIZER_RECIPE, inventory, world).orElse(null)
                , 60);
    }


    protected void craft(PressurizerRecipe recipe) {
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

    protected boolean canAcceptRecipeOutput(PressurizerRecipe recipe) {
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
        return new TranslatableText("block.mechanix.pressurizer");
    }


    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new PressurizerScreenHandler(syncId,inv,this,this.getProperties());

    }

}
