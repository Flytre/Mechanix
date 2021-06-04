package net.flytre.mechanix.block.fluid_pipe;

import net.flytre.mechanix.api.fluid.FluidFilterInventory;
import net.flytre.mechanix.api.fluid.FluidHandler;
import net.flytre.mechanix.api.fluid.FluidSlot;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.mixin.BucketItemAccessor;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;

public class FluidPipeScreenHandler extends FluidHandler {

    private final FluidFilterInventory inv;
    private BlockPos pos;
    private boolean synced;
    private int filterType;
    private boolean matchMod;


    public FluidPipeScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new FluidPipeEntity());
        pos = buf.readBlockPos();
        synced = true;
        filterType = buf.readInt();
        matchMod = buf.readBoolean();
    }

    public FluidPipeScreenHandler(int syncId, PlayerInventory playerInventory, FluidPipeEntity entity) {

        super(MachineRegistry.FLUID_PIPE_SCREEN_HANDLER, syncId);
        this.inv = entity.getFilter();
        pos = BlockPos.ORIGIN;

        inv.onOpen(playerInventory.player);
        int m;
        int l;
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 3; ++l) {
                this.addSlot(new FluidSlot(entity.getFilter(), l + m * 3, 62 + l * 18, 17 + m * 18, true));
            }
        }

        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }

        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }


    @Override
    public FluidStack onFluidSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity) {
        FluidSlot slot = fluidSlots.get(slotId);
        FluidStack slotStack = slot.getStack();

        if (!slotStack.isEmpty()) {
            slot.setStack(FluidStack.EMPTY);
            return slotStack;
        }
        return FluidStack.EMPTY;
    }


    @Override
    public ItemStack onSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity) {


        if (slotId < 0)
            return super.onSlotClick(slotId, clickData, actionType, playerEntity);

        ItemStack stack = getSlot(slotId).getStack();

        if (stack.getItem() instanceof BucketItem) {
            BucketItem item = (BucketItem) stack.getItem();
            Fluid fluid = ((BucketItemAccessor) item).getFluid();
            HashSet<Fluid> fluids = new HashSet<>();
            fluids.add(fluid);
            if (!inv.containsAnyFluid(fluids))
                inv.addInternal(new FluidStack(fluid, 1));
            else
                return stack;
        }

        return ItemStack.EMPTY;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inv.onClose(player);
    }

    public boolean getSynced() {
        return synced;
    }

    public int getFilterType() {
        return filterType;
    }

    public int getModMatch() {
        return matchMod ? 1 : 0;
    }
}
