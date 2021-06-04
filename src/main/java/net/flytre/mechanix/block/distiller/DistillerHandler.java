package net.flytre.mechanix.block.distiller;

import net.flytre.mechanix.api.energy.ItemEnergyScreenHandler;
import net.flytre.mechanix.api.fluid.screen.FluidSlot;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;

public class DistillerHandler extends ItemEnergyScreenHandler<DistillerEntity> {


    public DistillerHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(MachineRegistry.DISTILLER.getHandlerType(), syncId, playerInventory, DistillerEntity::new, buf);
    }

    public DistillerHandler(int syncId, PlayerInventory playerInventory, DistillerEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.DISTILLER.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);
    }

    @Override
    protected void constCommon(PlayerInventory playerInventory, DistillerEntity entity) {
        this.addSlot(new FluidSlot(entity, 0, 42, 13, false));
        this.addSlot(new FluidSlot(entity, 1, 75, 13, false));
        this.addSlot(new FluidSlot(entity, 2, 141, 13, false));
        super.constCommon(playerInventory, entity);
    }

    public double operationProgress() {
        return getPropertyDelegate().get(5) == 0 ? 0 : getPropertyDelegate().get(4) / (double) getPropertyDelegate().get(5);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return super.simpleTransferSlot(player, index);
    }
}
