package net.flytre.mechanix.block.cell;

import net.flytre.mechanix.base.EnergyScreenHandler;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class EnergyCellScreenHandler extends EnergyScreenHandler {

    public EnergyCellScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId,playerInventory,new EnergyCellEntity(), new ArrayPropertyDelegate(12));
        pos = buf.readBlockPos();
    }

    public EnergyCellScreenHandler(int syncId, PlayerInventory playerInventory, EnergyCellEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.ENERGY_CELL_SCREEN_HANDLER,syncId,playerInventory,entity,propertyDelegate);

        pos = BlockPos.ORIGIN;

        int o;
        int n;
        for(o = 0; o < 3; ++o) {
            for(n = 0; n < 9; ++n) {
                this.addSlot(new Slot(playerInventory, n + o * 9 + 9, 8 + n * 18, 84 + o * 18));
            }
        }

        for(o = 0; o < 9; ++o) {
            this.addSlot(new Slot(playerInventory, o, 8 + o * 18, 142));
        }


    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
