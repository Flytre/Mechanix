package net.flytre.mechanix.api.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.flytre_lib.client.gui.CoordinateProvider;
import net.flytre.flytre_lib.client.gui.MultistateButton;
import net.flytre.flytre_lib.client.gui.ToggleButton;
import net.flytre.mechanix.api.energy.StandardEnergyScreenHandler;
import net.flytre.mechanix.util.Packets;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

/**
 * Energy Screen is a screen extension class that adds a meter for energy and configuration panel to draw the panel (type dependent on panel mode
 * specified in the energy class) on a sidebar to configure the machine's input/output.
 *
 * @param <T> the type parameter
 */
public abstract class EnergyScreen<T extends StandardEnergyScreenHandler<?>> extends HandledScreen<T> implements CoordinateProvider {
    /**
     * Location of the buttons texture
     */
    public static final Identifier TOGGLE_PANEL_BUTTONS = new Identifier("mechanix:textures/gui/button/io.png");
    public static final Identifier QUAD_PANEL_BUTTONS = new Identifier("mechanix:textures/gui/button/quad.png");

    /**
     * The Screen Handler.
     */
    protected final T handler;
    private final Identifier panel;
    private EnergyMeterWidget meter;
    private boolean synced;

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
        this.playerInventoryTitleY += 2;

    }


    private void onEnergyClicked(int sideId, ToggleButton buttonWidget) {
        if (!synced)
            return;
        buttonWidget.toggleState();
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeBlockPos(handler.getPos());
        passedData.writeInt(sideId);
        passedData.writeInt(buttonWidget.getState());
        ClientPlayNetworking.send(Packets.ENERGY_IO_CHANGE, passedData);
    }

    @Override
    public void tick() {
        super.tick();

        if (!synced && handler.getSynced()) {
            onSynced();
            synced = true;
        }
    }

    private void addPanelButton(MultistateButton button) {
        button.setTooltipRenderer(this::renderTooltip);
        TranslatableText arg = new TranslatableText("gui.mechanix.item");
        button.setTooltips(new TranslatableText("gui.mechanix.output", arg), new TranslatableText("gui.mechanix.input", arg), new TranslatableText("gui.mechanix.io", arg), new TranslatableText("gui.mechanix.neither", arg));
        this.addButton(button);
    }

    private void addEnergyButton(ToggleButton button) {
        button.setTooltipRenderer(this::renderTooltip);
        TranslatableText arg = new TranslatableText("gui.mechanix.energy2");
        button.setTooltips(new TranslatableText("gui.mechanix.output", arg), new TranslatableText("gui.mechanix.input", arg));
        this.addButton(button);
    }

    /**
     * Add buttons and meters and stuff here. Remember to call super. This basically runs once the handler's
     * data has synced which makes it much, much better for adding stuff then in the constructor.
     */
    protected void onSynced() {

        int baseX = x + backgroundWidth;

        if (handler.getPanelMode() == 0) {
            this.addEnergyButton(new ToggleButton(baseX + 24, this.y + 24, 16, 16, handler.energyButtonState(Direction.NORTH), TOGGLE_PANEL_BUTTONS, (buttonWidget) -> onEnergyClicked(0, (ToggleButton) buttonWidget), 'N'));
            //left
            this.addEnergyButton(new ToggleButton(baseX + 4, this.y + 24, 16, 16, handler.energyButtonState(Direction.WEST), TOGGLE_PANEL_BUTTONS, (buttonWidget) -> onEnergyClicked(1, (ToggleButton) buttonWidget), 'W'));
            //right
            this.addEnergyButton(new ToggleButton(baseX + 44, this.y + 24, 16, 16, handler.energyButtonState(Direction.EAST), TOGGLE_PANEL_BUTTONS, (buttonWidget) -> onEnergyClicked(2, (ToggleButton) buttonWidget), 'E'));
            //up
            this.addEnergyButton(new ToggleButton(baseX + 24, this.y + 4, 16, 16, handler.energyButtonState(Direction.UP), TOGGLE_PANEL_BUTTONS, (buttonWidget) -> onEnergyClicked(3, (ToggleButton) buttonWidget), 'U'));
            //down
            this.addEnergyButton(new ToggleButton(baseX + 24, this.y + 44, 16, 16, handler.energyButtonState(Direction.DOWN), TOGGLE_PANEL_BUTTONS, (buttonWidget) -> onEnergyClicked(4, (ToggleButton) buttonWidget), 'D'));
            //behind
            this.addEnergyButton(new ToggleButton(baseX + 44, this.y + 44, 16, 16, handler.energyButtonState(Direction.SOUTH), TOGGLE_PANEL_BUTTONS, (buttonWidget) -> onEnergyClicked(5, (ToggleButton) buttonWidget), 'S'));

        } else if (handler.getPanelMode() == 1) {
            //in front
            this.addPanelButton(new MultistateButton(baseX + 24, this.y + 24, 16, 16, getItemButtonState(Direction.NORTH), 4, QUAD_PANEL_BUTTONS, (buttonWidget) -> onItemClicked(0, (MultistateButton) buttonWidget), 'N'));
            //left
            this.addPanelButton(new MultistateButton(baseX + 4, this.y + 24, 16, 16, getItemButtonState(Direction.WEST), 4, QUAD_PANEL_BUTTONS, (buttonWidget) -> onItemClicked(1, (MultistateButton) buttonWidget), 'W'));
            //right
            this.addPanelButton(new MultistateButton(baseX + 44, this.y + 24, 16, 16, getItemButtonState(Direction.EAST), 4, QUAD_PANEL_BUTTONS, (buttonWidget) -> onItemClicked(2, (MultistateButton) buttonWidget), 'E'));
            //up
            this.addPanelButton(new MultistateButton(baseX + 24, this.y + 4, 16, 16, getItemButtonState(Direction.UP), 4, QUAD_PANEL_BUTTONS, (buttonWidget) -> onItemClicked(3, (MultistateButton) buttonWidget), 'U'));
            //down
            this.addPanelButton(new MultistateButton(baseX + 24, this.y + 44, 16, 16, getItemButtonState(Direction.DOWN), 4, QUAD_PANEL_BUTTONS, (buttonWidget) -> onItemClicked(4, (MultistateButton) buttonWidget), 'D'));
            //behind
            this.addPanelButton(new MultistateButton(baseX + 44, this.y + 44, 16, 16, getItemButtonState(Direction.SOUTH), 4, QUAD_PANEL_BUTTONS, (buttonWidget) -> onItemClicked(5, (MultistateButton) buttonWidget), 'S'));
        }
        //meter
        this.meter = new EnergyMeterWidget(this.x + 10, this.y + 13, 0, handler, this::renderTooltip);

        this.addButton(meter);
    }

    protected int getItemButtonState(Direction dir) {
        return handler.itemButtonState(dir);
    }

    public void onItemClicked(int sideId, MultistateButton buttonWidget) {
        if (!synced)
            return;
        buttonWidget.cycleState();
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeBlockPos(handler.getPos());
        passedData.writeInt(sideId);
        passedData.writeInt(buttonWidget.getState());
        ClientPlayNetworking.send(Packets.ITEM_IO_CHANGE, passedData);
    }


    @Override
    public void init() {
        synced = false;
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        for (AbstractButtonWidget button : buttons) {
            if (button.isHovered())
                button.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.client != null;
        this.client.getTextureManager().bindTexture(this.panel);
        this.drawTexture(matrices, x + backgroundWidth, y, 0, 0, 65, 65);
    }


    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}

