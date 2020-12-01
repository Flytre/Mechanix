package net.flytre.mechanix.block.generator;

import net.flytre.mechanix.base.energy.EnergyEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GeneratorBlockEntity extends EnergyEntity implements SidedInventory {

    protected DefaultedList<ItemStack> inventory;
    private int burnTime;
    private int fuelTime;
    private static final int genPerTick = 20;


    public GeneratorBlockEntity() {
        super(MachineRegistry.GENERATOR.getEntityType());

        setMaxEnergy(25000);
        setMaxTransferRate(20);
        panelMode = 1;
        setEnergyMode(true, true, true, true, true, true);
        setIOMode(false, false, false, false, false, false);

        inventory = DefaultedList.ofSize(1,ItemStack.EMPTY);
    }

    @Override
    public void updateDelegate() {
        super.updateDelegate();
        getProperties().set(8, burnTime);
        getProperties().set(9, fuelTime);
    }


    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);
        this.burnTime = tag.getShort("BurnTime");
        this.fuelTime = this.getFuelTime(this.inventory.get(0));
    }

    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putShort("BurnTime", (short) this.burnTime);
        Inventories.toTag(tag, this.inventory);
        return tag;
    }

    static Map<Item, Integer> createFuelTimeMap() {
        return AbstractFurnaceBlockEntity.createFuelTimeMap();
    }

    static boolean canUseAsFuel(ItemStack stack) {
        return createFuelTimeMap().containsKey(stack.getItem());
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public void repeatTick() {
        boolean isBurning = this.isBurning();
        boolean updateData = false;
        if (this.isBurning()) {
            --this.burnTime;
        }

        if (this.world != null && !this.world.isClient) {

            if(this.hasEnergy(this.getMaxEnergy() - genPerTick + 0.001))
                return;

            ItemStack fuelSlot = this.inventory.get(0);

            if(this.isBurning() || !fuelSlot.isEmpty()) {
                if (!this.isBurning()) {
                    this.burnTime = this.getFuelTime(fuelSlot);
                    this.fuelTime = this.burnTime;
                    if (this.isBurning()) {
                        updateData = true;
                        if (!fuelSlot.isEmpty()) {
                            Item item = fuelSlot.getItem();
                            fuelSlot.decrement(1);
                            if (fuelSlot.isEmpty()) {
                                Item item2 = item.getRecipeRemainder();
                                this.inventory.set(0, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                            }
                        }
                    }
                } else {
                    this.addEnergy(genPerTick);
                }
            }

            if (isBurning != this.isBurning()) {
                updateData = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(GeneratorBlock.LIT, this.isBurning()), 3);
            }
        }

        if (updateData) {
            this.markDirty();
        }
    }

    @Override
    public void onceTick() {

    }

    private int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return createFuelTimeMap().getOrDefault(item, 0) / 3;
        }
    }

    protected Text getContainerName() {
        return new TranslatableText("block.mechanix.generator");
    }

    public int[] getAvailableSlots(Direction side) {
        return new int[]{0};
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        //input mode & valid item
        return !ioMode.get(dir) && this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return ioMode.get(dir);
    }

    public int size() {
        return this.inventory.size();
    }


    public boolean isEmpty() {
        return inventory.get(0) == ItemStack.EMPTY;
    }
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

    }

    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    public boolean isValid(int slot, ItemStack stack) {
            ItemStack itemStack = this.inventory.get(0);
            return canUseAsFuel(stack) || stack.getItem() == Items.BUCKET && itemStack.getItem() != Items.BUCKET;
    }

    public void clear() {
        this.inventory.clear();
    }


    @Override
    public Text getDisplayName() {
        return getContainerName();
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new GeneratorScreenHandler(syncId, inv, this, getProperties());
    }
}
