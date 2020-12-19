package net.flytre.mechanix.block.crafter;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.api.recipe.RecipeUtils;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class CrafterBlockEntity extends EnergyEntity implements EasyInventory {
    private final DefaultedList<ItemStack> items;
    private int craftTime;
    private CraftingRecipe recipe;


    public CrafterBlockEntity() {
        super(MachineRegistry.CRAFTER.getEntityType());
        items = DefaultedList.ofSize(11, ItemStack.EMPTY);
        setEnergyMode(false, false, false, false, false, false);
        setIOMode(false, true, false, false, false, false);
        setMaxEnergy(300000);
        setMaxTransferRate(200);
        panelMode = 1;
        recipe = null;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public HashMap<Direction, Boolean> getItemIO() {
        return ioMode;
    }

    private boolean canAcceptRecipeOutput(@Nullable CraftingRecipe recipe) {
        return recipe != null && RecipeUtils.craftingInputMatch(recipe, this, 0, 9)
                && (getStack(10).isEmpty() || EasyInventory.canMergeItems(getStack(10),recipe.getOutput()));
    }

    @Override
    public void updateDelegate() {
        super.updateDelegate();
        getProperties().set(8, craftTime);
    }

    @Override
    public void repeatTick() {

    }

    @Override
    public void onceTick() {
        if (this.world == null || this.world.isClient)
            return;
        boolean currActivated = world.getBlockState(getPos()).get(MachineBlock.ACTIVATED);
        boolean shouldBeActivated = false;
        boolean reset = false;
        int tierTimes = getTier() + 1;
        if (!isFull())
            requestEnergy(Math.min(80 * tierTimes, getMaxEnergy() - getEnergy()));
        recipe = getStack(9).isEmpty() ? null : RecipeUtils.getFirstCraftingMatch(getStack(9).getItem(),this,world,0,9);
        if (this.hasEnergy(40 * tierTimes) && canAcceptRecipeOutput(recipe)) {
            this.addEnergy(-40 * tierTimes);
            shouldBeActivated = true;
            this.craftTime += tierTimes;
            if (this.craftTime >= 120) {
                craft(recipe);
                reset = true;
            }
        } else
            reset = true;

        if (reset)
            craftTime = 0;

        if (shouldBeActivated != currActivated) {
            world.setBlockState(getPos(), world.getBlockState(pos).with(MachineBlock.ACTIVATED, shouldBeActivated));
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot != 10;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot != 10 && EasyInventory.super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 10 && EasyInventory.super.canExtract(slot, stack, dir);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        Inventories.fromTag(tag, items);
        this.craftTime = tag.getInt("craftTime");
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, items);
        tag.putInt("craftTime", craftTime);
        return super.toTag(tag);
    }



    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.crafter");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CrafterScreenHandler(syncId,inv,this,getProperties());
    }

    private void craft(CraftingRecipe recipe) {
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

}
