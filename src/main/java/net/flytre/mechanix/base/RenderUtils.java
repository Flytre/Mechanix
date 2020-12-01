package net.flytre.mechanix.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.flytre.mechanix.util.FluidRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

public class RenderUtils {

    public static int color(World world, BlockPos pos, Fluid fluid) {
        int c = FluidRenderHandlerRegistry.INSTANCE.get(fluid).getFluidColor(world, pos, fluid.getDefaultState());
        if(fluid.isIn(FluidTags.WATER))
            c += 0xFF000000;
        return c;
    }

    public static int meterColor(World world, BlockPos pos, Fluid fluid) {
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


    public static void render(BlockEntity entity, Identifier id, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(
                RenderLayer.of(
                        "vmscreen",
                        VertexFormats.POSITION_COLOR_TEXTURE,
                        7,
                        16,
                        false,
                        true,
                        RenderLayer.MultiPhaseParameters.builder().texture(
                                new RenderPhase.Texture(
                                        id,
                                        false,
                                        false
                                )
                        ).alpha(new RenderPhase.Alpha(0.003921569F)).transparency(
                                new RenderPhase.Transparency("translucent_transparency", () -> {
                                    RenderSystem.enableBlend();
                                    RenderSystem.defaultBlendFunc();
                                }, RenderSystem::disableBlend)
                        ).build(false)
                )
        );
        ModelPart model = new ModelPart(16, 16, 0, 0);

        //all:
        model.addCuboid(-0.01F, -0.01F, -0.01F, 16.02F, 16.02F, 16.02F);

//        //west
//        model.addCuboid(-0.01F, -0.01F, -0.01F, 0.00F, 16.02F, 16.02F);
//
//        //east
//        model.addCuboid(16.01F, -0.01F, -0.01F, 0.00F, 16.02F, 16.02F);


        model.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }
}
