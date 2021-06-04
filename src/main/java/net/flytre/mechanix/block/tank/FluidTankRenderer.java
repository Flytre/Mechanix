package net.flytre.mechanix.block.tank;

import net.flytre.flytre_lib.client.util.RenderUtils;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;

public class FluidTankRenderer extends BlockEntityRenderer<FluidTankEntity> {
    public FluidTankRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(FluidTankEntity entity, float tickDelta, MatrixStack matrix, VertexConsumerProvider buffer, int light, int overlay) {
        MinecraftClient.getInstance().getProfiler().push("fluid_tank");
        matrix.push();

        FluidStack stack = entity.getStack();
        long max = entity.capacity();

        if(!stack.isEmpty()) {
            float percent = (float)((double)stack.getAmount() / max);

            if(percent > 0)
                percent = Math.max(0.04f,percent);

            Sprite texture = RenderUtils.textureName(entity.getWorld(),entity.getPos(), stack.getFluid());
            int[] color = RenderUtils.unpackColor(RenderUtils.color(entity.getWorld(),entity.getPos(), stack.getFluid()));
            RenderUtils.renderBlockSprite(buffer.getBuffer(RenderLayer.getTranslucent()), matrix.peek().getModel(), texture, light, overlay, 2.1f / 16, 13.9f / 16, 1.00f / 16, (1.0f + (14f*percent)) / 16, 2.1f / 16, 13.9f / 16, color);

            }

        matrix.pop();
        MinecraftClient.getInstance().getProfiler().pop();

    }


}
