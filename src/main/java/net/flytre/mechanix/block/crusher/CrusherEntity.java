package net.flytre.mechanix.block.crusher;

import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.recipe.ItemProcessingRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrusherEntity extends MachineEntity<Inventory, ItemProcessingRecipe<Inventory>> {

    public CrusherEntity() {
        super(MachineRegistry.CRUSHER.getEntityType(), RecipeRegistry.CRUSHING_RECIPE, DefaultedList.ofSize(2, ItemStack.EMPTY),
                (World world, Inventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.CRUSHING_RECIPE, inventory, world).orElse(null)
                , 40);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.crusher");
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
        return new CrusherHandler(syncId, inv, this, getDelegate());
    }

}
