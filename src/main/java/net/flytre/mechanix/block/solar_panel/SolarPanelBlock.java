package net.flytre.mechanix.block.solar_panel;

import net.flytre.mechanix.api.connectable.CableConnectable;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlock extends BlockWithEntity implements CableConnectable {

    private static VoxelShape OUTLINE;

    static {
        OUTLINE = Block.createCuboidShape(0,0,0,16,1,16);
        OUTLINE = VoxelShapes.combineAndSimplify(OUTLINE, Block.createCuboidShape(0,12,0,16,16,16), BooleanBiFunction.OR);
        OUTLINE = VoxelShapes.combineAndSimplify(OUTLINE, Block.createCuboidShape(6,1,6,11,12,11), BooleanBiFunction.OR);

    }

    public SolarPanelBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.isClient)
            return ActionResult.SUCCESS;
        else {
            this.openScreen(world, pos, player);
            return ActionResult.CONSUME;
        }
    }


    private void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof NamedScreenHandlerFactory) {
            player.openHandledScreen((NamedScreenHandlerFactory)blockEntity);
        }

    }


    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new SolarPanelEntity();
    }
}
