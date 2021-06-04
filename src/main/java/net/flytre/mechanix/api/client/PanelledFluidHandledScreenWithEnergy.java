package net.flytre.mechanix.api.client;

import net.flytre.mechanix.api.energy.ItemEnergyScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


/**
 * Screen to use for machines containing both FLUIDS & ENERGY
 * Fine to use with or without items, will support but not mandated!
 *
 * @param <T>
 */
public abstract class PanelledFluidHandledScreenWithEnergy<T extends ItemEnergyScreenHandler<?>> extends PanelledFluidHandledScreen<T> {

    private EnergyMeterWidget meter;


    public PanelledFluidHandledScreenWithEnergy(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.meter = null;
        this.titleY--;
        this.playerInventoryTitleY += 2;
    }

    @Override
    protected BlockPos getPos() {
        return handler.getPos();
    }

    @Override
    protected boolean synced() {
        return handler.getSynced();
    }

    @Override
    protected int getFluidButtonState(Direction dir) {
        return handler.itemButtonState(dir);
    }

    @Override
    protected TranslatableText getPanelArg() {
        return new TranslatableText("gui.mechanix.fluid_items");
    }

    @Override
    protected void onSynced() {
        super.onSynced();
        //meter
        this.meter = new EnergyMeterWidget(this.x + 10, this.y + 13, 0, handler, this::renderTooltip);
        this.addButton(meter);
    }
}
