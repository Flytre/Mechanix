package net.flytre.mechanix.block.fluid_pipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.common.compat.wrench.WrenchItem;
import net.flytre.flytre_lib.common.connectable.FluidPipeConnectable;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.util.ItemRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
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

public class FluidPipe extends BlockWithEntity implements FluidPipeConnectable {
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
    private static final VoxelShape S_UP;
    private static final VoxelShape S_DOWN;
    private static final VoxelShape S_EAST;
    private static final VoxelShape S_WEST;
    private static final VoxelShape S_NORTH;
    private static final VoxelShape S_SOUTH;

    static {
        UP = EnumProperty.of("pipe_up", PipeSide.class);
        DOWN = EnumProperty.of("pipe_down", PipeSide.class);
        NORTH = EnumProperty.of("pipe_north", PipeSide.class);
        SOUTH = EnumProperty.of("pipe_south", PipeSide.class);
        EAST = EnumProperty.of("pipe_east", PipeSide.class);
        WEST = EnumProperty.of("pipe_west", PipeSide.class);
        NODE = Block.createCuboidShape(4.5, 4.5, 4.5, 11.5, 11.5, 11.5);
        C_DOWN = Block.createCuboidShape(4.5, 0, 4.5, 11.5, 5, 11.5);
        C_UP = Block.createCuboidShape(4.5, 11, 4.5, 11.5, 16, 11.5);
        C_EAST = Block.createCuboidShape(11, 4.5, 4.5, 16, 11.5, 11.5);
        C_WEST = Block.createCuboidShape(0, 4.5, 4.5, 5, 11.5, 11.5);
        C_NORTH = Block.createCuboidShape(4.5, 4.5, 0, 11.5, 11.5, 5);
        C_SOUTH = Block.createCuboidShape(4.5, 4.5, 11, 11.5, 11.5, 16);
        S_UP = Block.createCuboidShape(3.5, 14, 3.5, 12.5, 16, 12.5);
        S_DOWN = Block.createCuboidShape(3.5, 0, 3.5, 12.5, 2, 12.5);
        S_EAST = Block.createCuboidShape(14, 3.5, 3.5, 16, 12.5, 12.5);
        S_WEST = Block.createCuboidShape(0, 3.5, 3.5, 2, 12.5, 12.5);
        S_NORTH = Block.createCuboidShape(3.5, 3.5, 0, 12.5, 12.5, 2);
        S_SOUTH = Block.createCuboidShape(3.5, 3.5, 14, 12.5, 12.5, 16);
    }

    private final int maxTransferRate;

    public FluidPipe(Settings settings, float bucketsPerTick) {
        super(settings);
        this.maxTransferRate = (int) (FluidStack.UNITS_PER_BUCKET * bucketsPerTick);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(UP, PipeSide.NONE)
                .with(DOWN, PipeSide.NONE)
                .with(NORTH, PipeSide.NONE)
                .with(SOUTH, PipeSide.NONE)
                .with(EAST, PipeSide.NONE)
                .with(WEST, PipeSide.NONE));
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

    public int getMaxTransferRate() {
        return maxTransferRate;
    }

    public VoxelShape getVisualShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Environment(EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.isOf(this) || super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        Item item = player.getStackInHand(hand).getItem();
        if (world.isClient) {
            return item == ItemRegistry.SERVO || item instanceof WrenchItem ? ActionResult.SUCCESS : ActionResult.PASS;
        } else {

            if (!(item == ItemRegistry.SERVO) && !(item instanceof WrenchItem)) {
                for (Direction dir : Direction.values()) {
                    if (state.get(getProperty(dir)) == PipeSide.SERVO) {
                        this.openScreen(world, pos, player);
                        return ActionResult.CONSUME;
                    }
                }
                return ActionResult.PASS;
            }

            Direction side = hit.getSide();
            PipeSide current = state.get(getProperty(side));
            //SERVO
            if (item == ItemRegistry.SERVO) {
                if (current == PipeSide.CONNECTED || current == PipeSide.NONE) {
                    BlockState newState = state.with(getProperty(side), PipeSide.SERVO);
                    world.setBlockState(pos, newState);
                    setWrenched(world, pos, side, false);
                    if (!player.isCreative()) {
                        player.getStackInHand(hand).decrement(1);
                    }
                }
            } else {
                //WRENCH
                if (current == PipeSide.SERVO) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemRegistry.SERVO));
                    world.setBlockState(pos, state.with(getProperty(side), PipeSide.NONE));
                    setWrenched(world, pos, side, false);
                } else {
                    BlockState state1 = world.getBlockState(pos.offset(side));
                    if (state1.getBlock() instanceof FluidPipe && state.get(getProperty(side)) == PipeSide.NONE && isWrenched(world, pos.offset(side), side.getOpposite())) {
                        world.setBlockState(pos.offset(side), state1.with(getProperty(side.getOpposite()), PipeSide.NONE));
                        setWrenched(world, pos.offset(side), side.getOpposite(), false);
                    } else if (!isWrenched(world, pos, side)) {
                        setWrenched(world, pos, side, true);
                        if (state1.getBlock() instanceof FluidPipe && state1.get(getProperty(side.getOpposite())) != PipeSide.SERVO) {
                            world.setBlockState(pos.offset(side), state1.with(getProperty(side.getOpposite()), PipeSide.NONE));
                            setWrenched(world, pos.offset(side), side.getOpposite(), false);
                        }
                    } else {
                        world.setBlockState(pos, state.with(getProperty(side), PipeSide.NONE));
                        setWrenched(world, pos, side, false);
                        Block block = state1.getBlock();
                        BlockEntity entity = world.getBlockEntity(pos.offset(side));
                        if (isConnectable(block, entity))
                            world.setBlockState(pos, state.with(getProperty(side), PipeSide.CONNECTED));

                    }
                }
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = NODE;
        if (state.get(UP) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_UP, BooleanBiFunction.OR);
        if (state.get(DOWN) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_DOWN, BooleanBiFunction.OR);
        if (state.get(NORTH) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_NORTH, BooleanBiFunction.OR);
        if (state.get(EAST) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_EAST, BooleanBiFunction.OR);
        if (state.get(SOUTH) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_SOUTH, BooleanBiFunction.OR);
        if (state.get(WEST) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_WEST, BooleanBiFunction.OR);

