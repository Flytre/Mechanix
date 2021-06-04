package net.flytre.mechanix.api.energy;

import net.flytre.mechanix.block.cell.EnergyCellEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

/**
 * Simple Energy Handler for generators
 */
public class SimpleEnergyScreenHandler extends StandardEnergyScreenHandler<StandardEnergyEntity> {

    public SimpleEnergyScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(MachineRegistry.SIMPLE_SCREEN_HANDLER, syncId, playerInventory, EnergyCellEntity::new, buf);
    }

    public SimpleEnergyScreenHandler(int syncId, PlayerInventory playerInventory, StandardEnergyEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.SIMPLE_SCREEN_HANDLER, syncId, playerInventory, entity, propertyDelegate);
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
