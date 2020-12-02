package net.flytre.mechanix.api.machine;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;

/**
 * Make your machines inherit from this class, this class is needed to make it all work properly!
 */
public abstract class MachineBlock extends BlockWithEntity {
    public static final DirectionProperty FACING;
    public static final BooleanProperty WEST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    public static final BooleanProperty ACTIVATED;

    static {
        FACING = HorizontalFacingBlock.FACING;
        WEST = Properties.WEST;
        SOUTH = Properties.SOUTH;
        EAST = Properties.EAST;
        UP = Properties.UP;
        DOWN = Properties.DOWN;
        ACTIVATED = BooleanProperty.of("activated");
    }

    public MachineBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(WEST, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(UP, false)
                .with(DOWN, false)
                .with(ACTIVATED,false)
        );
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WEST, SOUTH, EAST, UP, DOWN, ACTIVATED);
    }

    public static BooleanProperty getProperty(Direction direction) {
        switch (direction) {
            case UP:
                return UP;
            case DOWN:
                return DOWN;
            case EAST:
                return EAST;
            case WEST:
                return WEST;
            case NORTH:
                return null;
            case SOUTH:
                return SOUTH;
        }
        return null;
    }



    public static void fixBlockState(HashMap<Direction, Boolean> ioMode, BlockPos pos, World world) {
        BlockState current = world.getBlockState(pos);
        boolean bl = false;
        for(Direction dir : Direction.values()) {
            BooleanProperty property = getProperty(dir);
            if(property == null)
                continue;
            boolean currentValue = current.get(property);
            boolean shouldValue = ioMode.get(dir);
            if(currentValue != shouldValue) {
                current = current.with(property,shouldValue);
                bl = true;
            }
        }

        if(bl)
            world.setBlockState(pos,current);
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


    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }



    private void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof NamedScreenHandlerFactory) {
            player.openHandledScreen((NamedScreenHandlerFactory)blockEntity);
        }

    }

}
