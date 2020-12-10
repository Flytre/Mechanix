package net.flytre.mechanix.block.solar_panel;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.machine.NotUgradable;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public class SolarPanelEntity extends EnergyEntity implements NotUgradable {

    private int genPerTick;
    private boolean corrected = false;


    public SolarPanelEntity() {
        super(MachineRegistry.SOLAR_PANEL_ENTITY);
        setMaxEnergy(300000);
        setMaxTransferRate(100);
        panelMode = 1;
        setEnergyMode(true, true, true, true, true, true);
        setIOMode(false, false, false, false, false, false);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        this.genPerTick = tag.getInt("gpt");
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("gpt",genPerTick);
        return super.toTag(tag);
    }

    @Override
    public void repeatTick() {
        if(world == null || world.isClient)
            return;
        if(world.isDay() && world.isSkyVisible(pos))
            addEnergy(genPerTick);
    }

    @Override
    public void onceTick() {
        if(!corrected && world !=null && !world.isClient) {
            Block block = world.getBlockState(pos).getBlock();
            if(block == MachineRegistry.SOLAR_PANELS.getStandard()) {
                setMaxEnergy(100000);
                genPerTick = 3;
            }
            if(block == MachineRegistry.SOLAR_PANELS.getGilded()) {
                setMaxEnergy(150000);
                genPerTick = 5;
            }
            if(block == MachineRegistry.SOLAR_PANELS.getVysterium()) {
                setMaxEnergy(250000);
                genPerTick = 12;
            }
            if(block == MachineRegistry.SOLAR_PANELS.getNeptunium()) {
                setMaxEnergy(400000);
                genPerTick = 20;
            }
            setMaxTransferRate(genPerTick * 1.5);
            corrected = true;
        }
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.solar_panel");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new SolarPanelScreenHandler(syncId,inv,this,getProperties());
    }
}
