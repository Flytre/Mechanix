package net.flytre.mechanix.block.transmuter;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.client.EnergyScreen;
import net.flytre.mechanix.block.crusher.CrusherHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TransmuterScreen extends EnergyScreen<TransmuterHandler> {

    private final Identifier background = new Identifier("mechanix:textures/gui/container/processing.png");


    public TransmuterScreen(TransmuterHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.client != null;
        this.client.getTextureManager().bindTexture(this.background);
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        this.drawTexture(matrices, this.x + 100, y + 35, 176, 0, (int) (23 * handler.operationProgress()), 17);

        super.drawBackground(matrices,delta,mouseX,mouseY);
    }
}
