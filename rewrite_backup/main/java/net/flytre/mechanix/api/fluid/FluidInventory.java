package net.flytre.mechanix.api.fluid;


import net.flytre.flytre_lib.common.inventory.IOType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;


/**
 * Like inventory, but for fluids
 * <p>
 * Internal Methods are meant to be used only by the inventory - for example, the hydrator gathering water or the
 * distiller crafting molten fluid. Usually these have much looser criteria: For example, your machine can accept a
 * recipe output into the recipe output slot internally, but a pipe cannot directly add fluids to that slot externally.
 * <p>
 * External Methods are interfaces to be used by other blocks - for example, a fluid pipe figuring out what it can add
 * or extract from an inventory or a playing clicking with a bucket on a tank. This also includes canInsert()
 * and canExtract().
 */
public interface FluidInventory extends Clearable {


    /**
     * Same as Inventories::splitStack
     */
    static FluidStack splitStack(List<FluidStack> stacks, int slot, Fraction amount) {
        return slot >= 0 && slot < stacks.size() && !stacks.get(slot).isEmpty() && amount.isPositive() ? stacks.get(slot).split(amount) : FluidStack.EMPTY;
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

        Set<Integer> visited = new HashSet<>();

        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 255;
            if (j < stacks.size()) {
                stacks.set(j, FluidStack.fromTag(compoundTag));
                visited.add(j);
            }
        }

