package net.flytre.mechanix.block.enchanter;

import net.flytre.mechanix.api.energy.MachineEntity;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.recipe.EnchanterRecipe;
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

public class EnchanterEntity extends MachineEntity<DoubleInventory, EnchanterRecipe>  implements DoubleInventory {
    private final DefaultedList<FluidStack> fluidInventory;

    //input, output size = 2
    public EnchanterEntity() {
        super(MachineRegistry.ENCHANTER.getEntityType(), DefaultedList.ofSize(2, ItemStack.EMPTY),
                (World world, DoubleInventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.ENCHANTING_RECIPE, inventory, world).orElse(null)
                , 25);
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
        return new TranslatableText("block.mechanix.enchanter");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EnchanterScreenHandler(syncId,inv,this,getProperties());
    }

    @Override
    protected void craft(EnchanterRecipe recipe) {
        ItemStack primary = recipe.craft(this);
        this.setStack(1,primary);
        this.getStack(0).decrement(1);
        this.getFluidStack(0).decrement(600);
    }

    @Override
    protected boolean canAcceptRecipeOutput(EnchanterRecipe recipe) {
        if (recipe == null)
            return false;
        return getStack(1).isEmpty();
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
            return stack.isEnchantable();
        else
            return false;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0 && super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 1 && super.canExtract(slot, stack, dir);
    }
}
