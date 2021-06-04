package net.flytre.mechanix.recipe;

import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.util.FluidRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
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

public class EnchantingRecipe implements MechanixRecipe<DoubleInventory> {

    public static final FluidStack EXP = new FluidStack(FluidRegistry.LIQUID_XP.getStill(), 3 * FluidStack.UNITS_PER_BUCKET / 5);


    private static final Random r = new Random();

    private final int craftTime;
    private final Item input;
    private final Identifier id;

    public EnchantingRecipe(Identifier id, Item input, int craftTime) {
        this.input = input;
        this.craftTime = craftTime;
        this.id = id;
    }

    public static List<EnchantingRecipe> getRecipes() {
        List<EnchantingRecipe> recipes = new ArrayList<>();
        for (Item item : Registry.ITEM) {
            if (item.isEnchantable(new ItemStack(item, 1))) {
                Identifier itemId = Registry.ITEM.getId(item);
                Identifier id = new Identifier("mechanix", "enchanting/" + itemId.getNamespace() + "/" + itemId.getPath());
                recipes.add(new EnchantingRecipe(id, item, 120));
            }
        }
        return recipes;
    }

    @Override
    public boolean cancelLoad() {
        return input == Items.AIR;
    }

    @Override
    public boolean canAcceptRecipeOutput(DoubleInventory inv) {
        return inv.getStack(1).isEmpty();
    }

    @Override
    public boolean matches(DoubleInventory inv, World world) {
        return !cancelLoad() && input == inv.getStack(0).getItem() && !inv.getStack(0).hasEnchantments() && EXP.test(inv.getFluidStack(0));
    }

    private ItemStack enchant(ItemStack stack) {
        if (EnchantmentHelper.get(stack).size() > 0)
            return stack;
        return EnchantmentHelper.enchant(r, stack, 30, true);
    }

    @Override
    public ItemStack craft(DoubleInventory inv) {
        ItemStack output = enchant(inv.getStack(0).copy());
        inv.setStack(1, output);
        inv.getStack(0).decrement(1);
        inv.getFluidStack(0).decrement(EXP.getAmount());
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return enchant(input.getDefaultStack());
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

    @Override
    public int getCraftTime() {
        return craftTime;
    }

    @Override
    public ItemStack getRecipeKindIcon() {
        return enchant(input.getDefaultStack());
    }

    public Item getInput() {
        return input;
    }
}
