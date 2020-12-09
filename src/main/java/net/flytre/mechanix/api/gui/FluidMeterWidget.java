package net.flytre.mechanix.api.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.util.Formatter;
import net.flytre.mechanix.api.util.SimpleFluidRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * A widget to show the amount of fluid a machine has!
 */
public class FluidMeterWidget extends ButtonWidget {

    private static final Identifier BCKG = new Identifier("mechanix:textures/gui/container/fluid_cell.png");
    private static final Identifier OVERLAY = new Identifier("mechanix:textures/gui/container/fluid_cell_overlay.png");
    private final int stackIndex;
    private final Supplier<Integer> amount;
    private final Supplier<Integer> capacity;
    private final Function<Integer, Fluid> fluid;
    private final ButtonTooltipRenderer tooltipRenderer;

    /**
     * Instantiates a new Fluid meter widget.
     *
     * @param x          the x location
     * @param y          the y location
     * @param stackIndex which index of the fluid inventory this is a widget for
     * @param amount     a function giving the amount of fluid to display
     * @param capacity   a function giving the capacity of the tank
     * @param fluid      a function which gives the fluid to render (given a state index)
     */
    public FluidMeterWidget(int x, int y, int stackIndex, Supplier<Integer> amount, Supplier<Integer> capacity, Function<Integer, Fluid> fluid, ButtonTooltipRenderer tooltipRenderer) {
        super(x, y, 30, 60, Text.of(""), (b) -> {
        });
        this.stackIndex = stackIndex;
        this.amount = amount;
        this.capacity = capacity;
        this.fluid = fluid;
        this.tooltipRenderer = tooltipRenderer;
    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
        List<Text> textList = new FluidStack(this.fluid.apply(stackIndex), amount.get()).toTooltip(true);
        textList.add(Formatter.getModNameToolTip(Registry.FLUID.getId(this.fluid.apply(stackIndex)).getNamespace()));
        tooltipRenderer.draw(matrices, textList, mouseX, mouseY);
    }


    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        super.renderButton(matrices, mouseX, mouseY, delta);

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(BCKG);
        RenderSystem.enableDepthTest();
        drawTexture(matrices, this.x, this.y, 0, 0, this.width, this.height, 30, 60);

        if (amount.get() > 0 && capacity.get() != 0) {
            double percent = 1.0 - (double) amount.get() / capacity.get();

            percent = Math.min(0.95, percent);

            Fluid fluid = this.fluid.apply(stackIndex);
            SimpleFluidRenderer.render(fluid,matrices,x + 1, (int) (y + (height*(percent)) + 1),width - 2, (int) Math.ceil(height*(1 - percent) - 2),getZOffset());
        }

        minecraftClient.getTextureManager().bindTexture(OVERLAY);
        drawTexture(matrices, this.x, this.y, 0, 0, this.width, this.height, 30, 60);


        if (isHovering(mouseX, mouseY))
            renderToolTip(matrices, mouseX, mouseY);

    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

}
