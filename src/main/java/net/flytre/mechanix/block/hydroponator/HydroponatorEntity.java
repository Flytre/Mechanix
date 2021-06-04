package net.flytre.mechanix.block.hydroponator;

import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.api.upgrade.UpgradeInventory;
import net.flytre.mechanix.recipe.ItemFluidProcessingRecipe;
import net.flytre.mechanix.util.ItemRegistry;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HydroponatorEntity extends MachineEntity<DoubleInventory, ItemFluidProcessingRecipe<DoubleInventory>> implements DoubleInventory {

    private final DefaultedList<FluidStack> fluidInventory;

    //input, fertilizer, 2 output
    public HydroponatorEntity() {
        super(MachineRegistry.HYDROPONATOR.getEntityType(), RecipeRegistry.HYDROPONATOR_RECIPE, DefaultedList.ofSize(4, ItemStack.EMPTY),
                (World world, DoubleInventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.HYDROPONATOR_RECIPE, inventory, world).orElse(null)
                , 60);
        fluidInventory = DefaultedList.ofSize(1, FluidStack.EMPTY);
    }

    @Override
    public void onceTick() {
        super.onceTick();
        if (world != null && !world.isClient)
            UpgradeInventory.waterFabricatorEffect(this, FluidStack.UNITS_PER_BUCKET / 400, 0);
    }

    @Override
    public Set<Item> validUpgrades() {
        return Stream.concat(
                Stream.of(ItemRegistry.WATER_FABRICATOR), super.validUpgrades().stream())
                .collect(Collectors.toSet());
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
        return new TranslatableText("block.mechanix.hydroponator");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new HydroponatorHandler(syncId, inv, this, getDelegate());
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 0)
            return stack.getItem() != Items.BONE_MEAL;
        if (slot == 1)
            return stack.getItem() == Items.BONE_MEAL;
        return false;
    }


    @Override
    public boolean isValidInternal(int slot, FluidStack stack) {
        return stack.getFluid() == Fluids.WATER;
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
