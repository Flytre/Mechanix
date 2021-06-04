package net.flytre.mechanix.recipe;

import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.*;

public class DisenchantingRecipe implements MechanixRecipe<Inventory> {

    private static final Random r = new Random();


    private final int craftTime;
    private final Item input;
    private final Identifier id;

    public DisenchantingRecipe(Identifier id, Item input, int craftTime) {
        this.input = input;
        this.craftTime = craftTime;
        this.id = id;
    }

    public static List<DisenchantingRecipe> getRecipes() {
        List<DisenchantingRecipe> recipes = new ArrayList<>();
        for (Item item : Registry.ITEM) {
            ItemStack stack = new ItemStack(item, 1);
            if (item.isEnchantable(stack)) {
                stack = EnchantmentHelper.enchant(r, stack, 30, true);
                Identifier itemId = Registry.ITEM.getId(stack.getItem());
                Identifier id = new Identifier("mechanix", "disenchanting/" + itemId.getNamespace() + "/" + itemId.getPath());
                recipes.add(new DisenchantingRecipe(id, stack.getItem(), 120));
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
        return !cancelLoad() && input == inv.getStack(0).getItem() && EnchantmentHelper.get(inv.getStack(0)).keySet().size() > 0 && inv.getStack(1).getItem() == Items.BOOK;
    }


    public Enchantment randomEnchantment(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        Enchantment randomEnchant = null;
        if (enchantments.size() == 0)
            return null;
        int random = r.nextInt(enchantments.size());
        for (Enchantment obj : enchantments.keySet()) {
            if (random-- == 0)
                randomEnchant = obj;
        }
        return randomEnchant;
    }

    @Override
    public ItemStack craft(Inventory inv) {
        ItemStack stack = inv.getStack(0);
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        Enchantment randomEnchant = randomEnchantment(stack);
        int level = enchantments.get(randomEnchant);
        enchantments.remove(randomEnchant);

        if (stack.getItem() == Items.ENCHANTED_BOOK)
            stack.removeSubTag("StoredEnchantments");

        if (stack.getItem() == Items.ENCHANTED_BOOK && enchantments.size() == 0) {
            stack = new ItemStack(Items.BOOK, 1);
            inv.setStack(0, stack);
        }

        EnchantmentHelper.set(enchantments, stack);


        ItemStack bookOutput = new ItemStack(Items.ENCHANTED_BOOK, 1);
        addStoredEnchantment(bookOutput, randomEnchant, level);

        if (!stack.hasEnchantments()) {
            inv.setStack(2, stack);
            inv.setStack(0, ItemStack.EMPTY);
        }
        inv.getStack(1).decrement(1);
        inv.setStack(3, bookOutput);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canAcceptRecipeOutput(Inventory inv) {
        return inv.getStack(2).isEmpty() && inv.getStack(3).isEmpty();
    }


    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    @Deprecated
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

    @Override
    public int getCraftTime() {
        return craftTime;
    }

    @Override
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(input, 1);
    }

    public Item getInput() {
        return input;
    }


    public List<List<ItemStack>> generateSample() {
        List<List<ItemStack>> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            ItemStack inStack = new ItemStack(input, 1);
            EnchantmentHelper.enchant(r, inStack, 30, true);

            Enchantment enchantment = randomEnchantment(inStack);

            if (enchantment == null)
                return Collections.singletonList(Arrays.asList(input.getDefaultStack(), Items.BOOK.getDefaultStack(), input.getDefaultStack()));


            int level = EnchantmentHelper.get(inStack).get(enchantment);

            ItemStack bookOutput = new ItemStack(Items.ENCHANTED_BOOK, 1);
            bookOutput.addEnchantment(enchantment, level);

            ItemStack outStack = inStack.copy();
            Map<Enchantment, Integer> outMap = EnchantmentHelper.get(outStack);
            outMap.remove(enchantment);
            EnchantmentHelper.set(outMap, outStack);
            result.add(Arrays.asList(inStack, bookOutput, outStack));
        }
        return result;
    }


    private void addStoredEnchantment(ItemStack stack, Enchantment enchantment, int level) {
        stack.getOrCreateTag();
        if (!stack.getTag().contains("StoredEnchantments", 9)) {
            stack.getTag().put("StoredEnchantments", new ListTag());
        }

        ListTag listTag = stack.getTag().getList("StoredEnchantments", 10);
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
        compoundTag.putShort("lvl", (byte) level);
        listTag.add(compoundTag);
    }
}
