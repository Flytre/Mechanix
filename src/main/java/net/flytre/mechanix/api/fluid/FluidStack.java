package net.flytre.mechanix.api.fluid;

import com.google.gson.JsonObject;
import net.flytre.flytre_lib.common.util.EnchantmentUtils;
import net.flytre.flytre_lib.common.util.Formatter;
import net.flytre.mechanix.api.fluid.mixin.FluidBlockAccessor;
import net.flytre.mechanix.util.FluidRegistry;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * ItemStacks but for fluids!
 */
public class FluidStack {

    /**
     * The empty fluid stack!
     */
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);
    public static final long UNITS_PER_BUCKET = 81000;
    public static final long UNITS_PER_EXPERIENCE = UNITS_PER_BUCKET / 250L;
    private final Fluid fluid;
    private long units;

    /**
     * Instantiates a new Fluid stack.
     *
     * @param fluid the fluid
     * @param units the number of units
     */
    public FluidStack(@NotNull Fluid fluid, long units) {
        this.fluid = fluid;
        this.units = units;
    }

    /**
     * Whether two fluid stacks are equal
     *
     * @param left  the left
     * @param right the right
     * @return the boolean
     */
    public static boolean areEqual(FluidStack left, FluidStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        } else {
            return !left.isEmpty() && !right.isEmpty() && left.isEqual(right);
        }
    }

    /**
     * Get a fluid stack from a tag
     *
     * @param tag the tag
     * @return the fluid stack
     */
    public static FluidStack fromTag(CompoundTag tag) {
        Fluid fluid = tag.contains("id") ? Registry.FLUID.get(Identifier.tryParse(tag.getString("id"))) : Fluids.EMPTY;
        long amount = tag.getLong("amount");
        return fluid != Fluids.EMPTY ? new FluidStack(fluid, amount) : FluidStack.EMPTY;
    }

    public static FluidStack fromJson(JsonObject object) {
        long amount = FluidStack.UNITS_PER_BUCKET;
        if (JsonHelper.hasPrimitive(object, "amount")) {
            amount = JsonHelper.getLong(object, "amount");
        } else if (JsonHelper.hasElement(object, "amount")) {
            JsonFraction fraction = JsonFraction.fromJson(object.getAsJsonObject("amount"));
            amount = (long) (((double) fraction.getNumerator() / (double) fraction.getDenominator()) * FluidStack.UNITS_PER_BUCKET);
        }
        String attempt = JsonHelper.getString(object, "fluid");
        Identifier fluid = new Identifier(attempt);
        return new FluidStack(Registry.FLUID.getOrEmpty(fluid).orElseThrow(() -> new IllegalStateException("Fluid: " + attempt + " does not exist")), amount);
    }

    public static FluidStack fromPacket(PacketByteBuf buf) {
        Identifier fluid = buf.readIdentifier();
        Fluid f = Registry.FLUID.getOrEmpty(fluid).orElseThrow(() -> new IllegalStateException("Fluid: " + fluid + " does not exist"));
        long amount = buf.readLong();
        return new FluidStack(f, amount);
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public long getAmount() {
        return units;
    }

    /**
     * Sets amount.
     *
     * @param units the number of units
     */
    public void setAmount(long units) {
        this.units = units;
    }

    public double getBuckets() {
        return (double) units / UNITS_PER_BUCKET;
    }

    /**
     * Split fluid stack into two stacks with the new stack containing up to |amount| fluid.
     *
     * @param amount the amount
     * @return the fluid stack
     */
    public FluidStack split(long amount) {
        long i = Math.min(amount, this.units);
        FluidStack stack = this.copy();
        stack.setAmount(i);
        this.decrement(i);
        return stack;
    }

    /**
     * Whether the stack is empty.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        if (this == EMPTY) {
            return true;
        } else if (this.getFluid() != Fluids.EMPTY) {
            return this.units <= 0;
        } else {
            return true;
        }
    }

    /**
     * Copy fluid stack.
     *
     * @return the fluid stack
     */
    public FluidStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            return new FluidStack(this.fluid, this.units);
        }
    }

    /**
     * Decrement amount.
     *
     * @param units the number of units
     */
    public void decrementAmount(long units) {
        this.units = Math.max(0, this.units - units);
    }

    private boolean isEqual(FluidStack stack) {
        return this.units == stack.units && this.fluid == stack.fluid;
    }

    public String toString() {
        return this.units + " " + Registry.FLUID.getId(this.getFluid());
    }

    /**
     * Increment.
     *
     * @param amount the amount
     */
    public void increment(long amount) {
        this.setAmount(this.units + amount);
    }

    /**
     * Decrement.
     *
     * @param amount the amount
     */
    public void decrement(long amount) {
        this.increment(-amount);
    }

    /**
     * Gets fluid.
     *
     * @return the fluid
     */
    public Fluid getFluid() {
        return fluid;
    }

    /**
     * Can increment boolean.
     *
     * @param amount the amount
     * @param cap    the cap
     * @return the boolean
     */
    public boolean canIncrement(long amount, long cap) {
        return this.units + amount <= cap;
    }


    /**
     * Store a fluid stack in a tag
     *
     * @param tag the tag
     * @return the compound tag
     */
    public CompoundTag toTag(CompoundTag tag) {
        if (this.fluid != Fluids.EMPTY)
            tag.putString("id", Registry.FLUID.getId(fluid).toString());
        tag.putLong("amount", this.units);
        return tag;
    }


    public List<Text> toTooltip(boolean multiline) {
        ArrayList<Text> tooltip = new ArrayList<>();
        for (FluidBlock block : FluidBlocks.fluidBlocks) {
            FlowableFluid fluid = ((FluidBlockAccessor) block).getFluid();
            if (fluid == getFluid()) {
                if (multiline) {
                    MutableText line = new TranslatableText(block.getTranslationKey());
                    line = line.setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                    tooltip.add(line);
                    line = new LiteralText(Formatter.formatNumber((double) getAmount() / UNITS_PER_BUCKET, "B "));
                    line = line.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                    tooltip.add(line);

                    if (fluid instanceof FluidTooltipData)
                        ((FluidTooltipData) fluid).addTooltipInfo(this, tooltip);

                    if (MinecraftClient.getInstance().options.advancedItemTooltips) {
                        Identifier id = Registry.FLUID.getId(fluid);
                        line = new LiteralText(id.toString());
                        line = line.setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                        tooltip.add(line);
                    }
                    tooltip.add(Formatter.getModNameToolTip(Registry.FLUID.getId(getFluid()).getNamespace()));
                } else {
                    MutableText line = new LiteralText(Formatter.formatNumber((double) getAmount() / UNITS_PER_BUCKET, "B ")).append(new TranslatableText(block.getTranslationKey()));
                    line = line.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                    tooltip.add(line);
                }
                break;
            }
        }
        return tooltip;
    }

    public void toPacket(PacketByteBuf packet) {
        Identifier fluid = Registry.FLUID.getId(getFluid());
        packet.writeIdentifier(fluid);
        packet.writeLong(getAmount());
    }

    /**
     * Ingredient::test, check if the stack's amount exceeds this stack's amount and is of the same fluid
     */
    public boolean test(FluidStack stack) {
        return stack.getFluid() == getFluid() && stack.getAmount() >= getAmount();
    }
}
