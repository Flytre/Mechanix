package net.flytre.mechanix.item;

import net.flytre.mechanix.api.machine.Tiered;
import net.flytre.mechanix.util.ItemRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TierUpgradeItem extends Item {
    public TierUpgradeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {


        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Item type = context.getStack().getItem();

        if (world.isClient)
            return ActionResult.CONSUME;
        BlockEntity entity = world.getBlockEntity(pos);
        if (!(entity instanceof Tiered))
            return super.useOnBlock(context);
        Tiered machine = (Tiered) entity;

        int destinationTier = 0;
        if (type == ItemRegistry.GILDED_UPGRADE)
            destinationTier = 1;
        if (type == ItemRegistry.VYSTERIUM_UPGRADE)
            destinationTier = 2;
        if (type == ItemRegistry.NEPTUNIUM_UPGRADE)
            destinationTier = 3;

        if(machine.getTier() >= destinationTier)
            return ActionResult.FAIL;

        machine.setTier(Math.max(machine.getTier(), destinationTier));

        if (context.getPlayer() != null)
            context.getPlayer().getStackInHand(context.getHand()).decrement(1);
        return ActionResult.CONSUME;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        TranslatableText text = new TranslatableText("item.mechanix.upgrade.tooltip");
        text.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        tooltip.add(text);
    }
}
