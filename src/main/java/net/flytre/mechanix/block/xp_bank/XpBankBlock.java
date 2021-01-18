package net.flytre.mechanix.block.xp_bank;

import net.flytre.mechanix.api.machine.MachineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class XpBankBlock extends MachineBlock {
    public XpBankBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new XpBankBlockEntity();
    }
}
