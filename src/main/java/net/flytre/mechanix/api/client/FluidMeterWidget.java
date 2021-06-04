package net.flytre.mechanix.api.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.flytre.flytre_lib.client.gui.ButtonTooltipRenderer;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;
import java.util.function.Supplier;


/**
 * A widget to show the amount of fluid a machine has!
 * Largely redundant --> The standard way to implement a widget like this
 * is to extend FluidHandler and FluidHandledScreen and add a fluid slot.
 * See FluidTankScreenHandler & FluidTankScreen for an example.
 */

@Deprecated
public class FluidMeterWidget extends ButtonWidget {

    private static final Identifier BACKGROUND = new Identifier("mechanix:textures/gui/container/fluid_cell.png");
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
        tooltipRenderer.draw(matrices, new FluidStack(this.fluid.apply(stackIndex), amount.get()).toTooltip(true), mouseX, mouseY);
    }


    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        super.renderButton(matrices, mouseX, mouseY, delta);

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(BACKGROUND);
        RenderSystem.enableDepthTest();
        drawTexture(matrices, this.x, this.y, 0, 0, this.width, this.height, 30, 60);

        if (amount.get() > 0 && capacity.get() != 0) {
            double percent = 1.0 - (double) amount.get() / capacity.get();

            percent = Math.min(0.95, percent);

            Fluid fluid = this.fluid.apply(stackIndex);
            drawFluid(matrices, fluid, (int) Math.ceil(height * (1 - percent) - 2), x + 1, y + 1, width - 2, 58);
//            SimpleFluidRenderer.render(fluid,matrices,x + 1, (int) (y + (height*(percent)) + 1),width - 2, (int) Math.ceil(height*(1 - percent) - 2),getZOffset());
        }

        minecraftClient.getTextureManager().bindTexture(OVERLAY);
        drawTexture(matrices, this.x, this.y, 0, 0, this.width, this.height, 30, 60);


        if (isHovering(mouseX, mouseY))
            renderToolTip(matrices, mouseX, mouseY);

    }

    public void drawFluid(MatrixStack matrixStack, Fluid fluid, int drawHeight, int x, int y, int width, int height) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        y += height;

        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);

        // If registry can't find it, don't render.
        if (handler == null) {
            return;
        }

        final Sprite sprite = handler.getFluidSprites(MinecraftClient.getInstance().world, BlockPos.ORIGIN, fluid.getDefaultState())[0];
        int color = FluidRenderHandlerRegistry.INSTANCE.get(fluid).getFluidColor(MinecraftClient.getInstance().world, BlockPos.ORIGIN, fluid.getDefaultState());

        final int iconHeight = sprite.getHeight();
        int offsetHeight = drawHeight;

        RenderSystem.color3f((color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F);

        int iteration = 0;
        while (offsetHeight != 0) {
            final int curHeight = Math.min(offsetHeight, iconHeight);

            DrawableHelper.drawSprite(matrixStack, x, y - offsetHeight, 0, width, curHeight, sprite);
            offsetHeight -= curHeight;
            iteration++;
            if (iteration > 50) {
                break;
            }
        }
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

}
