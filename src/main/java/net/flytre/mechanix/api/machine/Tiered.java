package net.flytre.mechanix.api.machine;


import net.minecraft.nbt.CompoundTag;

/**
 * Simple interface to tell Mechanix that ur machine has tiers
 */
public interface Tiered {

    //0-3 : standard - vysterium
    static void fromTag(CompoundTag tag, Tiered entity) {
        entity.setTier(tag.getInt("tier"));
    }

    static CompoundTag toTag(Tiered entity, CompoundTag tag) {
        tag.putInt("tier", entity.getTier());
        return tag;
    }

    int getTier();

    void setTier(int tier);
}
