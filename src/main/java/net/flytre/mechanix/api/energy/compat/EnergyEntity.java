package net.flytre.mechanix.api.energy.compat;

import net.flytre.flytre_lib.common.util.Formatter;
import net.flytre.mechanix.api.machine.MachineOverlay;
import net.flytre.mechanix.block.cable.Cable;
import net.flytre.mechanix.block.cable.CableResult;
import net.flytre.mechanix.block.cable.CableSide;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHandler;

import java.util.*;


/**
 * The Energy entity class is for BlockEntities with energy.
 * <p>
 * properties: The property delegate for screen handlers. You can use indices 4+ for your custom subclasses,
 * 0 - 3 are reserved by the energy entity class
 *
 * If your block entity either implements this, EnergyEntityWithItems, or uses the TechReborn compat layer
 * then you should be fully compatible energy-wise with Mechanix!
 */
public interface EnergyEntity extends MachineOverlay {


    static ArrayList<Direction> transferableDirections(BlockPos startingPos, World world) {
        ArrayList<Direction> result = new ArrayList<>();

        if (world == null)
            return result;

        BlockEntity thisEntity = world.getBlockEntity(startingPos);
        BlockState thisState = world.getBlockState(startingPos);
        for (Direction direction : Direction.values()) {

            if (thisState.getBlock() instanceof Cable && (thisState.get(Cable.getProperty(direction)) != CableSide.CONNECTED)) {
                continue;
            }

            if (thisEntity instanceof EnergyEntity && ((EnergyEntity) thisEntity).getEnergyIO().get(direction)) {
                continue;
            }

            BlockPos pos = startingPos.offset(direction);
            BlockState state = world.getBlockState(pos);
            BlockEntity entity = world.getBlockEntity(pos);

            if ((entity instanceof EnergyEntity && ((EnergyEntity) entity).canTransferFrom(direction.getOpposite()))) {
                result.add(direction);
            }

            if (entity != null && !(entity instanceof EnergyEntity) && Energy.valid(entity)) {
                EnergyHandler handler = Energy.of(entity);
                EnergyHandler sideHandler = handler.side(direction.getOpposite());
                if (sideHandler.getEnergy() > 0) {
                    result.add(direction);
                }
            }

            if (state.getBlock() instanceof Cable) {
                if (state.get(Cable.getProperty(direction.getOpposite())) == CableSide.CONNECTED)
                    result.add(direction);
            }
        }

        return result;
    }


    static ArrayList<Direction> pushableDirections(BlockPos startingPos, World world) {
        ArrayList<Direction> result = new ArrayList<>();

        if (world == null)
            return result;

        BlockEntity thisEntity = world.getBlockEntity(startingPos);
        BlockState thisState = world.getBlockState(startingPos);
        for (Direction direction : Direction.values()) {

            if (thisState.getBlock() instanceof Cable && (thisState.get(Cable.getProperty(direction)) != CableSide.CONNECTED)) {
                continue;
            }

            if (thisEntity instanceof EnergyEntity && !((EnergyEntity) thisEntity).getEnergyIO().get(direction)) {
                continue;
            }

            BlockPos pos = startingPos.offset(direction);
            BlockState state = world.getBlockState(pos);
            BlockEntity entity = world.getBlockEntity(pos);

            if (entity != null && !(entity instanceof EnergyEntity) && Energy.valid(entity)) {
                EnergyHandler handler = Energy.of(entity);
                EnergyHandler sideHandler = handler.side(direction.getOpposite());
                if (sideHandler.getEnergy() < sideHandler.getMaxStored()) {
                    result.add(direction);
                }
            }

            if (state.getBlock() instanceof Cable) {
                if (state.get(Cable.getProperty(direction.getOpposite())) == CableSide.CONNECTED)
                    result.add(direction);
            }
        }

        return result;
    }

