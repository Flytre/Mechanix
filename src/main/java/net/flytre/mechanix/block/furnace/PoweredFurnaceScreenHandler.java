package net.flytre.mechanix.block.furnace;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.base.energy.EnergyScreenHandler;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PoweredFurnaceScreenHandler extends EnergyScreenHandler {
    private final Inventory inventory;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final World world;

    public PoweredFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId,playerInventory,new PoweredFurnaceBlockEntity(), new ArrayPropertyDelegate(24));
        this.pos = buf.readBlockPos();
    }

    public PoweredFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, PoweredFurnaceBlockEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.POWERED_FURNACE.getHandlerType(), syncId,playerInventory,entity,propertyDelegate);

        this.pos = BlockPos.ORIGIN;
        this.addSlot(new Slot(entity, 0, 61, 36));
        this.addSlot(new FurnaceOutputSlot(playerInventory.player, entity, 1, 123, 36));


        int o;
        int n;
        for(o = 0; o < 3; ++o) {
            for(n = 0; n < 9; ++n) {
                this.addSlot(new Slot(playerInventory, n + o * 9 + 9, 8 + n * 18, 84 + o * 18));
            }
        }

        for(o = 0; o < 9; ++o) {
            this.addSlot(new Slot(playerInventory, o, 8 + o * 18, 142));
        }

        this.inventory = entity;
        this.recipeType = RecipeType.SMELTING;
        this.world = playerInventory.player.world;

    }

    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }


    protected boolean isSmeltable(ItemStack itemStack) {
        return this.world.getRecipeManager().getFirstMatch(this.recipeType, new SimpleInventory(itemStack), this.world).isPresent();
    }


    @Environment(EnvType.CLIENT)
    public int getCookProgress() {
        //cooktime (i), cooktime total (j)
        int i = this.propertyDelegate.get(8);
        int j = this.propertyDelegate.get(9);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }


    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 1) {
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onStackChanged(itemStack2, itemStack);
            } else if (index != 0) {
                if (this.isSmeltable(itemStack2)) {
                    if (!this.insertItem(itemStack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 29) {
                    if (!this.insertItem(itemStack2, 30, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 38 && !this.insertItem(itemStack2, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 2, 38, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }


}
