package net.flytre.mechanix.base;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class MachineBlockEntityRenderer<T extends BlockEntity, MachineTier> extends BlockEntityRenderer {


    public MachineBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        if(!(entity instanceof TieredMachine))
            return;

        int tier = ((TieredMachine) entity).getTier();

        Identifier id = null;

        if(tier == 1)
            id = new Identifier("mechanix","textures/machine/gilded.png");
        if(tier == 2)
            id = new Identifier("mechanix","textures/machine/vysterium.png");
        if(tier == 3)
            id = new Identifier("mechanix","textures/machine/neptunium.png");

        if(tier >= 1 && tier <= 3)
            RenderUtils.render(entity,id,tickDelta,matrices,vertexConsumers,light,overlay);
    }
}
