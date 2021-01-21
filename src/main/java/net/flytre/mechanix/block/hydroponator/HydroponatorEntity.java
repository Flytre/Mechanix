package net.flytre.mechanix.block.hydroponator;

import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.recipe.HydroponatorRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class HydroponatorEntity extends MachineEntity<DoubleInventory, HydroponatorRecipe> implements DoubleInventory {

    private final DefaultedList<FluidStack> fluidInventory;

    //input, fertilizer, 2 output
    public HydroponatorEntity() {
        super(MachineRegistry.HYDROPONATOR.getEntityType(), DefaultedList.ofSize(4, ItemStack.EMPTY),
                (World world, DoubleInventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.HYDROPONATOR_RECIPE, inventory, world).orElse(null)
                , 60);
        fluidInventory = DefaultedList.ofSize(1, FluidStack.EMPTY);
    }

    @Override
    public void updateDelegate() {
        super.updateDelegate();
        getProperties().set(10,getFluidStack(0).getAmount());
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
        return new TranslatableText("block.mechanix.hydroponator");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new HydroponatorScreenHandler(syncId,inv,this,getProperties());
    }

    @Override
    protected void craft(HydroponatorRecipe recipe) {
        ItemStack primary = recipe.getOutputProvider(0).getStack();
        ItemStack secondary = recipe.getOutputProvider(1).getStack();
        if (this.getStack(2).isEmpty()) {
            this.setStack(2, primary);
        } else {
            if (EasyInventory.canMergeItems(getStack(2), primary)) {
                this.getStack(2).increment(primary.getCount());
            }
        }
        if (Math.random() < recipe.getOutputProvider(1).getChance()) {
            if (this.getStack(3).isEmpty()) {
                this.setStack(3, secondary);
            } else if (EasyInventory.canMergeItems(getStack(3), secondary)) {
                this.getStack(3).increment(secondary.getCount());
            }
        }
        getStack(0).decrement(recipe.getInput().getQuantity());
        getStack(1).decrement(recipe.getFertilizer().getQuantity());
        getFluidStack(0).decrement(500);
    }

    @Override
    protected boolean canAcceptRecipeOutput(HydroponatorRecipe recipe) {
        if (recipe == null)
            return false;
        return (getStack(2).isEmpty() || EasyInventory.canMergeItems(getStack(2), recipe.getOutputProvider(0).getStack()))
                && (getStack(3).isEmpty() || EasyInventory.canMergeItems(getStack(3), recipe.getOutputProvider(1).getStack()));
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        FluidInventory.fromTag(tag,fluidInventory);
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        FluidInventory.toTag(tag,fluidInventory);
        return super.toTag(tag);
    }


    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if(slot == 0)
            return stack.getItem() != Items.BONE_MEAL;
        if(slot == 1)
            return stack.getItem() == Items.BONE_MEAL;
        return false;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot < 2 && super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot > 1 && super.canExtract(slot, stack, dir);
    }
}
