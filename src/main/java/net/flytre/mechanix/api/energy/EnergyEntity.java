package net.flytre.mechanix.api.energy;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.flytre.mechanix.api.machine.TieredMachine;
import net.flytre.mechanix.api.util.Formatter;
import net.flytre.mechanix.block.cable.Cable;
import net.flytre.mechanix.block.cable.CableResult;
import net.flytre.mechanix.block.cable.CableSide;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHandler;

import java.util.*;


/**
 * The Energy entity class is for BlockEntities with energy.
 * <p>
 * properties: The property delegate for screen handlers. You can use indices 8-11 for your custom subclasses,
 * 0 - 7 are reserved by the energy entity class
 */
public abstract class EnergyEntity extends BlockEntity implements Tickable, ExtendedScreenHandlerFactory, BlockEntityClientSerializable, TieredMachine {


    private final PropertyDelegate properties;
    /**
     * Which sides can input / output energy. Setting a side to false means it can only input, and true means only output
     */
    public HashMap<Direction, Boolean> energyMode; //true = output, false = input
    /**
     * Which sides can input / output items/fluids. Same as above
     */
    public HashMap<Direction, Boolean> ioMode; //true = output, false = input
    /**
     * Determines in the machine's screen whether the configuration panel on the right will toggle energy io or item io.
     * 0 is energy, 1 is item
     */
    protected int panelMode; //whether using the panel should change item mode / energy mode, 0 = energy, 1 = item
    private double energy;
    private double maxEnergy;
    private double maxTransferRate;

    private boolean sync;
    private int tier;


    /**
     * Instantiates a new Energy entity.
     *
     * @param type the type
     */
    public EnergyEntity(BlockEntityType<?> type) {
        super(type);
        energyMode = new HashMap<>();
        ioMode = new HashMap<>();
        properties = new ArrayPropertyDelegate(12); //4 unused for subclasses

        //defaults - PLEASE OVERRIDE
        maxEnergy = 300000;
        maxTransferRate = 300;
        panelMode = 0;
        setEnergyMode(true, true, true, true, true, true);
        setIOMode(false, true, true, true, true, true);

    }


