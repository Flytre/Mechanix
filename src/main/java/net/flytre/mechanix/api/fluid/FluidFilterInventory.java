package net.flytre.mechanix.api.fluid;


import net.flytre.flytre_lib.common.inventory.IOType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Parallel to filter inventories, for fluids
 */
public class FluidFilterInventory implements FluidInventory {

    private final int height;
    public DefaultedList<FluidStack> fluids;
    private boolean matchMod;
    private int filterType;


    public FluidFilterInventory(DefaultedList<FluidStack> fluids, int filterType, int height, boolean matchMod) {
        this.fluids = fluids;
        this.filterType = filterType;
        this.height = height;
        this.matchMod = matchMod;
    }

    public static FluidFilterInventory fromTag(CompoundTag tag, int defaultHeight) {
        int height = tag.contains("height") ? tag.getInt("height") : defaultHeight;
        int filterType = tag.getInt("type");
        boolean matchMod = tag.getBoolean("modMatch");
        DefaultedList<FluidStack> fluids = DefaultedList.ofSize(height * 9, FluidStack.EMPTY);
        FluidInventory.fromTag(tag, fluids);
        return new FluidFilterInventory(fluids, filterType, height, matchMod);
    }

    /**
     * Will never be used, dummy hashmap
     *
     * @return dummy hashmap
     */
    @Override
    public Map<Direction, IOType> getFluidIO() {
        return new HashMap<>();
    }

    @Override
    public DefaultedList<FluidStack> getFluids() {
        return fluids;
    }

    @Override
    public long capacity() {
        return height * 9L;
    }

    @Override
    public long slotCapacity() {
        return 1;
    }

    @Override
    public void markDirty() {
        fixFluids();
    }


    private void fixFluids() {
        DefaultedList<FluidStack> fixed = DefaultedList.ofSize(this.fluids.size(), FluidStack.EMPTY);
        int index = 0;
        for (FluidStack stack : this.fluids) {
            if (!stack.isEmpty()) {
                fixed.set(index++, stack);
            }
        }
        this.fluids = fixed;
    }


    public Set<Fluid> getFilterFluids() {
        Set<Fluid> res = new HashSet<>();
        for (FluidStack item : fluids)
            res.add(item.getFluid());
        res.remove(Fluids.EMPTY);
        return res;
    }

    public boolean passFilterTest(FluidStack stack) {
        Set<Fluid> filterItems = getFilterFluids();

        if (matchMod) {
            Set<String> mods = filterItems.stream().map(Registry.FLUID::getId).map(Identifier::getNamespace).collect(Collectors.toSet());
            String itemId = Registry.FLUID.getId(stack.getFluid()).getNamespace();
            return (filterType == 0) == mods.contains(itemId);
        }

        return (filterType == 0) == filterItems.contains(stack.getFluid());

    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public boolean isMatchMod() {
        return matchMod;
    }

    public void setMatchMod(boolean matchMod) {
        this.matchMod = matchMod;
    }

    public void toggleModMatch() {
        this.matchMod = !this.matchMod;
    }

    public void toggleFilterType() {
        this.filterType = (this.filterType == 1 ? 0 : 1);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        FluidInventory.toTag(tag, this.fluids);
        tag.putInt("type", this.filterType);
        tag.putInt("height", this.height);
        tag.putBoolean("modMatch", this.matchMod);
        return tag;
    }

    public void onOpen(PlayerEntity player) {
        player.playSound(SoundEvents.BLOCK_METAL_HIT, SoundCategory.PLAYERS, 1f, 1f);
    }

    public void onClose(PlayerEntity player) {
        player.playSound(SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.PLAYERS, 1f, 1f);
        player.playSound(SoundEvents.BLOCK_METAL_HIT, SoundCategory.PLAYERS, 1f, 1f);
    }

}
