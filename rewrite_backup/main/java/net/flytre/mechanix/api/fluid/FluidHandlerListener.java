package net.flytre.mechanix.api.fluid;

import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.util.collection.DefaultedList;

/**
 * TODO: IMPLEMENTATIONS
 */
public interface FluidHandlerListener extends ScreenHandlerListener {

    void onHandlerRegistered(FluidHandler handler, DefaultedList<FluidStack> stacks);

    void onSlotUpdate(FluidHandler handler, int slotId, FluidStack stack);
}
