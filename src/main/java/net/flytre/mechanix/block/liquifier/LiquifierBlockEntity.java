package net.flytre.mechanix.block.liquifier;

import net.flytre.mechanix.base.DoubleInventory;
import net.flytre.mechanix.base.energy.EnergyEntity;
import net.flytre.mechanix.base.fluid.FluidStack;
import net.flytre.mechanix.util.FluidRegistry;
import net.flytre.mechanix.util.ItemRegistery;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class LiquifierBlockEntity extends EnergyEntity implements DoubleInventory {

    private final DefaultedList<ItemStack> itemInventory;
    private final DefaultedList<FluidStack> fluidInventory;
    private static final HashMap<Item, Fluid> recipes;

    static {
        recipes = new HashMap<>();
        recipes.put(ItemRegistery.PERLIUM_INGOT,FluidRegistry.STILL_PERLIUM);
    }

    public LiquifierBlockEntity() {
        //TODO: OBVIOUSLY FIX
        super(null);
        fluidInventory = DefaultedList.ofSize(1,FluidStack.EMPTY);
        itemInventory = DefaultedList.ofSize(1,ItemStack.EMPTY);
        setEnergyMode(false, false, false, false, false, false);
        setIOMode(false,false,false,false,false,false);
        setMaxEnergy(300000);
        setMaxTransferRate(200);
        panelMode = 1;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return itemInventory;
    }

    @Override
    public HashMap<Direction, Boolean> getItemIO() {
        return ioMode;
    }

    @Override
    public HashMap<Direction, Boolean> getFluidIO() {
        return ioMode;
    }

    @Override
    public DefaultedList<FluidStack> getFluids() {
        return fluidInventory;
    }

    @Override
    public int capacity() {
        return 8000;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.liquifier");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return null;
    }

    @Override
    public void repeatTick() {

    }

    @Override
    public void onceTick() {

    }
}
