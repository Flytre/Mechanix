package net.flytre.mechanix.block.xp_bank;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.IOMode;
import net.flytre.mechanix.api.util.Formatter;
import net.flytre.mechanix.block.hydrator.HydratorBlock;
import net.flytre.mechanix.util.FluidRegistry;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class XpBankBlockEntity extends BlockEntity implements Tickable, IOMode, FluidInventory, ExtendedScreenHandlerFactory, BlockEntityClientSerializable {
    public HashMap<Direction, Boolean> ioMode; //true = output, false = input
    private final DefaultedList<FluidStack> inventory;
    private final PropertyDelegate properties;


    public XpBankBlockEntity() {
        super(MachineRegistry.XP_BANK.getEntityType());
        ioMode = new HashMap<>();
        setFluidMode(false, true, true, true, true, true);
        inventory = DefaultedList.ofSize(1,FluidStack.EMPTY);
        this.properties = new ArrayPropertyDelegate(4);
    }

    public void setFluidMode(boolean up, boolean down, boolean north, boolean east, boolean south, boolean west) {
        ioMode.put(Direction.UP, up);
        ioMode.put(Direction.DOWN, down);
        ioMode.put(Direction.NORTH, north);
        ioMode.put(Direction.EAST, east);
        ioMode.put(Direction.SOUTH, south);
        ioMode.put(Direction.WEST, west);
    }


    public int getAmount() {
        return inventory.get(0).getAmount();
    }

    public void updateDelegate() {
        if (world != null && world.isClient)
            return;
        int[] splitFluid = Formatter.splitInt(getAmount());
        properties.set(0, splitFluid[0]);
        properties.set(1, splitFluid[1]);
        properties.set(2, Formatter.hashToInt(ioMode));
        properties.set(3,1);
    }

    @Override
    public void tick() {

        if(world == null || world.isClient)
            return;

        world.getBlockState(pos).getLuminance();
        updateDelegate();

        boolean currActivated = world.getBlockState(getPos()).get(XpBankBlock.ACTIVATED);
        boolean shouldBeActivated = false;
        if(getAmount() > 0) {
            shouldBeActivated = true;
        }

        if(shouldBeActivated != currActivated) {
            world.setBlockState(getPos(),world.getBlockState(pos).with(HydratorBlock.ACTIVATED,shouldBeActivated));
        }

    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        FluidInventory.fromTag(tag,inventory);
        ioMode = Formatter.intToHash(tag.getInt("IOMode"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        FluidInventory.toTag(tag,inventory);
        tag.putInt("IOMode", Formatter.hashToInt(ioMode));
        return super.toTag(tag);
    }



    public PropertyDelegate getProperties() {
        return properties;
    }

    @Override
    public HashMap<Direction, Boolean> getFluidIO() {
        return ioMode;
    }

    @Override
    public DefaultedList<FluidStack> getFluids() {
        return inventory;
    }

    @Override
    public boolean isValidInternal(int slot, FluidStack stack) {
        return stack.getFluid() == FluidRegistry.LIQUID_XP.getStill();
    }

    @Override
    public boolean isValidExternal(int slot, FluidStack stack) {
        return stack.getFluid() == FluidRegistry.LIQUID_XP.getStill();
    }

    public FluidStack getStack() {
        return inventory.get(0);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);

    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.xp_bank");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new XpBankScreenHandler(syncId,inv,this,this.getProperties());
    }
    @Override
    public int capacity() {
        return 32000;
    }

    @Override
    public void markDirty() {
        if(world != null && !world.isClient)
            sync();
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.fromTag(this.getCachedState(),compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.toTag(compoundTag);
    }

    @Override
    public HashMap<Direction, Boolean> getIO() {
        return ioMode;
    }
}
