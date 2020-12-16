package net.flytre.mechanix.block.centrifuge;

import net.flytre.mechanix.api.machine.MachineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class CentrifugeBlock extends MachineBlock {
    public CentrifugeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new CentrifugeBlockEntity();
    }
}
