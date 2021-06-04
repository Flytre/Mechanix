package net.flytre.mechanix.block.tank;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.fluid.FluidBlocks;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.fluid.Fraction;
import net.flytre.mechanix.mixin.BucketItemAccessor;
import net.flytre.mechanix.mixin.FluidBlockAccessor;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FluidTankEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable, FluidInventory, ExtendedScreenHandlerFactory {


    private final DefaultedList<FluidStack> inventory;
    public Map<Direction, IOType> fluidMode; //true = output, false = input
    private Fraction capacity;
    private boolean corrected = false;

    public FluidTankEntity() {
        super(MachineRegistry.FLUID_TANK_ENTITY);

        fluidMode = new HashMap<>();
        setFluidMode(IOType.INPUT, IOType.OUTPUT, IOType.BOTH, IOType.BOTH, IOType.BOTH, IOType.BOTH);
        inventory = DefaultedList.ofSize(1, FluidStack.EMPTY);
        capacity = new Fraction(16, 1);
    }

    public static int getLightLevel(FluidStack stack) {
        if (stack.isEmpty())
            return 0;
        for (FluidBlock block : FluidBlocks.fluidBlocks) {
            FlowableFluid attempt = ((FluidBlockAccessor) block).getFluid();
            if (attempt == stack.getFluid())
                return block.getDefaultState().getLuminance();
        }
        return 0;
    }

    public FluidStack getStack() {
        return inventory.get(0);
    }

    private Fluid getFluid() {
        return getStack().getFluid();
    }

    private Fraction getAmount() {
        return getStack().getAmount();
    }

    public void setFluidMode(IOType up, IOType down, IOType north, IOType east, IOType south, IOType west) {
        fluidMode.put(Direction.UP, up);
        fluidMode.put(Direction.DOWN, down);
        fluidMode.put(Direction.NORTH, north);
        fluidMode.put(Direction.EAST, east);
        fluidMode.put(Direction.SOUTH, south);
        fluidMode.put(Direction.WEST, west);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.capacity = Fraction.fromTag(tag.getCompound("capacity"));
        FluidInventory.fromTag(tag, inventory);
        fluidMode = IOType.intToMap(tag.getInt("IOMode"));
        corrected = tag.getBoolean("init");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        FluidInventory.toTag(tag, inventory);
        tag.put("capacity", this.capacity.toTag(new CompoundTag()));
        tag.putInt("IOMode", IOType.mapToInt(fluidMode));
        tag.putBoolean("init", corrected);
        return super.toTag(tag);
    }


    public void correct() {
        assert world != null;
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof FluidTank)
            this.capacity = ((FluidTank) block).getCapacity();
        corrected = true;
    }

    @Override
    public void tick() {
        if (world == null || world.isClient)
            return;

        if (getAmount().isZero() || getFluid() == null) {
            setStack(0, FluidStack.EMPTY);
        }

        //Tiers/
        if (!corrected) {
            correct();
        }

        BlockState state = world.getBlockState(pos);
        if (state.get(FluidTank.LIGHT_LEVEL) != getLightLevel(getStack()))
            world.setBlockState(pos, state.with(FluidTank.LIGHT_LEVEL, getLightLevel(getStack())));
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.capacity = Fraction.fromTag(tag.getCompound("capacity"));
        FluidInventory.fromTag(tag, inventory);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        FluidInventory.toTag(tag, inventory);
        tag.put("capacity", this.capacity.toTag(new CompoundTag()));
        return tag;
    }


    public boolean tryAddFluid(PlayerEntity player, Hand hand, ItemStack stack) {

        if (!(stack.getItem() instanceof BucketItem))
            return false;
        BucketItem item = (BucketItem) stack.getItem();
        Fluid fluid = ((BucketItemAccessor) item).getFluid();
        FluidStack attempt = new FluidStack(fluid, Fraction.ONE);

        if (!canAdd(attempt))
            return false;

        FluidStack newStack = addExternal(attempt);

        if (newStack.isEmpty()) {
            markDirty();
            player.setStackInHand(hand, !player.abilities.creativeMode ? new ItemStack(Items.BUCKET) : stack);
            return true;
        }
        return false;

    }

    public boolean tryRemoveFluid(PlayerEntity player, Hand hand, ItemStack stack) {

        if (!(stack.getItem() == Items.BUCKET))
            return false;


        if (getAmount().isGreaterOrEqual(Fraction.ONE) & getFluid() != Fluids.EMPTY) {
            player.setStackInHand(hand, ItemUsage.method_30012(stack, player, new ItemStack(getFluid().getBucketItem())));
            getStack().decrement(Fraction.ONE);
            markDirty();
            return true;
        }

        return false;
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
    public Fraction capacity() {
        return capacity;
    }


    @Override
    public void markDirty() {
        if (world != null && !world.isClient)
            sync();
        super.markDirty();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
        capacity.toPacket(packetByteBuf);
        packetByteBuf.writeInt(IOType.mapToInt(fluidMode));
    }

    public void setCapacity(Fraction capacity) {
        this.capacity = capacity;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.tank");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FluidTankScreenHandler(syncId, inv, this);
    }

}
