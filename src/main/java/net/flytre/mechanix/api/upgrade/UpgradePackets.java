package net.flytre.mechanix.api.upgrade;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ConfirmScreenActionS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class UpgradePackets {


    public static final Identifier INVENTORY_S2C_PACKET = new Identifier("mechanix:upgrade_inv_s2c");
    public static final Identifier SLOT_UPDATE_S2C_PACKET = new Identifier("mechanix:upgrade_slot_update_s2c");
    public static final Identifier CLICK_SLOT_C2S_PACKET = new Identifier("mechanix:upgrade_click_slot_c2s");


    public static void initServer() {
        ServerPlayNetworking.registerGlobalReceiver(CLICK_SLOT_C2S_PACKET, (server, player, handler, buf, responseSender) -> {
            int syncId = buf.readByte();
            int slot = buf.readShort();
            int clickData = buf.readByte();
            short actionId = buf.readShort();
            SlotActionType actionType = buf.readEnumConstant(SlotActionType.class);
            ItemStack packetStack = buf.readItemStack();

            server.execute(() -> {
                player.updateLastActionTime();
                UpgradeHandler upgradeHandler = (UpgradeHandler) player.currentScreenHandler;
                if (player.currentScreenHandler.syncId == syncId && player.currentScreenHandler.isNotRestricted(player)) {
                    if (player.isSpectator()) {
                        DefaultedList<ItemStack> defaultedList = DefaultedList.of();

                        for (int i = 0; i < upgradeHandler.upgradeSlots.size(); ++i)
                            defaultedList.add((upgradeHandler.upgradeSlots.get(i)).getStack());

                        ((UpgradeHandlerListener) player).onHandlerRegistered(upgradeHandler, defaultedList);
                    } else {
                        ItemStack itemStack = upgradeHandler.onUpgradeSlotClick(slot, clickData, actionType, player);
                        if (ItemStack.areEqual(packetStack, itemStack)) {
                            player.networkHandler.sendPacket(new ConfirmScreenActionS2CPacket(syncId, actionId, true));
                            player.skipPacketSlotUpdates = true;
                            player.currentScreenHandler.sendContentUpdates();
                            player.updateCursorStack();
                            player.skipPacketSlotUpdates = false;
                        } else {
                            player.networkHandler.sendPacket(new ConfirmScreenActionS2CPacket(syncId, actionId, false));
//                            player.currentScreenHandler.setPlayerRestriction(player, false);
                            DefaultedList<ItemStack> defaultedList2 = DefaultedList.of();
                            for (int j = 0; j < upgradeHandler.upgradeSlots.size(); ++j) {
                                ItemStack itemStack2 = upgradeHandler.upgradeSlots.get(j).getStack();
                                defaultedList2.add(itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2);
                            }
                            ((UpgradeHandlerListener) player).onHandlerRegistered(upgradeHandler, defaultedList2);
                        }
                    }
                }
            });
        });
    }
}
