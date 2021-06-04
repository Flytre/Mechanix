package net.flytre.mechanix.block.alloyer;

import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.recipe.AlloyingRecipe;
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

public class AlloyerEntity extends MachineEntity<Inventory, AlloyingRecipe> {

    public AlloyerEntity() {
        super(MachineRegistry.ALLOYER.getEntityType(), DefaultedList.ofSize(4, ItemStack.EMPTY),
                (World world, Inventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.ALLOYING_RECIPE, inventory, world).orElse(null)
                , 50);
    }


    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.alloyer");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new AlloyerHandler(syncId, inv, this, getDelegate());
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot <= 2 && super.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 3 && super.canExtract(slot, stack, dir);

    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot <= 2;
    }

}
