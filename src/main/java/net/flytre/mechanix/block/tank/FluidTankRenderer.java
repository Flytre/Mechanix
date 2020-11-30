package net.flytre.mechanix.block.tank;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.flytre.mechanix.base.fluid.FluidStack;
import net.flytre.mechanix.util.FluidRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

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

            Sprite texture = textureName(entity.getWorld(),entity.getPos(), stack.getFluid());
            int[] color = unpackColor(color(entity.getWorld(),entity.getPos(), stack.getFluid()));
            renderBlockSprite(buffer.getBuffer(RenderLayer.getTranslucent()), matrix.peek().getModel(), texture, light, overlay, 2.1f / 16, 13.9f / 16, 2.1f / 16, (15*percent) / 16, 2.1f / 16, 13.9f / 16, color);

            }

        matrix.pop();
        MinecraftClient.getInstance().getProfiler().pop();

    }

    public static int color(World world, BlockPos pos, Fluid fluid) {
        int c = FluidRenderHandlerRegistry.INSTANCE.get(fluid).getFluidColor(world, pos, fluid.getDefaultState());
        if(fluid.isIn(FluidTags.WATER))
            c += 0xFF000000;
        return c;
    }

    public static int color2(World world, BlockPos pos, Fluid fluid) {
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        int c = handler == null ? -1 : handler.getFluidColor(world, pos, fluid.getDefaultState());
        if(fluid.isIn(FluidTags.WATER))
            c += 0xFF000000;

        if(fluid.isIn(FluidTags.LAVA))
            c += 0xFFbd5902;

        if(fluid == FluidRegistry.STILL_PERLIUM || fluid == FluidRegistry.FLOWING_PERLIUM)
            c += 0xFFff1fdf;

        return c;
    }


    public static Sprite textureName(World world, BlockPos pos, Fluid fluid) {
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        Sprite[] sprites = handler.getFluidSprites(world, pos, fluid.getDefaultState());
        return sprites[0];
    }


    public static int[] unpackColor (int color) {

        final int[] colors = new int[4];
        colors[0] = color >> 24 & 0xff; // alpha
        colors[1] = color >> 16 & 0xff; // red
        colors[2] = color >> 8 & 0xff; // green
        colors[3] = color & 0xff; // blue
        return colors;
    }

    public static void renderBlockSprite (VertexConsumer builder, MatrixStack stack, Sprite sprite, int light, int overlay, int[] color) {

        renderBlockSprite(builder, stack.peek().getModel(), sprite, light, overlay, 0f, 1f, 0f, 1f, 0f, 1f, color);
    }

    public static void renderBlockSprite(VertexConsumer builder, Matrix4f pos, Sprite sprite, int light, int overlay, float x1, float x2, float y1, float y2, float z1, float z2, int[] color) {
        renderSpriteSide(builder, pos, sprite, Direction.DOWN, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.UP, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.NORTH, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.SOUTH, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.WEST, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.EAST, light, overlay, x1, x2, y1, y2, z1, z2, color);
    }

    private static void renderSpriteSide(VertexConsumer builder, Matrix4f pos, Sprite sprite, Direction side, int light, int overlay, float x1, float x2, float y1, float y2, float z1, float z2, int[] color) {
        // Convert block size to pixel size
        final double px1 = x1 * 16;
        final double px2 = x2 * 16;
        final double py1 = y1 * 16;
        final double py2 = y2 * 16;
        final double pz1 = z1 * 16;
        final double pz2 = z2 * 16;

        if (side == Direction.DOWN) {
            final float u1 = sprite.getFrameU(px1);
            final float u2 = sprite.getFrameU(px2);
            final float v1 = sprite.getFrameV(pz1);
            final float v2 = sprite.getFrameV(pz2);
            builder.vertex(pos, x1, y1, z2).color(color[1], color[2], color[3], color[0]).texture(u1, v2).overlay(overlay).light(light).normal(0f, -1f, 0f).next();
            builder.vertex(pos, x1, y1, z1).color(color[1], color[2], color[3], color[0]).texture(u1, v1).overlay(overlay).light(light).normal(0f, -1f, 0f).next();
            builder.vertex(pos, x2, y1, z1).color(color[1], color[2], color[3], color[0]).texture(u2, v1).overlay(overlay).light(light).normal(0f, -1f, 0f).next();
            builder.vertex(pos, x2, y1, z2).color(color[1], color[2], color[3], color[0]).texture(u2, v2).overlay(overlay).light(light).normal(0f, -1f, 0f).next();
        }

        if (side == Direction.UP) {
            final float u1 = sprite.getFrameU(px1);
            final float u2 = sprite.getFrameU(px2);
            final float v1 = sprite.getFrameV(pz1);
            final float v2 = sprite.getFrameV(pz2);
            builder.vertex(pos, x1, y2, z2).color(color[1], color[2], color[3], color[0]).texture(u1, v2).overlay(overlay).light(light).normal(0f, 1f, 0f).next();
            builder.vertex(pos, x2, y2, z2).color(color[1], color[2], color[3], color[0]).texture(u2, v2).overlay(overlay).light(light).normal(0f, 1f, 0f).next();
            builder.vertex(pos, x2, y2, z1).color(color[1], color[2], color[3], color[0]).texture(u2, v1).overlay(overlay).light(light).normal(0f, 1f, 0f).next();
            builder.vertex(pos, x1, y2, z1).color(color[1], color[2], color[3], color[0]).texture(u1, v1).overlay(overlay).light(light).normal(0f, 1f, 0f).next();
        }

        if (side == Direction.NORTH) {
            final float u1 = sprite.getFrameU(px1);
            final float u2 = sprite.getFrameU(px2);
            final float v1 = sprite.getFrameV(py1);
            final float v2 = sprite.getFrameV(py2);
            builder.vertex(pos, x1, y1, z1).color(color[1], color[2], color[3], color[0]).texture(u1, v1).overlay(overlay).light(light).normal(0f, 0f, -1f).next();
            builder.vertex(pos, x1, y2, z1).color(color[1], color[2], color[3], color[0]).texture(u1, v2).overlay(overlay).light(light).normal(0f, 0f, -1f).next();
            builder.vertex(pos, x2, y2, z1).color(color[1], color[2], color[3], color[0]).texture(u2, v2).overlay(overlay).light(light).normal(0f, 0f, -1f).next();
            builder.vertex(pos, x2, y1, z1).color(color[1], color[2], color[3], color[0]).texture(u2, v1).overlay(overlay).light(light).normal(0f, 0f, -1f).next();
        }

        if (side == Direction.SOUTH) {
            final float u1 = sprite.getFrameU(px1);
            final float u2 = sprite.getFrameU(px2);
            final float v1 = sprite.getFrameV(py1);
            final float v2 = sprite.getFrameV(py2);
            builder.vertex(pos, x2, y1, z2).color(color[1], color[2], color[3], color[0]).texture(u2, v1).overlay(overlay).light(light).normal(0f, 0f, 1f).next();
            builder.vertex(pos, x2, y2, z2).color(color[1], color[2], color[3], color[0]).texture(u2, v2).overlay(overlay).light(light).normal(0f, 0f, 1f).next();
            builder.vertex(pos, x1, y2, z2).color(color[1], color[2], color[3], color[0]).texture(u1, v2).overlay(overlay).light(light).normal(0f, 0f, 1f).next();
            builder.vertex(pos, x1, y1, z2).color(color[1], color[2], color[3], color[0]).texture(u1, v1).overlay(overlay).light(light).normal(0f, 0f, 1f).next();
        }

        if (side == Direction.WEST) {
            final float u1 = sprite.getFrameU(py1);
            final float u2 = sprite.getFrameU(py2);
            final float v1 = sprite.getFrameV(pz1);
            final float v2 = sprite.getFrameV(pz2);
            builder.vertex(pos, x1, y1, z2).color(color[1], color[2], color[3], color[0]).texture(u1, v2).overlay(overlay).light(light).normal(-1f, 0f, 0f).next();
            builder.vertex(pos, x1, y2, z2).color(color[1], color[2], color[3], color[0]).texture(u2, v2).overlay(overlay).light(light).normal(-1f, 0f, 0f).next();
            builder.vertex(pos, x1, y2, z1).color(color[1], color[2], color[3], color[0]).texture(u2, v1).overlay(overlay).light(light).normal(-1f, 0f, 0f).next();
            builder.vertex(pos, x1, y1, z1).color(color[1], color[2], color[3], color[0]).texture(u1, v1).overlay(overlay).light(light).normal(-1f, 0f, 0f).next();
        }

        if (side == Direction.EAST) {
            final float u1 = sprite.getFrameU(py1);
            final float u2 = sprite.getFrameU(py2);
            final float v1 = sprite.getFrameV(pz1);
            final float v2 = sprite.getFrameV(pz2);
            builder.vertex(pos, x2, y1, z1).color(color[1], color[2], color[3], color[0]).texture(u1, v1).overlay(overlay).light(light).normal(1f, 0f, 0f).next();
            builder.vertex(pos, x2, y2, z1).color(color[1], color[2], color[3], color[0]).texture(u2, v1).overlay(overlay).light(light).normal(1f, 0f, 0f).next();
            builder.vertex(pos, x2, y2, z2).color(color[1], color[2], color[3], color[0]).texture(u2, v2).overlay(overlay).light(light).normal(1f, 0f, 0f).next();
            builder.vertex(pos, x2, y1, z2).color(color[1], color[2], color[3], color[0]).texture(u1, v2).overlay(overlay).light(light).normal(1f, 0f, 0f).next();
        }
    }
}
