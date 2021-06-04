package net.flytre.mechanix.api.energy;

import net.flytre.mechanix.api.machine.Tiered;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public abstract class TieredEnergyEntityWithItems extends StandardEnergyEntityWithItems implements Tiered {

    private int tier;

    public TieredEnergyEntityWithItems(BlockEntityType<?> type) {
        super(type);
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        this.tier = tier;
    }


    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        Tiered.fromTag(tag, this);
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Tiered.toTag(this, tag);
        return super.toTag(tag);
    }


    @Override
    public final void tick() {
        super.tick();
        for (int i = 0; i <= tier; i++)
            repeatTick();

        onceTick();

        updateDelegate();
    }

    /**
     * Repeat this 1 time every tick.
     */
    protected abstract void onceTick();



    /**
     * Repeat this once per tier (so up to 4 times)
     */
    protected abstract void repeatTick();
}
