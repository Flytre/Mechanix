package net.flytre.mechanix.block.item_collector;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.flytre.flytre_lib.common.inventory.EasyInventory;
import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.flytre_lib.common.inventory.filter.FilterInventory;
import net.flytre.flytre_lib.common.inventory.filter.Filtered;
import net.flytre.mechanix.api.upgrade.UpgradeInventory;
import net.flytre.mechanix.util.ItemRegistry;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemCollectorEntity extends BlockEntity implements EasyInventory, Tickable, ExtendedScreenHandlerFactory, Filtered, UpgradeInventory {

    private final DefaultedList<ItemStack> inventory;
    private final DefaultedList<ItemStack> upgrades;
    private Map<Direction, IOType> ioMode;
    private FilterInventory filter;

    public ItemCollectorEntity() {
        super(MachineRegistry.ITEM_COLLECTOR.getEntityType());
        inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
        ioMode = new HashMap<>();
        setIOMode(IOType.OUTPUT, IOType.OUTPUT, IOType.OUTPUT, IOType.OUTPUT, IOType.OUTPUT, IOType.OUTPUT);
        filter = FilterInventory.fromTag(new CompoundTag(), 1);
        upgrades = DefaultedList.ofSize(4, ItemStack.EMPTY);
    }

    @Override
    public DefaultedList<ItemStack> getUpgrades() {
        return upgrades;
    }

    public FilterInventory getFilter() {
        return filter;
    }


    public void setIOMode(IOType up, IOType down, IOType north, IOType east, IOType south, IOType west) {
        ioMode.put(Direction.UP, up);
        ioMode.put(Direction.DOWN, down);
        ioMode.put(Direction.NORTH, north);
        ioMode.put(Direction.EAST, east);
        ioMode.put(Direction.SOUTH, south);
        ioMode.put(Direction.WEST, west);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Map<Direction, IOType> getItemIO() {
        return ioMode;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        Inventories.fromTag(tag, inventory);
        UpgradeInventory.fromTag(tag, upgrades);
        ioMode = IOType.intToMap(tag.getInt("IOMode"));
        CompoundTag filter = tag.getCompound("filter");
        this.filter = FilterInventory.fromTag(filter, 1);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("IOMode", IOType.mapToInt(ioMode));
        Inventories.toTag(tag, inventory);
        UpgradeInventory.toTag(tag, upgrades);
        tag.put("filter", filter.toTag());
        return super.toTag(tag);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return filter.isEmpty() || filter.passFilterTest(stack);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public void tick() {

        if (this.world == null || this.world.isClient)
            return;

        List<ItemEntity> items = this.world.getEntitiesByClass(ItemEntity.class, new Box(this.pos).expand(hasUpgrade(ItemRegistry.ADVANCED_SUCTION) ? 5 : 3), i -> true);
        for (ItemEntity entity : items) {
            ItemStack stack = this.addStackInternal(entity.getStack());
            if (stack.isEmpty())
                entity.remove();
            else
                entity.setStack(stack);
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
        packetByteBuf.writeInt(this.filter.getFilterType());
        packetByteBuf.writeBoolean(this.filter.isMatchMod());
        packetByteBuf.writeBoolean(this.filter.isMatchNbt());
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.item_collector");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new ItemCollectorHandler(syncId, inv, this);
    }


    @Override
    public Set<Item> validUpgrades() {
        return new HashSet<Item>() {{
            add(ItemRegistry.ADVANCED_SUCTION);
        }};
    }
}
