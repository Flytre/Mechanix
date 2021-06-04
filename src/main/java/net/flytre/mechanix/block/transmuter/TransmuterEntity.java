package net.flytre.mechanix.block.transmuter;

import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.block.crusher.CrusherHandler;
import net.flytre.mechanix.recipe.ItemProcessingRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class TransmuterEntity extends MachineEntity<Inventory, ItemProcessingRecipe<Inventory>> {

    public TransmuterEntity() {
        super(MachineRegistry.TRANSMUTER.getEntityType(), RecipeRegistry.TRANSMUTING_RECIPE, DefaultedList.ofSize(2, ItemStack.EMPTY),
                (World world, Inventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.TRANSMUTING_RECIPE, inventory, world).orElse(null)
                , 40);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.transmuter");
    }


    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0 && super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 1 && super.canExtract(slot, stack, dir);
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new TransmuterHandler(syncId, inv, this, getDelegate());
    }


    @Override
    public Set<Item> validUpgrades() {
        return super.validUpgrades();
    }
}
