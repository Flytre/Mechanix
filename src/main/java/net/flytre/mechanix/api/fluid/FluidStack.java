package net.flytre.mechanix.api.fluid;

import com.google.gson.JsonObject;
import net.flytre.mechanix.api.util.Formatter;
import net.flytre.mechanix.mixin.FluidBlockMixin;
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
    private final Fluid fluid;
    private int mB;

    /**
     * Instantiates a new Fluid stack.
     *
     * @param fluid        the fluid
     * @param mB the millibuckets (1000 = bucket)
     */
    public FluidStack(@NotNull Fluid fluid, int mB) {
        this.fluid = fluid;
        this.mB = mB;
    }

    /**
     * Whether two fluid stacks are equal
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
        int amount = tag.getInt("amount");
        return fluid != Fluids.EMPTY ? new FluidStack(fluid, amount) : FluidStack.EMPTY;
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public int getAmount() {
        return mB;
    }

    /**
     * Sets amount.
     *
     * @param millibuckets the millibuckets
     */
    public void setAmount(int millibuckets) {
        this.mB = millibuckets;
    }

    /**
     * Split fluid stack into two stacks with the new stack containing up to |amount| fluid.
     *
     * @param amount the amount
     * @return the fluid stack
     */
    public FluidStack split(int amount) {
        int i = Math.min(amount, this.mB);
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
            return this.mB <= 0;
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
            return new FluidStack(this.fluid, this.mB);
        }
    }

    /**
     * Decrement amount.
     *
     * @param mB the millibuckets
     */
    public void decrementAmount(int mB) {
        this.mB = Math.max(0, this.mB - mB);
    }

    private boolean isEqual(FluidStack stack) {
        return this.mB == stack.mB && this.fluid == stack.fluid;
    }

    public String toString() {
        return this.mB + " " + this.getFluid();
    }

    /**
     * Increment.
     *
     * @param amount the amount
     */
    public void increment(int amount) {
        this.setAmount(this.mB + amount);
    }

    /**
     * Decrement.
     *
     * @param amount the amount
     */
    public void decrement(int amount) {
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
    public boolean canIncrement(int amount, int cap) {
        return this.mB + amount <= cap;
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
        tag.putInt("amount", this.mB);
        return tag;
    }

    public static FluidStack fromJson(JsonObject object) {
        int amount = 1000;
        if(JsonHelper.hasPrimitive(object,"amount")) {
            amount = JsonHelper.getInt(object,"amount");
        }
        String attempt = JsonHelper.getString(object, "fluid");
        Identifier fluid = new Identifier(attempt);
        return new FluidStack(Registry.FLUID.getOrEmpty(fluid).orElseThrow(() -> new IllegalStateException("Fluid: " + attempt + " does not exist")),amount);
    }

    public List<Text> toTooltip(boolean multiline) {
        ArrayList<Text> tooltip = new ArrayList<>();
        for (FluidBlock block : FluidBlocks.fluidBlocks) {
            FlowableFluid fluid = ((FluidBlockMixin) block).getFluid();
            if(fluid == getFluid()) {
                if(multiline) {
                    MutableText line = new TranslatableText(block.getTranslationKey());
                    line = line.setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                    tooltip.add(line);
                    line = new LiteralText(Formatter.formatNumber(getAmount()/1000.0, "B "));
                    line = line.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                    tooltip.add(line);
                    if(MinecraftClient.getInstance().options.advancedItemTooltips) {
                        Identifier id = Registry.FLUID.getId(fluid);
                        line = new LiteralText(id.toString());
                        line = line.setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                        tooltip.add(line);
                    }
                    tooltip.add(Formatter.getModNameToolTip(Registry.FLUID.getId(getFluid()).getNamespace()));
                } else {
                    MutableText line = new LiteralText(Formatter.formatNumber(getAmount()/1000.0, "B ")).append(new TranslatableText(block.getTranslationKey()));
                    line = line.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                    tooltip.add(line);
                }
                break;
            }
        }
        return tooltip;
    }

    public static FluidStack fromPacket(PacketByteBuf buf) {
        Identifier fluid = buf.readIdentifier();
        Fluid f = Registry.FLUID.getOrEmpty(fluid).orElseThrow(() -> new IllegalStateException("Fluid: " + fluid + " does not exist"));
        int amount = buf.readInt();
        return new FluidStack(f,amount);
    }

    public void toPacket(PacketByteBuf packet) {
        Identifier fluid = Registry.FLUID.getId(getFluid());
        packet.writeIdentifier(fluid);
        packet.writeInt(getAmount());
    }
}
