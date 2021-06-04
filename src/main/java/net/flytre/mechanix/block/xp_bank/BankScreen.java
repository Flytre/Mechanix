package net.flytre.mechanix.block.xp_bank;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.mechanix.api.client.PanelledFluidHandledScreen;
import net.flytre.mechanix.block.tank.FluidTankScreen;
import net.flytre.mechanix.block.tank.FluidTankScreenHandler;
import net.flytre.mechanix.util.Packets;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BankScreen extends FluidTankScreen {
    public BankScreen(FluidTankScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        background = new Identifier("mechanix:textures/gui/container/basic.png");
    }

    @Override
    protected void onSynced() {
        super.onSynced();

        //+ buttons
        this.addButton(new ButtonWidget(x + 8, y + 20, 32, 20, Text.of("§2+1"), (i) -> requestXPTransfer(1)));
        this.addButton(new ButtonWidget(x + 8, y + 50, 32, 20, Text.of("§2+10"), (i) -> requestXPTransfer(10)));
        this.addButton(new ButtonWidget(x + 40, y + 20, 32, 20, Text.of("§2+100"), (i) -> requestXPTransfer(100)));
        this.addButton(new ButtonWidget(x + 40, y + 50, 32, 20, Text.of("§2+1000"), (i) -> requestXPTransfer(1000)));

        //- buttons
        this.addButton(new ButtonWidget(x + 105, y + 20, 32, 20, Text.of("§c-1"), (i) -> requestXPTransfer(-1)));
        this.addButton(new ButtonWidget(x + 105, y + 50, 32, 20, Text.of("§c-10"), (i) -> requestXPTransfer(-10)));
        this.addButton(new ButtonWidget(x + 137, y + 20, 32, 20, Text.of("§c-100"), (i) -> requestXPTransfer(-100)));
        this.addButton(new ButtonWidget(x + 137, y + 50, 32, 20, Text.of("§c-1000"), (i) -> requestXPTransfer(-1000)));

    }

    private void requestXPTransfer(int amount) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(handler.getPos());
        buf.writeInt(amount);
        ClientPlayNetworking.send(Packets.XP_TRANSFER, buf);
    }
}
