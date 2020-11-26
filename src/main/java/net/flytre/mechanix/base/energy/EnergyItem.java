package net.flytre.mechanix.base.energy;

import net.flytre.mechanix.base.Formatter;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyItem extends BlockItem {
    public EnergyItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

        if(stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
            CompoundTag bet = stack.getTag().getCompound("BlockEntityTag");
            MutableText line = new TranslatableText("tooltip.mechanix.energy").append(
                    Text.of(" " + Formatter.formatNumber(bet.getDouble("energy"),"J") + " / " + Formatter.formatNumber( bet.getDouble("maxEnergy"),"J")));
            tooltip.add(line);

            line = new TranslatableText("tooltip.mechanix.transfer_rate").append(
                    Text.of(" " + Formatter.formatNumber(bet.getDouble("transferRate"),"J")));
            tooltip.add(line);

        }

        super.appendTooltip(stack, world, tooltip, context);
    }
}
