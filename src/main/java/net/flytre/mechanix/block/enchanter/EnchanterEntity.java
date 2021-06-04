package net.flytre.mechanix.block.enchanter;

import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.block.foundry.FoundryHandler;
import net.flytre.mechanix.recipe.EnchantingRecipe;
import net.flytre.mechanix.recipe.ItemFluidProcessingRecipe;
import net.flytre.mechanix.util.FluidRegistry;
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

import java.util.Map;

public class EnchanterEntity extends MachineEntity<DoubleInventory, EnchantingRecipe> implements DoubleInventory {

    private final DefaultedList<FluidStack> fluidInventory;

    public EnchanterEntity() {
        super(MachineRegistry.ENCHANTER.getEntityType(), RecipeRegistry.ENCHANTING_RECIPE, DefaultedList.ofSize(2, ItemStack.EMPTY),
                (World world, DoubleInventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.ENCHANTING_RECIPE, inventory, world).orElse(null)
                , 25);
        fluidInventory = DefaultedList.ofSize(1, FluidStack.EMPTY);
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
    public Map<Direction, IOType> getFluidIO() {
        return getItemIO();
    }

    @Override
    public DefaultedList<FluidStack> getFluids() {
        return fluidInventory;
    }


    @Override
    public long capacity() {
        return 8 * FluidStack.UNITS_PER_BUCKET;
    }


    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.enchanter");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EnchanterHandler(syncId, inv, this, this.getDelegate());
    }


    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 0)
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

    @Override
    public boolean isValidInternal(int slot, FluidStack stack) {
        return stack.getFluid() == FluidRegistry.LIQUID_XP.getStill();
    }
}