    static void fromTag(EnergyEntity entity, CompoundTag tag) {
        entity.setEnergy(tag.getDouble("energy"));
        entity.setMaxTransferRate(tag.getDouble("transferRate"));

        if (tag.contains("maxEnergy"))
            entity.setMaxEnergy(tag.getDouble("maxEnergy"));

        entity.setEnergyIO(Formatter.intToMap(tag.getInt("EnergyMode")));
    }

    static void toTag(EnergyEntity entity, CompoundTag tag) {
        tag.putInt("EnergyMode", Formatter.mapToInt(entity.getEnergyIO()));
        tag.putDouble("energy", entity.getEnergy());
        tag.putDouble("maxEnergy", entity.getMaxEnergy());
        tag.putDouble("transferRate", entity.getMaxTransferRate());
    }

    PropertyDelegate getDelegate();

    /**
     * Which sides can input / output energy. Setting a side to false means it can only input, and true means only output
     */
    Map<Direction, Boolean> getEnergyIO();


    void setEnergyIO(Map<Direction, Boolean> energyIO);

    /**
     * Determines in the machine's screen whether the configuration panel on the right will toggle energy io or item io.
     * 0 is energy, 1 is item
     */
    int getPanelType();

    double getEnergy();

    void setEnergy(double energy);

    default void addEnergy(double delta) {
        setEnergy(getEnergy() + delta);
        clampEnergy();
    }

    double getMaxEnergy();

    void setMaxEnergy(double energy);

    double getMaxTransferRate();

    void setMaxTransferRate(double amount);

    default boolean canTransferFrom(Direction d) {
        return getEnergyIO().get(d);
    }

    default void updateDelegate() {
        PropertyDelegate properties = getDelegate();
        int[] en = Formatter.splitInt((int) getEnergy());
        int[] max = Formatter.splitInt((int) getMaxEnergy());
        properties.set(0, en[0]);
        properties.set(1, en[1]);
        properties.set(2, max[0]);
        properties.set(3, max[1]);
    }

    default boolean isFull() {
        return getEnergy() >= getMaxEnergy();
    }

    default boolean hasEnergy(double charge) {
        return getEnergy() >= charge;
    }


    default void clampEnergy() {
        if (getEnergy() > getMaxEnergy())
            setEnergy(getMaxEnergy());
        if (getEnergy() < 0)
            setEnergy(0);
    }

    default void removeEnergy(double delta) {
        addEnergy(-delta);
    }

    /**
     * Transfer \<amount> energy to yourself from a neighbor
     *
     * @param maxAmount Used to signal the neighbor's transfer rate / cable limited transfer rate
     * @return the remaining energy you still need
     */
    default double transferEnergy(EnergyEntity neighbor, double amount, double maxAmount) {
        double min = Math.min(amount, maxAmount);
        if (neighbor.getEnergy() >= min) {
            this.addEnergy(min);
            neighbor.removeEnergy(min);
            return amount - min;
        } else if (neighbor.getEnergy() > 0) {
            double energy = neighbor.getEnergy();
            this.addEnergy(energy);
            neighbor.setEnergy(0);
            return amount - energy;
        } else {
            return amount;
        }
    }

    BlockPos getPos();

    World getWorld();


