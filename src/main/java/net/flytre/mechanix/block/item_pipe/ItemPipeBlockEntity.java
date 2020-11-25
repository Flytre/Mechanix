package net.flytre.mechanix.block.item_pipe;

import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

public class ItemPipeBlockEntity extends BlockEntity implements Tickable {

    public final HashMap<Direction, Boolean> servo;
    private final Queue<PipeResult> items;
    private int cooldown;

    public ItemPipeBlockEntity() {
        super(MachineRegistry.ITEM_PIPE_ENTITY);
        servo = new HashMap<>();
        items = new LinkedList<>();
        servoSides(false, false, false, false, false, false);
    }

    public static ArrayList<Direction> transferrableDirections(BlockPos startingPos, World world, ItemStack stack) {
        ArrayList<Direction> result = new ArrayList<>();

        if (world == null)
            return result;

        BlockEntity me = world.getBlockEntity(startingPos);

        for (Direction direction : Direction.values()) {

            if (me instanceof ItemPipeBlockEntity && ((ItemPipeBlockEntity) me).servo.get(direction))
                continue;
            BlockPos pos = startingPos.offset(direction);
            BlockEntity entity = world.getBlockEntity(pos);


            if (entity instanceof ItemPipeBlockEntity)
                result.add(direction);

            if (entity instanceof Inventory) {
                Inventory dInv = (Inventory) entity;
                int[] slots = getAvailableSlots(dInv, direction.getOpposite()).toArray();
                for (int i : slots) {
                    ItemStack currentStack = dInv.getStack(i);
                    if (canInsert(dInv, stack, i, direction.getOpposite())) {
                        if (currentStack.isEmpty() || (canMergeItems(currentStack,stack) && currentStack.getCount() < currentStack.getMaxCount()) ) {
                            result.add(direction);
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        if (first.getItem() != second.getItem()) {
            return false;
        } else if (first.getDamage() != second.getDamage()) {
            return false;
        } else if (first.getCount() > first.getMaxCount()) {
            return false;
        } else {
            return ItemStack.areTagsEqual(first, second);
        }
    }

    private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
        if (!inventory.isValid(slot, stack)) {
            return false;
        } else {
            return !(inventory instanceof SidedInventory) || ((SidedInventory) inventory).canInsert(slot, stack, side);
        }
    }

    private static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
        return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canExtract(slot, stack, facing);
    }

    private static boolean isInventoryEmpty(Inventory inv, Direction facing) {
        return getAvailableSlots(inv, facing).allMatch((i) -> inv.getStack(i).isEmpty());
    }

    private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
        return inventory instanceof SidedInventory ? IntStream.of(((SidedInventory) inventory).getAvailableSlots(side)) : IntStream.range(0, inventory.size());
    }

    @Nullable
    public static Inventory getInventoryAt(World world, BlockPos blockPos) {
        return getInventoryAt(world, (double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D);
    }

    @Nullable
    public static Inventory getInventoryAt(World world, double x, double y, double z) {
        Inventory inventory = null;
        BlockPos blockPos = new BlockPos(x, y, z);
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block instanceof InventoryProvider) {
            inventory = ((InventoryProvider) block).getInventory(blockState, world, blockPos);
        } else if (block.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof Inventory) {
                inventory = (Inventory) blockEntity;
                if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
                    inventory = ChestBlock.getInventory((ChestBlock) block, blockState, world, blockPos, true);
                }
            }
        }

        if (inventory == null) {
            List<Entity> list = world.getOtherEntities((Entity) null, new Box(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntityPredicates.VALID_INVENTORIES);
            if (!list.isEmpty()) {
                inventory = (Inventory) list.get(world.random.nextInt(list.size()));
            }
        }

        return inventory;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("cooldown", this.cooldown);

        ListTag list = new ListTag();
        for (PipeResult piped : items)
            list.add(piped.toTag(new CompoundTag()));
        tag.put("queue", list);

        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        for (Direction dir : Direction.values()) {
            EnumProperty<PipeSide> property = ItemPipe.getProperty(dir);
            if (state.get(property) == PipeSide.SERVO)
                servo.put(dir, true);
            else
                servo.put(dir, false);
        }
        this.cooldown = tag.getInt("cooldown");
        ListTag list = tag.getList("queue", 10);
        for (int i = 0; i < list.size(); i++) {
            PipeResult result = PipeResult.fromTag(list.getCompound(i));
            items.add(result);
        }
        super.fromTag(state, tag);
    }

    @Override
    public void tick() {
        if (world == null || world.isClient)
            return;
        //Add to queue
        if (cooldown <= 0) {
            for (Direction d : Direction.values()) {
                if (servo.get(d) && cooldown <= 0) {
                    Inventory out = getInventoryAt(world, this.pos.offset(d));
                    Direction opp = d.getOpposite();

                    if (out == null || isInventoryEmpty(out, opp))
                        continue;

                    int[] arr = getAvailableSlots(out, opp).toArray();
                    for (int i : arr) {

                        ItemStack stack = out.getStack(i);
                        if(!canExtract(out,stack,i,opp))
                            continue;

                        if (stack.isEmpty())
                            continue;
                        ItemStack one = stack.copy();
                        one.setCount(1);
                        PipeResult result = findDestination(one, this.pos.offset(d));
                        if (result != null) {
                            items.add(result);
                            stack.decrement(1);
                            cooldown = 10;
                            markDirty();
                            break;
                        }
                    }
                }
            }
        }


        //Remove from queue
        boolean succeeded = transferItem();
        if (!succeeded) {
            MinecraftClient.getInstance().player.sendMessage(Text.of("FAILED"), true);
        }

        if (cooldown > 0)
            cooldown--;
    }

    private boolean transferItem() {
        //Remove from queue
        if (items.size() > 0) {
            PipeResult processed = items.remove();
            BlockEntity destination = world.getBlockEntity(processed.getDestination());

            if (!(destination instanceof Inventory)) {
                items.add(processed);
                return false;
            } else {
                Inventory dInv = (Inventory) destination;

                if (isInventoryFull(dInv, processed.getDirection())) {
                    items.add(processed);
                    return false;
                }

                int[] slots = getAvailableSlots(dInv, processed.getDirection()).toArray();
                for (int i : slots) {
                    ItemStack currentStack = dInv.getStack(i);
                    if (canInsert(dInv, processed.getStack(), i, processed.getDirection())) {
                        if (currentStack.isEmpty()) {
                            dInv.setStack(i, processed.getStack());
                            dInv.markDirty();
                            return true;
                        } else if (canMergeItems(currentStack, processed.getStack())) {
                            if (currentStack.getCount() < currentStack.getMaxCount()) {
                                currentStack.increment(1);
                                dInv.markDirty();
                                return true;
                            }
                        }
                    }
                }
            }
            items.add(processed);
        }
        return items.size() == 0;
    }

    private boolean isInventoryFull(Inventory inv, Direction direction) {
        return getAvailableSlots(inv, direction).allMatch((i) -> {
            ItemStack itemStack = inv.getStack(i);
            return itemStack.getCount() >= itemStack.getMaxCount();
        });
    }

    public @Nullable PipeResult findDestination(ItemStack stack, BlockPos start) {
        if (world == null)
            return null;

        Deque<PipeResult> to_visit = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        to_visit.add(new PipeResult(this.getPos(), new ArrayList<>(), stack, Direction.NORTH));

        while (to_visit.size() > 0) {
            PipeResult popped = to_visit.pop();
            BlockPos current = popped.getDestination();
            ArrayList<BlockPos> path = popped.getPath();
            BlockEntity entity = world.getBlockEntity(current);
            if (!current.equals(start) && entity instanceof Inventory) {
                return popped;
            }
            ArrayList<Direction> neighbors = ItemPipeBlockEntity.transferrableDirections(current, world, stack);
            for (Direction d : neighbors) {
                if (!visited.contains(current.offset(d))) {
                    ArrayList<BlockPos> newPath = new ArrayList<>(path);
                    newPath.add(current);
                    to_visit.add(new PipeResult(current.offset(d), newPath, stack, d.getOpposite()));
                }
            }
            visited.add(current);
        }

        return null;
    }

    public void servoSides(boolean up, boolean down, boolean north, boolean east, boolean south, boolean west) {
        servo.put(Direction.UP, up);
        servo.put(Direction.DOWN, down);
        servo.put(Direction.NORTH, north);
        servo.put(Direction.EAST, east);
        servo.put(Direction.SOUTH, south);
        servo.put(Direction.WEST, west);
    }


}
