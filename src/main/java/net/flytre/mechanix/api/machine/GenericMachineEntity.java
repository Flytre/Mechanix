package net.flytre.mechanix.api.machine;

import net.flytre.flytre_lib.common.inventory.EasyInventory;
import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.energy.TieredEnergyEntityWithItems;
import net.flytre.mechanix.api.upgrade.UpgradeInventory;
import net.flytre.mechanix.recipe.MechanixRecipeType;
import net.flytre.mechanix.recipe.ReloadTracker;
import net.flytre.mechanix.util.ItemRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public abstract class GenericMachineEntity<R extends Recipe<?>> extends TieredEnergyEntityWithItems implements EasyInventory, UpgradeInventory {
    protected final DefaultedList<ItemStack> items;
    protected final DefaultedList<ItemStack> upgrades;
    protected final @Nullable RecipeType<?> recipeType;
    protected Supplier<R> recipeSupplier;
    protected double craftTime;
    protected int costPerTick;
    protected R recipe;
    private Set<R> recipeCache;
    private long lastReloadTime = -1L;

    public GenericMachineEntity(BlockEntityType<?> type, @Nullable RecipeType<?> recipeType, DefaultedList<ItemStack> items, int costPerTick) {
        super(type);
        this.recipeType = recipeType;
        this.items = items;
        this.upgrades = DefaultedList.ofSize(4, ItemStack.EMPTY);

        //Defaults
        setEnergyMode(false, false, false, false, false, false);
        setIOMode(IOType.INPUT, IOType.OUTPUT, IOType.BOTH, IOType.BOTH, IOType.BOTH, IOType.BOTH);
        setMaxEnergy(300000);
        setMaxTransferRate(costPerTick * 2);
        recipe = null;

        this.costPerTick = costPerTick;
        this.recipeCache = new HashSet<>();
    }

    public void setRecipeSupplier(Supplier<R> recipeSupplier) {
        this.recipeSupplier = recipeSupplier;
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


    @Override
    public DefaultedList<ItemStack> getUpgrades() {
        return upgrades;
    }

    protected int calcCraftTime() {
        return 200;
    }

    @Override
    public void updateDelegate() {
        super.updateDelegate();

        if (recipe != null) {
            getDelegate().set(4, (int) craftTime);
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

        if (lastReloadTime != ((ReloadTracker) world.getRecipeManager()).lastReloadTime()) {
            lastReloadTime = ((ReloadTracker) world.getRecipeManager()).lastReloadTime();
            recipeCache.clear();
        }

        boolean currActivated = world.getBlockState(getPos()).get(MachineBlock.ACTIVATED);
        boolean shouldBeActivated = false;
        boolean reset = false;
        double costSpeedMultiplier = 0.75d * getTier() + 1.0d;
        double transferMultiplier = costSpeedMultiplier;
        double energyMultiplier = 1.0;
        if (hasUpgrade(ItemRegistry.OVERCLOCKER))
            costSpeedMultiplier *= 0.5d * upgradeQuantity(ItemRegistry.OVERCLOCKER) + 1;
        if (hasUpgrade(ItemRegistry.ENERGY_SAVER))
            energyMultiplier *= 0.4d * upgradeQuantity(ItemRegistry.ENERGY_SAVER) + 1;

        if (!isFull()) {
            if (getMaxTransferRate() < costPerTick * 2 * transferMultiplier)
                setMaxTransferRate(costPerTick * 2 * transferMultiplier);
            requestEnergy(Math.min(costPerTick * 2 * transferMultiplier, getMaxEnergy() - getEnergy()));
        }

        recipe = recipeCache.stream().filter(this::matches).findFirst().orElse(null);
        if (recipe == null) {
            recipe = recipeSupplier.get();
            if (recipe != null)
                recipeCache.add(recipe);
        }

        if (this.hasEnergy(costPerTick * costSpeedMultiplier * 1 / energyMultiplier) && recipe != null && canAcceptRecipeOutput(recipe)) {
            this.addEnergy(-costPerTick * costSpeedMultiplier * 1 / energyMultiplier);
            shouldBeActivated = true;
            this.craftTime += costSpeedMultiplier;
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


    protected abstract boolean matches(R recipe);

    protected abstract void craft(R recipe);

    protected abstract boolean canAcceptRecipeOutput(R recipe);

    @Override
    public void repeatTick() {
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        Inventories.fromTag(tag, items);
        UpgradeInventory.fromTag(tag, upgrades);
        this.craftTime = tag.getDouble("craftTime");
        recipeCache = new HashSet<>();
        super.fromTag(state, tag);
    }


    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, items);
        UpgradeInventory.toTag(tag, upgrades);
        tag.putDouble("craftTime", craftTime);
        return super.toTag(tag);
    }

    @Override
    public Set<Item> validUpgrades() {
        return new HashSet<Item>() {{
            add(ItemRegistry.OVERCLOCKER);
            add(ItemRegistry.ENERGY_SAVER);
        }};
    }

    @Override
    public boolean isValidUpgrade(ItemStack stack) {
        return UpgradeInventory.super.isValidUpgrade(stack) || (recipeType instanceof MechanixRecipeType && ((MechanixRecipeType<?>) recipeType).test(stack));
    }
}
