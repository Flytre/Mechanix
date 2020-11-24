package net.flytre.mechanix.block.cell;

import net.flytre.mechanix.base.Connectable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnergyCell extends Block implements BlockEntityProvider, Connectable {

    public static final Property<Boolean> UP;
    public static final Property<Boolean> DOWN;
    public static final Property<Boolean> NORTH;
    public static final Property<Boolean> SOUTH;
    public static final Property<Boolean> EAST;
    public static final Property<Boolean> WEST;

    static {
        UP = Properties.UP;
        DOWN = Properties.DOWN;
        NORTH = Properties.NORTH;
        SOUTH = Properties.SOUTH;
        EAST = Properties.EAST;
        WEST = Properties.WEST;
    }


    public EnergyCell(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(UP, true)
                .with(DOWN, true)
                .with(NORTH, true)
                .with(SOUTH, true)
                .with(EAST, true)
                .with(WEST, true));
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new EnergyCellEntity();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            this.openScreen(world, pos, player);
            return ActionResult.CONSUME;
        }
    }


    private void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EnergyCellEntity) {
            player.openHandledScreen((NamedScreenHandlerFactory) blockEntity);
        }
    }


}
