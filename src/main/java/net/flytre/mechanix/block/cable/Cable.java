package net.flytre.mechanix.block.cable;

import net.flytre.mechanix.base.Connectable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class Cable extends Block implements Connectable {

    public static final Property<Boolean> UP;
    public static final Property<Boolean> DOWN;
    public static final Property<Boolean> NORTH;
    public static final Property<Boolean> SOUTH;
    public static final Property<Boolean> EAST;
    public static final Property<Boolean> WEST;

    private static final VoxelShape NODE;
    private static final VoxelShape C_UP;
    private static final VoxelShape C_DOWN;
    private static final VoxelShape C_EAST;
    private static final VoxelShape C_WEST;
    private static final VoxelShape C_NORTH;
    private static final VoxelShape C_SOUTH;


    static {
        UP = Properties.UP;
        DOWN = Properties.DOWN;
        NORTH = Properties.NORTH;
        SOUTH = Properties.SOUTH;
        EAST = Properties.EAST;
        WEST = Properties.WEST;
        NODE = Block.createCuboidShape(4.5,4.5,4.5,11.5,11.5,11.5);
        C_DOWN = Block.createCuboidShape(4.5,0,4.5,11.5,5,11.5);
        C_UP = Block.createCuboidShape(4.5,11,4.5,11.5,16,11.5);
        C_EAST = Block.createCuboidShape(11,4.5,4.5,16,11.5,11.5);
        C_WEST = Block.createCuboidShape(0,4.5,4.5,5,11.5,11.5);
        C_NORTH = Block.createCuboidShape(4.5,4.5,0,11.5,11.5,5);
        C_SOUTH = Block.createCuboidShape(4.5,4.5,11,11.5,11.5,16);
    }

    public Cable(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(UP, false)
                .with(DOWN, false)
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false));
    }


    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = NODE;
        if(state.get(UP))
            shape = VoxelShapes.combineAndSimplify(shape, C_UP, BooleanBiFunction.OR);
        if(state.get(DOWN))
            shape = VoxelShapes.combineAndSimplify(shape, C_DOWN, BooleanBiFunction.OR);
        if(state.get(NORTH))
            shape = VoxelShapes.combineAndSimplify(shape, C_NORTH, BooleanBiFunction.OR);
        if(state.get(EAST))
            shape = VoxelShapes.combineAndSimplify(shape, C_EAST, BooleanBiFunction.OR);
        if(state.get(SOUTH))
            shape = VoxelShapes.combineAndSimplify(shape, C_SOUTH, BooleanBiFunction.OR);
        if(state.get(WEST))
            shape = VoxelShapes.combineAndSimplify(shape, C_WEST, BooleanBiFunction.OR);
        return shape;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        Block neighbor = world.getBlockState(posFrom).getBlock();
        return state.with(getProperty(direction), isConnectable(neighbor));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState();
        BlockPos blockPos = ctx.getBlockPos();
        for (Direction direction : Direction.values()) {
            Block neighbor = ctx.getWorld().getBlockState(blockPos.offset(direction)).getBlock();
            state = state.with(getProperty(direction), isConnectable(neighbor));

        }
        return state;
    }

    private boolean isConnectable(Block block) {
        return block instanceof Connectable;
    }

    private Property<Boolean> getProperty(Direction facing) {
        switch (facing) {
            case UP:
                return UP;
            case DOWN:
                return DOWN;
            case EAST:
                return EAST;
            case WEST:
                return WEST;
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
        }
        return null;
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }



}
