package net.flytre.mechanix.base;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;

public abstract class EnergyScreenHandler  extends ScreenHandler {
    protected final PropertyDelegate propertyDelegate;
    protected BlockPos pos;

    public EnergyScreenHandler(ScreenHandlerType<? extends EnergyScreenHandler> type, int syncId, PlayerInventory playerInventory, EnergyEntity entity, PropertyDelegate propertyDelegate) {
        super(type, syncId);

        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean getSynced() {
        return propertyDelegate.get(0) == 1;
    }


    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    public int getEnergy() {
        return DelegateFixer.energy(propertyDelegate);
    }

    public int getMaxEnergy() {
        return DelegateFixer.maxEnergy(propertyDelegate);
    }

    public int getPanelMode() {
        return propertyDelegate.get(7);
    }


    public boolean getEnergyTransferable(Direction direction) {
        HashMap<Direction,Boolean> energyMap = DelegateFixer.intToHash(propertyDelegate.get(1));
        return energyMap.get(direction);
    }

    public int energyButtonState(Direction direction) {
        return getEnergyTransferable(direction) ? 0 : 1;
    }



    public boolean getItemTransferable(Direction direction) {
        HashMap<Direction,Boolean> itemMap = DelegateFixer.intToHash(propertyDelegate.get(2));
        return itemMap.get(direction);
    }

    public int itemButtonState(Direction direction) {
        return getItemTransferable(direction) ? 0 : 1;
    }


}
