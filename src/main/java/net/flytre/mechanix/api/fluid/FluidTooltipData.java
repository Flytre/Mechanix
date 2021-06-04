package net.flytre.mechanix.api.fluid;

import net.minecraft.text.Text;

import java.util.List;

/**
 * If a fluid implements this, then when the fluid's tooltips are rendered by Mechanix this function will be called so
 * the tooltip can be modified.
 *
 * This is NOT a functional interface
 */
public interface FluidTooltipData {


    void addTooltipInfo(FluidStack stack, List<Text> tooltip);
}
