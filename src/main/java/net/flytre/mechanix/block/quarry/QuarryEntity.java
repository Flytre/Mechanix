package net.flytre.mechanix.block.quarry;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class QuarryEntity extends EnergyEntity implements EasyInventory {

    private final DefaultedList<ItemStack> items;

    private int x; //-16 to + 16
    private int y; //-1 to -bedrock
    private int z; //- 16 to + 16
    private int delta; //time until next block break

    public QuarryEntity() {
        super(MachineRegistry.QUARRY.getEntityType());
        items = DefaultedList.ofSize(21, ItemStack.EMPTY);
        x = pos.getX() - 16;
        y = pos.getY() - 1;
        z = pos.getZ() - 16;
        delta = 0;
        setEnergyMode(false, false, false, false, false, false);
        setIOMode(false, true, false, false, false, false);
        setMaxEnergy(1000000);
        setMaxTransferRate(500);
        panelMode = 1;
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

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        Inventories.fromTag(tag, items);
        CompoundTag quarry = tag.getCompound("quarry");
        x = quarry.getInt("x");
        y = quarry.getInt("y");
        z = quarry.getInt("z");
        delta = quarry.getInt("delta");
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, items);
        CompoundTag quarry = new CompoundTag();
        quarry.putInt("x", x);
        quarry.putInt("y", y);
        quarry.putInt("z", z);
        quarry.putInt("delta", delta);
        tag.put("quarry", quarry);
        return super.toTag(tag);
    }

    @Override
    public void repeatTick() {
        //get & store pos
        //break blocks
        //fill fluids with blocks
        //end when done
    }

    @Override
    public void onceTick() {

        if (world == null || world.isClient || !(world instanceof ServerWorld))
            return;

        if (!isFull())
            requestEnergy(Math.min(150 * (getTier() + 1), getMaxEnergy() - getEnergy()));

        if (!this.hasEnergy(75 * getTier() + 1))
            return;
        if (x == 16 && z == 16 && getPos().getY() + y <= 0)
            return;
        this.removeEnergy(75 * getTier() + 1);
        int aX = x + pos.getX();
        int aY = pos.getY() + y;
        int aZ = pos.getZ() + z;
        delta += getTier() + 1;
        if (delta >= 100) {
            ItemStack toolStack = getStack(0);
            if (toolStack.isEmpty())
                return;
            BlockPos pos = new BlockPos(aX, aY, aZ);
            if (!world.canSetBlock(pos)) {
                incrementPos();
                return;
            }
            BlockState state = world.getBlockState(new BlockPos(aX, aY, aZ));
            if (state.getHardness(world, pos) < 0) {
                incrementPos();
                return;
            }
            List<ItemStack> dropped = Block.getDroppedStacks(state, (ServerWorld) world, pos, this, null, toolStack);
            addDroppedStacks(dropped);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            toolStack.damage(1, world.random, null);
            incrementPos();
            delta = 0;
        }
    }

    private void incrementPos() {
        y--;
        if (getPos().getY() + y <= 0) {
            y = -1;
            x++;
            if (x > 16) {
                x = -16;
                z++;
            }
        }
    }

    public void addDroppedStacks(List<ItemStack> dropped) {
        for (ItemStack stack : dropped) {
            for (int i = 1; i <= 20 && !stack.isEmpty(); i++) {
                stack = merge(stack, i);
            }
            if (!stack.isEmpty()) {
                ItemScatterer.spawn(world, getPos().getX(), getPos().getY(), getPos().getZ(), stack);
            }
        }
    }

    private ItemStack merge(ItemStack stack, int slot) {
        ItemStack itemStack = getStack(slot);
        boolean bl = false;

        if (itemStack.isEmpty()) {
            setStack(slot, stack);
            stack = ItemStack.EMPTY;
            bl = true;
        } else if (canMergeItems(itemStack, stack)) {
            int i = stack.getMaxCount() - itemStack.getCount();
            int j = Math.min(stack.getCount(), i);
            stack.decrement(j);
            itemStack.increment(j);
            bl = j > 0;
        }

        if (bl) {
            markDirty();
        }


        return stack;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public HashMap<Direction, Boolean> getItemIO() {
        return ioMode;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.quarry");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new QuarryScreenHandler(syncId, inv, this, getProperties());
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot != 0 && EasyInventory.super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot != 0 && EasyInventory.super.canExtract(slot, stack, dir);
    }

}
