package net.flytre.mechanix.block.tank;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.client.PanelledFluidHandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FluidTankScreen extends PanelledFluidHandledScreen<FluidTankScreenHandler> {

    protected Identifier background;


    public FluidTankScreen(FluidTankScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.titleY--;
        this.playerInventoryTitleY += 2;
        this.background = new Identifier("mechanix:textures/gui/container/basic.png");
        ++this.backgroundHeight;
    }

    @Override
    protected BlockPos getPos() {
        return handler.getPos();
    }

    @Override
    protected boolean synced() {
        return handler.isSynced();
    }

    @Override
    protected int getFluidButtonState(Direction dir) {
        return handler.fluidButtonState(dir);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        super.drawBackground(matrices, delta, mouseX, mouseY);
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
}
