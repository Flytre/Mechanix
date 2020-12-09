package net.flytre.mechanix.api.util;


import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class SimpleFluidRenderer {
    private static final Map<Fluid, FluidRenderingData> FLUID_DATA = new HashMap<>();

    private SimpleFluidRenderer() {
    }

    @Nullable
    public static FluidRenderingData fromFluid(Fluid fluid) {
        return FLUID_DATA.computeIfAbsent(fluid, FluidRenderingDataImpl::from);
    }

    public static void render(Fluid fluid, MatrixStack matrices, int x0, int y0, int width, int height, int zOffset) {
        SimpleFluidRenderer.FluidRenderingData renderingData = SimpleFluidRenderer.fromFluid(fluid);
        if (renderingData != null) {
            Sprite sprite = renderingData.getSprite();
            int color = renderingData.getColor();
            int a = 255;
            int r = (color >> 16 & 255);
            int g = (color >> 8 & 255);
            int b = (color & 255);
            MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder bb = tess.getBuffer();
            Matrix4f matrix = matrices.peek().getModel();
            if (height / width >= 1) {
                int times = (int) Math.ceil((double) height / width);
                for (int i = 0; i < times; i++) {
                    int newY0 = y0 + i * width;
                    int newY1 = Math.min(newY0 + width, y0 + height);
                    bb.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                    bb.vertex(matrix, x0 + width, newY0, zOffset).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, a).next();
                    bb.vertex(matrix, x0, newY0, zOffset).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, a).next();
                    bb.vertex(matrix, x0, newY1, zOffset).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, a).next();
                    bb.vertex(matrix, x0 + width, newY1, zOffset).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, a).next();
                    tess.draw();
                }
            } else {
                int times = Math.min((int) Math.ceil((double) width / height), 3);
                for (int i = 0; i < times; i++) {
                    int newX0 = x0 + i * Math.max(height, width / 3);
                    int newX1 = Math.min(newX0 + Math.max(height, width / 3 + 1), x0 + width);
                    bb.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                    bb.vertex(matrix, newX1, y0, zOffset).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, a).next();
                    bb.vertex(matrix, newX0, y0, zOffset).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, a).next();
                    bb.vertex(matrix, newX0, y0 + height, zOffset).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, a).next();
                    bb.vertex(matrix, newX1, y0 + height, zOffset).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, a).next();
                    tess.draw();
                }
            }
        }
    }

    public interface FluidRenderingData {
        Sprite getSprite();

        int getColor();
    }

    public static final class FluidRenderingDataImpl implements FluidRenderingData {
        private final Sprite sprite;
        private final int color;

        public FluidRenderingDataImpl(Sprite sprite, int color) {
            this.sprite = sprite;
            this.color = color;
        }

        public static FluidRenderingData from(Fluid fluid) {
            FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
            if (fluidRenderHandler == null)
                return null;
            Sprite[] sprites = fluidRenderHandler.getFluidSprites(MinecraftClient.getInstance().world, MinecraftClient.getInstance().world == null ? null : BlockPos.ORIGIN, fluid.getDefaultState());
            int color = -1;
            if (MinecraftClient.getInstance().world != null)
                color = fluidRenderHandler.getFluidColor(MinecraftClient.getInstance().world, BlockPos.ORIGIN, fluid.getDefaultState());
            return new FluidRenderingDataImpl(sprites[0], color);
        }

        @Override
        public Sprite getSprite() {
            return sprite;
        }

        @Override
        public int getColor() {
            return color;
        }
    }
}

