package net.flytre.mechanix.block.fluid_pipe;

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

public class FluidPipeBlockEntity extends BlockEntity implements Tickable {

    public final HashMap<Direction, Boolean> servo;
    private final int perTick;

    public FluidPipeBlockEntity() {
        super(MachineRegistry.FLUID_PIPE_ENTITY);
        servo = new HashMap<>();
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


            if (entity instanceof FluidPipeBlockEntity)
                result.add(direction);

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
        return super.toTag(tag);
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
        super.fromTag(state, tag);
    }

    @Override
    public void tick() {
        if (world == null || world.isClient)
            return;



//        Add to queue
            for (Direction d : Direction.values()) {
                if (servo.get(d)) {
                    FluidInventory out = getInventoryAt(world, this.pos.offset(d));
                    Direction opp = d.getOpposite();

                    if (out == null || isInventoryEmpty(out, opp))
                        continue;

                    int[] arr = getAvailableSlots(out, opp).toArray();
                    for (int i : arr) {

                        FluidStack stack = out.getStack(i);
                        if (!canExtract(out, stack, i, opp) || stack.isEmpty() || stack.getAmount() < perTick)
                            continue;

                        FluidStack one = stack.copy();
                        one.setAmount(perTick);
                        FluidPipeResult result = findDestination(one, this.pos.offset(d));
                        if (result != null) {
                            FluidInventory inv = (FluidInventory) world.getBlockEntity(result.getDestination());
                            assert inv != null;
                            inv.add(one);
                            stack.decrement(perTick);
                            markDirty();

                            out.markDirty();
                            inv.markDirty();

                            break;
                        }
                    }
                }
            }
    }




    public @Nullable FluidPipeResult findDestination(FluidStack stack, BlockPos start) {
        if (world == null)
            return null;

        Deque<FluidPipeResult> to_visit = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        to_visit.add(new FluidPipeResult(this.getPos(), stack.getAmount(), Direction.NORTH));

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
                    to_visit.add(new FluidPipeResult(current.offset(d), stack.getAmount(), d.getOpposite()));
                }
            }
            visited.add(current);
        }

        return null;
    }
}

