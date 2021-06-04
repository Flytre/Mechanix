package net.flytre.mechanix.api.machine;

import net.flytre.mechanix.api.energy.EnergyDisplayItem;
import net.flytre.mechanix.api.energy.compat.EnergyEntity;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Simple Machine block class for machines!
 */
public class MachineBlock extends BlockWithEntity {
    public static final DirectionProperty FACING;
    public static final BooleanProperty ACTIVATED;

    static {
        FACING = HorizontalFacingBlock.FACING;
        ACTIVATED = BooleanProperty.of("activated");
    }

    private final Supplier<BlockEntity> entityCreator;

    public MachineBlock(Settings settings, Supplier<BlockEntity> entityCreator) {
        super(settings);
        this.entityCreator = entityCreator;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(ACTIVATED, false)
        );
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof Inventory)
            return ScreenHandler.calculateComparatorOutput(be);
        else if (be instanceof FluidInventory)
            return FluidInventory.calculateComparatorOutput((FluidInventory) be);
        else if (be instanceof EnergyEntity)
            return (int) Math.floor(15.0f * ((EnergyEntity) be).getEnergy() / ((EnergyEntity) be).getMaxEnergy());
        return super.getComparatorOutput(state, world, pos);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack toolStack) {
        EnergyDisplayItem.afterBreak(this, world, player, pos, state, blockEntity, toolStack);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVATED);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient)
            return ActionResult.SUCCESS;
        else {
            this.openScreen(world, pos, player);
            return ActionResult.CONSUME;
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }


    private void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof NamedScreenHandlerFactory) {
            player.openHandledScreen((NamedScreenHandlerFactory) blockEntity);
        }

    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return entityCreator.get();
    }
}
