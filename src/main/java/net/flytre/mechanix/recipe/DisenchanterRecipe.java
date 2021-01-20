package net.flytre.mechanix.recipe;

import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class DisenchanterRecipe implements MechanixRecipe<Inventory> {

    private final Item input;
    private final Identifier id;


    public DisenchanterRecipe(Identifier id, Item input) {
        this.input = input;
        this.id = id;
    }

    public static List<DisenchanterRecipe> getRecipes() {
        List<DisenchanterRecipe> recipes = new ArrayList<>();
        for (Item item : Registry.ITEM) {
            if (item.isEnchantable(new ItemStack(item, 1))) {
                recipes.add(new DisenchanterRecipe(Registry.ITEM.getId(item), item));
            }
        }
        return recipes;
    }

    @Override
    public boolean cancelLoad() {
        return input == Items.AIR;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        return !cancelLoad() && input == inv.getStack(0).getItem() && inv.getStack(1).getItem() == Items.BOOK;
    }

    //do not use
    @Override
    public ItemStack craft(Inventory inv) {
        return new ItemStack(input, 1);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    //REI only
    @Override
    public ItemStack getOutput() {
        return new ItemStack(input, 1);
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.DISENCHANTING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.DISENCHANTING_RECIPE;
    }

    public Item getInput() {
        return input;
    }

    public ItemStack getREIInput() {
        ItemStack out = new ItemStack(input, 1);
        out.addEnchantment(Enchantments.UNBREAKING, 1);
        return out;
    }
}
