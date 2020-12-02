package net.flytre.mechanix.block.foundry;

import net.flytre.mechanix.api.connectable.CableConnectable;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class FoundryBlock extends MachineBlock implements CableConnectable {
    public FoundryBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new FoundryBlockEntity();
    }

}
