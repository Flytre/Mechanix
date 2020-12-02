package net.flytre.mechanix.api.fluid;

import net.flytre.mechanix.api.util.Formatter;
import net.flytre.mechanix.mixin.FluidBlockMixin;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.*;
import net.minecraft.util.Clearable;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;


/**
 * Like inventory, but for fluids
 */
public interface FluidInventory extends Clearable {


    /**
     * Same as Inventories::splitStack
     */
    static FluidStack splitStack(List<FluidStack> stacks, int slot, int amount) {
        return slot >= 0 && slot < stacks.size() && !stacks.get(slot).isEmpty() && amount > 0 ? stacks.get(slot).split(amount) : FluidStack.EMPTY;
    }

    /**
     * Same as Inventories::removeFluidStack
     */
    static FluidStack removeFluidStack(List<FluidStack> stacks, int slot) {
        return slot >= 0 && slot < stacks.size() ? stacks.set(slot, FluidStack.EMPTY) : FluidStack.EMPTY;
    }

    /**
     * Save a Fluid Inventory to a compound tag. You can use the root tag, no need to put a custom key and then
     * call this method on that.
     */
    static CompoundTag toTag(CompoundTag tag, DefaultedList<FluidStack> stacks) {
        return toTag(tag, stacks, true);
    }

    /**
     * Same as other toTag.
     */
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

    /**
     * Fluid Inventory from a tag
     */
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

    /**
     * Gets fluid io configuration, like EnergyEntity#ioMode.
     *
     * @return the fluid io
     */
    HashMap<Direction,Boolean> getFluidIO();

    /**
     * Gets the list of fluids which make up the fluid. (Inventories are fundamentally DefaultedLists in Minecraft)
     *
     * @return the fluids
     */
    DefaultedList<FluidStack> getFluids();

    /**
     * Capacity int.
     *
     * @return the int
     */
    int capacity();

    /**
     * Slot capacity int.
     *
     * @return the int
     */
    default int slotCapacity() {
        return capacity();
    }

    /**
     * Slots int.
     *
     * @return the int
     */
    default int slots() {
        return getFluids().size();
    }

    /**
     * Is the inventory empty?.
     *
     * @return the boolean
     */
    default boolean isFluidInventoryEmpty() {
        for (FluidStack stack : getFluids())
            if (!stack.isEmpty())
                return false;
        return true;
    }

    /**
     * Gets the stack in a slot.
     *
     * @param slot the slot
     * @return the fluid stack
     */
    default FluidStack getFluidStack(int slot) {
        return getFluids().get(slot);
    }

