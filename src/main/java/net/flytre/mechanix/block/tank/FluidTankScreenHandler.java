package net.flytre.mechanix.block.tank;

import net.flytre.mechanix.api.util.Formatter;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;

public class FluidTankScreenHandler extends ScreenHandler {
    private final PropertyDelegate propertyDelegate;
    private BlockPos pos;

    public FluidTankScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId,playerInventory,new FluidTankBlockEntity(), new ArrayPropertyDelegate(7));
        pos = buf.readBlockPos();
    }


    public FluidTankScreenHandler(int syncId, PlayerInventory playerInventory, FluidTankBlockEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.FLUID_TANK_SCREEN_HANDLER, syncId);

        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
        pos = BlockPos.ORIGIN;
    }

    public BlockPos getPos() {
        return pos;
    }


    public int getAmount() {
        return Formatter.unsplit(new int[]{propertyDelegate.get(1),propertyDelegate.get(2)});
    }

    public int getCapacity() {
        return Formatter.unsplit(new int[]{propertyDelegate.get(3),propertyDelegate.get(4)});
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }


    public boolean getFluidTransferable(Direction direction) {
        HashMap<Direction,Boolean> itemMap = Formatter.intToHash(propertyDelegate.get(5));
        return itemMap.get(direction);
    }

    public int fluidButtonState(Direction direction) {
        return getFluidTransferable(direction) ? 0 : 1;
    }

    public boolean getSynced() {
        return propertyDelegate.get(6) == 1;
    }

    public PropertyDelegate getDelegate() {
        return this.propertyDelegate;
    }
}
