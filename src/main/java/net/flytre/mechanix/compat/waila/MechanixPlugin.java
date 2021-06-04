package net.flytre.mechanix.compat.waila;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.block.entity.BlockEntity;

public class MechanixPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addComponent(FluidTooltipProvider.INSTANCE, TooltipPosition.BODY, BlockEntity.class);
        registrar.addComponent(EnergyTooltipProvider.INSTANCE, TooltipPosition.BODY, BlockEntity.class, 1100);
        registrar.addBlockData(FluidServerDataProvider.INSTANCE, BlockEntity.class);
        registrar.addBlockData(EnergyServerDataProvider.INSTANCE, BlockEntity.class);

    }
}
