package net.flytre.mechanix.block.disenchanter;

import net.flytre.mechanix.api.machine.MachineEntity;
import net.flytre.mechanix.recipe.DisenchanterRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
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

import java.util.Map;
import java.util.Random;

public class DisenchanterEntity extends MachineEntity<Inventory, DisenchanterRecipe> implements Inventory {

    //input, output size = 4
    public DisenchanterEntity() {
        super(MachineRegistry.DISENCHANTER.getEntityType(), DefaultedList.ofSize(4, ItemStack.EMPTY),
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
        return new DisenchanterScreenHandler(syncId, inv, this, getProperties());
    }

    @Override
    protected void craft(DisenchanterRecipe recipe) {
        ItemStack stack = getStack(0);
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        Enchantment randomEnchant = null;
        int random = new Random().nextInt(enchantments.size());
        for (Enchantment obj : enchantments.keySet()) {
            if (random-- == 0)
                randomEnchant = obj;
        }
        int level = enchantments.get(randomEnchant);
        enchantments.remove(randomEnchant);
        EnchantmentHelper.set(enchantments, stack);

        ItemStack bookOutput = new ItemStack(Items.ENCHANTED_BOOK, 1);
        bookOutput.addEnchantment(randomEnchant, level);


        if (!stack.hasEnchantments()) {
            this.setStack(2, stack);
            this.setStack(0, ItemStack.EMPTY);
        }
        this.getStack(1).decrement(1);
        this.setStack(3, bookOutput);
    }

    @Override
    protected boolean canAcceptRecipeOutput(DisenchanterRecipe recipe) {
        if (recipe == null)
            return false;
        if (!getStack(2).isEmpty() || !getStack(3).isEmpty())
            return false;
        return getStack(0).hasEnchantments() && getStack(1).getItem() == Items.BOOK;
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
