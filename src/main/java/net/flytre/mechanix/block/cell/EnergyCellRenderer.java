package net.flytre.mechanix.block.cell;

import net.flytre.mechanix.api.util.Formatter;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class EnergyCellRenderer extends BlockEntityRenderer<EnergyCellEntity> {
    public EnergyCellRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(EnergyCellEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        if(blockEntity.getWorld() == null)
            return;

        matrices.push();

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        Matrix4f matrix4f = matrices.peek().getModel();


        double percent = (double) Formatter.energy(blockEntity.getProperties()) / Formatter.maxEnergy(blockEntity.getProperties());


        for(float k = 0.25f; k <= 0.25f + (percent * 0.50f); k+= 0.01f) {
            consumer.vertex(matrix4f, 0.75f, k, 0.0f).color(1.0f, 0.0f, 0.0f, 1.0f).light(light).next();
            consumer.vertex(matrix4f, 0.25f, k, 0.0f).color(1.0f, 0.0f, 0.0f, 1.0f).light(light).next();

            consumer.vertex(matrix4f, 0.75f, k, 1.0f).color(1.0f, 0.0f, 0.0f, 1.0f).light(light).next();
            consumer.vertex(matrix4f, 0.25f, k, 1.0f).color(1.0f, 0.0f, 0.0f, 1.0f).light(light).next();

            consumer.vertex(matrix4f, 1.0f, k, 0.75f).color(1.0f, 0.0f, 0.0f, 1.0f).light(light).next();
            consumer.vertex(matrix4f, 1.0f, k, 0.25f).color(1.0f, 0.0f, 0.0f, 1.0f).light(light).next();

            consumer.vertex(matrix4f, 0.0f, k, 0.75f).color(1.0f, 0.0f, 0.0f, 1.0f).light(light).next();
            consumer.vertex(matrix4f, 0.0f, k, 0.25f).color(1.0f, 0.0f, 0.0f, 1.0f).light(light).next();
        }
        matrices.pop();


    }
}
