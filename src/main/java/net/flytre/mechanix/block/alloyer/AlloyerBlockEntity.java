package net.flytre.mechanix.block.alloyer;

import net.flytre.mechanix.api.energy.MachineEntity;
import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.recipe.AlloyingRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class AlloyerBlockEntity extends MachineEntity<AlloyerBlockEntity, AlloyingRecipe> {

    public AlloyerBlockEntity() {
        super(MachineRegistry.ALLOYER.getEntityType(), DefaultedList.ofSize(4, ItemStack.EMPTY),
                (World world, AlloyerBlockEntity inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.ALLOYING_RECIPE, inventory, world).orElse(null)
                , 50);
    }

    @Override
    protected boolean canAcceptRecipeOutput(@Nullable AlloyingRecipe recipe) {
        if (recipe == null)
            return false;
        return getStack(3).isEmpty() || EasyInventory.canMergeItems(getStack(3), recipe.craft(this));
    }

    @Override
    public void craft(AlloyingRecipe recipe) {
        ItemStack result = recipe.craft(this);
        boolean crafted = false;
        if (this.getStack(3).isEmpty()) {
            this.setStack(3, result);
            crafted = true;
        } else {
            if (EasyInventory.canMergeItems(getStack(3), result)) {
                this.getStack(3).increment(result.getCount());
                crafted = true;
            }
        }

        if (crafted) {
            HashMap<Integer, Integer> used = recipe.getUsedStacks(this);
            for (int i : used.keySet()) {
                this.getStack(i).decrement(used.get(i));
            }
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot <= 2;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot <= 2 && super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 3 && super.canExtract(slot, stack, dir);

    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.alloyer");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new AlloyerScreenHandler(syncId, inv, this, this.getProperties());
    }
}
