package net.flytre.mechanix.block.crafter;

import net.flytre.mechanix.api.machine.MachineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class CrafterBlock extends MachineBlock {
    public CrafterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new CrafterBlockEntity();
    }
}
