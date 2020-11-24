package net.flytre.mechanix.block.item_pipe;

import net.flytre.mechanix.util.ItemRegistery;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
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
import org.jetbrains.annotations.Nullable;

public class ItemPipe extends BlockWithEntity implements ItemPipeConnectable {
    public static final EnumProperty<PipeSide> UP;
    public static final EnumProperty<PipeSide> DOWN;
    public static final EnumProperty<PipeSide> NORTH;
    public static final EnumProperty<PipeSide> SOUTH;
    public static final EnumProperty<PipeSide> EAST;
    public static final EnumProperty<PipeSide> WEST;

    private static final VoxelShape NODE;
    private static final VoxelShape C_UP;
    private static final VoxelShape C_DOWN;
    private static final VoxelShape C_EAST;
    private static final VoxelShape C_WEST;
    private static final VoxelShape C_NORTH;
    private static final VoxelShape C_SOUTH;


    static {
        UP = EnumProperty.of("pipe_up", PipeSide.class);
        DOWN = EnumProperty.of("pipe_down", PipeSide.class);
        NORTH = EnumProperty.of("pipe_north", PipeSide.class);
        SOUTH = EnumProperty.of("pipe_south", PipeSide.class);
        EAST = EnumProperty.of("pipe_east", PipeSide.class);
        WEST = EnumProperty.of("pipe_west", PipeSide.class);
        NODE = Block.createCuboidShape(4.5,4.5,4.5,11.5,11.5,11.5);
        C_DOWN = Block.createCuboidShape(4.5,0,4.5,11.5,5,11.5);
        C_UP = Block.createCuboidShape(4.5,11,4.5,11.5,16,11.5);
        C_EAST = Block.createCuboidShape(11,4.5,4.5,16,11.5,11.5);
        C_WEST = Block.createCuboidShape(0,4.5,4.5,5,11.5,11.5);
        C_NORTH = Block.createCuboidShape(4.5,4.5,0,11.5,11.5,5);
        C_SOUTH = Block.createCuboidShape(4.5,4.5,11,11.5,11.5,16);
    }

    public ItemPipe(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(UP, PipeSide.NONE)
                .with(DOWN, PipeSide.NONE)
                .with(NORTH, PipeSide.NONE)
                .with(SOUTH, PipeSide.NONE)
                .with(EAST, PipeSide.NONE)
                .with(WEST, PipeSide.NONE));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if(world.isClient) {
            return player.getStackInHand(hand).getItem() == ItemRegistery.SERVO ? ActionResult.SUCCESS : ActionResult.PASS;
        } else {

            if(!(player.getStackInHand(hand).getItem() == ItemRegistery.SERVO))
                return ActionResult.PASS;

            Direction side = hit.getSide();
            PipeSide current = state.get(getProperty(side));
            if(current == PipeSide.CONNECTED || current == PipeSide.NONE) {
                BlockState newState = state.with(getProperty(side),PipeSide.SERVO);
                world.setBlockState(pos,newState);
                BlockEntity entity = world.getBlockEntity(pos);

                if(entity instanceof ItemPipeBlockEntity) {
                    ((ItemPipeBlockEntity) entity).servo.put(side,true);
                }

                if(!player.isCreative()) {
                    player.getStackInHand(hand).decrement(1);
                }
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = NODE;
        if(state.get(UP) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_UP, BooleanBiFunction.OR);
        if(state.get(DOWN) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_DOWN, BooleanBiFunction.OR);
        if(state.get(NORTH) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_NORTH, BooleanBiFunction.OR);
        if(state.get(EAST) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_EAST, BooleanBiFunction.OR);
        if(state.get(SOUTH) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_SOUTH, BooleanBiFunction.OR);
        if(state.get(WEST) != PipeSide.NONE)
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
        if(state.get(getProperty(direction)) == PipeSide.SERVO)
            return state;

        return state.with(getProperty(direction), isConnectable(neighbor,world.getBlockEntity(posFrom)) ? PipeSide.CONNECTED : PipeSide.NONE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState();
        BlockPos blockPos = ctx.getBlockPos();
        for (Direction direction : Direction.values()) {
            Block neighbor = ctx.getWorld().getBlockState(blockPos.offset(direction)).getBlock();
            state = state.with(getProperty(direction), isConnectable(neighbor,ctx.getWorld().getBlockEntity(blockPos.offset(direction))) ? PipeSide.CONNECTED : PipeSide.NONE);

        }
        return state;
    }

    private boolean isConnectable(Block block, BlockEntity entity) {
        return block instanceof ItemPipeConnectable || (entity instanceof Inventory);
    }
    public static EnumProperty<PipeSide> getProperty(Direction facing) {
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


    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new ItemPipeBlockEntity();
    }
}
