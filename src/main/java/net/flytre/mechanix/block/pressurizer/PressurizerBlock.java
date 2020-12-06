package net.flytre.mechanix.block.pressurizer;

import net.flytre.mechanix.api.connectable.CableConnectable;
import net.flytre.mechanix.api.connectable.ItemPipeConnectable;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class PressurizerBlock extends MachineBlock implements CableConnectable, ItemPipeConnectable {
    public PressurizerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new PressurizerBlockEntity();
    }
}
