package net.flytre.mechanix.api.machine;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.recipe.MechanixRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.function.BiFunction;

public abstract class MachineEntity<T extends Inventory, R extends Recipe<T>> extends EnergyEntity implements EasyInventory {
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
        setIOMode(false, true, false, false, false, false);
        setMaxEnergy(300000);
        setMaxTransferRate(200);
        panelMode = 1;
        recipe = null;


        this.recipeSupplier = recipeSupplier;
        this.costPerTick = costPerTick;
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

    @Override
    public HashMap<Direction, Boolean> getItemIO() {
        return ioMode;
    }


    private int calcCraftTime() {
        return recipe instanceof MechanixRecipe<?> ? ((MechanixRecipe<?>) recipe).getCraftTime() : 200;
    }

    @Override
    public void updateDelegate() {
        super.updateDelegate();

        if (recipe != null) {
            getProperties().set(8, craftTime);
            getProperties().set(9, calcCraftTime());
        } else {
            getProperties().set(8, 0);
            getProperties().set(9, 1);
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
        if (this.hasEnergy(costPerTick * tierTimes) && canAcceptRecipeOutput(recipe)) {
            this.addEnergy(-costPerTick * tierTimes);
            shouldBeActivated = true;
            this.craftTime += tierTimes;
            if (this.craftTime >= calcCraftTime()) {
                craft(recipe);
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

    ;

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


    protected abstract void craft(R recipe);

    protected abstract boolean canAcceptRecipeOutput(R recipe);

}
