package net.flytre.mechanix.base;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.flytre.mechanix.block.cable.Cable;
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

import java.util.*;

public abstract class EnergyEntity extends BlockEntity implements Tickable, ExtendedScreenHandlerFactory, BlockEntityClientSerializable {

    public final HashMap<Direction, Boolean> energyMode; //true = output, false = input
    public final HashMap<Direction, Boolean> itemMode; //true = output, false = input
    private final PropertyDelegate properties;
    private double energy;
    private double maxEnergy;
    private double maxTransferRate;
    protected int panelMode; //whether using the panel should change item mode / energy mode, 0 = energy, 1 = item
    private boolean sync;

    public EnergyEntity(BlockEntityType<?> type) {
        super(type);
        energyMode = new HashMap<>();
        itemMode = new HashMap<>();
        properties = new ArrayPropertyDelegate(12); //4 unused for subclasses

        //defaults - PLEASE OVERRIDE unless ur an energy cell
        maxEnergy = 300000;
        maxTransferRate = 300;
        panelMode = 0;
        setEnergyMode(true, true, true, true, true, true);
        setItemMode(true, false, true, false, true, false);

    }

    public void syncEntity() {
        this.sync = true;
    }

    public PropertyDelegate getProperties() {
        return properties;
    }

    public void updateDelegate() {

        properties.set(0, 1); //when this is 1 you know its synced
        properties.set(1,DelegateFixer.hashToInt(this.energyMode));
        properties.set(2,DelegateFixer.hashToInt(this.itemMode));
        int[] en = DelegateFixer.splitInt((int) getEnergy());
        int[] max = DelegateFixer.splitInt((int) getMaxEnergy());
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


        if(tag.contains("maxEnergy"))
            this.maxEnergy = tag.getDouble("maxEnergy");

        CompoundTag dirs = tag.getCompound("Directions");
        for (Direction d : Direction.values()) {
                energyMode.put(d, dirs.getByte(d.getName()) == 1);
        }

        CompoundTag items = tag.getCompound("ItemMode");
        for (Direction d : Direction.values()) {
            itemMode.put(d, items.getByte(d.getName()) == 1);
        }

    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {

        CompoundTag dirs = new CompoundTag();
        tag.put("Directions", dirs);
        for (Direction direction : energyMode.keySet()) {
            dirs.putByte(direction.getName(), (byte) (energyMode.get(direction) ? 1 : 0));
        }

        CompoundTag items = new CompoundTag();
        tag.put("ItemMode", items);
        for (Direction direction : itemMode.keySet()) {
            items.putByte(direction.getName(), (byte) (itemMode.get(direction) ? 1 : 0));
        }

        tag.putDouble("energy", this.energy);
        tag.putDouble("maxEnergy", this.maxEnergy);
        return super.toTag(tag);
    }

    public boolean canTransferFrom(Direction d) {
        return energyMode.get(d);
    }


    public double getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(double maxEnergy) {
        this.maxEnergy = maxEnergy;
        sync = true;
    }

    public double getMaxTransferRate() {
        return maxTransferRate;
    }

    public void setMaxTransferRate(double maxTransferRate) {
        this.maxTransferRate = maxTransferRate;
        sync = true;
    }

    public boolean isFull() {
        return this.energy >= this.maxEnergy;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double charge) {
        this.energy = charge;
        sync = true;
    }

    public boolean hasEnergy(double charge) {
        return energy > charge;
    }

    public void addEnergy(double charge) {
        energy += charge;
        sync = true;
    }

    public void removeEnergy(double charge) {
        energy -= charge;
        sync = true;
    }




    /*
    Transfer energy and return the amount remaining needed to transfer
    */
    public double transferEnergy(EnergyEntity neighbor, double amount) {
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


    public static ArrayList<Direction> transferrableDirections(BlockPos startingPos, World world) {
        ArrayList<Direction> result = new ArrayList<>();

        if(world == null)
            return result;

        for(Direction direction : Direction.values()) {

            BlockEntity thisEntity = world.getBlockEntity(startingPos);
            if(thisEntity instanceof EnergyEntity && ((EnergyEntity)thisEntity).energyMode.get(direction)) {
                continue;
            }

            BlockPos pos = startingPos.offset(direction);
            BlockState state = world.getBlockState(pos);
            BlockEntity entity = world.getBlockEntity(pos);
            if(state.getBlock() instanceof Cable ||
                    (entity instanceof EnergyEntity && ((EnergyEntity) entity).canTransferFrom(direction.getOpposite())))
                result.add(direction);
        }

        return result;
    }



    public void requestEnergy(double amt) {

        if(world == null)
            return;

        Deque<BlockPos> to_visit = new LinkedList<>();
        Set<BlockPos> visited  = new HashSet<>();
        to_visit.add(this.getPos());

        while(to_visit.size() > 0) {
            BlockPos current = to_visit.pop();
            BlockEntity entity = world.getBlockEntity(current);
            if(entity instanceof EnergyEntity && !(this.getPos().equals(current))) {
                amt = transferEnergy((EnergyEntity) entity,amt);
                if(amt == 0)
                    return;
            }
            ArrayList<Direction> neighbors = EnergyEntity.transferrableDirections(current,world);
            for(Direction d : neighbors) {
                if(!visited.contains(current))
                    to_visit.add(current.offset(d));
            }
            visited.add(current);
        }


    }

    public void setEnergyMode(boolean up, boolean down, boolean north, boolean east, boolean south, boolean west) {
        energyMode.put(Direction.UP, up);
        energyMode.put(Direction.DOWN, down);
        energyMode.put(Direction.NORTH, north);
        energyMode.put(Direction.EAST, east);
        energyMode.put(Direction.SOUTH, south);
        energyMode.put(Direction.WEST, west);
    }

    public void setItemMode(boolean up, boolean down, boolean north, boolean east, boolean south, boolean west) {
        itemMode.put(Direction.UP, up);
        itemMode.put(Direction.DOWN, down);
        itemMode.put(Direction.NORTH, north);
        itemMode.put(Direction.EAST, east);
        itemMode.put(Direction.SOUTH, south);
        itemMode.put(Direction.WEST, west);
    }


    @Override
    public void tick() {
        updateDelegate();
        if(sync) {
            if (world != null && !world.isClient ) {
                sync();
            }
            sync = false;
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
    }

    public void fromClientTag(CompoundTag var1) {
        this.fromTag(null,var1);
    };

    public CompoundTag toClientTag(CompoundTag var1) {
        return this.toTag(var1);
    };
}
