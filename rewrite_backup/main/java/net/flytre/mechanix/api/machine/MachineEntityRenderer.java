package net.flytre.mechanix.api.machine;

import net.flytre.flytre_lib.client.util.RenderUtils;
import net.flytre.flytre_lib.common.inventory.EasyInventory;
import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.energy.compat.EnergyEntity;
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
 *
 * @param <T>
 */
public class MachineEntityRenderer<T extends BlockEntity> extends BlockEntityRenderer<T> {

    private static final Identifier IN = new Identifier("mechanix", "textures/machine/in.png");
    private static final Identifier OUT = new Identifier("mechanix", "textures/machine/out.png");
    private static final Identifier BOTH = new Identifier("mechanix", "textures/machine/both.png");
    private static final Identifier NEITHER = new Identifier("mechanix", "textures/machine/neither.png");

    private static final Identifier GILDED = new Identifier("mechanix", "textures/machine/gilded.png");
    private static final Identifier VYSTERIUM = new Identifier("mechanix", "textures/machine/vysterium.png");
    private static final Identifier NEPTUNIUM = new Identifier("mechanix", "textures/machine/neptunium.png");


    public MachineEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    private static Identifier getTexture(IOType type) {
        switch (type) {
            case BOTH:
                return BOTH;
            case NEITHER:
                return NEITHER;
            case INPUT:
                return IN;
            case OUTPUT:
                return OUT;
        }
        throw new AssertionError("TF HAPPENED LMAO");
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {


        if (!(entity.getCachedState().getBlock() instanceof MachineBlock) || !(entity instanceof MachineOverlay))
            return;

        if (entity instanceof Tiered) {
            int tier = ((Tiered) entity).getTier();


            Identifier id = null;

            if (tier == 1)
                id = GILDED;
            if (tier == 2)
                id = VYSTERIUM;
            if (tier == 3)
                id = NEPTUNIUM;

            if (tier >= 1 && tier <= 3)
                RenderUtils.render(id, matrices, vertexConsumers, light, overlay);
        }

        Direction facing = entity.getCachedState().get(MachineBlock.FACING);
        for (Direction direction : Direction.values()) {
            if (direction == facing)
                continue;
            if (!((MachineOverlay) entity).renderOverlayOnSides() && direction != Direction.DOWN && direction != Direction.UP)
                continue;

            if (((EnergyEntity) entity).getPanelType() == 0)
                RenderUtils.renderSide(((EnergyEntity) entity).getEnergyIO().get(direction) ? OUT : IN, matrices, vertexConsumers, light, overlay, direction);
            else if (((EnergyEntity) entity).getPanelType() == 1 && entity instanceof EasyInventory)
                RenderUtils.renderSide(getTexture(((EasyInventory) entity).getItemIO().get(direction)), matrices, vertexConsumers, light, overlay, direction);

        }

    }
}
