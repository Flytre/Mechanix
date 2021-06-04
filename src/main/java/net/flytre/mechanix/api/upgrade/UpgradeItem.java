package net.flytre.mechanix.api.upgrade;

import net.flytre.mechanix.util.MiscRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class UpgradeItem extends Item {

    public UpgradeItem(int maxCount) {
        super(new Item.Settings().group(MiscRegistry.TAB).maxCount(maxCount));
    }


    public abstract boolean isValid(UpgradeInventory inv, ItemStack stack, int slot);
}
