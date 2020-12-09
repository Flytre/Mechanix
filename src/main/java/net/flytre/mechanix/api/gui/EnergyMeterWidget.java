package net.flytre.mechanix.api.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.mechanix.api.util.Formatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;


/**
 * A meter showing how much energy a machine has.
 */
public class EnergyMeterWidget extends TexturedButtonWidget {

    private static final Identifier EMPTY = new Identifier("mechanix:textures/gui/container/energy_empty.png");
    private static final Identifier FULL = new Identifier("mechanix:textures/gui/container/energy_full.png");
    private final PropertyDelegate delegate;
    private ButtonTooltipRenderer tooltipRenderer;

    /**
     * Instantiates a new Energy meter widget.
     *
     * @param x              the x coordinate of where to render this.
     * @param y              the y coordinate of where to render this.
     * @param hoveredVOffset no clue what this does, just set it to 30.
     * @param delegate       The delegate used to calculate how full the bar is. Indexes 3 and 4 MUST contain a formatted
     *                       energy quantity and 5 and 6 must contain the same for max energy. See the Formatter class.
     */
    public EnergyMeterWidget(int x, int y, int hoveredVOffset, PropertyDelegate delegate, ButtonTooltipRenderer tooltipRenderer) {
        super(x, y, 30, 60, 0, 0, hoveredVOffset, EMPTY, 30, 60, (b) -> {
        });
        this.delegate = delegate;
        this.tooltipRenderer = tooltipRenderer;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {

        MutableText line = new TranslatableText("gui.mechanix.energy").append(new LiteralText(Formatter.formatNumber(Formatter.energy(delegate), "J")));
        MutableText line2 = new TranslatableText("gui.mechanix.max").append(new LiteralText(Formatter.formatNumber(Formatter.maxEnergy(delegate), "J")));
        line = line.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        line2 = line2.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        tooltipRenderer.draw(matrices, Arrays.asList(line,line2), mouseX, mouseY);
    }


    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        super.renderButton(matrices, mouseX, mouseY, delta);
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(FULL);

        assert minecraftClient.world != null;
        double energy = Formatter.energy(delegate);
        double max = Formatter.maxEnergy(delegate);
        double u = 60 * (1.0 - (energy / max));
        u = (int) MathHelper.clamp(u, 0, 60);
        RenderSystem.enableDepthTest();
        int startY = (int) (this.y + Math.floor(u));
        int height = (int) (this.height - Math.ceil(u));
        drawTexture(matrices, this.x, startY, (float) 0, (float) u, this.width, height, 30, 60);
        if (isHovering(mouseX, mouseY))
            renderToolTip(matrices, mouseX, mouseY);
    }
}

