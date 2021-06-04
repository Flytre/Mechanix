package net.flytre.mechanix.api.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.flytre_lib.client.gui.CoordinateProvider;
import net.flytre.flytre_lib.client.gui.MultistateButton;
import net.flytre.mechanix.api.fluid.screen.FluidHandler;
import net.flytre.mechanix.util.Packets;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


import static net.flytre.mechanix.api.client.EnergyScreen.QUAD_PANEL_BUTTONS;

/**
 * Screen to use for blocks which have FLUIDS but not ENERGY.
 * ITEMS are optional!
 * This subclass adds a panel to TOGGLE IOTYPES
 *
 * @param <T>
 */
public abstract class PanelledFluidHandledScreen<T extends FluidHandler> extends FluidHandledScreen<T> implements CoordinateProvider {

    public static Identifier PANEL = new Identifier("mechanix:textures/gui/container/panel.png");
    protected final T handler;
    protected boolean synced;

    public PanelledFluidHandledScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        this.synced = false;
    }

    protected abstract BlockPos getPos();

    protected abstract boolean synced();

    protected abstract int getFluidButtonState(Direction dir);

    public void onClicked(int sideId, MultistateButton buttonWidget) {
        if (!synced)
            return;
        buttonWidget.cycleState();
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeBlockPos(getPos());
        passedData.writeInt(sideId);
        passedData.writeInt(buttonWidget.getState());
        ClientPlayNetworking.send(Packets.FLUID_IO_CHANGE, passedData);
    }

    @Override
    public void tick() {
        super.tick();

        if (!synced && synced()) {
            onSynced();
            synced = true;
        }
    }

    protected TranslatableText getPanelArg() {
        return new TranslatableText("gui.mechanix.fluid");
    }

    private void addPanelButton(MultistateButton button) {
        button.setTooltipRenderer(this::renderTooltip);
        TranslatableText arg = getPanelArg();
        button.setTooltips(new TranslatableText("gui.mechanix.output", arg), new TranslatableText("gui.mechanix.input", arg), new TranslatableText("gui.mechanix.io", arg), new TranslatableText("gui.mechanix.neither", arg));
        button.setRenderTooltipWithButton(false);
        this.addButton(button);
    }

    protected void onSynced() {

        int baseX = x + backgroundWidth;

        //in front
        this.addPanelButton(new MultistateButton(baseX + 24, this.y + 24, 16, 16, getFluidButtonState(Direction.NORTH), 4, QUAD_PANEL_BUTTONS, (buttonWidget) -> onClicked(0, (MultistateButton) buttonWidget), 'N'));
        //left
        this.addPanelButton(new MultistateButton(baseX + 4, this.y + 24, 16, 16, getFluidButtonState(Direction.WEST), 4, QUAD_PANEL_BUTTONS, (buttonWidget) -> onClicked(1, (MultistateButton) buttonWidget), 'W'));
        //right
        this.addPanelButton(new MultistateButton(baseX + 44, this.y + 24, 16, 16, getFluidButtonState(Direction.EAST), 4, QUAD_PANEL_BUTTONS, (buttonWidget) -> onClicked(2, (MultistateButton) buttonWidget), 'E'));
        //up
        this.addPanelButton(new MultistateButton(baseX + 24, this.y + 4, 16, 16, getFluidButtonState(Direction.UP), 4, QUAD_PANEL_BUTTONS, (buttonWidget) -> onClicked(3, (MultistateButton) buttonWidget), 'U'));
        //down
        this.addPanelButton(new MultistateButton(baseX + 24, this.y + 44, 16, 16, getFluidButtonState(Direction.DOWN), 4, QUAD_PANEL_BUTTONS, (buttonWidget) -> onClicked(4, (MultistateButton) buttonWidget), 'D'));
        //behind
        this.addPanelButton(new MultistateButton(baseX + 44, this.y + 44, 16, 16, getFluidButtonState(Direction.SOUTH), 4, QUAD_PANEL_BUTTONS, (buttonWidget) -> onClicked(5, (MultistateButton) buttonWidget), 'S'));
    }


    @Override
    public void init() {
        synced = false;
        super.init();
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.client != null;
        this.client.getTextureManager().bindTexture(PANEL);
        this.drawTexture(matrices, x + backgroundWidth, y, 0, 0, 65, 65);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        for (AbstractButtonWidget button : buttons) {
            if (button.isHovered() && button instanceof MultistateButton)
                button.renderToolTip(matrices, mouseX, mouseY);
        }
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
