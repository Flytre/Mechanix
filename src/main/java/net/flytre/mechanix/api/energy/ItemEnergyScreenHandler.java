package net.flytre.mechanix.api.energy;

import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.upgrade.UpgradeInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.Direction;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Standard Energy Handler but for Energy Entities that also contain an inventory, like an Alloyer
 *
 * @param <T>
 */
public abstract class ItemEnergyScreenHandler<T extends StandardEnergyEntityWithItems> extends StandardEnergyScreenHandler<T> {

    protected int itemIO;

    public ItemEnergyScreenHandler(ScreenHandlerType<? extends StandardEnergyScreenHandler> type, int syncId, PlayerInventory playerInventory, Supplier<T> entityCreator, PacketByteBuf buf) {
        super(type, syncId, playerInventory, entityCreator, buf);
        this.itemIO = buf.readInt();
    }

    public ItemEnergyScreenHandler(ScreenHandlerType<? extends StandardEnergyScreenHandler> type, int syncId, PlayerInventory playerInventory, T entity, PropertyDelegate propertyDelegate) {
        super(type, syncId, playerInventory, entity, propertyDelegate);
    }

    private IOType getItemTransferable(Direction direction) {
        Map<Direction, IOType> itemMap = IOType.intToMap(itemIO);
        return itemMap.get(direction);
    }

    /**
     * Same as energy but for items.
     *
     * @param direction the direction
     * @return the int
     */
    public int itemButtonState(Direction direction) {
        return getItemTransferable(direction).getIndex();
    }

    @Override
    protected void constCommon(PlayerInventory playerInventory, T entity) {
        if (entity instanceof UpgradeInventory)
            addStandardUpgradeSlots((UpgradeInventory) entity);
        super.constCommon(playerInventory, entity);
    }
}
