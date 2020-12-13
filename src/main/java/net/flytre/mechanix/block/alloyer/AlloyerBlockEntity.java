package net.flytre.mechanix.block.alloyer;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.recipe.AlloyingRecipe;
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
import java.util.HashSet;

public class AlloyerBlockEntity extends EnergyEntity implements EasyInventory {
    private final DefaultedList<ItemStack> items;
    private int craftTime;

    public AlloyerBlockEntity() {
        super(MachineRegistry.ALLOYER.getEntityType());
        items = DefaultedList.ofSize(4, ItemStack.EMPTY);
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


    private boolean canAcceptRecipeOutput(@Nullable AlloyingRecipe recipe) {
        if(recipe == null)
            return false;
        return getStack(3).isEmpty() || EasyInventory.canMergeItems(getStack(3),recipe.craft(this));
    }

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
            HashSet<Integer> used = recipe.getUsedStacks(this);
            for (int i : used)
                this.getStack(i).decrement(1);
        }
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
        if (getEnergy() + 100 * tierTimes < getMaxEnergy())
            requestEnergy(100 * tierTimes);
        AlloyingRecipe recipe = world.getRecipeManager().getFirstMatch(RecipeRegistry.ALLOYING_RECIPE, this, this.world).orElse(null);
        if (this.hasEnergy(50 * tierTimes) && canAcceptRecipeOutput(recipe)) {
            this.addEnergy(-50 * tierTimes);
            shouldBeActivated = true;
            this.craftTime -= tierTimes;
            if (this.craftTime <= 0) {
                craft(recipe);
                reset = true;
            }
        } else

            reset = true;

        if (reset)
            craftTime = 120;

        if (shouldBeActivated != currActivated) {
            world.setBlockState(getPos(), world.getBlockState(pos).with(MachineBlock.ACTIVATED, shouldBeActivated));
        }
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
    public boolean isValid(int slot, ItemStack stack) {
        return slot <= 2;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot <= 2 && EasyInventory.super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 3 && EasyInventory.super.canExtract(slot, stack, dir);

    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.alloyer");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new AlloyerScreenHandler(syncId,inv,this,this.getProperties());
    }
}
