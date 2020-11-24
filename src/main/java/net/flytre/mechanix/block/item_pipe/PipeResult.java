package net.flytre.mechanix.block.item_pipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public class PipeResult {
    private final ArrayList<BlockPos> path;
    private final BlockPos destination;
    private final ItemStack stack;
    private final Direction direction;

    public PipeResult(BlockPos destination,ArrayList<BlockPos> path, ItemStack stack, Direction direction) {
        this.path = path;
        this.destination = destination;
        this.stack = stack.copy();
        this.direction = direction;
    }

    public ArrayList<BlockPos> getPath() {
        return path;
    }

    public BlockPos getDestination() {
        return destination;
    }

    public Direction getDirection() {
        return direction;
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.put("end",posToTag(destination));
        ListTag list = new ListTag();
        for(BlockPos pathPos : path)
            list.add(posToTag(pathPos));
        tag.put("path",list);

        CompoundTag stack = new CompoundTag();
        System.out.println(this.stack);
        this.stack.toTag(stack);
        tag.put("stack",stack);
        tag.putString("dir",direction.asString());
        return tag;
    }

    public ItemStack getStack() {
        return stack;
    }

    public static PipeResult fromTag(CompoundTag tag) {
        BlockPos end = tagToPos(tag.getCompound("end"));

        ArrayList<BlockPos> path = new ArrayList<>();
        ListTag list = tag.getList("path",10);
        for(int i = 0; i < list.size(); i++) {
            CompoundTag element = list.getCompound(i);
            path.add(tagToPos(element));
        }

        CompoundTag stack = tag.getCompound("stack");
        ItemStack stack2 = ItemStack.fromTag(stack);
        Direction d = Direction.byName(tag.getString("dir"));
        return new PipeResult(end,path,stack2,d);
    }

    private static CompoundTag posToTag(BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        return tag;
    }

    private static BlockPos tagToPos(CompoundTag tag) {
        int x = tag.getInt("x");
        int y = tag.getInt("y");
        int z = tag.getInt("z");
        return new BlockPos(x,y,z);
    }


}
