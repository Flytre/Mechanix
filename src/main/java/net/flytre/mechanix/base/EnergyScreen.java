package net.flytre.mechanix.base;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.flytre.mechanix.util.Packets;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public abstract class EnergyScreen<T extends EnergyScreenHandler> extends HandledScreen<T> {
    private static final Identifier BUTTONS = new Identifier("mechanix:textures/gui/button/io.png");

    protected final T handler;
    private EnergyMeterWidget meter;
    private boolean synced;
    private final Identifier panel;

    public EnergyScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        this.meter = null;
        synced = false;
        this.panel = new Identifier("mechanix:textures/gui/container/panel.png");
        this.titleY--;
        this.playerInventoryTitleY+= 2;

    }

    public void onClicked(int side_id, ToggleButton buttonWidget) {
        if(!synced)
            return;
        buttonWidget.toggleState();
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeBlockPos(handler.getPos());
        passedData.writeInt(side_id);
        passedData.writeInt(buttonWidget.getState());
        passedData.writeInt(handler.getPanelMode());
        ClientSidePacketRegistry.INSTANCE.sendToServer(Packets.IO_CHANGE, passedData);

    }

    @Override
    public void tick() {
        super.tick();

        if(!synced && handler.getSynced()) {
            onSynced();
            synced = true;
        }
    }

    private void onSynced() {

        int baseX = x + backgroundWidth;
        this.addButton(new ToggleButton(baseX + 24, this.y + 24, 16, 16, handler.getPanelMode() == 0 ? handler.energyButtonState(Direction.NORTH) : handler.itemButtonState(Direction.NORTH), BUTTONS, (buttonWidget) -> onClicked(0, (ToggleButton) buttonWidget), 'N'));
        //left
        this.addButton(new ToggleButton(baseX + 4, this.y + 24, 16, 16,  handler.getPanelMode() == 0 ? handler.energyButtonState(Direction.WEST) : handler.itemButtonState(Direction.WEST), BUTTONS,  (buttonWidget) -> onClicked(1, (ToggleButton) buttonWidget), 'W'));
        //right
        this.addButton(new ToggleButton(baseX + 44, this.y + 24, 16, 16,  handler.getPanelMode() == 0 ? handler.energyButtonState(Direction.EAST) : handler.itemButtonState(Direction.EAST), BUTTONS,  (buttonWidget) -> onClicked(2, (ToggleButton) buttonWidget), 'E'));
        //up
        this.addButton(new ToggleButton(baseX + 24, this.y + 4, 16, 16,  handler.getPanelMode() == 0 ? handler.energyButtonState(Direction.UP) : handler.itemButtonState(Direction.UP), BUTTONS,  (buttonWidget) -> onClicked(3, (ToggleButton) buttonWidget), 'U'));
        //down
        this.addButton(new ToggleButton(baseX + 24, this.y + 44, 16, 16,  handler.getPanelMode() == 0 ? handler.energyButtonState(Direction.DOWN) : handler.itemButtonState(Direction.DOWN), BUTTONS,  (buttonWidget) -> onClicked(4, (ToggleButton) buttonWidget), 'D'));
        //behind
        this.addButton(new ToggleButton(baseX + 44, this.y + 44, 16, 16, handler.getPanelMode() == 0 ?  handler.energyButtonState(Direction.SOUTH) : handler.itemButtonState(Direction.SOUTH), BUTTONS, (buttonWidget) -> onClicked(5, (ToggleButton) buttonWidget), 'S'));

        //meter
        this.meter = new EnergyMeterWidget(this.x + 10, this.y + 13,30,60,0,handler.getPropertyDelegate());

        this.addButton(meter);
    }


    @Override
    public void init() {
        super.init();
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.client != null;
        this.client.getTextureManager().bindTexture(this.panel);
        this.drawTexture(matrices, x + backgroundWidth, y, 0, 0, 65, 65);
    }


}
