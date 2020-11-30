package net.flytre.mechanix.base.fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.mechanix.block.tank.FluidTankRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;
import java.util.function.Supplier;

public class FluidMeterWidget extends ButtonWidget {

    private final PropertyDelegate delegate;
    private final BlockPos pos;
    private static final Identifier BCKG = new Identifier("mechanix:textures/gui/container/fluid_cell.png");
    private final int stackIndex;
    private final Supplier<Integer> amount;
    private final Supplier<Integer> capacity;
    private final Function<Integer,Fluid> fluid;

    public FluidMeterWidget(int x, int y, int width, int height, PropertyDelegate delegate, BlockPos pos, int stackIndex, Supplier<Integer> amount, Supplier<Integer> capacity, Function<Integer,Fluid> fluid) {
        super(x, y, width, height, Text.of(""),(b) -> {});
        this.delegate = delegate;
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

            Fluid fluid = this.fluid.apply(stackIndex);
            int color = FluidTankRenderer.color2(MinecraftClient.getInstance().world, pos, fluid);
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