    /**
     * Remove the stack in a slot.
     *
     * @param slot   the slot
     * @param amount the amount
     * @return the fluid stack
     */
    default FluidStack removeFluidStack(int slot, int amount) {
        FluidStack result = FluidInventory.splitStack(getFluids(), slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    /**
     * Remove the stack in a slot.
     *
     * @param slot the slot
     * @return the fluid stack
     */
    default FluidStack removeFluidStack(int slot) {
        return FluidInventory.removeFluidStack(getFluids(), slot);

    }

    /**
     * Current capacity - sum capacities of stacks.
     *
     * @return the int
     */
    default int currentCapacity() {
        int x = 0;
        for (int i = 0; i < slots(); i++) {
            FluidStack stack = getFluidStack(i);
            x += stack.isEmpty() ? 0 : stack.getAmount();
        }
        return x;
    }

    /**
     * Sets the stack in a slot.
     *
     * @param slot  the slot
     * @param stack the stack
     */
    default void setStack(int slot, FluidStack stack) {
        int newCapacity = currentCapacity() + stack.getAmount();
        if (newCapacity > capacity()) {
            stack.setAmount(capacity() - currentCapacity());
        }
        getFluids().set(slot, stack);
        if(stack.getAmount() > slotCapacity())
            stack.setAmount(slotCapacity());
    }

    /**
     * Checks if there's space, not foolproof.
     *
     * @param stack the stack
     * @return the boolean
     */
    default boolean canAdd(FluidStack stack) {
        if(stack.getAmount() + currentCapacity() > capacity())
            return false;
        return count(stack.getFluid()) > 0 || !slotsFilled();
    }

    /**
     * Add fluid stack to the inventory.
     *
     * @param stack the stack
     * @return the remainder of what's left afterward
     */
    default FluidStack add(FluidStack stack) {
        if(!canAdd(stack))
            return stack;

        for(int i = 0; i < slots(); i++) {
            if(!isValid(i,stack))
                continue;
            if(getFluidStack(i) == FluidStack.EMPTY) {
                setStack(i,stack);
                return FluidStack.EMPTY;
            } else if(getFluidStack(i).getFluid() == stack.getFluid()) {
                getFluidStack(i).increment(stack.getAmount());
                return FluidStack.EMPTY;
            }
        }
        return stack;
    }

    /**
     * Whether all slots are occupied.
     *
     * @return the boolean
     */
    default boolean slotsFilled() {
        for(int i = 0; i < slots(); i++) {
            if(getFluidStack(i).isEmpty())
                return false;
        }
        return true;
    }

    @Override
    default void clear() {
        getFluids().clear();
    }

    /**
     * Mark dirty.
     */
    void markDirty();

    /**
     * Unused as its not needed.
     *
     * @param player the player
     * @return the boolean
     */
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    /**
     * On open.
     *
     * @param player the player
     */
    default void onOpen(PlayerEntity player) {
    }

    /**
     * On close.
     *
     * @param player the player
     */
    default void onClose(PlayerEntity player) {
    }

    /**
     * Checks if a stack can be added to a slot
     *
     * @param slot  the slot
     * @param stack the stack
     * @return the boolean
     */
    default boolean isValid(int slot, FluidStack stack) {
        return (getFluidStack(slot) == FluidStack.EMPTY ||
                (getFluidStack(slot).getFluid() == stack.getFluid()  && stack.getAmount() + getFluidStack(slot).getAmount() <= slotCapacity()))
                && currentCapacity() + stack.getAmount() <= capacity();
    }

    /**
     * Count the amount of a fluid in the inv.
     *
     * @param fluid the fluid
     * @return the int
     */
    default int count(Fluid fluid) {
        for (int j = 0; j < this.slots(); ++j) {
            FluidStack fluidStack = this.getFluidStack(j);
            if (fluidStack.getFluid().equals(fluid)) {
                return fluidStack.getAmount();
            }
        }

        return 0;
    }

    /**
     * Self explanatory.
     *
     * @param fluids the fluids
     * @return the boolean
     */
    default boolean containsAnyFluid(Set<Fluid> fluids) {
        for (int i = 0; i < this.slots(); ++i) {
            FluidStack fluidStack = this.getFluidStack(i);
            if (fluids.contains(fluidStack.getFluid()) && fluidStack.getAmount() > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get available fluid slots.
     *
     * @param side the side
     * @return the int [ ]
     */
    default int[] getAvailableFluidSlots(Direction side)  {
        return IntStream.range(0,slots()).toArray();
    };

    /**
     * Whether a stack can be inserted into a slot. Strongest check and should always be used.
     *
     * @param slot  the slot
     * @param stack the stack
     * @param dir   the dir
     * @return the boolean
     */
    default boolean canInsert(int slot, FluidStack stack, @Nullable Direction dir) {
        return this.isValid(slot,stack) && !getFluidIO().get(dir);
    }

    /**
     * Same as insert but for extracting
     *
     * @param slot  the slot
     * @param stack the stack
     * @param dir   the dir
     * @return the boolean
     */
    default boolean canExtract(int slot, FluidStack stack, Direction dir) {
        return getFluidIO().get(dir);
    }

    /**
     * Add a tooltip showing the fluids the item stack contains. Check fluid tank loot table and implementation.
     *
     * @param stack   the stack
     * @param tooltip the tooltip
     */
    static void toToolTip(ItemStack stack, List<Text> tooltip) {

        if(stack == null || tooltip == null || stack.getTag() == null)
            return;

        if(!stack.getTag().contains("BlockEntityTag"))
            return;

        CompoundTag blockEntityTag = stack.getTag().getCompound("BlockEntityTag");
        DefaultedList<FluidStack> stacks = DefaultedList.ofSize(blockEntityTag.getList("Fluids",10).size(),FluidStack.EMPTY);
        fromTag(blockEntityTag,stacks);

        for(FluidStack fluidStack : stacks) {
            for (FluidBlock block : FluidBlocks.fluidBlocks) {
                FlowableFluid fluid = ((FluidBlockMixin) block).getFluid();
                if(fluid == fluidStack.getFluid()) {
                    MutableText line = new LiteralText(Formatter.formatNumber(fluidStack.getAmount()/1000.0, "B ")).append(new TranslatableText(block.getTranslationKey()));
                    line = line.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                    tooltip.add(line);
                }
            }
        }
    }
}
