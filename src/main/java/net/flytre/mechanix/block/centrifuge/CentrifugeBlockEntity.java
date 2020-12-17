package net.flytre.mechanix.block.centrifuge;

import net.flytre.mechanix.api.energy.MachineEntity;
import net.flytre.mechanix.api.recipe.RecipeUtils;
import net.flytre.mechanix.recipe.CentrifugeRecipe;
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

public class CentrifugeBlockEntity extends MachineEntity<Inventory,CentrifugeRecipe> {

    public CentrifugeBlockEntity() {
        super(MachineRegistry.CENTRIFUGE.getEntityType(),DefaultedList.ofSize(5, ItemStack.EMPTY),
                (World world, Inventory inventory) -> world.getRecipeManager().getFirstMatch(RecipeRegistry.CENTRIFUGE_RECIPE, inventory, world).orElse(null)
                ,40);
    }

    @Override
    protected boolean canAcceptRecipeOutput(@Nullable CentrifugeRecipe recipe) {
        return recipe != null && RecipeUtils.matches(this, 1, 5, recipe.getOutputProviders());
    }

    @Override
    protected void craft(CentrifugeRecipe recipe) {
        RecipeUtils.craftOutput(this,1,5,recipe.getOutputProviders());
        getStack(0).decrement(recipe.getInput().getQuantity());
    }
    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot == 0;
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
        return new TranslatableText("block.mechanix.centrifuge");
    }


    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CentrifugeScreenHandler(syncId,inv,this,getProperties());
    }
}
