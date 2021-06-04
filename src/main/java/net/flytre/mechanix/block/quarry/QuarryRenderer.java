package net.flytre.mechanix.block.quarry;

import net.flytre.flytre_lib.client.util.RenderUtils;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.machine.MachineEntityRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.Matrix4f;

public class QuarryRenderer extends MachineEntityRenderer<QuarryEntity> {
    public QuarryRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(QuarryEntity entity, float tickDelta, MatrixStack matrix, VertexConsumerProvider buffer, int light, int overlay) {
        MinecraftClient.getInstance().getProfiler().push("quarry");
        matrix.push();


        VertexConsumer consumer = buffer.getBuffer(RenderLayer.getLines());


        Matrix4f matrix4f = matrix.peek().getModel();


        for (float k = entity.getPos().getY(); k >= -1; k -= 0.25) {
            consumer.vertex(matrix4f, 17f, -k, -16.0f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
            consumer.vertex(matrix4f, -16f, -k, -16.0f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();

            consumer.vertex(matrix4f, 17f, -k, 17f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
            consumer.vertex(matrix4f, -16f, -k, 17f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();

            consumer.vertex(matrix4f, 17f, -k, 17f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
            consumer.vertex(matrix4f, 17f, -k, -16f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();

            consumer.vertex(matrix4f, -16.0f, -k, 17f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
            consumer.vertex(matrix4f, -16.0f, -k, -16f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();


        }

        int xPos = entity.getQuarryPos()[0];
        int zPos = entity.getQuarryPos()[2];

        for (float k = 0; k <= 1; k += 0.25f) {
            consumer.vertex(matrix4f, 17f, k, zPos).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
            consumer.vertex(matrix4f, -16f, k, zPos).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();

            consumer.vertex(matrix4f, 17f, k, zPos + 1f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
            consumer.vertex(matrix4f, -16f, k, zPos + 1f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();

            consumer.vertex(matrix4f, xPos + 1f, k, 17f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
            consumer.vertex(matrix4f, xPos + 1f, k, -16f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();

            consumer.vertex(matrix4f, xPos, k, 17f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
            consumer.vertex(matrix4f, xPos, k, -16f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
        }


        for (float k = 0; k - 1 >= entity.getQuarryPos()[1]; k -= 0.25) {
            consumer.vertex(matrix4f, xPos + 1f, k, zPos).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
            consumer.vertex(matrix4f, xPos, k, zPos).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();

            consumer.vertex(matrix4f, xPos + 1, k, zPos + 1f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
            consumer.vertex(matrix4f, xPos, k, zPos + 1f).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();

            consumer.vertex(matrix4f, xPos + 1f, k, zPos + 1).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
            consumer.vertex(matrix4f, xPos + 1f, k, zPos).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();

            consumer.vertex(matrix4f, xPos, k, zPos + 1).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
            consumer.vertex(matrix4f, xPos, k, zPos).color(0.24f, 0.57f, 0.57f, 0.55f).light(light).next();
        }

        matrix.pop();
        MinecraftClient.getInstance().getProfiler().pop();

        super.render(entity, tickDelta, matrix, buffer, light, overlay);
    }
}
