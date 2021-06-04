package net.flytre.mechanix.block.disenchanter;

import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.recipe.DisenchantingRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DisenchanterEntity extends MachineEntity<Inventory, DisenchantingRecipe> implements Inventory {

    //input, output size = 4
    public DisenchanterEntity() {
        super(MachineRegistry.DISENCHANTER.getEntityType(), RecipeRegistry.DISENCHANTING_RECIPE, DefaultedList.ofSize(4, ItemStack.EMPTY),
                (World world, Inventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.DISENCHANTING_RECIPE, inventory, world).orElse(null)
                , 40);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.disenchanter");
    }


    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new DisenchanterHandler(syncId, inv, this, getDelegate());
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 0)
            return stack.hasEnchantments();
        else if (slot == 1)
            return stack.getItem() == Items.BOOK;
        return false;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot <= 1 && super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot >= 2 && super.canExtract(slot, stack, dir);
    }


}
