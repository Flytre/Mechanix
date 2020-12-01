package net.flytre.mechanix.block.tank;

import net.flytre.mechanix.base.RenderUtils;
import net.flytre.mechanix.base.fluid.FluidStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;

public class FluidTankRenderer extends BlockEntityRenderer<FluidTankBlockEntity> {
    public FluidTankRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(FluidTankBlockEntity entity, float tickDelta, MatrixStack matrix, VertexConsumerProvider buffer, int light, int overlay) {
        MinecraftClient.getInstance().getProfiler().push("fluid_tank");
        matrix.push();

        FluidStack stack = entity.getStack();
        int max = entity.capacity();

        if(!stack.isEmpty()) {
            float percent = (float)((double)stack.getAmount() / max);

            if(percent > 0)
                percent = Math.max(0.04f,percent);

            Sprite texture = RenderUtils.textureName(entity.getWorld(),entity.getPos(), stack.getFluid());
            int[] color = RenderUtils.unpackColor(RenderUtils.color(entity.getWorld(),entity.getPos(), stack.getFluid()));
            RenderUtils.renderBlockSprite(buffer.getBuffer(RenderLayer.getTranslucent()), matrix.peek().getModel(), texture, light, overlay, 2.1f / 16, 13.9f / 16, 2.1f / 16, (15*percent) / 16, 2.1f / 16, 13.9f / 16, color);

            }

        matrix.pop();
        MinecraftClient.getInstance().getProfiler().pop();

    }


}
