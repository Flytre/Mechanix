package net.flytre.mechanix.base.energy;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.flytre.mechanix.base.Formatter;
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

import java.util.*;

public abstract class EnergyEntity extends BlockEntity implements Tickable, ExtendedScreenHandlerFactory, BlockEntityClientSerializable {

    public HashMap<Direction, Boolean> energyMode; //true = output, false = input
    public HashMap<Direction, Boolean> ioMode; //true = output, false = input
    private final PropertyDelegate properties;
    private double energy;
    private double maxEnergy;
    private double maxTransferRate;
    protected int panelMode; //whether using the panel should change item mode / energy mode, 0 = energy, 1 = item
    private boolean sync;

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

    public void syncEntity() {
        this.sync = true;
    }

    public PropertyDelegate getProperties() {
        return properties;
    }

    public void updateDelegate() {

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


        if(tag.contains("maxEnergy"))
            this.maxEnergy = tag.getDouble("maxEnergy");


        energyMode = Formatter.intToHash(tag.getInt("EnergyMode"));
        ioMode = Formatter.intToHash(tag.getInt("IOMode"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {

        tag.putInt("EnergyMode", Formatter.hashToInt(energyMode));
        tag.putInt("IOMode", Formatter.hashToInt(ioMode));

        tag.putDouble("energy", this.energy);
        tag.putDouble("maxEnergy", this.maxEnergy);
        tag.putDouble("transferRate",this.maxTransferRate);
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
            if((entity instanceof EnergyEntity && ((EnergyEntity) entity).canTransferFrom(direction.getOpposite())))
                result.add(direction);

            if (state.getBlock() instanceof Cable) {
                if (state.get(Cable.getProperty(direction)) == CableSide.CONNECTED)
                    result.add(direction);
            }
        }

        return result;
    }



    public void requestEnergy(double amt) {

        if(world == null)
            return;

        BlockPos start = this.getPos();

        Deque<CableResult> to_visit = new LinkedList<>();
        Set<BlockPos> visited  = new HashSet<>();
        to_visit.add(new CableResult(this.getPos(),this.maxTransferRate));

        while(to_visit.size() > 0) {
            CableResult cableResult = to_visit.pop();
            BlockPos currentPos = cableResult.getPos();
            BlockEntity entity = world.getBlockEntity(currentPos);
            if(entity instanceof EnergyEntity && !(this.getPos().equals(currentPos))) {
                amt = transferEnergy((EnergyEntity) entity,Math.min(amt,cableResult.getMax()));
                if(amt == 0)
                    return;
            }
            if(entity == null || currentPos.equals(start)) {
                ArrayList<Direction> neighbors = EnergyEntity.transferrableDirections(currentPos, world);
                for (Direction d : neighbors) {
                    if (!visited.contains(currentPos.offset(d))) {
                        BlockEntity childEntity = world.getBlockEntity(currentPos.offset(d));
                        double maxAmount = cableResult.getMax();
                        if(childEntity instanceof EnergyEntity) {
                            maxAmount = Math.min(maxAmount, ((EnergyEntity) childEntity).maxTransferRate);
                        } else {
                            //cable transfer rates:
                            Block cable = world.getBlockState(currentPos.offset(d)).getBlock();
                            if(cable == MachineRegistry.CABLES.getStandard())
                                maxAmount = Math.min(maxAmount,25);
                            if(cable == MachineRegistry.CABLES.getGilded())
                                maxAmount = Math.min(maxAmount,100);
                            if(cable == MachineRegistry.CABLES.getVysterium())
                                maxAmount = Math.min(maxAmount,300);
                            if(cable == MachineRegistry.CABLES.getNeptunium())
                                maxAmount = Math.min(maxAmount,1000);
                        }
                        to_visit.add(new CableResult(currentPos.offset(d),maxAmount));
                    }
                }
            }
            visited.add(currentPos);
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

    public void setIOMode(boolean up, boolean down, boolean north, boolean east, boolean south, boolean west) {
        ioMode.put(Direction.UP, up);
        ioMode.put(Direction.DOWN, down);
        ioMode.put(Direction.NORTH, north);
        ioMode.put(Direction.EAST, east);
        ioMode.put(Direction.SOUTH, south);
        ioMode.put(Direction.WEST, west);
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
