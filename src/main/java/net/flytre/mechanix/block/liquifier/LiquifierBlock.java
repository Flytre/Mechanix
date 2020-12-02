package net.flytre.mechanix.block.liquifier;

import net.flytre.mechanix.api.connectable.CableConnectable;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class LiquifierBlock extends MachineBlock implements CableConnectable {
    public LiquifierBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new LiquifierBlockEntity();
    }
}
