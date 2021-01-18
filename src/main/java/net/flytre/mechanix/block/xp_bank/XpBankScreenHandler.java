package net.flytre.mechanix.block.xp_bank;

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

public class XpBankScreenHandler extends ScreenHandler {


    private final PropertyDelegate propertyDelegate;
    private BlockPos pos;

    public XpBankScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId,playerInventory,new XpBankBlockEntity(), new ArrayPropertyDelegate(4));
        pos = buf.readBlockPos();
    }


    public XpBankScreenHandler(int syncId, PlayerInventory playerInventory, XpBankBlockEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.XP_BANK.getHandlerType(), syncId);

        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
        pos = BlockPos.ORIGIN;
    }

    public BlockPos getPos() {
        return pos;
    }


    public int getAmount() {
        return Formatter.unsplit(new int[]{propertyDelegate.get(0),propertyDelegate.get(1)});
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }


    public boolean getFluidTransferable(Direction direction) {
        HashMap<Direction,Boolean> itemMap = Formatter.intToHash(propertyDelegate.get(2));
        return itemMap.get(direction);
    }

    public int fluidButtonState(Direction direction) {
        return getFluidTransferable(direction) ? 0 : 1;
    }

    public boolean getSynced() {
        return propertyDelegate.get(3) == 1;
    }

    public PropertyDelegate getDelegate() {
        return this.propertyDelegate;
    }
}
