package net.flytre.mechanix.block.xp_bank;

import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.api.machine.MachineOverlay;
import net.flytre.mechanix.api.upgrade.UpgradeInventory;
import net.flytre.mechanix.block.tank.FluidTankEntity;
import net.flytre.mechanix.util.FluidRegistry;
import net.flytre.mechanix.util.ItemRegistry;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BankEntity extends FluidTankEntity implements MachineOverlay, UpgradeInventory {

    private final DefaultedList<ItemStack> upgrades;


    public BankEntity() {
        super(MachineRegistry.XP_BANK.getEntityType());
        setFluidMode(IOType.INPUT, IOType.OUTPUT, IOType.BOTH, IOType.BOTH, IOType.BOTH, IOType.BOTH);
        setCapacity(128 * FluidStack.UNITS_PER_BUCKET);
        upgrades = DefaultedList.ofSize(4, ItemStack.EMPTY);
    }

    @Override
    public DefaultedList<ItemStack> getUpgrades() {
        return upgrades;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        UpgradeInventory.fromTag(tag, upgrades);
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        UpgradeInventory.toTag(tag, upgrades);
        return super.toTag(tag);
    }

    @Override
    public void tick() {

        if (world == null || world.isClient)
            return;

        if (getAmount() == 0 || getFluid() == null) {
            setStack(0, FluidStack.EMPTY);
        }

        boolean currActivated = world.getBlockState(getPos()).get(MachineBlock.ACTIVATED);
        if (getAmount() > 0 != currActivated) {
            world.setBlockState(getPos(), world.getBlockState(pos).with(MachineBlock.ACTIVATED, getAmount() > 0));
        }

        if (hasUpgrade(ItemRegistry.ADVANCED_SUCTION)) {
            List<ExperienceOrbEntity> items = this.world.getEntitiesByClass(ExperienceOrbEntity.class, new Box(this.pos).expand(5), i -> true);
            for (ExperienceOrbEntity orb : items) {
                long amountInBuckets = orb.getExperienceAmount() * FluidStack.UNITS_PER_EXPERIENCE;
                FluidStack stack = new FluidStack(FluidRegistry.LIQUID_XP.getStill(), amountInBuckets);
                if (canAdd(stack))
                    addInternal(stack);
                orb.remove();
            }
        }

    }

    @Override
    public boolean isValidInternal(int slot, FluidStack stack) {
        return stack.getFluid() == FluidRegistry.LIQUID_XP.getStill() && super.isValidInternal(slot, stack);
    }


    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.xp_bank");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new BankHandler(syncId, inv, this);
    }

    @Override
    public Set<Item> validUpgrades() {
        return new HashSet<Item>() {{
            add(ItemRegistry.ADVANCED_SUCTION);
        }};
    }
}
