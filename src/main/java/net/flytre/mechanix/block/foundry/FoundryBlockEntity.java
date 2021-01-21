package net.flytre.mechanix.block.foundry;

import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.api.inventory.EasyInventory;
import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.recipe.FoundryRecipe;
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

public class FoundryBlockEntity extends MachineEntity<FoundryBlockEntity,FoundryRecipe> implements DoubleInventory {

    private final DefaultedList<FluidStack> fluidInventory;

    public FoundryBlockEntity() {
        super(MachineRegistry.FOUNDRY.getEntityType(), DefaultedList.ofSize(2, ItemStack.EMPTY),
                (World world, FoundryBlockEntity inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.FOUNDRY_RECIPE, inventory, world).orElse(null)
                , 40);
        fluidInventory = DefaultedList.ofSize(1, FluidStack.EMPTY);
    }

    @Override
    public void updateDelegate() {
        super.updateDelegate();
        getProperties().set(10,getFluidStack(0).getAmount());
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

    @Override
    protected void craft(FoundryRecipe recipe) {
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
    protected boolean canAcceptRecipeOutput(FoundryRecipe recipe) {
        if(recipe == null)
            return false;
        return this.getStack(0).isEmpty() || EasyInventory.canMergeItems(this.getStack(0),recipe.getOutput());
    }
}
