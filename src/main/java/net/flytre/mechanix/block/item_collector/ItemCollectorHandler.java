package net.flytre.mechanix.block.item_collector;

import net.flytre.flytre_lib.common.inventory.OutputSlot;
import net.flytre.flytre_lib.common.inventory.filter.FilterInventory;
import net.flytre.mechanix.api.upgrade.UpgradeHandler;
import net.flytre.mechanix.api.upgrade.UpgradeItem;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;

public class ItemCollectorHandler extends UpgradeHandler {

    private final FilterInventory inv;
    protected boolean synced;
    private BlockPos pos;
    private int filterType;
    private boolean matchMod;
    private boolean matchNbt;

    public ItemCollectorHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new ItemCollectorEntity());
        pos = buf.readBlockPos();
        filterType = buf.readInt();
        matchMod = buf.readBoolean();
        matchNbt = buf.readBoolean();
        synced = true;
    }


    public ItemCollectorHandler(int syncId, PlayerInventory playerInventory, ItemCollectorEntity entity) {
        super(MachineRegistry.ITEM_COLLECTOR.getHandlerType(), syncId);
        this.inv = entity.getFilter();
        pos = BlockPos.ORIGIN;

        inv.onOpen(playerInventory.player);
        int m;
        int l;
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 3; ++l) {
                this.addSlot(new Slot(inv, l + m * 3, 26 + l * 18, 18 + m * 18));
            }
        }
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 3; ++l) {
                this.addSlot(new OutputSlot(entity, l + m * 3, 98 + l * 18, 18 + m * 18));
            }
        }
        addInventorySlots(playerInventory);
        addStandardUpgradeSlots(entity);
    }

    @Override
    public ItemStack onSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity) {
        if (slotId >= 0) {
            Inventory inventory = getSlot(slotId).inventory;
            boolean isFilterInventory = inventory instanceof FilterInventory;
            boolean isPlayerInventory = inventory instanceof PlayerInventory;
            ItemStack stack = getSlot(slotId).getStack();
            if (isFilterInventory) {
                inv.removeStack(slotId);
                getSlot(slotId).inventory.markDirty();
                return ItemStack.EMPTY;
            } else if (isPlayerInventory && ((PlayerInventory) inventory).getCursorStack().isEmpty() && !(stack.getItem() instanceof UpgradeItem)) {
                HashSet<Item> items = new HashSet<>();
                items.add(stack.getItem());
                if (!inv.containsAny(items)) {
                    inv.put(stack);
                    getSlot(slotId).inventory.markDirty();
                    return ItemStack.EMPTY;
                } else
                    return stack;
            }
        }
        return super.onSlotClick(slotId, clickData, actionType, playerEntity);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < 18) {
                if (!this.insertItem(itemStack2, 18, 54, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    public BlockPos getPos() {
        return pos;
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inv.onClose(player);
    }

    public boolean getSynced() {
        return synced;
    }

    public int getFilterType() {
        return filterType;
    }

    public int getModMatch() {
        return matchMod ? 1 : 0;
    }

    public int getNbtMatch() {
        return matchNbt ? 1 : 0;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        return false;
    }
}
