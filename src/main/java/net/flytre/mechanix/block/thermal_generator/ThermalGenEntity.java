package net.flytre.mechanix.block.thermal_generator;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.machine.NotUgradable;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ThermalGenEntity extends EnergyEntity implements NotUgradable {

    private static final Random RANDOM;

    static {
        RANDOM = new Random();
    }


    public ThermalGenEntity() {
        super(MachineRegistry.THERMAL_GENERATOR.getEntityType());
        setMaxEnergy(300000);
        setMaxTransferRate(100);
        panelMode = 0;
        setEnergyMode(true, true, true, true, true, true);
        setIOMode(false, false, false, false, false, false);
    }


    @Override
    public void repeatTick() {

    }

    @Override
    public void onceTick() {
        if(world == null || world.isClient)
            return;

        int counter = 0;
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                BlockPos pos = new BlockPos(getPos().getX() + i, getPos().getY(), getPos().getZ() + j);
                BlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                if(block == Blocks.LAVA && state.get(FluidBlock.LEVEL) == 0) {
                    counter++;
                    if (RANDOM.nextInt(25000) == 1)
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }
        }

        if(counter >= 4) {
            this.addEnergy(30);
        }
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.thermal_generator");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new ThermalGenScreenHandler(syncId,inv,this,getProperties());
    }
}
