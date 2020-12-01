package net.flytre.mechanix.base;

import net.minecraft.nbt.CompoundTag;

public interface TieredMachine {

    int getTier();

    void setTier(int tier);

    //0-3 : standard - vysterium
    static void fromTag(CompoundTag tag, TieredMachine machine) {
        machine.setTier(tag.getInt("tier"));
    }

    static CompoundTag toTag(TieredMachine machine, CompoundTag tag) {
        tag.putInt("tier",machine.getTier());
        return tag;
    }
}
