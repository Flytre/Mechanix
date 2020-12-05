package net.flytre.mechanix.block.alloyer;

import net.flytre.mechanix.api.connectable.CableConnectable;
import net.flytre.mechanix.api.connectable.ItemPipeConnectable;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class AlloyerBlock extends MachineBlock implements CableConnectable, ItemPipeConnectable {
    public AlloyerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new AlloyerBlockEntity();
    }
}
