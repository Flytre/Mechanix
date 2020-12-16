package net.flytre.mechanix.block.sawmill;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.recipe.SawmillRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class SawmillEntity extends EnergyEntity implements EasyInventory {
    private final DefaultedList<ItemStack> items;
    private int craftTime;

    public SawmillEntity() {
        super(MachineRegistry.SAWMILL.getEntityType());
        items = DefaultedList.ofSize(3, ItemStack.EMPTY);
        setEnergyMode(false, false, false, false, false, false);
        setIOMode(false, true, false, false, false, false);
        setMaxEnergy(300000);
        setMaxTransferRate(200);
        panelMode = 1;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public HashMap<Direction, Boolean> getItemIO() {
        return ioMode;
    }

    private boolean canAcceptRecipeOutput(@Nullable SawmillRecipe recipe) {
        if(recipe == null)
            return false;
        return getStack(1).isEmpty() || EasyInventory.canMergeItems(getStack(1),recipe.craft(this));
    }


    @Override
    public void updateDelegate() {
        super.updateDelegate();
        getProperties().set(8, craftTime);
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
        SawmillRecipe recipe = world.getRecipeManager().getFirstMatch(RecipeRegistry.SAWMILL_RECIPE, this, this.world).orElse(null);
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

    private void craft(SawmillRecipe recipe) {
        ItemStack result = recipe.craft(this);
        ItemStack secondary = recipe.getOutputProviders()[1].getStack().copy();
        if (this.getStack(1).isEmpty()) {
            this.setStack(1, result);
        } else {
            if (EasyInventory.canMergeItems(getStack(1), result)) {
                this.getStack(1).increment(result.getCount());
            }
        }
        if(Math.random() < recipe.getOutputProviders()[1].getChance()) {
            if (this.getStack(2).isEmpty()) {
                this.setStack(2, secondary);
            } else if (EasyInventory.canMergeItems(getStack(2), secondary)) {
                    this.getStack(2).increment(secondary.getCount());
            }
        }
        getStack(0).decrement(recipe.getInput().getQuantity());
    }

    @Override
    public void repeatTick() { }


    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot == 0;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0 && EasyInventory.super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot > 0 && EasyInventory.super.canExtract(slot, stack, dir);
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

    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.sawmill");
    }


    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new SawmillScreenHandler(syncId,inv,this,this.getProperties());
    }
}
