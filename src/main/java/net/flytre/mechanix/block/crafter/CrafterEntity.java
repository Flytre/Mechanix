package net.flytre.mechanix.block.crafter;


import net.flytre.flytre_lib.common.inventory.EasyInventory;
import net.flytre.flytre_lib.common.recipe.RecipeUtils;
import net.flytre.mechanix.api.machine.GenericMachineEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class CrafterEntity extends GenericMachineEntity<CraftingRecipe> {

    public CrafterEntity() {
        super(MachineRegistry.CRAFTER.getEntityType(), null, DefaultedList.ofSize(11, ItemStack.EMPTY), 80);
        setRecipeSupplier(() -> RecipeUtils.getFirstCraftingMatch(getStack(9).getItem(), this, world, 0, 9));
    }


    @Override
    protected boolean matches(CraftingRecipe recipe) {
        return recipe.getOutput().getItem() == getStack(9).getItem() && RecipeUtils.craftingInputMatch(recipe, this, 0, 9);
    }

    @Override
    protected void craft(CraftingRecipe recipe) {
        RecipeUtils.actuallyCraft(recipe, this, 0, 9);
        ItemStack result = recipe.getOutput().copy();
        if (this.getStack(10).isEmpty()) {
            this.setStack(10, result);
        } else {
            if (EasyInventory.canMergeItems(getStack(10), result)) {
                this.getStack(10).increment(result.getCount());
            }
        }
    }

    @Override
    protected boolean canAcceptRecipeOutput(CraftingRecipe recipe) {
        return recipe != null
                && (getStack(10).isEmpty() || EasyInventory.canMergeItems(getStack(10), recipe.getOutput()));
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.crafter");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CrafterHandler(syncId, inv, this, getDelegate());
    }


    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot != 10;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot != 10 && super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 10 && super.canExtract(slot, stack, dir);
    }

}
