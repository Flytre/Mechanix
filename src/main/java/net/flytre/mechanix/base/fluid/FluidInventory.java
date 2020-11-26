package net.flytre.mechanix.base.fluid;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public interface FluidInventory extends Clearable {

    static FluidStack splitStack(List<FluidStack> stacks, int slot, int amount) {
        return slot >= 0 && slot < stacks.size() && !stacks.get(slot).isEmpty() && amount > 0 ? stacks.get(slot).split(amount) : FluidStack.EMPTY;
    }

    static FluidStack removeStack(List<FluidStack> stacks, int slot) {
        return slot >= 0 && slot < stacks.size() ? stacks.set(slot, FluidStack.EMPTY) : FluidStack.EMPTY;
    }

    static CompoundTag toTag(CompoundTag tag, DefaultedList<FluidStack> stacks) {
        return toTag(tag, stacks, true);
    }

    static CompoundTag toTag(CompoundTag tag, DefaultedList<FluidStack> stacks, boolean setIfEmpty) {
        ListTag listTag = new ListTag();

        for (int i = 0; i < stacks.size(); ++i) {
            FluidStack fluidStack = stacks.get(i);
            if (!fluidStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte) i);
                fluidStack.toTag(compoundTag);
                listTag.add(compoundTag);
            } else {
                CompoundTag compoundTag = new CompoundTag();
                FluidStack.EMPTY.toTag(compoundTag);
                listTag.add(i,compoundTag);
            }
        }

        if (!listTag.isEmpty() || setIfEmpty) {
            tag.put("Fluids", listTag);
        }

        return tag;
    }

    static void fromTag(CompoundTag tag, DefaultedList<FluidStack> stacks) {
        ListTag listTag = tag.getList("Fluids", 10);

        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 255;
            if (j < stacks.size()) {
                stacks.set(j, FluidStack.fromTag(compoundTag));
            }
        }

    }

    HashMap<Direction,Boolean> getFluidIO();

    DefaultedList<FluidStack> getFluids();

    int capacity();

    default int slots() {
        return getFluids().size();
    }

    default boolean isEmpty() {
        for (FluidStack stack : getFluids())
            if (!stack.isEmpty())
                return false;
        return true;
    }

    default FluidStack getStack(int slot) {
        return getFluids().get(slot);
    }

    default FluidStack removeStack(int slot, int amount) {
        FluidStack result = FluidInventory.splitStack(getFluids(), slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    default FluidStack removeStack(int slot) {
        return FluidInventory.removeStack(getFluids(), slot);

    }

    default int currentCapacity() {
        int x = 0;
        for (int i = 0; i < slots(); i++) {
            FluidStack stack = getStack(i);
            x += stack.isEmpty() ? 0 : stack.getAmount();
        }
        return x;
    }

    default void setStack(int slot, FluidStack stack) {
        int newCapacity = currentCapacity() + stack.getAmount();
        if (newCapacity > capacity()) {
            stack.setAmount(capacity() - currentCapacity());
        }
        getFluids().set(slot, stack);
    }

    default boolean canAdd(FluidStack stack) {
        if(stack.getAmount() + currentCapacity() > capacity())
            return false;
        return count(stack.getFluid()) > 0 || !slotsFilled();
    }

    default FluidStack add(FluidStack stack) {
        if(!canAdd(stack))
            return stack;

        for(int i = 0; i < slots(); i++) {
            if(getStack(i) == FluidStack.EMPTY) {
                setStack(i,stack);
                return FluidStack.EMPTY;
            } else if(getStack(i).getFluid() == stack.getFluid()) {
                getStack(i).increment(stack.getAmount());
                return FluidStack.EMPTY;
            }
        }

        return stack;
    }

    default boolean slotsFilled() {
        for(int i = 0; i < slots(); i++) {
            if(getStack(i).isEmpty())
                return false;
        }
        return true;
    }

    @Override
    default void clear() {
        getFluids().clear();
    }

    void markDirty();

    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    default void onOpen(PlayerEntity player) {
    }

    default void onClose(PlayerEntity player) {
    }

    default boolean isValid(int slot, FluidStack stack) {
        return (getStack(slot) == FluidStack.EMPTY || getStack(slot).getFluid() == stack.getFluid())
                && currentCapacity() + stack.getAmount() <= capacity();
    }

    default int count(Fluid fluid) {
        for (int j = 0; j < this.slots(); ++j) {
            FluidStack fluidStack = this.getStack(j);
            if (fluidStack.getFluid().equals(fluid)) {
                return fluidStack.getAmount();
            }
        }

        return 0;
    }

    default boolean containsAny(Set<Fluid> fluids) {
        for (int i = 0; i < this.slots(); ++i) {
            FluidStack fluidStack = this.getStack(i);
            if (fluids.contains(fluidStack.getFluid()) && fluidStack.getAmount() > 0) {
                return true;
            }
        }

        return false;
    }

    default int[] getAvailableSlots(Direction side)  {
        return IntStream.range(0,slots()).toArray();
    };

    default boolean canInsert(int slot, FluidStack stack, @Nullable Direction dir) {
        return this.isValid(slot,stack) && !getFluidIO().get(dir);
    }

    default boolean canExtract(int slot, FluidStack stack, Direction dir) {
        return getFluidIO().get(dir);
    }

}
