package net.flytre.mechanix.base.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public class FluidStack {
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);
    private final Fluid fluid;
    private int millibuckets;

    public FluidStack(@NotNull Fluid fluid, int millibuckets) {
        this.fluid = fluid;
        this.millibuckets = millibuckets;
    }

    public static boolean areEqual(FluidStack left, FluidStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        } else {
            return !left.isEmpty() && !right.isEmpty() && left.isEqual(right);
        }
    }

    public static FluidStack fromTag(CompoundTag tag) {
        Fluid fluid = tag.contains("id") ? Registry.FLUID.get(Identifier.tryParse(tag.getString("id"))) : Fluids.EMPTY;
        int amount = tag.getInt("amount");
        return fluid != Fluids.EMPTY ? new FluidStack(fluid, amount) : FluidStack.EMPTY;
    }

    public int getAmount() {
        return millibuckets;
    }

    public void setAmount(int millibuckets) {
        this.millibuckets = millibuckets;
    }

    public FluidStack split(int amount) {
        int i = Math.min(amount, this.millibuckets);
        FluidStack stack = this.copy();
        stack.setAmount(i);
        this.decrement(i);
        return stack;
    }

    public boolean isEmpty() {
        if (this == EMPTY) {
            return true;
        } else if (this.getFluid() != Fluids.EMPTY) {
            return this.millibuckets <= 0;
        } else {
            return true;
        }
    }

    public FluidStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            return new FluidStack(this.fluid, this.millibuckets);
        }
    }

    public void decrementAmount(int millibuckets) {
        this.millibuckets = Math.max(0, this.millibuckets - millibuckets);
    }

    private boolean isEqual(FluidStack stack) {
        return this.millibuckets == stack.millibuckets && this.fluid == stack.fluid;
    }

    public String toString() {
        return this.millibuckets + " " + this.getFluid();
    }

    public void increment(int amount) {
        this.setAmount(this.millibuckets + amount);
    }

    public void decrement(int amount) {
        this.increment(-amount);
    }

    public Fluid getFluid() {
        return fluid;
    }

    public boolean canIncrement(int amount, int cap) {
        return this.millibuckets + amount <= cap;
    }

    public CompoundTag toTag(CompoundTag tag) {
        if (this.fluid != Fluids.EMPTY)
            tag.putString("id", Registry.FLUID.getId(fluid).toString());
        tag.putInt("amount", this.millibuckets);
        return tag;
    }
}
