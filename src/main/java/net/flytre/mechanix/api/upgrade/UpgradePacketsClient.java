package net.flytre.mechanix.api.upgrade;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.flytre_lib.common.util.PacketUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class UpgradePacketsClient {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(UpgradePackets.SLOT_UPDATE_S2C_PACKET, (client, handler, buf, responseSender) -> {
            int syncId = buf.readInt();
            int slot = buf.readInt();
            ItemStack stack = buf.readItemStack();
            PlayerEntity playerEntity = client.player;
            client.execute(() -> {
                assert playerEntity != null;
                if (syncId == playerEntity.currentScreenHandler.syncId && playerEntity.currentScreenHandler instanceof UpgradeHandler) {
                    ((UpgradeHandler) playerEntity.currentScreenHandler).setUpgradeStackInSlot(slot, stack);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(UpgradePackets.INVENTORY_S2C_PACKET, (client, handler, buf, responseSender) -> {
            int syncId = buf.readInt();
            List<ItemStack> temp = PacketUtils.listFromPacket(buf, PacketByteBuf::readItemStack);
            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(temp.size(), ItemStack.EMPTY);
            for (int j = 0; j < stacks.size(); ++j)
                stacks.set(j, temp.get(j));
            PlayerEntity playerEntity = client.player;
            client.execute(() -> {
                assert playerEntity != null;
                if (syncId == playerEntity.currentScreenHandler.syncId && playerEntity.currentScreenHandler instanceof UpgradeHandler) {
                    ((UpgradeHandler) playerEntity.currentScreenHandler).updateUpgradeSlotStacks(stacks);
                }
            });
        });
    }
}
