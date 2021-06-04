package net.flytre.mechanix.api.fluid;

import com.google.gson.JsonObject;
import net.flytre.flytre_lib.common.util.Formatter;
import net.flytre.mechanix.mixin.FluidBlockAccessor;
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
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, Fraction.ZERO);
    private final Fluid fluid;
    private Fraction amount;

    /**
     * Instantiates a new Fluid stack.
     *
     * @param fluid  the fluid
     * @param amount A fraction representing the exact amount of fluid
     */
    public FluidStack(@NotNull Fluid fluid, Fraction amount) {
        this.fluid = fluid;
        this.amount = amount;
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
        Fraction amount = Fraction.fromTag(tag.getCompound("amount"));
        return fluid != Fluids.EMPTY ? new FluidStack(fluid, amount) : FluidStack.EMPTY;
    }

    public static FluidStack fromJson(JsonObject object) {
        Fraction amount = object.has("amount") ? Fraction.fromJson(object.getAsJsonObject("amount")) : Fraction.ONE;
        String attempt = JsonHelper.getString(object, "fluid");
        Identifier fluid = new Identifier(attempt);
        return new FluidStack(Registry.FLUID.getOrEmpty(fluid).orElseThrow(() -> new IllegalStateException("Fluid: " + attempt + " does not exist")), amount);
    }

    public static FluidStack fromPacket(PacketByteBuf buf) {
        Identifier fluid = buf.readIdentifier();
        Fluid f = Registry.FLUID.getOrEmpty(fluid).orElseThrow(() -> new IllegalStateException("Fluid: " + fluid + " does not exist"));
        Fraction amount = Fraction.fromPacket(buf);
        return new FluidStack(f, amount);
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public Fraction getAmount() {
        return amount;
    }

    /**
     * Sets amount.
     *
     * @param fraction the the fraction
     */
    public void setAmount(Fraction fraction) {
        this.amount = fraction;
    }

    /**
     * Split fluid stack into two stacks with the new stack containing up to |fraction| fluid.
     *
     * @param fraction the fraction amount to split off
     * @return the split off fluid stack
     */
    public FluidStack split(Fraction fraction) {
        Fraction toTransfer = this.amount.min(fraction);
        FluidStack stack = this.copy();
        stack.setAmount(toTransfer);
        this.decrement(toTransfer);
        return stack;
    }

    /**
     * Whether the stack is empty.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        if (this == EMPTY)
            return true;
        else if (this.getFluid() != Fluids.EMPTY)
            return this.amount.isZero();
        else
            return true;
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
            return new FluidStack(this.fluid, this.amount);
        }
    }


    private boolean isEqual(FluidStack stack) {
        return this.amount.equals(stack.amount) && this.fluid == stack.fluid;
    }

    public String toString() {
        return this.amount + " " + this.getFluid();
    }

    /**
     * Increment fraction.
     *
     * @param fraction the amount
     */
    public void increment(Fraction fraction) {
        this.setAmount(Fraction.add(this.amount, fraction));
    }

    /**
     * Decrement.
     *
     * @param fraction the fraction
     */
    public void decrement(Fraction fraction) {
        this.increment(fraction.invert());
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
     * @param fraction the fraction
     * @param cap      the maximum fraction the fraction's value cannot exceed
     * @return the boolean
     */
    public boolean canIncrement(Fraction fraction, Fraction cap) {
        return Fraction.add(this.amount, fraction).isLessOrEqual(cap);
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
        tag.put("amount", this.amount.toTag(new CompoundTag()));
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
                    line = new LiteralText(this.amount.toString() + " B");
                    line = line.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                    tooltip.add(line);
                    if (MinecraftClient.getInstance().options.advancedItemTooltips) {
                        Identifier id = Registry.FLUID.getId(fluid);
                        line = new LiteralText(id.toString());
                        line = line.setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                        tooltip.add(line);
                    }
                    tooltip.add(Formatter.getModNameToolTip(Registry.FLUID.getId(getFluid()).getNamespace()));
                } else {
                    MutableText line = new LiteralText(this.amount.toString() + " B").append(new TranslatableText(block.getTranslationKey()));
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
        this.amount.toPacket(packet);
    }
}
