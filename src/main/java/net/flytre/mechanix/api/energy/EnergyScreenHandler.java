package net.flytre.mechanix.api.energy;

import net.flytre.mechanix.api.util.Formatter;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;

/**
 * Screen handler for entities containing energy.
 */
public abstract class EnergyScreenHandler  extends ScreenHandler {
    /**
     * The Property delegate.
     */
    protected final PropertyDelegate propertyDelegate;
    /**
     * The position of the block entity.
     */
    protected BlockPos pos;

    /**
     * Instantiates a new Energy screen handler.
     *
     * @param type             the type you registered in ur registry class
     * @param syncId           the sync id - used by MC
     * @param playerInventory  the player inventory
     * @param entity           the energy entity
     * @param propertyDelegate the property delegate
     */
    public EnergyScreenHandler(ScreenHandlerType<? extends EnergyScreenHandler> type, int syncId, PlayerInventory playerInventory, EnergyEntity entity, PropertyDelegate propertyDelegate) {
        super(type, syncId);

        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
    }

    /**
     * Gets pos.
     *
     * @return the pos
     */
    public BlockPos getPos() {
        return pos;
    }

    /**
     * Gets whether the handler is synced.
     *
     * @return the synced
     */
    public boolean getSynced() {
        return propertyDelegate.get(0) == 1;
    }


    /**
     * Gets the property delegate.
     *
     * @return the property delegate
     */
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    /**
     * Gets the energy the machine has.
     *
     * @return the energy
     */
    public int getEnergy() {
        return Formatter.energy(propertyDelegate);
    }

    /**
     * Gets the max energy the machine has.
     *
     * @return the max energy
     */
    public int getMaxEnergy() {
        return Formatter.maxEnergy(propertyDelegate);
    }

    /**
     * Gets the panel mode - whether it controls energy or fluids/items.
     *
     * @return the panel mode
     */
    public int getPanelMode() {
        return propertyDelegate.get(7);
    }



    private boolean getEnergyTransferable(Direction direction) {
        HashMap<Direction,Boolean> energyMap = Formatter.intToHash(propertyDelegate.get(1));
        return energyMap.get(direction);
    }

    /**
     * Determines what frame each button should have (orange/blue) in the panel in the right.
     *
     * @param direction the direction
     * @return the int
     */
    public int energyButtonState(Direction direction) {
        return getEnergyTransferable(direction) ? 0 : 1;
    }



    private boolean getItemTransferable(Direction direction) {
        HashMap<Direction,Boolean> itemMap = Formatter.intToHash(propertyDelegate.get(2));
        return itemMap.get(direction);
    }

    /**
     * Same as energy but for items.
     *
     * @param direction the direction
     * @return the int
     */
    public int itemButtonState(Direction direction) {
        return getItemTransferable(direction) ? 0 : 1;
    }


}
