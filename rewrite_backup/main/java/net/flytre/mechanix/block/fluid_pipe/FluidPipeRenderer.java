package net.flytre.mechanix.block.fluid_pipe;


import net.flytre.flytre_lib.client.util.RenderUtils;
import net.flytre.mechanix.block.fluid_pipe.PipeSide;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class FluidPipeRenderer extends BlockEntityRenderer<FluidPipeEntity> {

    public FluidPipeRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(FluidPipeEntity entity, float tickDelta, MatrixStack matrix, VertexConsumerProvider buffer, int light, int overlay) {

        if(entity.getWorld() == null)
            return;
        BlockState state = entity.getWorld().getBlockState(entity.getPos());
        if(!(state.getBlock() instanceof FluidPipe))
            return;


        if(!entity.getFluidStack().isEmpty()) {

            Sprite texture = RenderUtils.textureName(entity.getWorld(), entity.getPos(), entity.getFluidStack().getFluid());
            int[] color = RenderUtils.unpackColor(RenderUtils.color(entity.getWorld(), entity.getPos(), entity.getFluidStack().getFluid()));
            RenderUtils.renderBlockSprite(buffer.getBuffer(RenderLayer.getTranslucent()), matrix.peek().getModel(), texture, light, overlay, 5.1f / 16, 10.900f / 16, 5.1f / 16, 10.9f / 16, 5.1f / 16, 10.900f / 16, color);

            for(Direction dir : Direction.values()) {

                BlockEntity offset = entity.getWorld().getBlockEntity(entity.getPos().offset(dir));

                if(offset == null && state.get(FluidPipe.getProperty(dir)) == PipeSide.SERVO)
                    continue;

                if(offset instanceof FluidPipeEntity) {
                    if(((FluidPipeEntity) offset).getFluidStack().getFluid() != entity.getFluidStack().getFluid())
                        continue;
                }

                if (dir == Direction.UP && state.get(FluidPipe.getProperty(dir)) != PipeSide.NONE)
                    RenderUtils.renderBlockSprite(buffer.getBuffer(RenderLayer.getTranslucent()), matrix.peek().getModel(), texture, light, overlay, 5.1f / 16, 10.900f / 16, 11.1f / 16, 15.900f / 16, 5.1f / 16, 10.900f / 16, color);

                if (dir == Direction.DOWN && state.get(FluidPipe.getProperty(dir)) != PipeSide.NONE)
                    RenderUtils.renderBlockSprite(buffer.getBuffer(RenderLayer.getTranslucent()), matrix.peek().getModel(), texture, light, overlay, 5.1f / 16, 10.900f / 16, 0.1f / 16, 4.900f / 16, 5.1f / 16, 10.900f / 16, color);

                if (dir == Direction.WEST && state.get(FluidPipe.getProperty(dir)) != PipeSide.NONE)
                    RenderUtils.renderBlockSprite(buffer.getBuffer(RenderLayer.getTranslucent()), matrix.peek().getModel(), texture, light, overlay, 0.1f / 16, 4.900f / 16, 5.1f / 16, 10.9f / 16, 5.1f / 16, 10.900f / 16, color);

                if (dir == Direction.EAST && state.get(FluidPipe.getProperty(dir)) != PipeSide.NONE)
                    RenderUtils.renderBlockSprite(buffer.getBuffer(RenderLayer.getTranslucent()), matrix.peek().getModel(), texture, light, overlay, 11.1f / 16, 15.900f / 16, 5.1f / 16, 10.9f / 16, 5.1f / 16, 10.900f / 16, color);

                if (dir == Direction.NORTH && state.get(FluidPipe.getProperty(dir)) != PipeSide.NONE)
                    RenderUtils.renderBlockSprite(buffer.getBuffer(RenderLayer.getTranslucent()), matrix.peek().getModel(), texture, light, overlay, 5.1f / 16, 10.900f / 16, 5.1f / 16, 10.9f / 16, 0.1f / 16, 4.900f / 16, color);

                if (dir == Direction.SOUTH && state.get(FluidPipe.getProperty(dir)) != PipeSide.NONE)
                    RenderUtils.renderBlockSprite(buffer.getBuffer(RenderLayer.getTranslucent()), matrix.peek().getModel(), texture, light, overlay, 5.1f / 16, 10.900f / 16, 5.1f / 16, 10.9f / 16, 11.1f / 16, 15.900f / 16, color);

            }
        }
    }
}
