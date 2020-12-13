package net.flytre.mechanix.block.foundry;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.recipe.FoundryRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class FoundryBlockEntity extends EnergyEntity implements DoubleInventory {

    private final DefaultedList<ItemStack> itemInventory;
    private final DefaultedList<FluidStack> fluidInventory;
    private int craftTime;

    public FoundryBlockEntity() {
        super(MachineRegistry.FOUNDRY.getEntityType());
        fluidInventory = DefaultedList.ofSize(1,FluidStack.EMPTY);
        itemInventory = DefaultedList.ofSize(1,ItemStack.EMPTY);
        setEnergyMode(false, false, false, false, false, false);
        setIOMode(false,true,false,false,false,false);
        setMaxEnergy(300000);
        setMaxTransferRate(200);
        panelMode = 1;
    }

    @Override
    public void updateDelegate() {
        super.updateDelegate();
        getProperties().set(8,craftTime);

        //remember if this goes above 32k with upgrades it needs fixing
        getProperties().set(9,getFluidStack(0).getAmount());
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        FluidInventory.fromTag(tag,fluidInventory);
        Inventories.fromTag(tag,itemInventory);
        this.craftTime = tag.getInt("craftTime");
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        FluidInventory.toTag(tag,fluidInventory);
        Inventories.toTag(tag,itemInventory);
        tag.putInt("craftTime",craftTime);
        return super.toTag(tag);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return itemInventory;
    }

    @Override
    public HashMap<Direction, Boolean> getItemIO() {
        return ioMode;
    }

    @Override
    public HashMap<Direction, Boolean> getFluidIO() {
        return ioMode;
    }

    @Override
    public DefaultedList<FluidStack> getFluids() {
        return fluidInventory;
    }

    @Override
    public int capacity() {
        return 8000;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.foundry");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FoundryScreenHandler(syncId,inv,this,this.getProperties());
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    public void craft(FoundryRecipe recipe) {
        getFluidStack(0).decrement(recipe.getInput().getAmount());
        ItemStack result = recipe.craft(this);
        if (this.getStack(0).isEmpty()) {
            this.setStack(0, result);
        } else {
            if (EasyInventory.canMergeItems(getStack(0), result)) {
                this.getStack(0).increment(result.getCount());
            }
        }
    }

    @Override
    public void onceTick() {
        if(this.world == null || this.world.isClient)
            return;

        boolean currActivated = world.getBlockState(getPos()).get(MachineBlock.ACTIVATED);
        boolean shouldBeActivated = false;
        boolean reset = false;
        int tierTimes = getTier() + 1;
        if(getEnergy() + 100*tierTimes < getMaxEnergy())
            requestEnergy(100*tierTimes);
        FoundryRecipe recipe = world.getRecipeManager().getFirstMatch(RecipeRegistry.FOUNDRY_RECIPE, this, this.world).orElse(null);
        if(this.hasEnergy(50*tierTimes) && canAcceptRecipeOutput(recipe)) {
                this.addEnergy(-50*tierTimes);
                shouldBeActivated = true;
                this.craftTime -= tierTimes;
                if(this.craftTime <= 0)
                    craft(recipe);
        } else

            reset = true;
        if(this.craftTime <= 0)
            reset = true;

        if(reset)
            craftTime = 120;

        if(shouldBeActivated != currActivated) {
            world.setBlockState(getPos(),world.getBlockState(pos).with(FoundryBlock.ACTIVATED,shouldBeActivated));
        }
    }

    private boolean canAcceptRecipeOutput(FoundryRecipe recipe) {
        if(recipe == null)
            return false;
        return this.getStack(0).isEmpty() || EasyInventory.canMergeItems(this.getStack(0),recipe.getOutput());
    }

    @Override
    public void repeatTick() {}
}
