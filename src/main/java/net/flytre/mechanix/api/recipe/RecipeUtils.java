package net.flytre.mechanix.api.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.flytre.mechanix.api.inventory.EasyInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RecipeUtils {

    private static HashMap<Item, List<CraftingRecipe>> cache;

    static {
        cache = new HashMap<>();
    }

    public static void clearCache() {
        if (cache != null)
            cache.clear();
    }

    public static Ingredient fromJson(@Nullable JsonElement json) {
        return json == null || isIngredientInvalid(json) ? Ingredient.EMPTY : Ingredient.fromJson(json);
    }

    public static boolean isIngredientInvalid(@Nullable JsonElement json) {
        if (json != null && !json.isJsonNull()) {
            if (json.isJsonObject()) {
                return nullCheck(json.getAsJsonObject());
            } else if (json.isJsonArray()) {
                JsonArray jsonArray = json.getAsJsonArray();
                if (jsonArray.size() == 0) {
                    return true;
                } else {
                    return StreamSupport.stream(jsonArray.spliterator(), false).anyMatch((jsonElement) -> nullCheck(JsonHelper.asObject(jsonElement, "item")));
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    private static boolean nullCheck(JsonObject json) {
        if (json.has("item") && json.has("tag")) {
            return true;
        } else {
            Identifier identifier2;
            if (json.has("item")) {
                Identifier id = new Identifier(JsonHelper.getString(json, "item"));
                return !Registry.ITEM.getIds().contains(id);
            } else if (json.has("tag")) {
                identifier2 = new Identifier(JsonHelper.getString(json, "tag"));
                Tag<Item> tag = ServerTagManagerHolder.getTagManager().getItems().getTag(identifier2);
                return tag == null;
            } else {
                return true;
            }
        }
    }


    public static void craftOutput(Inventory inv, int lower, int upper, OutputProvider[] providers) {
        HashSet<Integer> checked = new HashSet<>();
        for (OutputProvider output : providers) {
            boolean matched = false;
            for (int i = lower; i < upper; i++)
                if (!checked.contains(i) && EasyInventory.canMergeItems(output.getStack(), inv.getStack(i))) {
                    matched = true;
                    checked.add(i);
                    inv.getStack(i).increment(output.getStack().getCount());
                    break;
                }
            if (!matched) {
                for (int i = lower; i < upper; i++) {
                    if (inv.getStack(i).isEmpty()) {
                        inv.setStack(i, output.getStack());
                        break;
                    }
                }
            }
        }
    }

    public static boolean matches(Inventory inv, int lower, int upper, OutputProvider[] outputProviders) {
        int blanks = 0; //number of empty slots
        for (int i = lower; i < upper; i++) {
            if (inv.getStack(i).isEmpty())
                blanks++;
        }
        HashSet<Integer> checked = new HashSet<>();
        for (OutputProvider output : outputProviders) {
            boolean matched = false;
            for (int i = lower; i < upper; i++)
                if (!checked.contains(i) && EasyInventory.canMergeItems(output.getStack(), inv.getStack(i))) {
                    matched = true;
                    checked.add(i);
                    break;
                }
            if (!matched)
                if (blanks == 0)
                    return false;
                else
                    blanks--;
        }
        return true;
    }


    public static boolean craftingInputMatch(CraftingRecipe recipe, Inventory inv, int lower, int upper) {
        //Get only non-empty ingredients
        DefaultedList<Ingredient> ingredients = recipe.getPreviewInputs();
        List<Ingredient> actual = ingredients.stream().filter(i -> !i.isEmpty()).collect(Collectors.toList());

        //Copy the inventory and use that for parsing as u can decrement
        ArrayList<ItemStack> copy = new ArrayList<>();
        for (int i = lower; i < upper; i++) {
            copy.add(i, inv.getStack(i).copy());
        }

        for (Ingredient ingredient : actual) {
            boolean matched = false;
            for (int i = lower; i < upper; i++) {
                if (ingredient.test(copy.get(i))) {
                    matched = true;
                    copy.get(i).decrement(1);
                    break;
                }
            }
            if (!matched)
                return false;
        }
        return true;
    }


    public static void actuallyCraft(CraftingRecipe recipe, Inventory inv, int lower, int upper) {
        //Get only non-empty ingredients
        DefaultedList<Ingredient> ingredients = recipe.getPreviewInputs();
        List<Ingredient> actual = ingredients.stream().filter(i -> !i.isEmpty()).collect(Collectors.toList());

        for (Ingredient ingredient : actual)
            for (int i = lower; i < upper; i++)
                if (ingredient.test(inv.getStack(i))) {
                    ItemStack stack = inv.getStack(i);
                    if(stack.getCount() == 1 && stack.getItem().hasRecipeRemainder()) {
                        inv.setStack(i, new ItemStack(stack.getItem().getRecipeRemainder(), 1));
                    } else
                        stack.decrement(1);
                    break;
                }
    }


    public static List<CraftingRecipe> craftingRecipesWithOutput(Item item, World world) {

        if (cache.containsKey(item))
            return cache.get(item);

        List<CraftingRecipe> recipes = world.getRecipeManager().listAllOfType(RecipeType.CRAFTING).stream().filter(i -> i.getOutput().getItem() == item).collect(Collectors.toList());
        cache.put(item, recipes);
        return recipes;
    }

    public static @Nullable CraftingRecipe getFirstCraftingMatch(Item item, Inventory inv, World world, int lower, int upper) {
        List<CraftingRecipe> outputs = craftingRecipesWithOutput(item, world);
        Optional<CraftingRecipe> result = outputs.stream().filter(i -> craftingInputMatch(i, inv, lower, upper)).findFirst();
        return result.orElse(null);
    }



    public static OutputProvider[] getOutputProviders(JsonObject json, String key) {
        OutputProvider[] result;
        if(JsonHelper.hasArray(json, key)) {
            JsonArray array = JsonHelper.getArray(json, key);
            result = new OutputProvider[array.size()];
            for(int i = 0; i < array.size(); i++)
                result[i] = OutputProvider.fromJson(array.get(i));
        } else {
            result = new OutputProvider[]{OutputProvider.fromJson(json.get(key))};
        }

        return result;
    }

}
