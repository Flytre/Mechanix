package net.flytre.mechanix.block.tank;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.flytre.mechanix.base.Formatter;
import net.flytre.mechanix.base.fluid.FluidInventory;
import net.flytre.mechanix.base.fluid.FluidStack;
import net.flytre.mechanix.mixin.BucketItemMixin;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class FluidTankBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable, FluidInventory, ExtendedScreenHandlerFactory {

    public HashMap<Direction, Boolean> fluidMode; //true = output, false = input
    private final PropertyDelegate properties;
    private final DefaultedList<FluidStack> inventory;
    private int capacity;
    private boolean corrected = false;

    public FluidTankBlockEntity() {
        super(MachineRegistry.FLUID_TANK_ENTITY);

        this.properties = new ArrayPropertyDelegate(7);
        fluidMode = new HashMap<>();
        setFluidMode(false, true, true, true, true, true);
        inventory = DefaultedList.ofSize(1,FluidStack.EMPTY);
        capacity = 16000;
    }

    public PropertyDelegate getProperties() {
        return properties;
    }


    private Fluid getFluid() {
        return inventory.get(0).getFluid();
    }

    private int getAmount() {
        return inventory.get(0).getAmount();
    }

    public void updateDelegate() {
        if (world != null && world.isClient)
            return;
        properties.set(0, getFluid() == Fluids.EMPTY ? -1 : Registry.FLUID.getRawId(getFluid()));
        int[] splitFluid = Formatter.splitInt(getAmount());
        int[] maxFluid = Formatter.splitInt(capacity);
        properties.set(1, splitFluid[0]);
        properties.set(2, splitFluid[1]);
        properties.set(3, maxFluid[0]);
        properties.set(4, maxFluid[1]);
        properties.set(5, Formatter.hashToInt(fluidMode));
        properties.set(6,1);
    }

    public void setFluidMode(boolean up, boolean down, boolean north, boolean east, boolean south, boolean west) {
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
        this.capacity = tag.getInt("capacity");
        FluidInventory.fromTag(tag,inventory);

        fluidMode = Formatter.intToHash(tag.getInt("FluidMode"));
        corrected = tag.getBoolean("init");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        FluidInventory.toTag(tag,inventory);
        tag.putInt("capacity", this.capacity);

        tag.putInt("FluidMode", Formatter.hashToInt(fluidMode));
        tag.putBoolean("init",corrected);
        return super.toTag(tag);
    }

    @Override
    public void tick() {
        updateDelegate();

        if (getAmount() == 0 || getFluid() == null) {
            setStack(0,FluidStack.EMPTY);
        }

        //Tiers/
        if(!corrected && world !=null && !world.isClient) {
            Block block = world.getBlockState(pos).getBlock();
            if(block == MachineRegistry.FLUID_TANK) {
                capacity = 16000;
            }
            if(block == MachineRegistry.GILDED_FLUID_TANK) {
                capacity = 64000;
            }
            if(block == MachineRegistry.VYSTERIUM_FLUID_TANK) {
                capacity = 256000;
            }
            if(block == MachineRegistry.NEPTUNIUM_FLUID_TANK) {
                capacity = 1024000;
            }
            corrected = true;
        }

    }

    public FluidStack getStack() {
        return inventory.get(0);
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.fromTag(null, compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }


    public boolean tryAddFluid(PlayerEntity player, Hand hand, ItemStack stack) {

        if (!(stack.getItem() instanceof BucketItem))
            return false;
        BucketItem item = (BucketItem) stack.getItem();
        Fluid fluid = ((BucketItemMixin) item).getFluid();
        FluidStack attempt = new FluidStack(fluid,1000);

        if(!canAdd(attempt))
            return false;

        FluidStack newStack = add(attempt);

        if(newStack.isEmpty()) {
            markDirty();
            player.setStackInHand(hand, !player.abilities.creativeMode ? new ItemStack(Items.BUCKET) : stack);
            return true;
        }
        return false;

    }

    public boolean tryRemoveFluid(PlayerEntity player, Hand hand, ItemStack stack) {

        if (!(stack.getItem() == Items.BUCKET))
            return false;


        if (getAmount() >= 1000 & getFluid() != Fluids.EMPTY) {
            player.setStackInHand(hand, ItemUsage.method_30012(stack, player, new ItemStack(getFluid().getBucketItem())));
            getStack().decrement(1000);
            markDirty();
            return true;
        }

        return false;
    }

    @Override
    public HashMap<Direction, Boolean> getFluidIO() {
        return fluidMode;
    }

    @Override
    public DefaultedList<FluidStack> getFluids() {
        return inventory;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public void markDirty() {
        if(world != null && !world.isClient)
            sync();
        super.markDirty();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.tank");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FluidTankScreenHandler(syncId,inv,this,this.getProperties());
    }
}
