package net.flytre.mechanix.api.energy;


import net.flytre.flytre_lib.common.util.Formatter;
import net.flytre.mechanix.api.energy.compat.EnergyEntity;
import net.minecraft.util.math.Direction;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

public class TREnergyStorageImpl implements EnergyStorage {

    private final EnergyEntity entity;

    public TREnergyStorageImpl(EnergyEntity entity) {
        this.entity = entity;
    }

    public static Direction fromFace(EnergySide side) {
        switch (side) {
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

    @Override
    public double getStored(EnergySide face) {
        return Formatter.sigmasEU(entity.getEnergy());
    }

    @Override
    public void setStored(double amount) {
        entity.setEnergy(Formatter.EUsigmas(amount));
    }

    @Override
    public double getMaxStoredPower() {
        return Formatter.sigmasEU(entity.getMaxEnergy());
    }

    @Override
    public EnergyTier getTier() {
        if (entity.getMaxTransferRate() <= Formatter.EUsigmas(EnergyTier.MICRO.getMaxInput()))
            return EnergyTier.MICRO;
        if (entity.getMaxTransferRate() <= Formatter.EUsigmas(EnergyTier.LOW.getMaxInput()))
            return EnergyTier.LOW;
        if (entity.getMaxTransferRate() <= Formatter.EUsigmas(EnergyTier.MEDIUM.getMaxInput()))
            return EnergyTier.MEDIUM;
        return EnergyTier.HIGH;
    }

    @Override
    public double getMaxInput(EnergySide side) {
        return !entity.canTransferFrom(fromFace(side)) ? Formatter.sigmasEU(entity.getMaxTransferRate()) : 0;
    }

    @Override
    public double getMaxOutput(EnergySide side) {
        return entity.canTransferFrom(fromFace(side)) ? Formatter.sigmasEU(entity.getMaxTransferRate()) : 0;
    }
}
