package net.flytre.mechanix.block.fluid_pipe;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.flytre.flytre_lib.common.util.Formatter;
import net.flytre.mechanix.api.fluid.FluidFilterInventory;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.fluid.Fraction;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;


public class FluidPipeEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable, ExtendedScreenHandlerFactory {

    public Map<Direction, Boolean> wrenched;
    private Fraction perTick;
    private FluidStack currentFluid;
    private int renderTime;
    private boolean corrected = false;
    private FluidFilterInventory filter;

    public FluidPipeEntity() {
        super(MachineRegistry.FLUID_PIPE_ENTITY);
        currentFluid = FluidStack.EMPTY;
        perTick = new Fraction(1,20);
        wrenched = new HashMap<>();
        for (Direction dir : Direction.values()) {
            wrenched.put(dir, false);
        }
        filter = FluidFilterInventory.fromTag(new CompoundTag(), 1);
    }

    public static ArrayList<Direction> transferableDirections(BlockPos startingPos, World world, FluidStack stack) {
        ArrayList<Direction> result = new ArrayList<>();

        if (world == null)
            return result;

        BlockEntity me = world.getBlockEntity(startingPos);

        for (Direction direction : Direction.values()) {

            if (me instanceof FluidPipeEntity && !(((FluidPipeEntity) me).getSide(direction) == PipeSide.CONNECTED))
                continue;
            BlockPos pos = startingPos.offset(direction);
            BlockEntity entity = world.getBlockEntity(pos);


            if (entity instanceof FluidPipeEntity) {

                PipeSide state = world.getBlockState(startingPos.offset(direction)).get(FluidPipe.getProperty(direction.getOpposite()));
                boolean bl = false;
                if (state == PipeSide.CONNECTED)
                    bl = true;
                if (state == PipeSide.SERVO) {
                    FluidPipeEntity pipeEntity = (FluidPipeEntity) entity;
                    bl = pipeEntity.filter.hasNoFluids() || pipeEntity.filter.passFilterTest(stack);
                }

                if (bl && ((FluidPipeEntity) entity).getFluidStack().isEmpty())
                    result.add(direction);
                else if (bl && ((FluidPipeEntity) entity).getFluidStack().getFluid() == stack.getFluid())
                    result.add(direction);
                continue;
            }
            FluidStack temp = stack.copy();
            temp.setAmount(Fraction.ONE_THOUSANDTH);
            if (entity instanceof FluidInventory) {
                FluidInventory dInv = (FluidInventory) entity;

                if (dInv.canAdd(temp)) {
                    int[] slots = getAvailableSlots(dInv, direction.getOpposite()).toArray();
                    for (int i : slots) {
                        if (canInsert(dInv, temp, i, direction.getOpposite())) {
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
        if (!inventory.isValidExternal(slot, stack)) {
            return false;
        }
        return inventory.canInsert(slot, stack, side);
    }

    private static boolean canExtract(FluidInventory inv, FluidStack stack, int slot, Direction facing) {
        return inv.canExtract(slot, stack, facing);
    }

    private static boolean isInventoryEmpty(FluidInventory inv, Direction facing) {
        return getAvailableSlots(inv, facing).allMatch((i) -> inv.getFluidStack(i).isEmpty());
    }

    private static IntStream getAvailableSlots(FluidInventory inventory, Direction side) {
        return IntStream.of(inventory.getAvailableFluidSlots(side));
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


    public FluidFilterInventory getFilter() {
        return filter;
    }


    public PipeSide getSide(Direction d) {
        if (world == null)
            return null;
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof FluidPipe))
            return null;
        return state.get(FluidPipe.getProperty(d));
    }

    public boolean hasServo(Direction d) {
        return getSide(d) == PipeSide.SERVO;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag fluid = new CompoundTag();
        this.currentFluid.toTag(fluid);
        tag.put("fluid", fluid);
        tag.putInt("wrenched", Formatter.mapToInt(wrenched));
        tag.put("filter", filter.toTag());
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        this.setFluidStack(FluidStack.fromTag(tag.getCompound("fluid")));
        this.wrenched = Formatter.intToMap(tag.getInt("wrenched"));
        CompoundTag filter = tag.getCompound("filter");
        this.filter = FluidFilterInventory.fromTag(filter, 1);
        super.fromTag(state, tag);
    }

    public FluidStack getFluidStack() {
        return currentFluid;
    }

    public void setFluidStack(FluidStack stack) {
        this.currentFluid = stack;
        renderTime = 2;

        if (world != null && !world.isClient)
            sync();
    }

    @Override
    public void tick() {
        if (world == null || world.isClient)
            return;

        if (!this.currentFluid.isEmpty() && renderTime == 0) {
            this.currentFluid = FluidStack.EMPTY;
            sync();
        } else if (renderTime > 0) {
            sync();
            renderTime--;
        }
        //Add to queue
        for (Direction d : Direction.values()) {
            if (hasServo(d)) {
                FluidInventory out = getInventoryAt(world, this.pos.offset(d));
                Direction opp = d.getOpposite();

                if (out == null || isInventoryEmpty(out, opp))
                    continue;

                int[] arr = getAvailableSlots(out, opp).toArray();
                for (int i : arr) {

                    FluidStack stack = out.getFluidStack(i);
                    if (!canExtract(out, stack, i, opp) || stack.isEmpty() || (!filter.hasNoFluids() && !filter.passFilterTest(stack)))
                        continue;

                    FluidStack one = stack.copy();
                    one.setAmount(Math.min(perTick, stack.getAmount()));
                    FluidPipeResult result = findDestination(one, this.pos.offset(d));
                    if (result != null) {
                        FluidInventory inv = (FluidInventory) world.getBlockEntity(result.getDestination());
                        assert inv != null;
                        inv.addExternal(new FluidStack(one.getFluid(), (int) result.getAmount()));
                        stack.decrement((int) result.getAmount());
                        markDirty();

                        for (BlockPos pos : result.getPath()) {
                            BlockEntity e = world.getBlockEntity(pos);
                            if (e instanceof FluidPipeEntity) {
                                ((FluidPipeEntity) e).setFluidStack(new FluidStack(one.getFluid(), 1));
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
        if (!corrected && world != null && !world.isClient) {
            correct();
        }
    }

    public void correct() {
        assert world != null;
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof FluidPipe)
            this.perTick = ((FluidPipe) block).getMaxTransferRate();
        corrected = true;
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

            if (entity instanceof FluidPipeEntity) {
                ArrayList<Direction> neighbors = FluidPipeEntity.transferableDirections(current, world, stack);
                for (Direction d : neighbors) {
                    if (!visited.contains(current.offset(d))) {
                        ArrayList<BlockPos> newPath = new ArrayList<>(popped.getPath());
                        newPath.add(current);

                        int amount = (int) Math.min(stack.getAmount(), popped.getAmount());
                        BlockEntity entity1 = world.getBlockEntity(current.offset(d));
                        if (entity1 instanceof FluidPipeEntity) {
                            amount = Math.min(amount, ((FluidPipeEntity) entity1).getPerTick());
                        }
                        if (entity1 instanceof FluidInventory) {
                            FluidInventory dInv = (FluidInventory) entity1;
                            int[] slots = getAvailableSlots(dInv, d.getOpposite()).toArray();
                            for (int i : slots) {
                                FluidStack temp = stack.copy();
                                temp.setAmount((1));
                                if (canInsert(dInv, temp, i, d.getOpposite())) {
                                    amount = Math.min(amount, dInv.slotCapacity() - dInv.getFluidStack(i).getAmount());
                                    break;
                                }
                            }
                        }
                        to_visit.add(new FluidPipeResult(current.offset(d), amount, d.getOpposite(), newPath));
                    }
                }
            }
            visited.add(current);
        }

        return null;
    }

    public int getPerTick() {
        return perTick;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.setFluidStack(FluidStack.fromTag(compoundTag.getCompound("fluid")));
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        CompoundTag fluid = new CompoundTag();
        this.currentFluid.toTag(fluid);
        compoundTag.put("fluid", fluid);
        return compoundTag;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(this.filter.getFilterType());
        buf.writeBoolean(this.filter.isMatchMod());
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.fluid_pipe");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FluidPipeScreenHandler(syncId, inv, this);
    }
}

