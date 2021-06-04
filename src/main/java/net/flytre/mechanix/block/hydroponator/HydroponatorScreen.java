package net.flytre.mechanix.block.hydroponator;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.client.PanelledFluidHandledScreenWithEnergy;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HydroponatorScreen extends PanelledFluidHandledScreenWithEnergy<HydroponatorHandler> {


    private final Identifier background = new Identifier("mechanix:textures/gui/container/hydroponator.png");

    public HydroponatorScreen(HydroponatorHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.client != null;
        this.client.getTextureManager().bindTexture(this.background);
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        double x = this.handler.operationProgress();
        this.drawTexture(matrices, this.x + 104, y + 35, 176, 0, (int) (x * 23) , 16);

        super.drawBackground(matrices,delta,mouseX,mouseY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
