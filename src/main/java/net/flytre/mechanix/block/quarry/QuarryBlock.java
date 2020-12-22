package net.flytre.mechanix.block.quarry;

import net.flytre.mechanix.api.machine.MachineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class QuarryBlock extends MachineBlock {
    public QuarryBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new QuarryEntity();
    }
}
