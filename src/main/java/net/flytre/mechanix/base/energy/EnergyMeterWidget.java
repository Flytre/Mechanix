package net.flytre.mechanix.base.energy;

import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.mechanix.base.Formatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EnergyMeterWidget extends TexturedButtonWidget {

    private static final Identifier EMPTY = new Identifier("mechanix:textures/gui/container/energy_empty.png");
    private static final Identifier FULL = new Identifier("mechanix:textures/gui/container/energy_full.png");
    private final PropertyDelegate delegate;

    public EnergyMeterWidget(int x, int y, int width, int height, int hoveredVOffset, PropertyDelegate delegate) {
        super(x, y, width, height, 0, 0, hoveredVOffset, EMPTY,30,60,(b) -> {});
        this.delegate = delegate;
    }


    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        super.renderButton(matrices, mouseX, mouseY, delta);
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(FULL);

        assert minecraftClient.world != null;
        double energy = Formatter.energy(delegate);
        double max = Formatter.maxEnergy(delegate);
        double u = 60 * (1.0 - (energy/max));
        u = (int)MathHelper.clamp(u,0,60);
        RenderSystem.enableDepthTest();
        int startY = (int) (this.y + Math.floor(u));
        int height = (int) (this.height - Math.ceil(u));
        drawTexture(matrices, this.x, startY, (float)0, (float)u, this.width, height, 30, 60);
    }
}