    /**
     * Request energy. Call every tick if you want your entity to actually receive energy which is usually the
     * case unless its a generator. This will handle all the logic, you need not worry about that!
     *
     * @param amt the amt
     */
    default void requestEnergy(double amt) {

        if (getWorld() == null || getWorld().isClient)
            return;

        if (getEnergy() + amt > getMaxEnergy())
            amt = getMaxEnergy() - getEnergy();


        if (amt <= 0)
            return;

        BlockPos start = this.getPos();

        Deque<CableResult> to_visit = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        to_visit.add(new CableResult(this.getPos(), this.getMaxTransferRate()));

        while (to_visit.size() > 0) {
            CableResult cableResult = to_visit.pop();
            BlockPos currentPos = cableResult.getPos();
            BlockEntity entity = getWorld().getBlockEntity(currentPos);
            if (entity instanceof EnergyEntity && !(this.getPos().equals(currentPos))) {
                amt = transferEnergy((EnergyEntity) entity, amt, cableResult.getMax());
                if (amt == 0)
                    return;
                continue;
            }
            if (!(entity instanceof EnergyEntity) && Energy.valid(entity) && !(this.getPos().equals(currentPos))) {
                EnergyHandler handler = Energy.of(entity);
                double energy = handler.extract(Math.min(Formatter.sigmasEU(amt), cableResult.getMax()));
                addEnergy(Formatter.EUsigmas(energy));
                if (amt == 0)
                    return;
                continue;
            }

            if (entity == null || currentPos.equals(start)) {
                ArrayList<Direction> neighbors = EnergyEntity.transferableDirections(currentPos, getWorld());
                for (Direction d : neighbors) {
                    if (!visited.contains(currentPos.offset(d))) {
                        BlockEntity childEntity = getWorld().getBlockEntity(currentPos.offset(d));
                        double maxAmount = cableResult.getMax();
                        if (childEntity instanceof EnergyEntity) {
                            maxAmount = Math.min(maxAmount, ((EnergyEntity) childEntity).getMaxTransferRate());
                        } else {
                            Block block = getWorld().getBlockState(currentPos.offset(d)).getBlock();
                            if (block instanceof Cable)
                                maxAmount = Math.min(maxAmount, ((Cable) block).getMaxTransferAmount());
                        }
                        to_visit.add(new CableResult(currentPos.offset(d), maxAmount));
                    }
                }
            }
            visited.add(currentPos);
        }


    }


    default void techRebornPush() {
        double amt = getMaxTransferRate();
        if (getWorld() == null || getWorld().isClient)
            return;

        if (getEnergy() < amt)
            amt = getEnergy();
        if (amt <= 0)
            return;

        BlockPos start = this.getPos();

        Deque<CableResult> to_visit = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        to_visit.add(new CableResult(this.getPos(), this.getMaxTransferRate()));

        while (to_visit.size() > 0) {
            CableResult cableResult = to_visit.pop();
            BlockPos currentPos = cableResult.getPos();
            BlockEntity entity = getWorld().getBlockEntity(currentPos);

            if (entity != null && !(entity instanceof EnergyEntity) && Energy.valid(entity) && !(this.getPos().equals(currentPos))) {
                EnergyHandler handler = Energy.of(entity);
                double temp = Math.min(amt, cableResult.getMax());
                double l = Formatter.EUsigmas(handler.insert(Math.min(Formatter.sigmasEU(temp), handler.getMaxStored() - handler.getEnergy())));
                amt -= l;
                removeEnergy(l);
                if (amt <= 0)
                    return;
            }

            if (entity == null || currentPos.equals(start)) {
                ArrayList<Direction> neighbors = EnergyEntity.pushableDirections(currentPos, getWorld());
                for (Direction d : neighbors) {
                    if (!visited.contains(currentPos.offset(d))) {
                        double maxAmount = cableResult.getMax();
                        Block block = getWorld().getBlockState(currentPos.offset(d)).getBlock();
                        if (block instanceof Cable)
                            maxAmount = Math.min(maxAmount, Formatter.sigmasEU(((Cable) block).getMaxTransferAmount()));
                        to_visit.add(new CableResult(currentPos.offset(d), maxAmount));
                    }
                }
            }
            visited.add(currentPos);
        }

    }


    default void setEnergyMode(boolean up, boolean down, boolean north, boolean east, boolean south, boolean west) {
        getEnergyIO().put(Direction.UP, up);
        getEnergyIO().put(Direction.DOWN, down);
        getEnergyIO().put(Direction.NORTH, north);
        getEnergyIO().put(Direction.EAST, east);
        getEnergyIO().put(Direction.SOUTH, south);
        getEnergyIO().put(Direction.WEST, west);
    }
}
