package net.flytre.mechanix.recipe;

import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.util.FluidRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
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
import java.util.Random;

public class EnchanterRecipe implements MechanixRecipe<DoubleInventory> {

    private final Item input;
    private final Identifier id;
    private static final Random r = new Random();


    public EnchanterRecipe(Identifier id, Item input) {
        this.input = input;
        this.id = id;
    }

    @Override
    public boolean cancelLoad() {
        return input == Items.AIR;
    }

    @Override
    public boolean matches(DoubleInventory inv, World world) {
        return !cancelLoad() && input == inv.getStack(0).getItem() && FluidRegistry.LIQUID_XP.getStill() == inv.getFluidStack(0).getFluid() && 150 <= inv.getFluidStack(0).getAmount();
    }

    private ItemStack enchant(ItemStack stack) {
        if(EnchantmentHelper.get(stack).size() > 0)
            return stack;
        return EnchantmentHelper.enchant(r, stack, 30, true);
    }



    @Override
    public ItemStack craft(DoubleInventory inv) {
        return enchant(inv.getStack(0).copy());
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        ItemStack out = new ItemStack(input,1);
        out.addEnchantment(Enchantments.UNBREAKING,1);
        return out;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.ENCHANTING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.ENCHANTING_RECIPE;
    }

    public Item getInput() {
        return input;
    }

    public static List<EnchanterRecipe> getRecipes() {
        List<EnchanterRecipe> recipes = new ArrayList<>();
        for(Item item : Registry.ITEM) {
            if(item.isEnchantable(new ItemStack(item,1))) {
                recipes.add(new EnchanterRecipe(Registry.ITEM.getId(item),item));
            }
        }
        return recipes;
    }
}
