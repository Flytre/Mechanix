package net.flytre.mechanix.fluid;

import net.flytre.flytre_lib.common.util.EnchantmentUtils;
import net.flytre.flytre_lib.common.util.Formatter;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.fluid.FluidTooltipData;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Supplier;

import static net.flytre.mechanix.api.fluid.FluidStack.UNITS_PER_EXPERIENCE;

public class ExperienceFluid extends MetallicFluid.Still implements FluidTooltipData {
    public ExperienceFluid(Supplier<FluidType> test) {
        super(test);
    }

    @Override
    public void addTooltipInfo(FluidStack stack, List<Text> tooltip) {
        MutableText line = new TranslatableText("tooltip.mechanix.levels", EnchantmentUtils.getExperienceLevel((int) (stack.getAmount() / UNITS_PER_EXPERIENCE)));
        line = line.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        tooltip.add(line);
        line = new TranslatableText("tooltip.mechanix.points", Formatter.formatNumber((int) (stack.getAmount() / UNITS_PER_EXPERIENCE), ""));
        line = line.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        tooltip.add(line);
    }
}
