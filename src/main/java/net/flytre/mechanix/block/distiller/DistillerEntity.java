package net.flytre.mechanix.block.distiller;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DistillerEntity extends MachineEntity<DoubleInventory, ItemFluidProcessingRecipe<DoubleInventory>> implements DoubleInventory {

    private final DefaultedList<FluidStack> fluidInventory;

    public DistillerEntity() {
        super(MachineRegistry.DISTILLER.getEntityType(), RecipeRegistry.DISENCHANTING_RECIPE, DefaultedList.ofSize(0, ItemStack.EMPTY),
                (World world, DoubleInventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.DISTILLING_RECIPE, inventory, world).orElse(null)
                , 40);
        fluidInventory = DefaultedList.ofSize(3, FluidStack.EMPTY);
        setMaxEnergy(500000);
        setMaxTransferRate(250);
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
    public void onceTick() {
        super.onceTick();
        if (world != null && !world.isClient)
            UpgradeInventory.waterFabricatorEffect(this, FluidStack.UNITS_PER_BUCKET / 400, 1);
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
        return 24 * FluidStack.UNITS_PER_BUCKET;
    }

    @Override
    public long slotCapacity() {
        return capacity() / 3;
    }

    @Override
    public boolean isValidInternal(int slot, FluidStack stack) {
        if (slot == 1 && stack.getFluid() != Fluids.WATER)
            return false;
        if (slot == 0 && stack.getFluid() == Fluids.WATER)
            return false;
        return DoubleInventory.super.isValidInternal(slot, stack);
    }

    @Override
    public Set<Item> validUpgrades() {
        return Stream.concat(
                Stream.of(ItemRegistry.WATER_FABRICATOR), super.validUpgrades().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValidExternal(int slot, FluidStack stack) {
        return slot != 2 && DoubleInventory.super.isValidExternal(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, FluidStack stack, Direction dir) {
        return slot == 2 & DoubleInventory.super.canExtract(slot, stack, dir);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.distiller");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new DistillerHandler(syncId, inv, this, getDelegate());
    }


}
