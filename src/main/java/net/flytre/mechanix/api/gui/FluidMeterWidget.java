package net.flytre.mechanix.api.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.mechanix.api.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;
import java.util.function.Supplier;


/**
 * A widget to show the amount of fluid a machine has!
 */
public class FluidMeterWidget extends ButtonWidget {

    private final BlockPos pos;
    private static final Identifier BCKG = new Identifier("mechanix:textures/gui/container/fluid_cell.png");
    private final int stackIndex;
    private final Supplier<Integer> amount;
    private final Supplier<Integer> capacity;
    private final Function<Integer,Fluid> fluid;

    /**
     * Instantiates a new Fluid meter widget.
     *
     * @param x          the x location
     * @param y          the y location
     * @param pos        the block position of the entity
     * @param stackIndex which index of the fluid inventory this is a widget for
     * @param amount     a function giving the amount of fluid to display
     * @param capacity   a function giving the capacity of the tank
     * @param fluid      a function which gives the fluid to render (given a state index)
     */
    public FluidMeterWidget(int x, int y, BlockPos pos, int stackIndex, Supplier<Integer> amount, Supplier<Integer> capacity, Function<Integer, Fluid> fluid) {
        super(x, y, 30, 60, Text.of(""),(b) -> {});
        this.pos = pos;
        this.stackIndex = stackIndex;
        this.amount = amount;
        this.capacity = capacity;
        this.fluid = fluid;
    }


    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        super.renderButton(matrices, mouseX, mouseY, delta);

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(BCKG);
        RenderSystem.enableDepthTest();
        drawTexture(matrices, this.x, this.y, 0, 0, this.width, this.height, 30, 60);

        if(getAmount() > 0 && getCapacity() != 0) {
            double percent = 1.0 - (double) getAmount() / getCapacity();

            percent = Math.min(0.95,percent);

            Fluid fluid = this.fluid.apply(stackIndex);
            int color = RenderUtils.meterColor(MinecraftClient.getInstance().world, pos, fluid);
            DrawableHelper.fill(matrices, x + 1, y + height - 1, x + width - 1, (int) (y + (height * percent) + 1), color);

        }
    }


    private int getAmount() {
        return amount.get();
    }

    private int getCapacity() {
        return capacity.get();
    }


}
