package net.flytre.mechanix.block.generator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.energy.ItemEnergyScreenHandler;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class GeneratorHandler extends ItemEnergyScreenHandler<GeneratorEntity> {

    public GeneratorHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(MachineRegistry.GENERATOR.getHandlerType(), syncId, playerInventory, GeneratorEntity::new, buf);
    }

    public GeneratorHandler(int syncId, PlayerInventory playerInventory, GeneratorEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.GENERATOR.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);
    }

    @Override
    protected void constCommon(PlayerInventory playerInventory, GeneratorEntity entity) {
        this.addSlot(new GeneratorFuelSlot(this, entity, 0, 88, 37));
        super.constCommon(playerInventory, entity);
    }

    protected boolean isFuel(ItemStack itemStack) {
        return GeneratorEntity.canUseAsFuel(itemStack);
    }


    @Environment(EnvType.CLIENT)
    public int getFuelProgress() {
        int i = this.propertyDelegate.get(5);
        if (i == 0)
            i = 200;
        return this.propertyDelegate.get(4) * 13 / i;
    }

    @Environment(EnvType.CLIENT)
    public boolean isBurning() {
        return this.propertyDelegate.get(4) > 0;
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index != 0) {
                if (this.isFuel(itemStack2)) {
                    if (!this.insertItem(itemStack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 28) {
                    if (!this.insertItem(itemStack2, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37 && !this.insertItem(itemStack2, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 1, 37, false)) {
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
