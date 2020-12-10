package net.flytre.mechanix.api.machine;

import net.flytre.mechanix.api.util.RenderUtils;
import net.flytre.mechanix.block.thermal_generator.ThermalGenEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

/**
 * Draws the tier brackets on the side of machines. PLEASE use this for your machines or its gonna look
 * pretty bad when it doesn't render right. Register in ModnameClient, see MechanixClient
 * @param <T>
 * @param <MachineTier>
 */
public class MachineBlockEntityRenderer<T extends BlockEntity, MachineTier> extends BlockEntityRenderer {

    private static final Identifier IN = new Identifier("mechanix","textures/machine/in.png");
    private static final Identifier OUT = new Identifier("mechanix","textures/machine/out.png");
    private static final Identifier GILDED = new Identifier("mechanix","textures/machine/gilded.png");
    private static final Identifier VYSTERIUM = new Identifier("mechanix","textures/machine/vysterium.png");
    private static final Identifier NEPTUNIUM = new Identifier("mechanix","textures/machine/neptunium.png");


    public MachineBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        if(!(entity instanceof TieredMachine) || !(entity.getCachedState().getBlock() instanceof MachineBlock))
            return;

        int tier = ((TieredMachine) entity).getTier();

        if(entity.getWorld() == null)
            return;

        Identifier id = null;

        if(tier == 1)
            id = GILDED;
        if(tier == 2)
            id = VYSTERIUM;
        if(tier == 3)
            id = NEPTUNIUM;

        if(tier >= 1 && tier <= 3)
            RenderUtils.render(id, matrices,vertexConsumers,light,overlay);

        Direction facing = entity.getCachedState().get(MachineBlock.FACING);
        for(Direction direction : Direction.values()) {
            if(direction == facing)
                continue;
            if(entity instanceof ThermalGenEntity && direction != Direction.DOWN && direction != Direction.UP)
                continue;

            RenderUtils.renderSide(((TieredMachine) entity).getIO().get(direction) ? OUT : IN, matrices, vertexConsumers, light, overlay, direction);
        }

    }
}
