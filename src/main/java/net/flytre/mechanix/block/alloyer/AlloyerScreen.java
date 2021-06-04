package net.flytre.mechanix.block.alloyer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.client.EnergyScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AlloyerScreen extends EnergyScreen<AlloyerHandler> {

    private final Identifier background = new Identifier("mechanix:textures/gui/container/alloyer.png");


    public AlloyerScreen(AlloyerHandler handler, PlayerInventory inventory, Text title) {
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
        this.drawTexture(matrices, this.x + 98, y + 40, 176, 0, 16, (int) (16 * handler.operationProgress()));
        super.drawBackground(matrices,delta,mouseX,mouseY);
    }
}
