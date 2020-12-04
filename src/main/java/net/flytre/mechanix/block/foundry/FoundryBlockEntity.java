package net.flytre.mechanix.block.foundry;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.util.FluidRegistry;
import net.flytre.mechanix.util.ItemRegistery;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
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

    private static final HashMap<Fluid, Item> recipes;

    static {
        recipes = new HashMap<>();
        recipes.put(FluidRegistry.STILL_PERLIUM, ItemRegistery.PERLIUM_INGOT);
    }

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
    public boolean isValid(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public boolean isValid(int slot, FluidStack stack) {
        return DoubleInventory.super.isValid(slot,stack)
                && recipes.containsKey(stack.getFluid());
    }

    public boolean canCraft() {
        FluidStack stack = getFluidStack(0);
        if(stack.getAmount() < 1000)
            return false;
        Item result = recipes.get(stack.getFluid());
        return isEmpty() || EasyInventory.canMergeItems(getStack(0),new ItemStack(result));
    }


    public void craft() {
        if(!canCraft())
            return;
        FluidStack stack = getFluidStack(0);

        if(!isEmpty())
            getStack(0).increment(1);
        else
            setStack(0, new ItemStack(recipes.get(stack.getFluid())));

        stack.decrement(1000);
    }

    @Override
    public void onceTick() {
        if(this.world == null || this.world.isClient)
            return;

        boolean currActivated = world.getBlockState(getPos()).get(FoundryBlock.ACTIVATED);
        boolean shouldBeActivated = false;
        boolean reset = false;
        int tierTimes = getTier() + 1;
        if(getEnergy() + 100*tierTimes < getMaxEnergy())
            requestEnergy(100*tierTimes);
        if(this.hasEnergy(60*tierTimes) && this.canCraft()) {
                this.addEnergy(-60*tierTimes);
                shouldBeActivated = true;
                this.craftTime -= tierTimes;
                if(this.craftTime <= 0)
                    craft();
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

    @Override
    public void repeatTick() {}
}
