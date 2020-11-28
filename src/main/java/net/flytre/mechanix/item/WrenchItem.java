package net.flytre.mechanix.item;

import net.flytre.mechanix.block.cable.Cable;
import net.flytre.mechanix.block.cable.CableSide;
import net.flytre.mechanix.block.fluid_pipe.FluidPipe;
import net.flytre.mechanix.block.item_pipe.ItemPipe;
import net.flytre.mechanix.block.item_pipe.ItemPipeBlockEntity;
import net.flytre.mechanix.block.item_pipe.PipeSide;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class WrenchItem extends Item {
    public WrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient && context.getPlayer() != null && context.getPlayer().isSneaking()) {
            BlockPos pos = context.getBlockPos();
            BlockEntity entity = context.getWorld().getBlockEntity(pos);

            if ((entity instanceof ItemPipeBlockEntity)) {
                ((ItemPipeBlockEntity) entity).setRoundRobinMode(!((ItemPipeBlockEntity) entity).isRoundRobinMode());
            }

        }
        return ActionResult.SUCCESS;
    }


    private void inventoryTickPipeNoShift(BlockHitResult hitResult, Block block, PlayerEntity player, BlockState state) {
        boolean wrenched = false;
        if (block instanceof Cable)
            wrenched = state.get(Cable.getProperty(hitResult.getSide())) == CableSide.WRENCHED;
        if (block instanceof ItemPipe)
            wrenched = state.get(ItemPipe.getProperty(hitResult.getSide())) == PipeSide.WRENCHED;
        if (block instanceof FluidPipe)
            wrenched = state.get(FluidPipe.getProperty(hitResult.getSide())) == PipeSide.WRENCHED;

        player.sendMessage(new TranslatableText("item.mechanix.wrench.1").append(" (" + hitResult.getSide().name() + "): " + wrenched), true);

    }

    private void inventoryTickPipeShift(World world, BlockHitResult hitResult, Block block, PlayerEntity player, BlockState state) {
        if (block instanceof ItemPipe) {
            BlockEntity entity = world.getBlockEntity(hitResult.getBlockPos());
            if(entity instanceof ItemPipeBlockEntity) {
                player.sendMessage(new TranslatableText("item.mechanix.wrench.2").append(": " + ((ItemPipeBlockEntity) entity).isRoundRobinMode()), true);
            }
        }
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!selected || !(entity instanceof PlayerEntity) || world.isClient)
            return;
        PlayerEntity player = (PlayerEntity) entity;
        BlockHitResult hitResult = Item.raycast(world, player, RaycastContext.FluidHandling.NONE);

        if (hitResult.getType() == HitResult.Type.MISS)
            return;

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (((block instanceof Cable)) || ((block instanceof FluidPipe)) || ((block instanceof ItemPipe)))
            if (player.isSneaking())
                inventoryTickPipeShift(world, hitResult, block, player, state);
            else
                inventoryTickPipeNoShift(hitResult, block, player, state);
    }

}
