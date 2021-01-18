package net.flytre.mechanix.block.xp_bank;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.mechanix.api.gui.FluidMeterWidget;
import net.flytre.mechanix.api.gui.PanelledScreen;
import net.flytre.mechanix.api.gui.ToggleButton;
import net.flytre.mechanix.util.FluidRegistry;
import net.flytre.mechanix.util.Packets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static net.flytre.mechanix.api.energy.EnergyScreen.BUTTONS;

public class XpBankScreen extends PanelledScreen<XpBankScreenHandler> {
    protected final XpBankScreenHandler handler;
    private boolean synced;
    private final Identifier panel;
    private final Identifier background;
    private FluidMeterWidget meter;


    public XpBankScreen(XpBankScreenHandler handler, PlayerInventory inventory, Text title) {
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
        ClientPlayNetworking.send(Packets.FLUID_IO_CHANGE, passedData);
    }

    @Override
    public void tick() {
        super.tick();

        if(!synced && handler.getSynced()) {
            onSynced();
            synced = true;
        }
    }


    private void requestXPTransfer(int amount) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(handler.getPos());
        buf.writeInt(amount);
        ClientPlayNetworking.send(Packets.XP_TRANSFER,buf);
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

        //+ buttons
        this.addButton(new ButtonWidget(x + 8,y + 20,32,20,Text.of("§2+1"),(i) -> requestXPTransfer(1)));
        this.addButton(new ButtonWidget(x + 8,y + 50,32,20,Text.of("§2+10"),(i) -> requestXPTransfer(10)));
        this.addButton(new ButtonWidget(x + 40,y + 20,32,20,Text.of("§2+100"),(i) -> requestXPTransfer(100)));
        this.addButton(new ButtonWidget(x + 40,y + 50,32,20,Text.of("§2+1000"),(i) -> requestXPTransfer(1000)));

        //- buttons
        this.addButton(new ButtonWidget(x + 105,y + 20,32,20,Text.of("§c-1"),(i) -> requestXPTransfer(-1)));
        this.addButton(new ButtonWidget(x + 105,y + 50,32,20,Text.of("§c-10"),(i) -> requestXPTransfer(-10)));
        this.addButton(new ButtonWidget(x + 137,y + 20,32,20,Text.of("§c-100"),(i) -> requestXPTransfer(-100)));
        this.addButton(new ButtonWidget(x + 137,y + 50,32,20,Text.of("§c-1000"),(i) -> requestXPTransfer(-1000)));


        meter = new FluidMeterWidget(x + 73, y + 15, 0,
                handler::getAmount,
                () -> 32000,
                (i) -> FluidRegistry.LIQUID_XP.getStill(), this::renderTooltip);
        this.addButton(meter);
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
        textRenderer.draw(matrices,new TranslatableText("gui.mechanix.xp").append(new LiteralText("" + MinecraftClient.getInstance().player.totalExperience)),x + 99,y + 5,0);
    }

}
