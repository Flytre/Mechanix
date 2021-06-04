package net.flytre.mechanix.api.fluid.screen;

import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.fluid.screen.FluidHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.util.collection.DefaultedList;

public interface FluidHandlerListener extends ScreenHandlerListener {

    void onHandlerRegistered(FluidHandler handler, DefaultedList<FluidStack> stacks);

    void onSlotUpdate(FluidHandler handler, int slotId, FluidStack stack);
}
