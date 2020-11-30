package net.flytre.mechanix.block.liquifier;

import net.flytre.mechanix.base.MachineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class LiquifierBlock extends MachineBlock {
    public LiquifierBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return null;
    }
}