    private static ArrayList<Direction> pushableDirections(BlockPos startingPos, World world) {
        ArrayList<Direction> result = new ArrayList<>();

        if (world == null)
            return result;

        BlockEntity thisEntity = world.getBlockEntity(startingPos);
        BlockState thisState = world.getBlockState(startingPos);
        for (Direction direction : Direction.values()) {

            if (thisState.getBlock() instanceof Cable && (thisState.get(Cable.getProperty(direction)) != CableSide.CONNECTED)) {
                continue;
            }

            if (thisEntity instanceof EnergyEntity && !((EnergyEntity) thisEntity).energyMode.get(direction)) {
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

    private static ArrayList<Direction> transferrableDirections(BlockPos startingPos, World world) {
        ArrayList<Direction> result = new ArrayList<>();

        if (world == null)
            return result;

        BlockEntity thisEntity = world.getBlockEntity(startingPos);
        BlockState thisState = world.getBlockState(startingPos);
        for (Direction direction : Direction.values()) {

            if (thisState.getBlock() instanceof Cable && (thisState.get(Cable.getProperty(direction)) != CableSide.CONNECTED)) {
                continue;
            }

            if (thisEntity instanceof EnergyEntity && ((EnergyEntity) thisEntity).energyMode.get(direction)) {
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

    public HashMap<Direction, Boolean> getIO() {
        return ioMode;
    }

    /**
     * Sync entity info client-side. Useful when doing custom block entity renders.
     */
    public void syncEntity() {
        this.sync = true;
    }

    /**
     * Gets the property delegate - This tells the screen / screenhandler info about the entity which is used
     * in rendering. Remember that indices 8 -11 can be set to ints of your choice, but 0 - 7 are reserved.
     *
     * @return the properties
     */
    public PropertyDelegate getProperties() {
        return properties;
    }

    /**
     * Update the delegate. Should be overriden and call by energy entities that want to give any custom information
     * to the screen: Example, for a furnace how long until the recipe finishes to render the arrow.
     */
    protected void updateDelegate() {

        properties.set(0, 1); //when this is 1 you know its synced
        properties.set(1, Formatter.hashToInt(this.energyMode));
        properties.set(2, Formatter.hashToInt(this.ioMode));
        int[] en = Formatter.splitInt((int) getEnergy());
        int[] max = Formatter.splitInt((int) getMaxEnergy());
        properties.set(3, en[0]);
        properties.set(4, en[1]);
        properties.set(5, max[0]);
        properties.set(6, max[1]);
        properties.set(7, panelMode);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {


        super.fromTag(state, tag);
        this.setEnergy(tag.getDouble("energy"));

        this.maxTransferRate = tag.getDouble("transferRate");


        if (tag.contains("maxEnergy"))
            this.maxEnergy = tag.getDouble("maxEnergy");

        TieredMachine.fromTag(tag, this);
        energyMode = Formatter.intToHash(tag.getInt("EnergyMode"));
        ioMode = Formatter.intToHash(tag.getInt("IOMode"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {

        tag.putInt("EnergyMode", Formatter.hashToInt(energyMode));
        tag.putInt("IOMode", Formatter.hashToInt(ioMode));

        tag.putDouble("energy", this.energy);
        tag.putDouble("maxEnergy", this.maxEnergy);
        tag.putDouble("transferRate", this.maxTransferRate);
        TieredMachine.toTag(this, tag);
        return super.toTag(tag);
    }

    public boolean canTransferFrom(Direction d) {
        return energyMode.get(d);
    }

    /**
     * Gets the energy this entity can store.
     *
     * @return the max energy
     */
    public double getMaxEnergy() {
        return maxEnergy;
    }

    /**
     * Sets the max energy this entity can store.
     *
     * @param maxEnergy the max energy
     */
    public void setMaxEnergy(double maxEnergy) {
        this.maxEnergy = maxEnergy;
        sync = true;
        fixEnergy();
    }

    /**
     * Gets the max transfer rate - how many kJ/tick this can transfer at once.
     *
     * @return the max transfer rate
     */
    public double getMaxTransferRate() {
        return maxTransferRate;
    }

    /**
     * Sets max transfer rate.
     *
     * @param maxTransferRate the max transfer rate
     */
    public void setMaxTransferRate(double maxTransferRate) {
        this.maxTransferRate = maxTransferRate;
        sync = true;
    }

    /**
     * Whether the entity is full of energy or not.
     *
     * @return the boolean
     */
    public boolean isFull() {
        return this.energy >= this.maxEnergy;
    }

    /**
     * Gets the amount of energy the entity has.
     *
     * @return the energy
     */
    public double getEnergy() {
        return energy;
    }

    /**
     * Sets the amount of energy in the machine.
     *
     * @param charge the charge
     */
    public void setEnergy(double charge) {
        this.energy = charge;
        sync = true;
        fixEnergy();
    }

    /**
     * How much energy the machine has
     *
     * @param charge the charge
     * @return the boolean
     */
    public boolean hasEnergy(double charge) {
        return energy > charge;
    }

    /**
     * Add energy.
     *
     * @param charge the charge
     */
    public void addEnergy(double charge) {
        energy += charge;
        sync = true;
        fixEnergy();
    }

    private void fixEnergy() {
        if (energy > maxEnergy)
            energy = maxEnergy;
        if (energy < 0)
            energy = 0;
    }

    /**
     * Remove energy.
     *
     * @param charge the charge
     */
    public void removeEnergy(double charge) {
        energy -= charge;
        sync = true;
        fixEnergy();
    }


    private double transferEnergy(EnergyEntity neighbor, double amount) {
        if (neighbor.getEnergy() >= amount) {
            this.addEnergy(amount);
            neighbor.removeEnergy(amount);
            return 0;
        } else if (neighbor.getEnergy() > 0) {
            double energy = neighbor.getEnergy();
            this.addEnergy(energy);
            neighbor.setEnergy(0);
            return amount - energy;
        } else {
            return amount;
        }
    }

    /**
     * Request energy. Call every tick if you want your entity to actually receive energy which is usually the
     * case unless its a generator. This will handle all the logic, you need not worry about that!
     *
     * @param amt the amt
     */
    public void requestEnergy(double amt) {

        if (world == null || world.isClient)
            return;

        if (getEnergy() + amt > getMaxEnergy())
            amt = getMaxEnergy() - getEnergy();


        if (amt <= 0)
            return;

        BlockPos start = this.getPos();

        Deque<CableResult> to_visit = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        to_visit.add(new CableResult(this.getPos(), this.maxTransferRate));

        while (to_visit.size() > 0) {
            CableResult cableResult = to_visit.pop();
            BlockPos currentPos = cableResult.getPos();
            BlockEntity entity = world.getBlockEntity(currentPos);
            if (entity instanceof EnergyEntity && !(this.getPos().equals(currentPos))) {
                amt = transferEnergy((EnergyEntity) entity, Math.min(amt, cableResult.getMax()));
                if (amt == 0)
                    return;
            }
            if (!(entity instanceof EnergyEntity) && Energy.valid(entity) && !(this.getPos().equals(currentPos))) {
                EnergyHandler handler = Energy.of(entity);
                double energy = handler.extract(Math.min(amt, cableResult.getMax()));
                addEnergy(Formatter.EUjoules(energy));
                if (amt == 0)
                    return;
            }

            if (entity == null || currentPos.equals(start)) {
                ArrayList<Direction> neighbors = EnergyEntity.transferrableDirections(currentPos, world);
                for (Direction d : neighbors) {
                    if (!visited.contains(currentPos.offset(d))) {
                        BlockEntity childEntity = world.getBlockEntity(currentPos.offset(d));
                        double maxAmount = cableResult.getMax();
                        if (childEntity instanceof EnergyEntity) {
                            maxAmount = Math.min(maxAmount, ((EnergyEntity) childEntity).maxTransferRate);
                        } else {
                            //cable transfer rates:
                            Block cable = world.getBlockState(currentPos.offset(d)).getBlock();
                            if (cable == MachineRegistry.CABLES.getStandard())
                                maxAmount = Math.min(maxAmount, 50);
                            if (cable == MachineRegistry.CABLES.getGilded())
                                maxAmount = Math.min(maxAmount, 150);
                            if (cable == MachineRegistry.CABLES.getVysterium())
                                maxAmount = Math.min(maxAmount, 500);
                            if (cable == MachineRegistry.CABLES.getNeptunium())
                                maxAmount = Math.min(maxAmount, 2000);
                        }
                        to_visit.add(new CableResult(currentPos.offset(d), maxAmount));
                    }
                }
            }
            visited.add(currentPos);
        }


    }


    public void techRebornPush() {
        double amt = getMaxTransferRate();
        if (world == null || world.isClient)
            return;

        if (getEnergy() < amt)
            amt = getEnergy();
        if (amt <= 0)
            return;

        BlockPos start = this.getPos();

        Deque<CableResult> to_visit = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        to_visit.add(new CableResult(this.getPos(), this.maxTransferRate));

        while (to_visit.size() > 0) {
            CableResult cableResult = to_visit.pop();
            BlockPos currentPos = cableResult.getPos();
            BlockEntity entity = world.getBlockEntity(currentPos);

            if (entity != null && !(entity instanceof EnergyEntity) && Energy.valid(entity) && !(this.getPos().equals(currentPos))) {
                EnergyHandler handler = Energy.of(entity);
                double temp = Math.min(amt, cableResult.getMax());
                double l = handler.insert(Math.min(temp, handler.getMaxStored() - handler.getEnergy()));
                amt -= l;
                removeEnergy(l);
                if (amt <= 0)
                    return;
            }

            if (entity == null || currentPos.equals(start)) {
                ArrayList<Direction> neighbors = EnergyEntity.pushableDirections(currentPos, world);
                for (Direction d : neighbors) {
                    if (!visited.contains(currentPos.offset(d))) {
                        double maxAmount = cableResult.getMax();
                        //cable transfer rates: (they can transfer more to TR machines)
                        Block cable = world.getBlockState(currentPos.offset(d)).getBlock();
                        if (cable == MachineRegistry.CABLES.getStandard())
                            maxAmount = Math.min(maxAmount, 150);
                        if (cable == MachineRegistry.CABLES.getGilded())
                            maxAmount = Math.min(maxAmount, 500);
                        if (cable == MachineRegistry.CABLES.getVysterium())
                            maxAmount = Math.min(maxAmount, 2500);
                        if (cable == MachineRegistry.CABLES.getNeptunium())
                            maxAmount = Math.min(maxAmount, 10000);
                        to_visit.add(new CableResult(currentPos.offset(d), maxAmount));
                    }
                }
            }
            visited.add(currentPos);
        }

    }

    /**
     * Sets the energy mode, usually called in the constructor. Remember this controls which sides can input
     * or output energy by default.
     *
     * @param up    the up
     * @param down  the down
     * @param north the north
     * @param east  the east
     * @param south the south
     * @param west  the west
     */
    public void setEnergyMode(boolean up, boolean down, boolean north, boolean east, boolean south, boolean west) {
        energyMode.put(Direction.UP, up);
        energyMode.put(Direction.DOWN, down);
        energyMode.put(Direction.NORTH, north);
        energyMode.put(Direction.EAST, east);
        energyMode.put(Direction.SOUTH, south);
        energyMode.put(Direction.WEST, west);
    }

    /**
     * Same as set energy mod but for fluids/items.
     *
     * @param up    the up
     * @param down  the down
     * @param north the north
     * @param east  the east
     * @param south the south
     * @param west  the west
     */
    public void setIOMode(boolean up, boolean down, boolean north, boolean east, boolean south, boolean west) {
        ioMode.put(Direction.UP, up);
        ioMode.put(Direction.DOWN, down);
        ioMode.put(Direction.NORTH, north);
        ioMode.put(Direction.EAST, east);
        ioMode.put(Direction.SOUTH, south);
        ioMode.put(Direction.WEST, west);
    }


    @Override
    public final void tick() {

        for (int i = 0; i <= tier; i++) {
            repeatTick();
        }
        onceTick();
        techRebornPush();
        updateDelegate();
        if (sync) {
            if (world != null && !world.isClient) {
                sync();
            }
            sync = false;
        }
    }

    /**
     * Repeat this once per tier (so up to 4 times)
     */
    public abstract void repeatTick();

    /**
     * Repeat this 1 time every tick.
     */
    public abstract void onceTick();

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
    }

    @Override
    public void fromClientTag(CompoundTag var1) {
        this.fromTag(null, var1);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag var1) {
        return this.toTag(var1);
    }


    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        this.tier = tier;
        if (world != null && !world.isClient)
            sync();
        markDirty();
    }

    @Override
    public void markDirty() {
        if (world != null && !world.isClient)
            sync();
    }
}
