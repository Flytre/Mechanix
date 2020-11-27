package net.flytre.mechanix.block.fluid_pipe;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.flytre.mechanix.base.fluid.FluidInventory;
import net.flytre.mechanix.base.fluid.FluidStack;
import net.flytre.mechanix.block.item_pipe.PipeSide;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

public class FluidPipeBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {

    public final HashMap<Direction, Boolean> servo;
    private int perTick;
    private FluidStack currentFluid;
    private int renderTime;
    private boolean corrected = false;

    public FluidPipeBlockEntity() {
        super(MachineRegistry.FLUID_PIPE_ENTITY);
        servo = new HashMap<>();
        currentFluid = FluidStack.EMPTY;
        servoSides(false, false, false, false, false, false);
        perTick = 50;
    }

    public void servoSides(boolean up, boolean down, boolean north, boolean east, boolean south, boolean west) {
        servo.put(Direction.UP, up);
        servo.put(Direction.DOWN, down);
        servo.put(Direction.NORTH, north);
        servo.put(Direction.EAST, east);
        servo.put(Direction.SOUTH, south);
        servo.put(Direction.WEST, west);
    }

    public static ArrayList<Direction> transferableDirections(BlockPos startingPos, World world, FluidStack stack) {
        ArrayList<Direction> result = new ArrayList<>();

        if (world == null)
            return result;

        BlockEntity me = world.getBlockEntity(startingPos);

        for (Direction direction : Direction.values()) {

            if (me instanceof FluidPipeBlockEntity && ((FluidPipeBlockEntity) me).servo.get(direction))
                continue;
            BlockPos pos = startingPos.offset(direction);
            BlockEntity entity = world.getBlockEntity(pos);


            if (entity instanceof FluidPipeBlockEntity) {
                if(((FluidPipeBlockEntity) entity).getFluidStack().isEmpty())
                    result.add(direction);
                else if(((FluidPipeBlockEntity) entity).getFluidStack().getFluid() == stack.getFluid())
                    result.add(direction);
                continue;
            }

            if (entity instanceof FluidInventory) {
                FluidInventory dInv = (FluidInventory) entity;

                if (dInv.canAdd(stack)) {
                    int[] slots = getAvailableSlots(dInv, direction.getOpposite()).toArray();
                    for (int i : slots) {
                        if (canInsert(dInv, stack, i, direction.getOpposite())) {
                            result.add(direction);
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    private static boolean canInsert(FluidInventory inventory, FluidStack stack, int slot, @Nullable Direction side) {
        if (!inventory.isValid(slot, stack)) {
            return false;
        }
        return inventory.canInsert(slot, stack, side);
    }

    private static boolean canExtract(FluidInventory inv, FluidStack stack, int slot, Direction facing) {
        return inv.canExtract(slot, stack, facing);
    }

    private static boolean isInventoryEmpty(FluidInventory inv, Direction facing) {
        return getAvailableSlots(inv, facing).allMatch((i) -> inv.getStack(i).isEmpty());
    }

    private static IntStream getAvailableSlots(FluidInventory inventory, Direction side) {
        return IntStream.of(inventory.getAvailableSlots(side));
    }

    @Nullable
    public static FluidInventory getInventoryAt(World world, BlockPos blockPos) {
        return getInventoryAt(world, (double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D);
    }

    @Nullable
    public static FluidInventory getInventoryAt(World world, double x, double y, double z) {
        FluidInventory inventory = null;
        BlockPos blockPos = new BlockPos(x, y, z);
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        if (block.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof FluidInventory) {
                inventory = (FluidInventory) blockEntity;
            }
        }

        if (inventory == null) {
            List<Entity> list = world.getOtherEntities(null, new Box(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntityPredicates.VALID_INVENTORIES);
            if (!list.isEmpty()) {
                inventory = (FluidInventory) list.get(world.random.nextInt(list.size()));
            }
        }

        return inventory;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag fluid = new CompoundTag();
        this.currentFluid.toTag(fluid);
        tag.put("fluid",fluid);
        tag.putBoolean("init",corrected);
        corrected = tag.getBoolean("init");
        return super.toTag(tag);
    }


    public void setFluidStack(FluidStack stack) {
        this.currentFluid = stack;
        renderTime = 2;

        if(world != null && !world.isClient)
        sync();
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        for (Direction dir : Direction.values()) {
            EnumProperty<PipeSide> property = FluidPipe.getProperty(dir);
            if (state.get(property) == PipeSide.SERVO)
                servo.put(dir, true);
            else
                servo.put(dir, false);
        }
        this.setFluidStack(FluidStack.fromTag(tag.getCompound("fluid")));
        corrected = tag.getBoolean("init");
        super.fromTag(state, tag);
    }


    public FluidStack getFluidStack() {
        return currentFluid;
    }

    @Override
    public void tick() {
        if (world == null || world.isClient)
            return;
        
        
        

        if(!this.currentFluid.isEmpty() && renderTime == 0) {
            this.currentFluid = FluidStack.EMPTY;
            sync();
        } else if(renderTime > 0) {
            sync();
            renderTime--;
        }

        //Add to queue
            for (Direction d : Direction.values()) {
                if (servo.get(d)) {
                    FluidInventory out = getInventoryAt(world, this.pos.offset(d));
                    Direction opp = d.getOpposite();

                    if (out == null || isInventoryEmpty(out, opp))
                        continue;

                    int[] arr = getAvailableSlots(out, opp).toArray();
                    for (int i : arr) {

                        FluidStack stack = out.getStack(i);
                        if (!canExtract(out, stack, i, opp) || stack.isEmpty())
                            continue;

                        FluidStack one = stack.copy();
                        one.setAmount(Math.min(perTick,stack.getAmount()));
                        FluidPipeResult result = findDestination(one, this.pos.offset(d));
                        if (result != null) {
                            FluidInventory inv = (FluidInventory) world.getBlockEntity(result.getDestination());
                            assert inv != null;
                            inv.add(new FluidStack(one.getFluid(), (int) result.getAmount()));
                            stack.decrement((int) result.getAmount());
                            markDirty();

                            for(BlockPos pos : result.getPath()) {
                                BlockEntity e = world.getBlockEntity(pos);
                                if(e instanceof FluidPipeBlockEntity) {
                                    ((FluidPipeBlockEntity) e).setFluidStack(new FluidStack(one.getFluid(),1));
                                }
                            }

                            out.markDirty();
                            inv.markDirty();

                            break;
                        }
                    }
                }
            }

        //Tiers/
        if(!corrected && world !=null && !world.isClient) {
            Block block = world.getBlockState(pos).getBlock();
            if(block == MachineRegistry.FLUID_PIPE) {
                perTick = 50;
            }
            if(block == MachineRegistry.GILDED_FLUID_PIPE) {
                perTick = 150;
            }
            if(block == MachineRegistry.VYSTERIUM_FLUID_PIPE) {
                perTick = 500;
            }
            if(block == MachineRegistry.NEPTUNIUM_FLUID_PIPE) {
                perTick = 1500;
            }
            corrected = true;
        }
    }




    public @Nullable FluidPipeResult findDestination(FluidStack stack, BlockPos start) {
        if (world == null)
            return null;

        Deque<FluidPipeResult> to_visit = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        to_visit.add(new FluidPipeResult(this.getPos(), stack.getAmount(), Direction.NORTH, new ArrayList<>()));

        while (to_visit.size() > 0) {
            FluidPipeResult popped = to_visit.pop();
            BlockPos current = popped.getDestination();

            BlockEntity entity = world.getBlockEntity(current);
            if (!current.equals(start) && entity instanceof FluidInventory) {
                return popped;
            }
            ArrayList<Direction> neighbors = FluidPipeBlockEntity.transferableDirections(current, world, stack);
            for (Direction d : neighbors) {
                if (!visited.contains(current.offset(d))) {
                    ArrayList<BlockPos> newPath = new ArrayList<>(popped.getPath());
                    newPath.add(current);

                    int amount = (int)Math.min(stack.getAmount(),popped.getAmount());
                    BlockState state = world.getBlockState(current.offset(d));
                    if(state.getBlock() == MachineRegistry.FLUID_PIPE)
                        amount = Math.min(amount,50);
                    if(state.getBlock() == MachineRegistry.GILDED_FLUID_PIPE)
                        amount = Math.min(amount,150);
                    if(state.getBlock() == MachineRegistry.VYSTERIUM_FLUID_PIPE)
                        amount = Math.min(amount,500);
                    if(state.getBlock() == MachineRegistry.NEPTUNIUM_FLUID_PIPE)
                        amount = Math.min(amount,1500);

                    to_visit.add(new FluidPipeResult(current.offset(d), amount, d.getOpposite(), newPath));
                }
            }
            visited.add(current);
        }

        return null;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.setFluidStack(FluidStack.fromTag(compoundTag.getCompound("fluid")));
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        CompoundTag fluid = new CompoundTag();
        this.currentFluid.toTag(fluid);
        compoundTag.put("fluid",fluid);
        return compoundTag;
    }
}

