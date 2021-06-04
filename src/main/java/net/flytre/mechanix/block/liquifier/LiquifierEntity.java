package net.flytre.mechanix.block.liquifier;

import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.recipe.ItemFluidProcessingRecipe;
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

public class LiquifierEntity extends MachineEntity<DoubleInventory, ItemFluidProcessingRecipe<DoubleInventory>> implements DoubleInventory {
    private final DefaultedList<FluidStack> fluidInventory;

    public LiquifierEntity() {
        super(MachineRegistry.LIQUIFIER.getEntityType(), RecipeRegistry.LIQUIFYING_RECIPE, DefaultedList.ofSize(1, ItemStack.EMPTY),
                (World world, DoubleInventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.LIQUIFYING_RECIPE, inventory, world).orElse(null)
                , 40);
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
        return new TranslatableText("block.mechanix.liquifier");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new LiquifierHandler(syncId, inv, this, this.getDelegate());
    }

    @Override
    public boolean isValidExternal(int slot, FluidStack stack) {
        return false;
    }
}

