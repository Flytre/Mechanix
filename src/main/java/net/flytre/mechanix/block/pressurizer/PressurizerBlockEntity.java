package net.flytre.mechanix.block.pressurizer;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.recipe.ItemProcessingRecipe;
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

public class PressurizerBlockEntity extends EnergyEntity implements EasyInventory {
    private final DefaultedList<ItemStack> items;
    private int craftTime;
    private ItemProcessingRecipe recipe;

    public PressurizerBlockEntity() {
        super(MachineRegistry.PRESSURIZER.getEntityType());
        items = DefaultedList.ofSize(2, ItemStack.EMPTY);
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

    @Override
    public void updateDelegate() {
        super.updateDelegate();

        if (recipe != null) {
            getProperties().set(8, craftTime);
            getProperties().set(9, recipe.getCraftTime());
        }else {
            getProperties().set(8, 0);
            getProperties().set(9,1);
        }
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
        recipe = world.getRecipeManager().getFirstMatch(RecipeRegistry.PRESSURIZER_RECIPE, this, this.world).orElse(null);
        if (this.hasEnergy(60 * tierTimes) && canAcceptRecipeOutput(recipe)) {
            this.addEnergy(-60 * tierTimes);
            shouldBeActivated = true;
            this.craftTime += tierTimes;
            if (this.craftTime >= recipe.getCraftTime()) {
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

    private void craft(ItemProcessingRecipe recipe) {
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

    private boolean canAcceptRecipeOutput(ItemProcessingRecipe recipe) {
        if (recipe == null)
            return false;
        return getStack(1).isEmpty() || EasyInventory.canMergeItems(getStack(1), recipe.craft(this));
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
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0 && EasyInventory.super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 1 && EasyInventory.super.canExtract(slot, stack, dir);
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
