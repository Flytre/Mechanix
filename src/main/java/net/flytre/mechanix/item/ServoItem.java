package net.flytre.mechanix.item;

import net.flytre.mechanix.block.fluid_pipe.FluidPipe;
import net.flytre.mechanix.block.item_pipe.ItemPipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ServoItem extends Item {
    public ServoItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        BlockState og =  context.getWorld().getBlockState(context.getBlockPos());
        if(og.getBlock() instanceof FluidPipe || og.getBlock() instanceof ItemPipe)
            return ActionResult.FAIL;

        BlockPos pos = context.getBlockPos().offset(context.getSide());
        Direction side = context.getSide().getOpposite();
        World world = context.getWorld();
        BlockState state = world.getBlockState(pos);

        if(!(state.getBlock() instanceof FluidPipe || state.getBlock() instanceof ItemPipe))
            return ActionResult.FAIL;

        if (!context.getWorld().isClient && context.getPlayer() != null) {

            PlayerEntity player = context.getPlayer();
            Hand hand = context.getHand();
            Vec3d vec = new Vec3d((double) pos.getX() + 0.5D + (double) side.getOffsetX() * 0.5D, (double) pos.getY() + 0.5D + (double) side.getOffsetY() * 0.5D, (double) pos.getZ() + 0.5D + (double) side.getOffsetZ() * 0.5D);
            BlockHitResult hit = new BlockHitResult(vec, side, pos, false);
            return state.getBlock().onUse(state, world, pos, player, hand, hit);
        }
        return ActionResult.SUCCESS;
    }
}
