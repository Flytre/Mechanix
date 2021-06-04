package net.flytre.mechanix.api.fluid.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.flytre_lib.common.util.PacketUtils;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.fluid.screen.FluidHandler;
import net.flytre.mechanix.api.fluid.screen.FluidPackets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class FluidPacketsClient {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(FluidPackets.SLOT_UPDATE_S2C_PACKET, (client, handler, buf, responseSender) -> {
            int syncId = buf.readInt();
            int slot = buf.readInt();
            FluidStack stack = FluidStack.fromPacket(buf);
            PlayerEntity playerEntity = client.player;
            client.execute(() -> {
                assert playerEntity != null;
                if (syncId == playerEntity.currentScreenHandler.syncId && playerEntity.currentScreenHandler instanceof FluidHandler) {
                    ((FluidHandler) playerEntity.currentScreenHandler).setFluidStackInSlot(slot, stack);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(FluidPackets.INVENTORY_S2C_PACKET, (client, handler, buf, responseSender) -> {
            int syncId = buf.readInt();
            List<FluidStack> temp = PacketUtils.listFromPacket(buf, FluidStack::fromPacket);
            DefaultedList<FluidStack> stacks = DefaultedList.ofSize(temp.size(), FluidStack.EMPTY);
            for (int j = 0; j < stacks.size(); ++j)
                stacks.set(j, temp.get(j));
            PlayerEntity playerEntity = client.player;
            client.execute(() -> {
                assert playerEntity != null;
                if (syncId == playerEntity.currentScreenHandler.syncId && playerEntity.currentScreenHandler instanceof FluidHandler) {
                    ((FluidHandler) playerEntity.currentScreenHandler).updateFluidSlotStacks(stacks);
                }
            });
        });
    }
}
