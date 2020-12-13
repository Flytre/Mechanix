package net.flytre.mechanix.api.energy;

import net.flytre.mechanix.api.util.Formatter;
import net.minecraft.util.math.Direction;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

public class TREnergyStorageImpl implements EnergyStorage {

    private final EnergyEntity entity;

    public TREnergyStorageImpl(EnergyEntity entity) {
        this.entity = entity;
    }

    @Override
    public double getStored(EnergySide face) {
        Direction dir = fromFace(face);
        if(dir == null)
            return Formatter.joulesEU(entity.getEnergy());
        return entity.canTransferFrom(dir) ? Formatter.joulesEU(entity.getEnergy()) : 0;
    }

    @Override
    public void setStored(double amount) {
        entity.setEnergy(Formatter.EUjoules(amount));
    }

    @Override
    public double getMaxStoredPower() {
        return entity.getMaxEnergy();
    }

    @Override
    public EnergyTier getTier() {
        if(entity.getMaxTransferRate() <= Formatter.EUjoules(EnergyTier.MICRO.getMaxInput()))
            return EnergyTier.MICRO;
        if(entity.getMaxTransferRate() <= Formatter.EUjoules(EnergyTier.LOW.getMaxInput()))
            return EnergyTier.LOW;
        if(entity.getMaxTransferRate() <= Formatter.EUjoules(EnergyTier.MEDIUM.getMaxInput()))
            return EnergyTier.MEDIUM;
        return EnergyTier.HIGH;
    }


    public static Direction fromFace(EnergySide side) {
        switch(side) {
            case WEST:
                return Direction.WEST;
            case SOUTH:
                return Direction.SOUTH;
            case NORTH:
                return Direction.NORTH;
            case UP:
                return Direction.UP;
            case DOWN:
                return Direction.DOWN;
            case EAST:
                return Direction.EAST;
            default:
                return null;
        }
    }
}
