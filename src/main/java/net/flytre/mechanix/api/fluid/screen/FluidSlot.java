package net.flytre.mechanix.api.fluid.screen;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class FluidSlot {

    public final FluidInventory inventory;
    public final int x;
    public final int y;
    private final int index;
    public int id;
    public boolean compact;

    public FluidSlot(FluidInventory inventory, int index, int x, int y) {
        this(inventory, index, x, y, false);
    }

    public FluidSlot(FluidInventory inventory, int index, int x, int y, boolean compact) {
        this.inventory = inventory;
        this.index = index;
        this.x = x;
        this.y = y;
        this.compact = compact;
    }

    public void onStackChanged(FluidStack originalItem, FluidStack fluidStack) {
        long i = fluidStack.getAmount() - originalItem.getAmount();
        if (i > 0) {
            this.onCrafted(fluidStack, i);
        }

    }

    protected void onCrafted(FluidStack stack, long amount) {
    }

    protected void onTake(long amount) {
    }

    protected void onCrafted(FluidStack stack) {
    }

    public FluidStack onTakeItem(PlayerEntity player, FluidStack stack) {
        this.markDirty();
        return stack;
    }


    public boolean canInsert(FluidStack stack) {
        return true;
    }

    public FluidStack getStack() {
        return this.inventory.getFluidStack(this.index);
    }

    public void setStack(FluidStack stack) {
        this.inventory.setStack(this.index, stack);
        this.markDirty();
    }

    public long getCapacity() {
        return this.inventory.slotCapacity();
    }

    public boolean hasStack() {
        return !this.getStack().isEmpty();
    }

    public void markDirty() {
        this.inventory.markDirty();
    }

    public long getMaxFluidAmount() {
        return this.inventory.slotCapacity();
    }

    @Nullable
    @Environment(EnvType.CLIENT)
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return null;
    }

    public FluidStack takeStack(long amount) {
        return this.inventory.removeFluidStack(this.index, amount);
    }

    public boolean canTakeItems(PlayerEntity playerEntity) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public boolean doDrawHoveringEffect() {
        return true;
    }

    public int getIndex() {
        return index;
    }
}
