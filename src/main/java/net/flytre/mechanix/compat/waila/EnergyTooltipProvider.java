package net.flytre.mechanix.compat.waila;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.flytre.flytre_lib.common.util.Formatter;
import net.flytre.mechanix.api.energy.compat.EnergyEntity;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class EnergyTooltipProvider implements IBlockComponentProvider {

    public static EnergyTooltipProvider INSTANCE = new EnergyTooltipProvider();


    @Override
    public void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {

        if (!(accessor.getBlockEntity() instanceof EnergyEntity))
            return;


        CompoundTag data = accessor.getServerData();
        double energy = data.getDouble("energy");
        double maxEnergy = data.getDouble("maxEnergy");

        MutableText line = new TranslatableText("gui.mechanix.waila.energy", Formatter.formatNumber(energy, "S"), Formatter.formatNumber(maxEnergy, "S"));
        tooltip.add(line);
    }
}
