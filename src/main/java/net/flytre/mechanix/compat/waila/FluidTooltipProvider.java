package net.flytre.mechanix.compat.waila;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.block.tank.FluidTankEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class FluidTooltipProvider implements IBlockComponentProvider {

    public static FluidTooltipProvider INSTANCE = new FluidTooltipProvider();


    @Override
    public void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {

        if (!(accessor.getBlockEntity() instanceof FluidInventory))
            return;


        CompoundTag data = accessor.getServerData();
        if (data != null && data.contains("fluid_size")) {
            int size = data.getInt("fluid_size");
            DefaultedList<FluidStack> fluids = DefaultedList.ofSize(size, FluidStack.EMPTY);
            FluidInventory.fromTag(data, fluids);
            fluids.forEach(stack -> {
                if (!stack.isEmpty())
                    tooltip.addAll(stack.toTooltip(false));

            });
        }
    }
}
