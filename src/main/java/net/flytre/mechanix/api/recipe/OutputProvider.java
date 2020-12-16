package net.flytre.mechanix.api.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class OutputProvider {

    private final boolean isTag;
    private final ItemStack stack;
    private final TaggedItem taggedItem;
    private final double chance;

    public OutputProvider(ItemStack stack, double chance) {
        this.isTag = false;
        this.stack = stack;
        this.taggedItem = null;
        this.chance = chance;
    }

    public OutputProvider(TaggedItem item, double chance) {
        this.isTag = true;
        this.stack = null;
        this.taggedItem = item;
        this.chance = chance;
    }

    public ItemStack getStack() {
        return isTag ? taggedItem.getItemStack() : stack.copy();
    }

    public static OutputProvider fromJson(JsonElement jsonElement) {
        JsonObject json = null;
        if(jsonElement.isJsonObject())
            json = (JsonObject) jsonElement;
        //If tag
        if(JsonHelper.hasString(json, "tag")) {
            Identifier id = new Identifier(JsonHelper.getString(json, "tag"));
            int i = JsonHelper.getInt(json, "count", 1);
            double chance = JsonHelper.getFloat(json, "chance", 1);
            return new OutputProvider(new TaggedItem(id,i), chance);
        }

        //If item
        if(jsonElement.isJsonPrimitive()) {
            String id = jsonElement.getAsString();
            Identifier identifier2 = new Identifier(id);
            ItemStack itemStack = Registry.ITEM.getOrEmpty(identifier2).map(ItemStack::new).orElse(ItemStack.EMPTY);
            return new OutputProvider(itemStack,1.0);
        }

        String string = JsonHelper.getString(json, "item");
        Item item = Registry.ITEM.getOrEmpty(new Identifier(string)).orElse(Items.AIR);
        int i = JsonHelper.getInt(json, "count", 1);
        double chance = JsonHelper.getFloat(json, "chance", 1);
        return new OutputProvider(new ItemStack(item,i),chance);
    }

    public double getChance() {
        return chance;
    }

    public void toPacket(PacketByteBuf packet) {
        packet.writeBoolean(isTag);
        if(isTag) {
            packet.writeIdentifier(taggedItem.getPath());
            packet.writeInt(taggedItem.getQty());
        } else {
            packet.writeItemStack(stack);
        }
        packet.writeDouble(chance);
    }

    public static OutputProvider fromPacket(PacketByteBuf buf) {
        boolean isTag = buf.readBoolean();
        if(isTag) {
            return new OutputProvider(new TaggedItem(buf.readIdentifier(),buf.readInt()),buf.readDouble());
        } else {
            return new OutputProvider(buf.readItemStack(),buf.readDouble());
        }
    }

}
