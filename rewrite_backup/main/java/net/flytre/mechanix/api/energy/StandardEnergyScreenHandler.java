package net.flytre.mechanix.api.energy;

import net.flytre.flytre_lib.common.util.Formatter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Screen handler for entities containing energy.
 */
public abstract class StandardEnergyScreenHandler<T extends StandardEnergyEntity> extends ScreenHandler {
    /**
     * The Property delegate.
     */
    protected final PropertyDelegate propertyDelegate;
    protected final T inventory;
    /**
     * The position of the block entity.
     */
    protected BlockPos pos;
    protected int panelType;
    protected int energyIO;
    protected boolean synced;


    public StandardEnergyScreenHandler(ScreenHandlerType<? extends StandardEnergyScreenHandler> type, int syncId, PlayerInventory playerInventory, Supplier<T> entityCreator, PacketByteBuf buf) {
        this(type, syncId, playerInventory, entityCreator.get(), new ArrayPropertyDelegate(24));
        this.pos = buf.readBlockPos();
        this.energyIO = buf.readInt();
        this.panelType = buf.readInt();
        this.synced = true;
    }


    /**
     * Instantiates a new Energy screen handler.
     *
     * @param type             the type you registered in ur registry class
     * @param syncId           the sync id - used by MC
     * @param playerInventory  the player inventory
     * @param entity           the energy entity
     * @param propertyDelegate the property delegate
     */
    public StandardEnergyScreenHandler(ScreenHandlerType<? extends StandardEnergyScreenHandler> type, int syncId, PlayerInventory playerInventory, T entity, PropertyDelegate propertyDelegate) {
        super(type, syncId);
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
        this.inventory = entity;
        this.pos = BlockPos.ORIGIN;
        constCommon(playerInventory, entity);
    }


    protected void constCommon(PlayerInventory playerInventory, T entity) {
        int o;
        int n;
        for (o = 0; o < 3; ++o) {
            for (n = 0; n < 9; ++n) {
                this.addSlot(new Slot(playerInventory, n + o * 9 + 9, 8 + n * 18, 84 + o * 18));
            }
        }

        for (o = 0; o < 9; ++o) {
            this.addSlot(new Slot(playerInventory, o, 8 + o * 18, 142));
        }
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
        return synced;
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
        return Formatter.unsplit(new int[]{propertyDelegate.get(0), propertyDelegate.get(1)});
    }

    /**
     * Gets the max energy the machine has.
     *
     * @return the max energy
     */
    public int getMaxEnergy() {
        return Math.max(Formatter.unsplit(new int[]{propertyDelegate.get(2), propertyDelegate.get(3)}), 1);
    }

    /**
     * Gets the panel mode - whether it controls energy or fluids/items.
     *
     * @return the panel mode
     */
    public int getPanelMode() {
        return panelType;
    }


    private boolean getEnergyTransferable(Direction direction) {
        Map<Direction, Boolean> energyMap = Formatter.intToMap(energyIO);
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


    public int itemButtonState(Direction dir) {
        return 0;
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return !(inventory instanceof Inventory) || ((Inventory) inventory).canPlayerUse(player);
    }
}
