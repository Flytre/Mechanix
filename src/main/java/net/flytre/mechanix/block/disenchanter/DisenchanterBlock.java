package net.flytre.mechanix.block.disenchanter;

import net.flytre.mechanix.api.machine.MachineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class DisenchanterBlock extends MachineBlock {
    public DisenchanterBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new DisenchanterEntity();
    }
}