        if (state.get(UP) == PipeSide.SERVO)
            shape = VoxelShapes.combineAndSimplify(shape, S_UP, BooleanBiFunction.OR);
        if (state.get(DOWN) == PipeSide.SERVO)
            shape = VoxelShapes.combineAndSimplify(shape, S_DOWN, BooleanBiFunction.OR);
        if (state.get(NORTH) == PipeSide.SERVO)
            shape = VoxelShapes.combineAndSimplify(shape, S_NORTH, BooleanBiFunction.OR);
        if (state.get(EAST) == PipeSide.SERVO)
            shape = VoxelShapes.combineAndSimplify(shape, S_EAST, BooleanBiFunction.OR);
        if (state.get(SOUTH) == PipeSide.SERVO)
            shape = VoxelShapes.combineAndSimplify(shape, S_SOUTH, BooleanBiFunction.OR);
        if (state.get(WEST) == PipeSide.SERVO)
            shape = VoxelShapes.combineAndSimplify(shape, S_WEST, BooleanBiFunction.OR);

        return shape;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {

        if (world.isClient())
            return state;

        Block neighbor = world.getBlockState(posFrom).getBlock();
        if (state.get(getProperty(direction)) == PipeSide.SERVO || isWrenched(world, pos, direction))
            return state;

        if (neighbor instanceof FluidPipe && isWrenched(world, posFrom, direction.getOpposite())) {
            return state;
        }

        return state.with(getProperty(direction), isConnectable(neighbor, world.getBlockEntity(posFrom)) ? PipeSide.CONNECTED : PipeSide.NONE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState();
        BlockPos blockPos = ctx.getBlockPos();
        for (Direction direction : Direction.values()) {
            BlockState neighborState = ctx.getWorld().getBlockState(blockPos.offset(direction));
            Block neighbor = neighborState.getBlock();

            if (neighbor instanceof FluidPipe && isWrenched(ctx.getWorld(), blockPos.offset(direction), direction.getOpposite()))
                state = state.with(getProperty(direction), PipeSide.NONE);
            else
                state = state.with(getProperty(direction), isConnectable(neighbor, ctx.getWorld().getBlockEntity(blockPos.offset(direction))) ? PipeSide.CONNECTED : PipeSide.NONE);

        }
        return state;
    }

    private boolean isConnectable(Block block, BlockEntity entity) {
        return block instanceof FluidPipeConnectable || (entity instanceof FluidInventory);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()))
            for (Direction dir : Direction.values())
                if (state.get(getProperty(dir)) == PipeSide.SERVO)
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemRegistry.SERVO));

        super.onStateReplaced(state, world, pos, newState, moved);
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
        return new FluidPipeEntity();
    }


    private boolean isWrenched(WorldAccess world, BlockPos pos, Direction d) {
        BlockEntity b = world.getBlockEntity(pos);
        if (!(b instanceof FluidPipeEntity))
            return false;
        return ((FluidPipeEntity) b).wrenched.get(d);
    }

    private void setWrenched(WorldAccess world, BlockPos pos, Direction d, boolean value) {
        BlockEntity b = world.getBlockEntity(pos);
        if (!(b instanceof FluidPipeEntity))
            return;
        ((FluidPipeEntity) b).wrenched.put(d, value);
    }


    private void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof FluidPipeEntity) {
            boolean bl = false;
            BlockState state = world.getBlockState(pos);
            for (Direction dir : Direction.values()) {
                if (state.get(getProperty(dir)) == PipeSide.SERVO)
                    bl = true;
            }

            if (bl)
                player.openHandledScreen((NamedScreenHandlerFactory) blockEntity);
        }


    }


}
