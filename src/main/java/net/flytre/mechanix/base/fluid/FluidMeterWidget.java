package net.flytre.mechanix.base.fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.mechanix.base.Formatter;
import net.flytre.mechanix.block.tank.FluidTankBlockEntity;
import net.flytre.mechanix.block.tank.FluidTankRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class FluidMeterWidget extends ButtonWidget {

    private final PropertyDelegate delegate;
    private final BlockPos pos;
    private static final Identifier BCKG = new Identifier("mechanix:textures/gui/container/fluid_cell.png");


    public FluidMeterWidget(int x, int y, int width, int height, PropertyDelegate delegate, BlockPos pos) {
        super(x, y, width, height, Text.of(""),(b) -> {});
        this.delegate = delegate;
        this.pos = pos;
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
            Fluid fluid = Fluids.LAVA;
            BlockEntity entity = MinecraftClient.getInstance().world.getBlockEntity(pos);
            if(entity instanceof FluidTankBlockEntity) {
                fluid = ((FluidTankBlockEntity) entity).getStack().getFluid();
            }
            int color = FluidTankRenderer.color2(MinecraftClient.getInstance().world, pos, fluid);

            DrawableHelper.fill(matrices, x + 1, y + height - 1, x + width - 1, (int) (y + (height * percent) + 1), color);

        }
    }


    private int getAmount() {
        return Formatter.unsplit(new int[]{delegate.get(1),delegate.get(2)});
    }

    private int getCapacity() {
        return Formatter.unsplit(new int[]{delegate.get(3),delegate.get(4)});
    }


}
