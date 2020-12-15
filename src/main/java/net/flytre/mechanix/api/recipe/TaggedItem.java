package net.flytre.mechanix.api.recipe;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class TaggedItem {
    private final Identifier path;
    private final int qty;

    public TaggedItem(Identifier path) {
        this(path,1);
    }

    public TaggedItem(Identifier path, int qty) {
        this.path = path;
        this.qty = qty;
    }

    public Item getItem() {
        return TagRegistry.item(path).values().size() >= 1 ? TagRegistry.item(path).values().get(0) : Items.AIR;
    }

    public ItemStack getItemStack() {
        return new ItemStack(getItem(),qty);
    }

    public int getQty() {
        return qty;
    }

    public Identifier getPath() {
        return path;
    }
}
