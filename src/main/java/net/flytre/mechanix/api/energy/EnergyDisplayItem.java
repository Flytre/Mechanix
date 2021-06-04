package net.flytre.mechanix.api.energy;

import net.flytre.flytre_lib.common.util.Formatter;
import net.flytre.mechanix.block.cell.EnergyCellEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Adds a tooltip showing the amount of energy an item has.
 */
public class EnergyDisplayItem extends BlockItem {
    public EnergyDisplayItem(Block block, Settings settings) {
        super(block, settings);
    }


    public static void afterBreak(Block block, World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack toolStack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(block));
        player.addExhaustion(0.005F);
        if (world instanceof ServerWorld) {
            Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, player, toolStack).forEach(stack -> {
                if (blockEntity != null) {
                    CompoundTag cellTag = blockEntity.toTag(new CompoundTag());
                    stack.getOrCreateTag().put("BlockEntityTag", cellTag);
                }
                Block.dropStack(world, pos, stack);
            });
            state.onStacksDropped((ServerWorld) world, pos, toolStack);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
            CompoundTag bet = stack.getTag().getCompound("BlockEntityTag");
            MutableText line = new TranslatableText("tooltip.mechanix.energy").append(
                    Text.of(" §7" + Formatter.formatNumber(bet.getDouble("energy"), "S", true) + " / " + Formatter.formatNumber(bet.getDouble("maxEnergy"), "J", true)));
            tooltip.add(line);

            line = new TranslatableText("tooltip.mechanix.transfer_rate").append(
                    Text.of(" §7" + Formatter.formatNumber(bet.getDouble("transferRate"), "S", true) + "/tick"));
            tooltip.add(line);

        }

        super.appendTooltip(stack, world, tooltip, context);
    }
}
