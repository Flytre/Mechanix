package net.flytre.mechanix.api.upgrade;


import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.fluid.screen.FluidHandler;
import net.flytre.mechanix.api.fluid.screen.FluidHandlerListener;
import net.flytre.mechanix.api.upgrade.mixin.ScreenHandlerAccessor;
import net.flytre.mechanix.api.upgrade.mixin.ServerPlayerEntityMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Superclass for all ScreenHandlers that need Upgrade Slots
 * Use Upgrade#addUpgradeSlot
 */
public abstract class UpgradeHandler extends FluidHandler {


    public final List<UpgradeSlot> upgradeSlots = Lists.newArrayList();
    private final DefaultedList<ItemStack> trackedUpgradeSlots = DefaultedList.of();
    private final ScreenHandlerType<?> type;

    protected UpgradeHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
        this.type = type;
    }

    public static boolean canInsertItemIntoSlot(@Nullable UpgradeSlot slot, ItemStack stack, boolean allowOverflow) {
        boolean bl = slot == null || !slot.hasStack();
        if (!bl && stack.isItemEqualIgnoreDamage(slot.getStack()) && ItemStack.areTagsEqual(slot.getStack(), stack)) {
            return slot.getStack().getCount() + (allowOverflow ? 0 : stack.getCount()) <= stack.getMaxCount();
        } else {
            return bl;
        }
    }

    protected void addUpgradeSlot(UpgradeSlot slot) {
        slot.id = this.upgradeSlots.size();
        this.upgradeSlots.add(slot);
        this.trackedUpgradeSlots.add(ItemStack.EMPTY);
    }

    public DefaultedList<ItemStack> getUpgradeStacks() {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();

        for (UpgradeSlot upgradeSlot : this.upgradeSlots)
            defaultedList.add(upgradeSlot.getStack());

        return defaultedList;
    }

    private List<ScreenHandlerListener> listeners() {
        return ((ScreenHandlerAccessor) this).getListeners();
    }

    public void sendContentUpdates() {

        super.sendContentUpdates();

        int j;
        for (j = 0; j < this.upgradeSlots.size(); ++j) {
            ItemStack itemStack = this.upgradeSlots.get(j).getStack();
            ItemStack trackedStack = this.trackedUpgradeSlots.get(j);
            if (!ItemStack.areEqual(trackedStack, itemStack)) {
                ItemStack itemStack3 = itemStack.copy();
                this.trackedUpgradeSlots.set(j, itemStack3);
                for (ScreenHandlerListener screenHandlerListener : this.listeners()) {
                    if (screenHandlerListener instanceof UpgradeHandlerListener)
                        ((UpgradeHandlerListener) screenHandlerListener).onSlotUpdate(this, j, itemStack3);
                }
            }
        }

    }

    @Override
    public void addListener(ScreenHandlerListener listener) {
        if (!this.listeners().contains(listener)) {
            this.listeners().add(listener);
            listener.onHandlerRegistered(this, this.getStacks());
            if (listener instanceof FluidHandlerListener)
                ((FluidHandlerListener) listener).onHandlerRegistered(this, this.getFluidStacks());
            else if (listener instanceof UpgradeHandler)
                ((UpgradeHandlerListener) listener).onHandlerRegistered(this, this.getUpgradeStacks());
            this.sendContentUpdates();
        }
    }

    public UpgradeSlot getUpgradeSlot(int index) {
        return this.upgradeSlots.get(index);
    }

    public void setUpgradeStackInSlot(int slot, ItemStack stack) {
        this.getUpgradeSlot(slot).setStack(stack);
    }

    @Environment(EnvType.CLIENT)
    public void updateUpgradeSlotStacks(List<ItemStack> stacks) {
        for (int i = 0; i < stacks.size(); ++i) {
            this.getUpgradeSlot(i).setStack(stacks.get(i));
        }

    }

    public ItemStack onUpgradeSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity) {
        try {
            ItemStack stack = this.slotClickHelper(slotId, clickData, actionType, playerEntity);
            if (playerEntity instanceof ServerPlayerEntity)
                ((UpgradeHandlerListener) playerEntity).onHandlerRegistered(this, getUpgradeStacks());
            return stack;
        } catch (Exception var8) {
            CrashReport crashReport = CrashReport.create(var8, "Mechanix Upgrade Container click");
            CrashReportSection crashReportSection = crashReport.addElement("Click info");
            crashReportSection.add("Menu Type", () -> this.type != null ? Objects.requireNonNull(Registry.SCREEN_HANDLER.getId(this.type)).toString() : "<no type>");
            crashReportSection.add("Menu Class", () -> this.getClass().getCanonicalName());
            crashReportSection.add("Slot Count", this.slots.size());
            crashReportSection.add("Slot", slotId);
            crashReportSection.add("Button", clickData);
            crashReportSection.add("Type", actionType);
            throw new CrashException(crashReport);
        }
    }

    public ItemStack transferUpgradeSlot(PlayerEntity player, int index) {
        UpgradeSlot slot = this.upgradeSlots.get(index);
        ItemStack stack = ItemStack.EMPTY;
        if (slot != null && slot.hasStack()) {
            stack = slot.getStack();
            if (!this.insertItem(stack, 0, slots.size(), false))
                return ItemStack.EMPTY;

        }
        return stack;
    }


    private ItemStack slotClickHelper(int slotId, int clickData, SlotActionType slotActionType, PlayerEntity playerEntity) {
        ItemStack itemStack = ItemStack.EMPTY;
        PlayerInventory playerInventory = playerEntity.inventory;
        ItemStack itemStack7;
        ItemStack itemStack8;
        int n;
        int l;
        UpgradeSlot slot4;
        int q;
        if ((slotActionType == SlotActionType.PICKUP || slotActionType == SlotActionType.QUICK_MOVE) && (clickData == 0 || clickData == 1)) {
            if (slotId == -999) {
                if (!playerInventory.getCursorStack().isEmpty()) {
                    if (clickData == 0) {
                        playerEntity.dropItem(playerInventory.getCursorStack(), true);
                        playerInventory.setCursorStack(ItemStack.EMPTY);
                    }

                    if (clickData == 1) {
                        playerEntity.dropItem(playerInventory.getCursorStack().split(1), true);
                    }
                }
            } else if (slotActionType == SlotActionType.QUICK_MOVE) {
                if (slotId < 0) {
                    return ItemStack.EMPTY;
                }

                slot4 = this.upgradeSlots.get(slotId);
                if (slot4 == null || !slot4.canTakeItems(playerEntity)) {
                    return ItemStack.EMPTY;
                }

                for (itemStack7 = this.transferUpgradeSlot(playerEntity, slotId); !itemStack7.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot4.getStack(), itemStack7); itemStack7 = this.transferUpgradeSlot(playerEntity, slotId)) {
                    itemStack = itemStack7.copy();
                }
            } else {
                if (slotId < 0) {
                    return ItemStack.EMPTY;
                }

                slot4 = this.upgradeSlots.get(slotId);
                if (slot4 != null) {
                    itemStack7 = slot4.getStack();
                    itemStack8 = playerInventory.getCursorStack();
                    if (!itemStack7.isEmpty()) {
                        itemStack = itemStack7.copy();
                    }

                    if (itemStack7.isEmpty()) {
                        q = clickData == 0 ? itemStack8.getCount() : 1;
                        if (!itemStack8.isEmpty() && slot4.canInsert(itemStack8, q)) {
                            if (q > slot4.getMaxItemCount(itemStack8)) {
                                q = slot4.getMaxItemCount(itemStack8);
                            }

                            slot4.setStack(itemStack8.split(q));
                        }
                    } else if (slot4.canTakeItems(playerEntity)) {
                        if (itemStack8.isEmpty()) {
                            if (itemStack7.isEmpty()) {
                                slot4.setStack(ItemStack.EMPTY);
                                playerInventory.setCursorStack(ItemStack.EMPTY);
                            } else {
                                q = clickData == 0 ? itemStack7.getCount() : (itemStack7.getCount() + 1) / 2;
                                playerInventory.setCursorStack(slot4.takeStack(q));
                                if (itemStack7.isEmpty()) {
                                    slot4.setStack(ItemStack.EMPTY);
                                }

                                slot4.onTakeItem(playerEntity, playerInventory.getCursorStack());
                            }
                        } else if (slot4.canInsert(itemStack8, clickData == 0 ? itemStack8.getCount() : 1)) {
                            if (canStacksCombine(itemStack7, itemStack8)) {
                                q = clickData == 0 ? itemStack8.getCount() : 1;
                                if (q > slot4.getMaxItemCount(itemStack8) - itemStack7.getCount()) {
                                    q = slot4.getMaxItemCount(itemStack8) - itemStack7.getCount();
                                }

                                if (q > itemStack8.getMaxCount() - itemStack7.getCount()) {
                                    q = itemStack8.getMaxCount() - itemStack7.getCount();
                                }

                                itemStack8.decrement(q);
                                itemStack7.increment(q);
                            } else if (itemStack8.getCount() <= slot4.getMaxItemCount(itemStack8)) {
                                slot4.setStack(itemStack8);
                                playerInventory.setCursorStack(itemStack7);
                            }
                        } else if (itemStack8.getMaxCount() > 1 && canStacksCombine(itemStack7, itemStack8) && !itemStack7.isEmpty()) {
                            q = itemStack7.getCount();
                            if (q + itemStack8.getCount() <= itemStack8.getMaxCount()) {
                                itemStack8.increment(q);
                                itemStack7 = slot4.takeStack(q);
                                if (itemStack7.isEmpty()) {
                                    slot4.setStack(ItemStack.EMPTY);
                                }

                                slot4.onTakeItem(playerEntity, playerInventory.getCursorStack());
                            }
                        }
                    }

                    slot4.markDirty();
                }
            }
        } else if (slotActionType == SlotActionType.SWAP) {
            slot4 = this.upgradeSlots.get(slotId);
            itemStack7 = playerInventory.getStack(clickData);
            itemStack8 = slot4.getStack();
            if (!itemStack7.isEmpty() || !itemStack8.isEmpty()) {
                if (itemStack7.isEmpty()) {
                    if (slot4.canTakeItems(playerEntity)) {
                        playerInventory.setStack(clickData, itemStack8);
//                        slot4.onTake(itemStack8.getCount());
                        slot4.setStack(ItemStack.EMPTY);
                        slot4.onTakeItem(playerEntity, itemStack8);
                    }
                } else if (itemStack8.isEmpty()) {
                    if (slot4.canInsert(itemStack7)) {
                        q = slot4.getMaxItemCount(itemStack7);
                        if (itemStack7.getCount() > q) {
                            slot4.setStack(itemStack7.split(q));
                        } else {
                            slot4.setStack(itemStack7);
                            playerInventory.setStack(clickData, ItemStack.EMPTY);
                        }
                    }
                } else if (slot4.canTakeItems(playerEntity) && slot4.canInsert(itemStack7)) {
                    q = slot4.getMaxItemCount(itemStack7);
                    if (itemStack7.getCount() > q) {
                        slot4.setStack(itemStack7.split(q));
                        slot4.onTakeItem(playerEntity, itemStack8);
                        if (!playerInventory.insertStack(itemStack8)) {
                            playerEntity.dropItem(itemStack8, true);
                        }
                    } else {
                        slot4.setStack(itemStack7);
                        playerInventory.setStack(clickData, itemStack8);
                        slot4.onTakeItem(playerEntity, itemStack8);
                    }
                }
            }
        } else if (slotActionType == SlotActionType.CLONE && playerEntity.abilities.creativeMode && playerInventory.getCursorStack().isEmpty() && slotId >= 0) {
            slot4 = this.upgradeSlots.get(slotId);
            if (slot4 != null && slot4.hasStack()) {
                itemStack7 = slot4.getStack().copy();
                itemStack7.setCount(itemStack7.getMaxCount());
                playerInventory.setCursorStack(itemStack7);
            }
        } else if (slotActionType == SlotActionType.THROW && playerInventory.getCursorStack().isEmpty() && slotId >= 0) {
            slot4 = this.upgradeSlots.get(slotId);
            if (slot4 != null && slot4.hasStack() && slot4.canTakeItems(playerEntity)) {
                itemStack7 = slot4.takeStack(clickData == 0 ? 1 : slot4.getStack().getCount());
                slot4.onTakeItem(playerEntity, itemStack7);
                playerEntity.dropItem(itemStack7, true);
            }
        } else if (slotActionType == SlotActionType.PICKUP_ALL && slotId >= 0) {
            slot4 = this.upgradeSlots.get(slotId);
            itemStack7 = playerInventory.getCursorStack();
            if (!itemStack7.isEmpty() && (slot4 == null || !slot4.hasStack() || !slot4.canTakeItems(playerEntity))) {
                l = clickData == 0 ? 0 : this.upgradeSlots.size() - 1;
                q = clickData == 0 ? 1 : -1;

                for (int w = 0; w < 2; ++w) {
                    for (int x = l; x >= 0 && x < this.upgradeSlots.size() && itemStack7.getCount() < itemStack7.getMaxCount(); x += q) {
                        UpgradeSlot slot9 = this.upgradeSlots.get(x);
                        if (slot9.hasStack() && canInsertItemIntoSlot(slot9, itemStack7, true) && slot9.canTakeItems(playerEntity)) {
                            ItemStack itemStack14 = slot9.getStack();
                            if (w != 0 || itemStack14.getCount() != itemStack14.getMaxCount()) {
                                n = Math.min(itemStack7.getMaxCount() - itemStack7.getCount(), itemStack14.getCount());
                                ItemStack itemStack15 = slot9.takeStack(n);
                                itemStack7.increment(n);
                                if (itemStack15.isEmpty()) {
                                    slot9.setStack(ItemStack.EMPTY);
                                }

                                slot9.onTakeItem(playerEntity, itemStack15);
                            }
                        }
                    }
                }
            }

            this.sendContentUpdates();
        }

        return itemStack;
    }


    protected void addStandardUpgradeSlots(UpgradeInventory entity) {
        this.addUpgradeSlot(new UpgradeSlot(entity, 0, 191, 84));
        this.addUpgradeSlot(new UpgradeSlot(entity, 1, 211, 84));
        this.addUpgradeSlot(new UpgradeSlot(entity, 2, 191, 104));
        this.addUpgradeSlot(new UpgradeSlot(entity, 3, 211, 104));
    }


}
