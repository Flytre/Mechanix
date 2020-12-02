package net.flytre.mechanix.api.energy;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.flytre.mechanix.api.gui.EnergyMeterWidget;
import net.flytre.mechanix.api.gui.ToggleButton;
import net.flytre.mechanix.util.Packets;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;


/**
 * Energy Screen is a simple class that adds a meter for energy and configuration panel to draw the panel (type dependent on panel mode
 * specified in the energy class) on a sidebar to configure the machine's input/output.
 *
 * @param <T> the type parameter
 */
public abstract class EnergyScreen<T extends EnergyScreenHandler> extends HandledScreen<T> {
    /**
     * Location of the buttons texture
     */
    public static final Identifier BUTTONS = new Identifier("mechanix:textures/gui/button/io.png");

    /**
     * The Screen Handler.
     */
    protected final T handler;
    private EnergyMeterWidget meter;
    private boolean synced;
    private final Identifier panel;

    /**
     * Instantiates a new Energy screen.
     *
     * @param handler   the handler
     * @param inventory the inventory
     * @param title     the title (rendered at the top of the screen)
     */
    public EnergyScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        this.meter = null;
        synced = false;
        this.panel = new Identifier("mechanix:textures/gui/container/panel.png");
        this.titleY--;
        this.playerInventoryTitleY+= 2;

    }


    private void onClicked(int side_id, ToggleButton buttonWidget) {
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

    /**
     * Add buttons and meters and stuff here. Remember to call super. This basically runs once the handler's
     * data has synced which makes it much, much better for adding stuff then in the constructor.
     */
    protected void onSynced() {

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
        this.meter = new EnergyMeterWidget(this.x + 10, this.y + 13,0,handler.getPropertyDelegate());

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
