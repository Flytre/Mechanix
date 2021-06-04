package net.flytre.mechanix.block.generator;

import net.flytre.mechanix.api.machine.GenericMachineEntity;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.util.ItemRegistry;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * craftTime represents how long the item is burning for in this case
 */
public class GeneratorEntity extends GenericMachineEntity<SmeltingRecipe> {

    private static Map<Item, Integer> FUEL_CACHE = null;

    public GeneratorEntity() {
        super(MachineRegistry.GENERATOR.getEntityType(), null, DefaultedList.ofSize(1, ItemStack.EMPTY), -80);
        setRecipeSupplier(() -> world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this, world).orElse(null));
        setEnergyMode(true, true, true, true, true, true);
    }

    private static Map<Item, Integer> createFuelTimeMap() {
        return FUEL_CACHE == null ? FUEL_CACHE = AbstractFurnaceBlockEntity.createFuelTimeMap() : FUEL_CACHE;
    }

    public static boolean canUseAsFuel(ItemStack stack) {
        return createFuelTimeMap().containsKey(stack.getItem());
    }

    @Override
    public void updateDelegate() {
        super.updateDelegate();
        //No recipe null check
        getDelegate().set(4, (int) craftTime);
        getDelegate().set(5, calcCraftTime());
    }

    private boolean isBurning() {
        return this.craftTime > 0;
    }

    @Override
    protected int calcCraftTime() {
        return this.getFuelTime(this.items.get(0));
    }

    @Override
    public void onceTick() {
    }

    @Override
    protected boolean matches(SmeltingRecipe recipe) {
        return recipe.matches(this, world);
    }

    private int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return createFuelTimeMap().getOrDefault(item, 0) / 3;
        }
    }

    @Override
    public void repeatTick() {
        boolean isBurning = this.isBurning();
        boolean updateData = false;
        if (this.isBurning()) {
            --this.craftTime;
        }

        if (this.world != null && !this.world.isClient) {

            if (this.hasEnergy(this.getMaxEnergy() + costPerTick))
                return;

            ItemStack fuelSlot = this.items.get(0);

            if (this.isBurning() || !fuelSlot.isEmpty()) {
                if (!this.isBurning()) {
                    this.craftTime = this.getFuelTime(fuelSlot);
                    if (this.isBurning()) {
                        updateData = true;
                        if (!fuelSlot.isEmpty()) {
                            Item item = fuelSlot.getItem();
                            fuelSlot.decrement(1);
                            if (fuelSlot.isEmpty()) {
                                Item item2 = item.getRecipeRemainder();
                                this.items.set(0, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                            }
                        }
                    }
                } else {
                    this.addEnergy(-costPerTick);
                }
            }

            if (isBurning != this.isBurning()) {
                updateData = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(MachineBlock.ACTIVATED, this.isBurning()), 3);
            }
        }

        if (updateData) {
            this.markDirty();
        }
    }

    @Override
    protected void craft(SmeltingRecipe recipe) {
    }

    @Override
    protected boolean canAcceptRecipeOutput(SmeltingRecipe recipe) {
        return false;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.generator");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new GeneratorHandler(syncId, inv, this, getDelegate());
    }

}
