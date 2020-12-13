package net.flytre.mechanix.block.distiller;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.recipe.DistillerRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class DistillerEntity extends EnergyEntity implements DoubleInventory {
    private final DefaultedList<FluidStack> fluidInventory;
    private int craftTime;


    public DistillerEntity() {
        super(MachineRegistry.DISTILLER.getEntityType());
        fluidInventory = DefaultedList.ofSize(3, FluidStack.EMPTY);
        setEnergyMode(false, false, false, false, false, false);
        setIOMode(false, true, false, false, false, false);
        setMaxEnergy(500000);
        setMaxTransferRate(250);
        panelMode = 1;
    }

    @Override
    public void updateDelegate() {
        super.updateDelegate();
        getProperties().set(8,craftTime);
        getProperties().set(9,getFluidStack(0).getAmount());
        getProperties().set(10,getFluidStack(1).getAmount());
        getProperties().set(11,getFluidStack(2).getAmount());
    }


    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        FluidInventory.fromTag(tag,fluidInventory);
        craftTime = tag.getInt("craftTime");
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("craftTime",craftTime);
        FluidInventory.toTag(tag,fluidInventory);
        return super.toTag(tag);
    }

    @Override
    public void repeatTick() {

    }


    private boolean canAcceptRecipeOutput(@Nullable DistillerRecipe recipe) {
        if(recipe == null)
            return false;
        return getFluidStack(2).isEmpty() || isValidInternal(2,recipe.fluidOutput());
    }

    public void craft(DistillerRecipe recipe) {

        getFluidStack(0).decrement(recipe.getInput().getAmount());
        getFluidStack(1).decrement(500);

        if (!getFluidStack(2).isEmpty())
            getFluidStack(2).increment(recipe.fluidOutput().getAmount());
        else
            setStack(2, recipe.fluidOutput());
    }


    @Override
    public void onceTick() {
        if(this.world == null || this.world.isClient)
            return;

        boolean currActivated = world.getBlockState(getPos()).get(MachineBlock.ACTIVATED);
        boolean shouldBeActivated = false;
        boolean reset = false;
        int tierTimes = getTier() + 1;
        if(getEnergy() + 100*tierTimes < getMaxEnergy())
            requestEnergy(100*tierTimes);
        DistillerRecipe recipe = world.getRecipeManager().getFirstMatch(RecipeRegistry.DISTILLER_RECIPE, this, this.world).orElse(null);
        if (this.hasEnergy(100 * tierTimes) && canAcceptRecipeOutput(recipe)) {
            this.addEnergy(-100 * tierTimes);
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
    public HashMap<Direction, Boolean> getFluidIO() {
        return ioMode;
    }

    @Override
    public DefaultedList<FluidStack> getFluids() {
        return fluidInventory;
    }

    @Override
    public int capacity() {
        return 24000;
    }

    @Override
    public int slotCapacity() {
        return 8000;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public boolean isValidInternal(int slot, FluidStack stack) {
        if(slot == 1 && stack.getFluid() != Fluids.WATER)
            return false;
        if(slot == 0 && stack.getFluid() == Fluids.WATER)
            return false;
        return DoubleInventory.super.isValidInternal(slot,stack);
    }

    @Override
    public boolean isValidExternal(int slot, FluidStack stack) {
        return slot != 2 && isValidInternal(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, FluidStack stack, Direction dir) {
        return slot == 2 & DoubleInventory.super.canExtract(slot,stack,dir);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.distiller");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new DistillerScreenHandler(syncId,inv,this,getProperties());
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return DefaultedList.ofSize(0,ItemStack.EMPTY);
    }

    @Override
    public HashMap<Direction, Boolean> getItemIO() {
        return null;
    }
}
