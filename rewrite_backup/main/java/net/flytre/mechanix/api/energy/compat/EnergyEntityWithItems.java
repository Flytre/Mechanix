package net.flytre.mechanix.api.energy.compat;

import net.flytre.flytre_lib.common.inventory.IOType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

import java.util.Map;

/**
 * A block entity with both energy and items
 */
public interface EnergyEntityWithItems extends EnergyEntity {


    static void fromTag(EnergyEntityWithItems entity, CompoundTag tag) {
        EnergyEntity.fromTag(entity, tag);
        entity.setItemIO(IOType.intToMap(tag.getInt("IOMode")));
    }

    static void toTag(EnergyEntityWithItems entity, CompoundTag tag) {
        EnergyEntity.toTag(entity, tag);
        tag.putInt("IOMode", IOType.mapToInt(entity.getItemIO()));

    }


    default void setIOMode(IOType up, IOType down, IOType north, IOType east, IOType south, IOType west) {
        getItemIO().put(Direction.UP, up);
        getItemIO().put(Direction.DOWN, down);
        getItemIO().put(Direction.NORTH, north);
        getItemIO().put(Direction.EAST, east);
        getItemIO().put(Direction.SOUTH, south);
        getItemIO().put(Direction.WEST, west);
    }

    /**
     * Which sides can input / output items. Setting a side to false means it can only input, and true means only output
     */
    Map<Direction, IOType> getItemIO();

    void setItemIO(Map<Direction, IOType> itemIO);

    @Override
    default int getPanelType() {
        return 1;
    }
}
