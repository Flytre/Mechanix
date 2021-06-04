package net.flytre.mechanix.api.machine;

import net.flytre.flytre_lib.common.inventory.EasyInventory;
import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.energy.TieredEnergyEntityWithItems;
import net.flytre.mechanix.recipe.MechanixRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public abstract class MachineEntity<T extends Inventory, R extends MechanixRecipe<T>> extends TieredEnergyEntityWithItems implements EasyInventory {
    private final DefaultedList<ItemStack> items;
    private final BiFunction<World, T, R> recipeSupplier;
    private int craftTime;
    private int costPerTick;
    private R recipe;

    public MachineEntity(BlockEntityType<?> type, DefaultedList<ItemStack> items, BiFunction<World, T, R> recipeSupplier, int costPerTick) {
        super(type);
        this.items = items;

        //Defaults
        setEnergyMode(false, false, false, false, false, false);
        setIOMode(IOType.INPUT, IOType.OUTPUT, IOType.BOTH, IOType.BOTH, IOType.BOTH, IOType.BOTH);
        setMaxEnergy(300000);
        setMaxTransferRate(200);
        recipe = null;


        this.recipeSupplier = recipeSupplier;
        this.costPerTick = costPerTick;
    }

    @Override
    public int getPanelType() {
        return 1;
    }

    public int getCostPerTick() {
        return costPerTick;
    }

    public void setCostPerTick(int costPerTick) {
        this.costPerTick = costPerTick;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }


    private int calcCraftTime() {
        return recipe != null ? recipe.getCraftTime() : 200;
    }

    @Override
    public void updateDelegate() {
        super.updateDelegate();

        if (recipe != null) {
            getDelegate().set(4, craftTime);
            getDelegate().set(5, calcCraftTime());
        } else {
            getDelegate().set(4, 0);
            getDelegate().set(5, 1);
        }
    }


    @Override
    public void onceTick() {
        if (this.world == null || this.world.isClient)
            return;
        boolean currActivated = world.getBlockState(getPos()).get(MachineBlock.ACTIVATED);
        boolean shouldBeActivated = false;
        boolean reset = false;
        int tierTimes = getTier() + 1;
        if (!isFull()) {
            if (getMaxTransferRate() < costPerTick * 2 * tierTimes)
                setMaxTransferRate(costPerTick * 2 * tierTimes);
            requestEnergy(Math.min(costPerTick * 2 * tierTimes, getMaxEnergy() - getEnergy()));
        }
        recipe = recipeSupplier.apply(world, (T) this);
        if (this.hasEnergy(costPerTick * tierTimes) && recipe != null && recipe.canAcceptRecipeOutput((T) this)) {
            this.addEnergy(-costPerTick * tierTimes);
            shouldBeActivated = true;
            this.craftTime += tierTimes;
            if (this.craftTime >= calcCraftTime()) {
                recipe.craft((T) this);
                reset = true;
            }
        } else
            reset = true;

        if (reset)
            craftTime = 0;

        if (shouldBeActivated != currActivated) {
            world.setBlockState(getPos(), world.getBlockState(pos).with(MachineBlock.ACTIVATED, shouldBeActivated));
        }

    }

    @Override
    public void repeatTick() {
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        Inventories.fromTag(tag, items);
        this.craftTime = tag.getInt("craftTime");
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, items);
        tag.putInt("craftTime", craftTime);
        return super.toTag(tag);
    }

}
