package net.flytre.mechanix.api.fluid.screen;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.fluid.screen.FluidHandler;
import net.flytre.mechanix.api.fluid.screen.FluidHandlerListener;
import net.minecraft.network.packet.s2c.play.ConfirmScreenActionS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class FluidPackets {

    public static final Identifier INVENTORY_S2C_PACKET = new Identifier("mechanix:fluid_inv_s2c");
    public static final Identifier SLOT_UPDATE_S2C_PACKET = new Identifier("mechanix:fluid_slot_update_s2c");
    public static final Identifier CLICK_SLOT_C2S_PACKET = new Identifier("mechanix:fluid_click_slot_c2s");

    public static void initServer() {
        ServerPlayNetworking.registerGlobalReceiver(CLICK_SLOT_C2S_PACKET, (server, player, handler, buf, responseSender) -> {
            int syncId = buf.readByte();
            int slot = buf.readShort();
            int clickData = buf.readByte();
            short actionId = buf.readShort();
            SlotActionType actionType = buf.readEnumConstant(SlotActionType.class);
            FluidStack packetStack = FluidStack.fromPacket(buf);

            server.execute(() -> {
                player.updateLastActionTime();
                FluidHandler fluidHandler = (FluidHandler) player.currentScreenHandler;
                if (player.currentScreenHandler.syncId == syncId && player.currentScreenHandler.isNotRestricted(player)) {
                    if (player.isSpectator()) {
                        DefaultedList<FluidStack> defaultedList = DefaultedList.of();

                        for (int i = 0; i < fluidHandler.fluidSlots.size(); ++i)
                            defaultedList.add((fluidHandler.fluidSlots.get(i)).getStack());


                        ((FluidHandlerListener) player).onHandlerRegistered(fluidHandler, defaultedList);
                    } else {
                        FluidStack fluidStack = fluidHandler.onFluidSlotClick(slot, clickData, actionType, player);
                        if (FluidStack.areEqual(packetStack, fluidStack)) {
                            player.networkHandler.sendPacket(new ConfirmScreenActionS2CPacket(syncId, actionId, true));
                            player.skipPacketSlotUpdates = true;
                            player.currentScreenHandler.sendContentUpdates();
                            player.updateCursorStack();
                            player.skipPacketSlotUpdates = false;
                        } else {
                            player.networkHandler.sendPacket(new ConfirmScreenActionS2CPacket(syncId, actionId, false));
//                            player.currentScreenHandler.setPlayerRestriction(player, false);
                            DefaultedList<FluidStack> defaultedList2 = DefaultedList.of();

                            for (int j = 0; j < fluidHandler.fluidSlots.size(); ++j) {
                                FluidStack fluidStack2 = fluidHandler.fluidSlots.get(j).getStack();
                                defaultedList2.add(fluidStack2.isEmpty() ? FluidStack.EMPTY : fluidStack2);
                            }

                            ((FluidHandlerListener) player).onHandlerRegistered(fluidHandler, defaultedList2);
                        }
                    }
                }
            });
        });

    }
}
