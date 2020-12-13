package net.flytre.mechanix.block.sawmill;

import net.flytre.mechanix.api.connectable.CableConnectable;
import net.flytre.mechanix.api.connectable.ItemPipeConnectable;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class SawmillBlock extends MachineBlock implements CableConnectable, ItemPipeConnectable {
    public SawmillBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new SawmillEntity();
    }
}
