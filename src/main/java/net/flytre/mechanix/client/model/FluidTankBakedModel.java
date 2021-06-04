package net.flytre.mechanix.client.model;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.flytre.flytre_lib.client.util.RenderUtils;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.block.tank.FluidTankEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class FluidTankBakedModel implements UnbakedModel, BakedModel, FabricBakedModel {


    private static final ModelIdentifier MODEL_TANK = new ModelIdentifier(
            new Identifier("mechanix", "tank"),
            "light=0"
    );

    private static final ModelIdentifier GILDED_MODEL_TANK = new ModelIdentifier(
            new Identifier("mechanix", "gilded_tank"),
            "light=0"
    );

    private static final ModelIdentifier VYSTERIUM_MODEL_TANK = new ModelIdentifier(
            new Identifier("mechanix", "vysterium_tank"),
            "light=0"
    );

    private static final ModelIdentifier NEPTUNIUM_MODEL_TANK = new ModelIdentifier(
            new Identifier("mechanix", "neptunium_tank"),
            "light=0"
    );

    private static final ModelIdentifier[] POTENTIALS = new ModelIdentifier[]{MODEL_TANK, GILDED_MODEL_TANK, VYSTERIUM_MODEL_TANK, NEPTUNIUM_MODEL_TANK};


    @Override
    public ModelTransformation getTransformation() {
        return MinecraftClient.getInstance().getBakedModelManager().getModel(
                new ModelIdentifier(new Identifier("stone"), "")
        ).getTransformation();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        Identifier registry = Registry.ITEM.getId(stack.getItem());
        for (ModelIdentifier potential : POTENTIALS) {
            if (potential.getPath().equals(registry.getPath())) {
                BakedModel tankModel = MinecraftClient.getInstance().getBakedModelManager().getModel(potential);
                context.fallbackConsumer().accept(tankModel);
                break;
            }
        }

        FluidTankEntity entity = new FluidTankEntity();
        entity.fromTag(MachineRegistry.FLUID_TANKS.getStandard().getDefaultState(), stack.getOrCreateSubTag("BlockEntityTag"));

        PlayerEntity player = MinecraftClient.getInstance().player;

        assert player != null;
        World world = player.world;
        BlockPos pos = player.getBlockPos();

        FluidStack fluidStack = entity.getStack();

        if (fluidStack.isEmpty())
            return;

        long max = entity.capacity();
        float percent = (float) ((double) fluidStack.getAmount() / max);

        if (percent > 0)
            percent = Math.max(0.04f, percent);

        FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluidStack.getFluid());
        int fluidColor = fluidRenderHandler.getFluidColor(world, pos, fluidStack.getFluid().getDefaultState());
        Sprite fluidSprite = RenderUtils.textureName(entity.getWorld(), entity.getPos(), fluidStack.getFluid());
        int color = 255 << 24 | fluidColor;
        context.pushTransform(quad -> {
            quad.spriteColor(0, color, color, color, color);
            return true;
        });

        QuadEmitter emitter = context.getEmitter();

        draw(emitter, Direction.NORTH, fluidSprite, 2.1f / 16, 1.00f / 16, 13.9f / 16, (1.0f + (14f * percent)) / 16, 0.09575f);
        draw(emitter, Direction.SOUTH, fluidSprite, 2.1f / 16, 1.00f / 16, 13.9f / 16, (1.0f + (14f * percent)) / 16, 0.09575f);
        draw(emitter, Direction.EAST, fluidSprite, 2.1f / 16, 1.00f / 16, 13.9f / 16, (1.0f + (14f * percent)) / 16, 0.09575f);
        draw(emitter, Direction.WEST, fluidSprite, 2.1f / 16, 1.00f / 16, 13.9f / 16, (1.0f + (14f * percent)) / 16, 0.09575f);
        draw(emitter, Direction.UP, fluidSprite, 1.1f / 16, 1.1f / 16, 14.9f / 16, 14.9f / 16, 1f - (1f + (14f * percent)) / 16);
        context.popTransform();
    }


    private void draw(QuadEmitter emitter, Direction side, Sprite sprite, float left, float bottom, float right, float top, float depth) {
        emitter.square(side, left, bottom, right, top, depth);
        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {

    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return new ArrayList<>();
    }

    @Override
    public Sprite getSprite() {
        return null;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return new ArrayList<>();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return new ArrayList<>();
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        return this;
    }
}
