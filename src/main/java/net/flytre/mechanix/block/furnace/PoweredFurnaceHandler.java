package net.flytre.mechanix.block.furnace;

import net.flytre.mechanix.api.energy.ItemEnergyScreenHandler;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class PoweredFurnaceHandler extends ItemEnergyScreenHandler<PoweredFurnaceEntity> {

    private World world;


    public PoweredFurnaceHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(MachineRegistry.POWERED_FURNACE.getHandlerType(), syncId, playerInventory, PoweredFurnaceEntity::new, buf);
    }

    public PoweredFurnaceHandler(int syncId, PlayerInventory playerInventory, PoweredFurnaceEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.POWERED_FURNACE.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);
    }

    @Override
    protected void constCommon(PlayerInventory playerInventory, PoweredFurnaceEntity entity) {
        this.addSlot(new Slot(entity, 0, 61, 36));
        this.addSlot(new FurnaceOutputSlot(playerInventory.player, entity, 1, 123, 36));
        this.world = playerInventory.player.world;
        super.constCommon(playerInventory, entity);
    }

    public double operationProgress() {
        return getPropertyDelegate().get(5) == 0 ? 0 : getPropertyDelegate().get(4) / (double) getPropertyDelegate().get(5);
    }

    private boolean isSmeltable(ItemStack itemStack) {
        return this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(itemStack), this.world).isPresent();
    }

    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 1) {
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onStackChanged(itemStack2, itemStack);
            } else if (index != 0) {
                if (this.isSmeltable(itemStack2)) {
                    if (!this.insertItem(itemStack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 29) {
                    if (!this.insertItem(itemStack2, 30, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 38 && !this.insertItem(itemStack2, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 2, 38, false)) {
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


}
