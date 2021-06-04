package net.flytre.mechanix.block.quarry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.common.inventory.EasyInventory;
import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.flytre_lib.common.util.InventoryUtils;
import net.flytre.mechanix.api.energy.TieredEnergyEntityWithItems;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.api.machine.MachineOverlay;
import net.flytre.mechanix.api.upgrade.UpgradeInventory;
import net.flytre.mechanix.util.ItemRegistry;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class QuarryEntity extends TieredEnergyEntityWithItems implements EasyInventory, UpgradeInventory, MachineOverlay {

    private final DefaultedList<ItemStack> items;
    private final DefaultedList<ItemStack> upgrades;
    private int x; //-16 to + 16
    private int y; //-1 to -bedrock
    private int z; //- 16 to + 16
    private double delta; //time until next block break

    public QuarryEntity() {
        super(MachineRegistry.QUARRY.getEntityType());
        items = DefaultedList.ofSize(21, ItemStack.EMPTY);
        upgrades = DefaultedList.ofSize(4, ItemStack.EMPTY);
        x = pos.getX() - 16;
        y = pos.getY() - 1;
        z = pos.getZ() - 16;
        setEnergyMode(false, false, false, false, false, false);
        setIOMode(IOType.INPUT, IOType.OUTPUT, IOType.OUTPUT, IOType.OUTPUT, IOType.OUTPUT, IOType.OUTPUT);
        setMaxEnergy(1000000);
        setMaxTransferRate(500);
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
    public DefaultedList<ItemStack> getUpgrades() {
        return upgrades;
    }

    public int[] getQuarryPos() {
        return new int[]{x, y, z};
    }

    @Override
    public int getPanelType() {
        return 1;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        Inventories.fromTag(tag, items);
        UpgradeInventory.fromTag(tag, upgrades);
        CompoundTag quarry = tag.getCompound("quarry");
        x = quarry.getInt("x");
        y = quarry.getInt("y");
        z = quarry.getInt("z");
        delta = quarry.getDouble("delta");
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, items);
        UpgradeInventory.toTag(tag, upgrades);
        CompoundTag quarry = new CompoundTag();
        quarry.putInt("x", x);
        quarry.putInt("y", y);
        quarry.putInt("z", z);
        quarry.putDouble("delta", delta);
        tag.put("quarry", quarry);
        return super.toTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        CompoundTag quarry = new CompoundTag();
        quarry.putInt("x", x);
        quarry.putInt("y", y);
        quarry.putInt("z", z);
        quarry.putDouble("delta", delta);
        tag.put("quarry", quarry);
        return super.toClientTag(tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        CompoundTag quarry = tag.getCompound("quarry");
        x = quarry.getInt("x");
        y = quarry.getInt("y");
        z = quarry.getInt("z");
        delta = quarry.getDouble("delta");
        super.fromClientTag(tag);
    }

    @Override
    public void repeatTick() {
        //get & store pos
        //break blocks
        //fill fluids with blocks
        //end when done
    }

    private void updateActivated(boolean should) {
        if (should != world.getBlockState(getPos()).get(MachineBlock.ACTIVATED)) {
            world.setBlockState(getPos(), world.getBlockState(pos).with(MachineBlock.ACTIVATED, should));
        }
    }

    @Override
    public void onceTick() {

        if (world == null || world.isClient || !(world instanceof ServerWorld))
            return;


        double costSpeedMultiplier = 0.75d * getTier() + 1.0d;
        double transferMultiplier = costSpeedMultiplier;
        double energyMultiplier = 1.0;
        if (hasUpgrade(ItemRegistry.OVERCLOCKER))
            costSpeedMultiplier *= 0.5d * upgradeQuantity(ItemRegistry.OVERCLOCKER) + 1;
        if (hasUpgrade(ItemRegistry.ENERGY_SAVER))
            energyMultiplier *= 0.4d * upgradeQuantity(ItemRegistry.ENERGY_SAVER) + 1;

        if (!isFull())
            requestEnergy(Math.min(150 * transferMultiplier, getMaxEnergy() - getEnergy()));

        if (!this.hasEnergy(75 * costSpeedMultiplier * 1 / energyMultiplier)) {
            updateActivated(false);
            return;
        }
        if (x == 16 && z == 16 && getPos().getY() + y <= 0) {
            updateActivated(false);
            return;
        }
        this.removeEnergy(75 * costSpeedMultiplier * 1 / energyMultiplier);
        updateActivated(true);

        int aX = x + pos.getX();
        int aY = pos.getY() + y;
        int aZ = pos.getZ() + z;
        delta += costSpeedMultiplier;
        if (delta >= 100) {
            sync();
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
            if (!(state.getBlock() instanceof AirBlock)) {
                List<ItemStack> dropped = Block.getDroppedStacks(state, (ServerWorld) world, pos, this, null, toolStack);
                addDroppedStacks(dropped);
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                if (toolStack.damage(1, world.random, null))
                    toolStack.decrement(1);
            }
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
            stack = InventoryUtils.putStackInInventory(stack, this, 1, 21);
            if (!stack.isEmpty()) {
                ItemScatterer.spawn(world, getPos().getX(), getPos().getY(), getPos().getZ(), stack);
            }
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.quarry");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new QuarryHandler(syncId, inv, this, getDelegate());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public double getSquaredRenderDistance() {
        return 4096D;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot != 0 && EasyInventory.super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot != 0 && EasyInventory.super.canExtract(slot, stack, dir);
    }


    @Override
    public Set<Item> validUpgrades() {
        return new HashSet<Item>() {{
            add(ItemRegistry.OVERCLOCKER);
            add(ItemRegistry.ENERGY_SAVER);
        }};
    }


}
