package net.flytre.mechanix.block.sawmill;


import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.recipe.ItemProcessingRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SawmillEntity extends MachineEntity<Inventory, ItemProcessingRecipe<Inventory>> {

    public SawmillEntity() {
        super(MachineRegistry.SAWMILL.getEntityType(), RecipeRegistry.SAWING_RECIPE, DefaultedList.ofSize(3, ItemStack.EMPTY),
                (World world, Inventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.SAWING_RECIPE, inventory, world).orElse(null)
                , 40);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0 && super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot > 0 && super.canExtract(slot, stack, dir);
    }


    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.sawmill");
    }


    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new SawmillHandler(syncId, inv, this, getDelegate());
    }
}
