package net.flytre.mechanix.api.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.stream.StreamSupport;

public class RecipeUtils {


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
                return !Registry.ITEM.containsId(id);
            } else if (json.has("tag")) {
                identifier2 = new Identifier(JsonHelper.getString(json, "tag"));
                Tag<Item> tag = ServerTagManagerHolder.getTagManager().getItems().getTag(identifier2);
                return tag == null;
            } else {
                return true;
            }
        }
    }

}
