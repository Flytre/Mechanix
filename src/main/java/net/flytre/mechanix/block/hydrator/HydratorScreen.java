package net.flytre.mechanix.block.hydrator;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.flytre.mechanix.api.gui.FluidMeterWidget;
import net.flytre.mechanix.api.gui.PanelledScreen;
import net.flytre.mechanix.api.gui.ToggleButton;
import net.flytre.mechanix.util.Packets;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static net.flytre.mechanix.api.energy.EnergyScreen.BUTTONS;

public class HydratorScreen extends PanelledScreen<HydratorScreenHandler> {
    protected final HydratorScreenHandler handler;
    private boolean synced;
    private final Identifier panel;
    private final Identifier background;
    private FluidMeterWidget meter;


    public HydratorScreen(HydratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        synced = false;
        this.panel = new Identifier("mechanix:textures/gui/container/panel.png");
        this.titleY--;
        this.playerInventoryTitleY+= 2;
        this.background = new Identifier("mechanix:textures/gui/container/basic.png");
        ++this.backgroundHeight;
        meter = null;

    }

    public void onClicked(int side_id, ToggleButton buttonWidget) {
        if(!synced)
            return;
        buttonWidget.toggleState();
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeBlockPos(handler.getPos());
        passedData.writeInt(side_id);
        passedData.writeInt(buttonWidget.getState());
        ClientSidePacketRegistry.INSTANCE.sendToServer(Packets.FLUID_IO_CHANGE, passedData);

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
        this.addButton(new ToggleButton(baseX + 24, this.y + 24, 16, 16, handler.fluidButtonState(Direction.NORTH), BUTTONS, (buttonWidget) -> onClicked(0, (ToggleButton) buttonWidget), 'N'));
        //left
        this.addButton(new ToggleButton(baseX + 4, this.y + 24, 16, 16,  handler.fluidButtonState(Direction.WEST), BUTTONS,  (buttonWidget) -> onClicked(1, (ToggleButton) buttonWidget), 'W'));
        //right
        this.addButton(new ToggleButton(baseX + 44, this.y + 24, 16, 16,  handler.fluidButtonState(Direction.EAST), BUTTONS,  (buttonWidget) -> onClicked(2, (ToggleButton) buttonWidget), 'E'));
        //up
        this.addButton(new ToggleButton(baseX + 24, this.y + 4, 16, 16,  handler.fluidButtonState(Direction.UP), BUTTONS,  (buttonWidget) -> onClicked(3, (ToggleButton) buttonWidget), 'U'));
        //down
        this.addButton(new ToggleButton(baseX + 24, this.y + 44, 16, 16,  handler.fluidButtonState(Direction.DOWN), BUTTONS,  (buttonWidget) -> onClicked(4, (ToggleButton) buttonWidget), 'D'));
        //behind
        this.addButton(new ToggleButton(baseX + 44, this.y + 44, 16, 16,  handler.fluidButtonState(Direction.SOUTH), BUTTONS, (buttonWidget) -> onClicked(5, (ToggleButton) buttonWidget), 'S'));

        meter = new FluidMeterWidget(x + 73, y + 15, handler.getPos(), 0,
                () -> handler.getAmount(),
                () -> 8000,
                (i) -> Fluids.WATER);
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

        this.client.getTextureManager().bindTexture(this.background);
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        this.client.getTextureManager().bindTexture(this.panel);
        this.drawTexture(matrices, x + backgroundWidth, y, 0, 0, 65, 65);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

}
