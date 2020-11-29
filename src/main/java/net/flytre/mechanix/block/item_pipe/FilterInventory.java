package net.flytre.mechanix.block.item_pipe;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashSet;
import java.util.Set;

public class FilterInventory implements Inventory {

    public DefaultedList<ItemStack> items;
    private int filterType;

    public FilterInventory(CompoundTag items_tag, int filterType) {
        this.fromTag(items_tag);
        this.filterType = filterType;
    }

    public int getMaxCountPerStack() {
        return 1;
    }

    public void clear() {
        this.items.clear();
    }

    public int getInventoryWidth() {
        return 9;
    }

    public int getInventoryHeight() {
        return 1;
    }


    public int size() {
        return getInventoryWidth() * getInventoryHeight();
    }

    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    public boolean put(Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, new ItemStack(item));
                return true;
            }
        }
        return false;
    }

    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.items, slot, amount);
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.items, slot);
    }


    public void setStack(int slot, ItemStack stack) {
        stack.setCount(1);
        this.items.set(slot, stack);
    }

    public void markDirty() {
        fixItems();
    }

    private void fixItems() {
        DefaultedList<ItemStack> fixed = DefaultedList.ofSize(this.items.size(),ItemStack.EMPTY);
        int index = 0;
        for(ItemStack stack : this.items) {
            if(!stack.isEmpty()) {
                fixed.set(index++,stack);
            }
        }
        this.items = fixed;
    }

    public Set<Item> getFilterItems() {
        HashSet<Item> res = new HashSet<>();
        for(ItemStack item : items)
            res.add(item.getItem());
        res.remove(Items.AIR);
        return res;
    }

    public boolean passFilterTest(ItemStack stack) {
        Set<Item> filterItems = getFilterItems();
        if(filterItems == null)
            return true;
        //0 =  whitelist, 1 = blacklist
        if(filterType == 0)
            return filterItems.contains(stack.getItem());
        else
            return !filterItems.contains(stack.getItem());

    }


    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }


    private void fromTag(CompoundTag tag) {
        this.items = DefaultedList.ofSize(9, ItemStack.EMPTY);
        this.filterType = tag.getInt("type");
        Inventories.fromTag(tag, this.items);
    }

    public int getFilterType() {
        return filterType;
    }



    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        Inventories.toTag(tag, this.items);
        tag.putInt("type",this.filterType);
        return tag;
    }

    public void onOpen(PlayerEntity player) {
        player.playSound(SoundEvents.BLOCK_METAL_HIT, SoundCategory.PLAYERS, 1f, 1f);
    }

    public void onClose(PlayerEntity player) {
        player.playSound(SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.PLAYERS, 1f, 1f);
        player.playSound(SoundEvents.BLOCK_METAL_HIT, SoundCategory.PLAYERS, 1f, 1f);
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }
}
