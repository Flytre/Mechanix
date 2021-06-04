package net.flytre.mechanix.block.fluid_pipe;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.flytre_lib.client.gui.ToggleButton;
import net.flytre.mechanix.api.client.FluidHandledScreen;
import net.flytre.mechanix.util.Packets;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class FluidPipeScreen extends FluidHandledScreen<FluidPipeScreenHandler> {

    private static final Identifier WHITELIST_BUTTON = new Identifier("mechanix:textures/gui/button/check_ex.png");
    private static final Identifier MOD_BUTTON = new Identifier("mechanix:textures/gui/button/mod.png");
    protected final FluidPipeScreenHandler handler;
    private final Identifier background;
    private boolean synced;

    public FluidPipeScreen(FluidPipeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        this.background = new Identifier("mechanix:textures/gui/container/fluid_pipe.png");
        synced = false;
    }

    private void onSynced() {
        int startFrame = handler.getFilterType();
        ToggleButton whitelistButton = new ToggleButton(this.x + 177, this.height / 2 - 80, 16, 16, startFrame, WHITELIST_BUTTON, (buttonWidget) -> {
            sendPacket((ToggleButton) buttonWidget, Packets.FILTER_TYPE);
        }, "");
        whitelistButton.setTooltips(new TranslatableText("block.mechanix.fluid_pipe.whitelist"), new TranslatableText("block.mechanix.fluid_pipe.blacklist"));
        whitelistButton.setTooltipRenderer(this::renderTooltip);

        this.addButton(whitelistButton);


        startFrame = handler.getModMatch();
        ToggleButton modButton = new ToggleButton(this.x + 177, this.height / 2 - 60, 16, 16, startFrame, MOD_BUTTON, (buttonWidget) -> {
            sendPacket((ToggleButton) buttonWidget, Packets.MOD_MATCH);
        }, "");
        modButton.setTooltips(new TranslatableText("block.mechanix.fluid_pipe.mod_match.false"), new TranslatableText("block.mechanix.fluid_pipe.mod_match.true"));
        modButton.setTooltipRenderer(this::renderTooltip);

        this.addButton(modButton);

    }


    public void sendPacket(ToggleButton button, Identifier channel) {
        button.toggleState();
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeBlockPos(handler.getPos());
        passedData.writeInt(button.getState());
        ClientPlayNetworking.send(channel, passedData);
    }

    @Override
    public void tick() {
        super.tick();

        if (!synced && handler.getSynced()) {
            onSynced();
            synced = true;
        }
    }


    @Override
    protected void init() {
        synced = false;
        super.init();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.client != null;

        this.client.getTextureManager().bindTexture(this.background);
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
