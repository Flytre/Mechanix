package net.flytre.mechanix.api.fluid.screen;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.fluid.mixin.ScreenHandlerAccessor;
import net.flytre.mechanix.api.fluid.mixin.BucketItemAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


/**
 * Superclass for all ScreenHandlers that need Fluid Slots
 * Use FluidHandler#addSlot
 */
public abstract class FluidHandler extends ScreenHandler {

    public final List<FluidSlot> fluidSlots = Lists.newArrayList();
    private final DefaultedList<FluidStack> trackedFluidStacks = DefaultedList.of();


    protected FluidHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    protected FluidSlot addSlot(FluidSlot slot) {
        slot.id = this.fluidSlots.size();
        this.fluidSlots.add(slot);
        this.trackedFluidStacks.add(FluidStack.EMPTY);
        return slot;
    }

    public DefaultedList<FluidStack> getFluidStacks() {
        DefaultedList<FluidStack> defaultedList = DefaultedList.of();

        for (FluidSlot fluidSlot : this.fluidSlots) {
            defaultedList.add(fluidSlot.getStack());
        }
        return defaultedList;
    }

    private List<ScreenHandlerListener> listeners() {
        return ((ScreenHandlerAccessor) this).getListeners();
    }


    @Override
    public void addListener(ScreenHandlerListener listener) {
        if (!this.listeners().contains(listener)) {
            this.listeners().add(listener);
            listener.onHandlerRegistered(this, this.getStacks());
            if (listener instanceof FluidHandlerListener)
                ((FluidHandlerListener) listener).onHandlerRegistered(this, this.getFluidStacks());
            this.sendContentUpdates();
        }
    }


    public void sendContentUpdates() {

        super.sendContentUpdates();

        int j;
        for (j = 0; j < this.fluidSlots.size(); ++j) {
            FluidStack fluidStack = this.fluidSlots.get(j).getStack();
            FluidStack fluidStack2 = this.trackedFluidStacks.get(j);
            if (!FluidStack.areEqual(fluidStack2, fluidStack)) {
                FluidStack fluidStack3 = fluidStack.copy();
                this.trackedFluidStacks.set(j, fluidStack3);

                for (ScreenHandlerListener screenHandlerListener : this.listeners()) {
                    if (screenHandlerListener instanceof FluidHandlerListener)
                        ((FluidHandlerListener) screenHandlerListener).onSlotUpdate(this, j, fluidStack3);
                }
            }
        }

    }

    public FluidSlot getFluidSlot(int index) {
        return this.fluidSlots.get(index);
    }

    public void setFluidStackInSlot(int slot, FluidStack stack) {
        this.getFluidSlot(slot).setStack(stack);
    }

    @Environment(EnvType.CLIENT)
    public void updateFluidSlotStacks(List<FluidStack> stacks) {
        for (int i = 0; i < stacks.size(); ++i) {
            this.getFluidSlot(i).setStack(stacks.get(i));
        }

    }

    public FluidStack onFluidSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity) {


        if (actionType != SlotActionType.PICKUP && actionType != SlotActionType.QUICK_MOVE) {
            //UNSUPPORTED OPERATION
            return FluidStack.EMPTY;
        }


        FluidSlot slot = fluidSlots.get(slotId);
        FluidStack slotStack = slot.getStack();
        ItemStack cursorStack = playerEntity.inventory.getCursorStack();

        if (cursorStack.getItem() instanceof BucketItem && ((BucketItemAccessor) cursorStack.getItem()).getFluid() == Fluids.EMPTY) {
            do {
                if (slotStack.getAmount() >= FluidStack.UNITS_PER_BUCKET & slotStack.getFluid() != Fluids.EMPTY) {
                    playerEntity.inventory.setCursorStack(ItemUsage.method_30012(cursorStack, playerEntity, new ItemStack(slotStack.getFluid().getBucketItem())));
                    slotStack.decrement(FluidStack.UNITS_PER_BUCKET);
                }
            } while (actionType == SlotActionType.QUICK_MOVE && !cursorStack.isEmpty() && slotStack.getAmount() >= FluidStack.UNITS_PER_BUCKET & slotStack.getFluid() != Fluids.EMPTY);
            slot.markDirty();
            return slotStack;
        } else if (cursorStack.getItem() instanceof BucketItem) {
            BucketItem item = (BucketItem) cursorStack.getItem();
            Fluid fluid = ((BucketItemAccessor) item).getFluid();


            FluidStack temp = new FluidStack(fluid, FluidStack.UNITS_PER_BUCKET);
            if (slot.inventory.isValidExternal(slot.getIndex(), temp)) {
                if (slot.getStack().isEmpty()) {
                    slot.setStack(temp.copy());
                } else
                    slotStack.increment(FluidStack.UNITS_PER_BUCKET);
                playerEntity.inventory.setCursorStack(!playerEntity.abilities.creativeMode ? new ItemStack(Items.BUCKET) : cursorStack);
                if (actionType == SlotActionType.QUICK_MOVE) {
                    List<Slot> matchingSlots = new ArrayList<>();
                    slots.forEach(i -> {
                        if (i.getStack().getItem() instanceof BucketItem && ((BucketItemAccessor) i.getStack().getItem()).getFluid() == fluid)
                            matchingSlots.add(i);
                    });
                    for (Slot matchingSlot : matchingSlots) {
                        if (slot.inventory.isValidExternal(slot.getIndex(), temp)) {
                            slot.getStack().increment(FluidStack.UNITS_PER_BUCKET);
                            matchingSlot.setStack(!playerEntity.abilities.creativeMode ? new ItemStack(Items.BUCKET) : matchingSlot.getStack());
                        } else
                            break;
                    }
                }
                slot.markDirty();
                return temp.copy();
            }
        }

        return FluidStack.EMPTY;
    }


    public ItemStack simpleTransferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            stack = slot.getStack();
            if (index <= 26) {
                if (!this.insertItem(stack, 27, 36, false))
                    return ItemStack.EMPTY;
            } else {
                if (!this.insertItem(stack, 0, 27, false))
                    return ItemStack.EMPTY;
            }
        }
        return stack;
    }


    public void addInventorySlots(PlayerInventory playerInventory) {
        int o;
        int n;
        for (o = 0; o < 3; ++o) {
            for (n = 0; n < 9; ++n) {
                this.addSlot(new Slot(playerInventory, n + o * 9 + 9, 8 + n * 18, 84 + o * 18));
            }
        }

        for (o = 0; o < 9; ++o) {
            this.addSlot(new Slot(playerInventory, o, 8 + o * 18, 142));
        }
    }
}
