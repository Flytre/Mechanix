package net.flytre.mechanix.api.upgrade;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.util.collection.DefaultedList;

public interface UpgradeHandlerListener extends ScreenHandlerListener {

    void onHandlerRegistered(UpgradeHandler handler, DefaultedList<ItemStack> stacks);


    void onSlotUpdate(UpgradeHandler handler, int slotId, ItemStack stack);

}
