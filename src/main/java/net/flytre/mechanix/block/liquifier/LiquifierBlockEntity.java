package net.flytre.mechanix.block.liquifier;

import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.recipe.LiquifierRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class LiquifierBlockEntity extends MachineEntity<LiquifierBlockEntity,LiquifierRecipe> implements DoubleInventory {

    private final DefaultedList<FluidStack> fluidInventory;

    public LiquifierBlockEntity() {
        super(MachineRegistry.LIQUIFIER.getEntityType(), DefaultedList.ofSize(1, ItemStack.EMPTY),
                (World world, LiquifierBlockEntity inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.LIQUIFIER_RECIPE, inventory, world).orElse(null)
                , 40);
        fluidInventory = DefaultedList.ofSize(1, FluidStack.EMPTY);
    }

    @Override
    public void updateDelegate() {
        super.updateDelegate();
        getProperties().set(10, getFluidStack(0).getAmount());
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        FluidInventory.fromTag(tag, fluidInventory);
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        FluidInventory.toTag(tag, fluidInventory);
        return super.toTag(tag);
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
        return new TranslatableText("block.mechanix.liquifier");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new LiquifierScreenHandler(syncId,inv,this,this.getProperties());
    }


    @Override
    protected void craft(LiquifierRecipe recipe) {
        getStack(0).decrement(recipe.getInput().getQuantity());
        if (!isFluidInventoryEmpty())
            getFluidStack(0).increment(recipe.fluidOutput().getAmount());
        else
            setStack(0, recipe.fluidOutput());
    }

    @Override
    protected boolean canAcceptRecipeOutput(@Nullable LiquifierRecipe recipe) {
        if(recipe == null)
            return false;
        return getFluidStack(0).isEmpty() || isValidInternal(0,recipe.fluidOutput());
    }

    @Override
    public boolean isValidExternal(int slot, FluidStack stack) {
        return false;
    }
}
