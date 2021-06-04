package net.flytre.mechanix.block.hydrator;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.api.machine.Tiered;
import net.flytre.mechanix.api.upgrade.UpgradeInventory;
import net.flytre.mechanix.util.ItemRegistry;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HydratorEntity extends BlockEntity implements Tickable, FluidInventory, ExtendedScreenHandlerFactory, Tiered, UpgradeInventory {

    private final DefaultedList<FluidStack> inventory;
    private final DefaultedList<ItemStack> upgrades;
    public Map<Direction, IOType> fluidMode; //true = output, false = input
    private int tier;

    public HydratorEntity() {
        super(MachineRegistry.HYDRATOR.getEntityType());
        fluidMode = new HashMap<>();
        setFluidMode(IOType.OUTPUT, IOType.OUTPUT, IOType.OUTPUT, IOType.OUTPUT, IOType.OUTPUT, IOType.OUTPUT);
        inventory = DefaultedList.ofSize(1, FluidStack.EMPTY);
        upgrades = DefaultedList.ofSize(4, ItemStack.EMPTY);
    }

    @Override
    public void tick() {

        if (world == null || world.isClient)
            return;
        boolean active = false;
        if (UpgradeInventory.waterFabricatorEffect(this, FluidStack.UNITS_PER_BUCKET / 50, 0))
            active = true;

        int counter = 0;
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                if (world.getBlockState(new BlockPos(getPos().getX() + i, getPos().getY(), getPos().getZ() + j)).getBlock() == Blocks.WATER)
                    counter++;

        if (counter >= 8)
            active = true;

        if (counter >= 8 && canAdd(new FluidStack(Fluids.WATER, FluidStack.UNITS_PER_BUCKET / 10 * (tier + 1))))
            addInternal(new FluidStack(Fluids.WATER, FluidStack.UNITS_PER_BUCKET / 10 * (tier + 1)));

        if (active != world.getBlockState(getPos()).get(MachineBlock.ACTIVATED)) {
            world.setBlockState(getPos(), world.getBlockState(pos).with(MachineBlock.ACTIVATED, active));
        }

    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        FluidInventory.fromTag(tag, inventory);
        UpgradeInventory.fromTag(tag, upgrades);
        Tiered.fromTag(tag, this);
        fluidMode = IOType.intToMap(tag.getInt("IOMode"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        FluidInventory.toTag(tag, inventory);
        UpgradeInventory.toTag(tag, upgrades);
        tag.putInt("IOMode", IOType.mapToInt(fluidMode));
        Tiered.toTag(this, tag);
        return super.toTag(tag);
    }

    @Override
    public DefaultedList<ItemStack> getUpgrades() {
        return upgrades;
    }

    @Override
    public Set<Item> validUpgrades() {
        return new HashSet<Item>() {{
            add(ItemRegistry.WATER_FABRICATOR);
        }};
    }

    @Override
    public Map<Direction, IOType> getFluidIO() {
        return fluidMode;
    }

    @Override
    public DefaultedList<FluidStack> getFluids() {
        return inventory;
    }

    @Override
    public long capacity() {
        return 8 * FluidStack.UNITS_PER_BUCKET;
    }

    @Override
    public boolean isValidInternal(int slot, FluidStack stack) {
        return stack.getFluid() == Fluids.WATER;
    }

    @Override
    public boolean isValidExternal(int slot, FluidStack stack) {
        return false;
    }


    public FluidStack getStack() {
        return inventory.get(0);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
        packetByteBuf.writeInt(IOType.mapToInt(fluidMode));
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.hydrator");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new HydratorHandler(syncId, inv, this);
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        this.tier = tier;
        markDirty();
    }

}
