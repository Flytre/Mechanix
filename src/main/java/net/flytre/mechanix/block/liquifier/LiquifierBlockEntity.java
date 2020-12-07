package net.flytre.mechanix.block.liquifier;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.recipe.LiquifierRecipe;
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

public class LiquifierBlockEntity extends EnergyEntity implements DoubleInventory {

    private final DefaultedList<ItemStack> itemInventory;
    private final DefaultedList<FluidStack> fluidInventory;
    private int craftTime;

    public LiquifierBlockEntity() {
        super(MachineRegistry.LIQUIFIER.getEntityType());
        fluidInventory = DefaultedList.ofSize(1, FluidStack.EMPTY);
        itemInventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        setEnergyMode(false, false, false, false, false, false);
        setIOMode(false, true, false, false, false, false);
        setMaxEnergy(300000);
        setMaxTransferRate(200);
        panelMode = 1;
    }


    @Override
    public void updateDelegate() {
        super.updateDelegate();
        getProperties().set(8, craftTime);

        //remember if this goes above 32k with upgrades it needs fixing
        getProperties().set(9, getFluidStack(0).getAmount());
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        FluidInventory.fromTag(tag, fluidInventory);
        Inventories.fromTag(tag, itemInventory);
        this.craftTime = tag.getInt("craftTime");
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        FluidInventory.toTag(tag, fluidInventory);
        Inventories.toTag(tag, itemInventory);
        tag.putInt("craftTime", craftTime);
        return super.toTag(tag);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return itemInventory;
    }

    @Override
    public HashMap<Direction, Boolean> getItemIO() {
        return ioMode;
    }

    @Override
    public HashMap<Direction, Boolean> getFluidIO() {
        return ioMode;
    }

    @Override
    public DefaultedList<FluidStack> getFluids() {
        return fluidInventory;
    }

    @Override
    public int capacity() {
        return 8000;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.liquifier");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new LiquifierScreenHandler(syncId,inv,this,this.getProperties());
    }


    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return DoubleInventory.super.isValid(slot, stack);
    }


    public void craft(LiquifierRecipe recipe) {
        getStack(0).decrement(1);
        if (!isFluidInventoryEmpty())
            getFluidStack(0).increment(recipe.fluidOutput().getAmount());
        else
            setStack(0, recipe.fluidOutput());
    }

    @Override
    public void repeatTick() {

    }

    private boolean canAcceptRecipeOutput(@Nullable LiquifierRecipe recipe) {
        if(recipe == null)
            return false;
        return getFluidStack(0).isEmpty() || isValid(0,recipe.fluidOutput());
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
        LiquifierRecipe recipe = world.getRecipeManager().getFirstMatch(RecipeRegistry.LIQUIFIER_RECIPE, this, this.world).orElse(null);
        if (this.hasEnergy(60 * tierTimes) && canAcceptRecipeOutput(recipe)) {
            this.addEnergy(-60 * tierTimes);
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
            world.setBlockState(getPos(), world.getBlockState(pos).with(LiquifierBlock.ACTIVATED, shouldBeActivated));
        }
    }

    @Override
    public boolean canInsert(int slot, FluidStack stack, @Nullable Direction dir) {
        return false;
    }
}
