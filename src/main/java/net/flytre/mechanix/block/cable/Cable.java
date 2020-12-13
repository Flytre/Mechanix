package net.flytre.mechanix.block.cable;

import net.flytre.mechanix.api.connectable.CableConnectable;
import net.flytre.mechanix.util.ItemRegistery;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import team.reborn.energy.Energy;

public class Cable extends Block implements CableConnectable {

    public static final EnumProperty<CableSide> UP;
    public static final EnumProperty<CableSide> DOWN;
    public static final EnumProperty<CableSide> NORTH;
    public static final EnumProperty<CableSide> SOUTH;
    public static final EnumProperty<CableSide> EAST;
    public static final EnumProperty<CableSide> WEST;

    private static final VoxelShape NODE;
    private static final VoxelShape C_UP;
    private static final VoxelShape C_DOWN;
    private static final VoxelShape C_EAST;
    private static final VoxelShape C_WEST;
    private static final VoxelShape C_NORTH;
    private static final VoxelShape C_SOUTH;


    static {
        UP = EnumProperty.of("up", CableSide.class);
        DOWN = EnumProperty.of("down", CableSide.class);;
        NORTH = EnumProperty.of("north", CableSide.class);;
        SOUTH = EnumProperty.of("south", CableSide.class);;
        EAST = EnumProperty.of("east", CableSide.class);;
        WEST = EnumProperty.of("west", CableSide.class);;
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
                .with(UP, CableSide.NONE)
                .with(DOWN,  CableSide.NONE)
                .with(NORTH,  CableSide.NONE)
                .with(SOUTH,  CableSide.NONE)
                .with(EAST,  CableSide.NONE)
                .with(WEST,  CableSide.NONE));
    }


    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = NODE;
        if(state.get(UP) == CableSide.CONNECTED)
            shape = VoxelShapes.combineAndSimplify(shape, C_UP, BooleanBiFunction.OR);
        if(state.get(DOWN) == CableSide.CONNECTED)
            shape = VoxelShapes.combineAndSimplify(shape, C_DOWN, BooleanBiFunction.OR);
        if(state.get(NORTH) == CableSide.CONNECTED)
            shape = VoxelShapes.combineAndSimplify(shape, C_NORTH, BooleanBiFunction.OR);
        if(state.get(EAST) == CableSide.CONNECTED)
            shape = VoxelShapes.combineAndSimplify(shape, C_EAST, BooleanBiFunction.OR);
        if(state.get(SOUTH) == CableSide.CONNECTED)
            shape = VoxelShapes.combineAndSimplify(shape, C_SOUTH, BooleanBiFunction.OR);
        if(state.get(WEST) == CableSide.CONNECTED)
            shape = VoxelShapes.combineAndSimplify(shape, C_WEST, BooleanBiFunction.OR);
        return shape;
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        Item item = player.getStackInHand(hand).getItem();
        if (world.isClient) {
            return item == ItemRegistery.WRENCH ? ActionResult.SUCCESS : ActionResult.PASS;
        } else {

            if (!(item == ItemRegistery.WRENCH))
                return ActionResult.PASS;

            Direction side = hit.getSide();
            CableSide current = state.get(getProperty(side));
            //WRENCH:
            BlockState state1 = world.getBlockState(pos.offset(side));
            if (state1.getBlock() instanceof Cable && state.get(getProperty(side)) == CableSide.NONE && state1.get(getProperty(side.getOpposite())) == CableSide.WRENCHED) {
                world.setBlockState(pos.offset(side), state1.with(getProperty(side.getOpposite()), CableSide.NONE));
            } else if (!(current == CableSide.WRENCHED)) {
                world.setBlockState(pos, state.with(getProperty(side), CableSide.WRENCHED));
                world.setBlockState(pos.offset(side),state1.with(getProperty(side.getOpposite()),CableSide.NONE));
            } else {
                world.setBlockState(pos, state.with(getProperty(side), isConnectable(state1.getBlock(), world.getBlockEntity(pos.offset(side))) ? CableSide.CONNECTED : CableSide.NONE));
            }

            return super.onUse(state, world, pos, player, hand, hit);
        }
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        Block neighbor = world.getBlockState(posFrom).getBlock();
        BlockState neighborState = world.getBlockState(posFrom);
        if (state.get(getProperty(direction)) ==  CableSide.WRENCHED)
            return state;

        if (neighbor instanceof Cable && neighborState.get(getProperty(direction.getOpposite())) == CableSide.WRENCHED) {
            return state;
        }

        return state.with(getProperty(direction), isConnectable(neighbor, world.getBlockEntity(posFrom)) ? CableSide.CONNECTED : CableSide.NONE);
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState();
        BlockPos blockPos = ctx.getBlockPos();
        for (Direction direction : Direction.values()) {
            BlockState neighborState = ctx.getWorld().getBlockState(blockPos.offset(direction));
            Block neighbor = neighborState.getBlock();

            if (neighbor instanceof Cable && neighborState.get(getProperty(direction.getOpposite())) == CableSide.WRENCHED)
                state = state.with(getProperty(direction), CableSide.NONE);
            else
                state = state.with(getProperty(direction), isConnectable(neighbor,ctx.getWorld().getBlockEntity(blockPos.offset(direction))) ? CableSide.CONNECTED : CableSide.NONE);

        }
        return state;
    }

    private boolean isConnectable(Block block, BlockEntity entity) {
        if(block instanceof CableConnectable)
            return true;
        return entity != null && Energy.valid(entity);
    }

    public static EnumProperty<CableSide> getProperty(Direction facing) {
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