        for (int i = 0; i < stacks.size(); ++i) {
            if (!visited.contains(i))
                stacks.set(i, FluidStack.EMPTY);
        }

    }

    /**
     * Add a tooltip showing the fluids the item stack contains. Check fluid tank loot table and implementation.
     *
     * @param stack   the stack
     * @param tooltip the tooltip
     */
    static void toToolTip(ItemStack stack, List<Text> tooltip) {

        if (stack == null || tooltip == null || stack.getTag() == null)
            return;

        if (!stack.getTag().contains("BlockEntityTag"))
            return;

        CompoundTag blockEntityTag = stack.getTag().getCompound("BlockEntityTag");
        DefaultedList<FluidStack> stacks = DefaultedList.ofSize(blockEntityTag.getList("Fluids", 10).size(), FluidStack.EMPTY);
        fromTag(blockEntityTag, stacks);

        for (FluidStack fluidStack : stacks) {
            List<Text> list = fluidStack.toTooltip(false);
            tooltip.addAll(list);
        }
    }

    /**
     * Gets fluid io configuration, like EnergyEntity#ioMode.
     *
     * @return the fluid io
     */
    Map<Direction, IOType> getFluidIO();

    /**
     * Gets the list of fluids which make up the fluid inventory. (Inventories are fundamentally DefaultedLists in Minecraft)
     *
     * @return the fluids
     */
    DefaultedList<FluidStack> getFluids();

    /**
     * Capacity int.
     *
     * @return the int
     */
    Fraction capacity();

    /**
     * Slot capacity int.
     *
     * @return the int
     */
    default Fraction slotCapacity() {
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
     * Remove x amount of the stack in a slot.
     *
     * @param slot   the slot
     * @param amount the amount
     * @return the fluid stack
     */
    default FluidStack removeFluidStack(int slot, Fraction amount) {
        FluidStack result = FluidInventory.splitStack(getFluids(), slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    /**
     * Fully remove the stack in a slot.
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
    default Fraction currentCapacity() {
        Fraction x = Fraction.ZERO;
        for (int i = 0; i < slots(); i++) {
            FluidStack stack = getFluidStack(i);
            if (!stack.isEmpty())
                x = Fraction.add(x, stack.getAmount());
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
        Fraction newCapacity = Fraction.subtract(Fraction.add(currentCapacity(), stack.getAmount()), getFluidStack(slot) != FluidStack.EMPTY ? getFluidStack(slot).getAmount() : Fraction.ZERO);
        if (newCapacity.isGreater(capacity())) {
            stack.setAmount(Fraction.subtract(capacity(), currentCapacity()));
        }
        getFluids().set(slot, stack);
        if (stack.getAmount().isGreater(slotCapacity()))
            stack.setAmount(slotCapacity());
    }

    /**
     * Checks if there's space, not foolproof.
     *
     * @param stack the stack
     * @return the boolean
     */
    default boolean canAdd(FluidStack stack) {
        if (Fraction.add(stack.getAmount(), currentCapacity()).isGreater(capacity()))
            return false;
        return count(stack.getFluid()).isPositive() || !slotsFilled();
    }

    /**
     * Add fluid stack to the inventory.
     *
     * @param stack the stack
     * @return the remainder of what's left afterward
     */
    default FluidStack addExternal(FluidStack stack) {
        if (!canAdd(stack))
            return stack;

        for (int i = 0; i < slots(); i++) {
            if (!isValidExternal(i, stack))
                continue;
            if (getFluidStack(i) == FluidStack.EMPTY) {
                setStack(i, stack);
                return FluidStack.EMPTY;
            } else if (getFluidStack(i).getFluid() == stack.getFluid()) {
                getFluidStack(i).increment(stack.getAmount());
                return FluidStack.EMPTY;
            }
        }
        return stack;
    }

    /**
     * Add fluid stack to the inventory internally.
     *
     * @param stack the stack
     * @return the remainder of what's left afterward
     */
    default FluidStack addInternal(FluidStack stack) {
        if (!canAdd(stack))
            return stack;

        for (int i = 0; i < slots(); i++) {
            if (!isValidInternal(i, stack))
                continue;
            if (getFluidStack(i) == FluidStack.EMPTY) {
                setStack(i, stack);
                return FluidStack.EMPTY;
            } else if (getFluidStack(i).getFluid() == stack.getFluid()) {
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
        for (int i = 0; i < slots(); i++) {
            if (getFluidStack(i).isEmpty())
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
     * Checks if a stack can be added to a slot internally - aka by recipes or anything that doesn't interface with the outside world
     *
     * @param slot  the slot
     * @param stack the stack
     * @return the boolean
     */
    default boolean isValidInternal(int slot, FluidStack stack) {
        return (
                (getFluidStack(slot) == FluidStack.EMPTY && stack.getAmount().isLessOrEqual(slotCapacity())) ||
                        (getFluidStack(slot).getFluid() == stack.getFluid() && Fraction.add(stack.getAmount(), getFluidStack(slot).getAmount()).isLessOrEqual(slotCapacity()))
        )
                && Fraction.add(currentCapacity(), stack.getAmount()).isLessOrEqual(capacity());
    }

    /**
     * Checks if a stack can be added to a slot externally - for example, by using buckets, pipes, etc.
     *
     * @param slot  the slot
     * @param stack the stack
     * @return the boolean
     */
    default boolean isValidExternal(int slot, FluidStack stack) {
        return isValidInternal(slot, stack);
    }

    /**
     * Count the amount of a fluid in the inv.
     *
     * @param fluid the fluid
     * @return the int
     */
    default Fraction count(Fluid fluid) {
        Fraction x = Fraction.ZERO;
        for (int j = 0; j < this.slots(); ++j) {
            FluidStack fluidStack = this.getFluidStack(j);
            if (fluidStack.getFluid().equals(fluid) && !fluidStack.isEmpty())
                x = Fraction.add(x, fluidStack.getAmount());
        }
        return x;
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
            if (fluids.contains(fluidStack.getFluid()) && fluidStack.getAmount().isPositive()) {
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
    default int[] getAvailableFluidSlots(Direction side) {
        return IntStream.range(0, slots()).toArray();
    }

    /**
     * Whether a stack can be inserted into a slot. Strongest check and should always be used.
     *
     * @param slot  the slot
     * @param stack the stack
     * @param dir   the dir
     * @return the boolean
     */
    default boolean canInsert(int slot, FluidStack stack, @Nullable Direction dir) {
        return this.isValidExternal(slot, stack) && getFluidIO().get(dir).canInsert();
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
        return getFluidIO().get(dir).canExtract();
    }

    default boolean hasNoFluids() {
        for (int i = 0; i < slots(); i++) {
            FluidStack stack = getFluidStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
