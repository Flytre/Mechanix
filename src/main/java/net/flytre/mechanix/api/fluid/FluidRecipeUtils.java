package net.flytre.mechanix.api.fluid;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.flytre.flytre_lib.common.recipe.RecipeUtils;
import net.minecraft.util.JsonHelper;

import java.util.HashSet;

public class FluidRecipeUtils {

    /**
     * Craft the fluid part of the output of a recipe containing fluids.
     * Does not decrement inputs.
     */
    public static void craftOutput(FluidInventory inv, int lower, int upper, FluidStack[] providers) {
        HashSet<Integer> checked = new HashSet<>();
        for (FluidStack output : providers) {
            boolean matched = false;
            for (int i = lower; i < upper; i++)
                if (!checked.contains(i) && inv.isValidInternal(i, output) && !inv.getFluidStack(i).isEmpty()) {
                    matched = true;
                    checked.add(i);
                    inv.getFluidStack(i).increment(output.getAmount());
                    break;
                }
            if (!matched) {
                for (int i = lower; i < upper; i++) {
                    if (inv.getFluidStack(i).isEmpty()) {
                        inv.setStack(i, output.copy());
                        break;
                    }
                }
            }
        }
    }

    /**
     * Check whether a set of fluid stacks can be placed between slots [lower, upper) in a fluid inventory
     */
    public static boolean matches(FluidInventory inv, int lower, int upper, FluidStack[] outputProviders) {
        int blanks = 0; //number of empty slots
        for (int i = lower; i < upper; i++) {
            if (inv.getFluidStack(i).isEmpty())
                blanks++;
        }

        HashSet<Integer> checked = new HashSet<>();
        for (FluidStack output : outputProviders) {
            boolean matched = false;
            for (int i = lower; i < upper; i++)
                if (!checked.contains(i) && inv.isValidInternal(i, output) && !inv.getFluidStack(i).isEmpty()) {
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


    /**
     * Get the array of fluid stacks from a Json recipe object
     */
    public static FluidStack[] getFluidStacks(JsonObject json, String pluralKey, String singularKey) {
        FluidStack[] result;
        if (JsonHelper.hasArray(json, pluralKey)) {
            JsonArray array = JsonHelper.getArray(json, pluralKey);
            result = new FluidStack[array.size()];
            for (int i = 0; i < array.size(); i++)
                result[i] = FluidStack.fromJson((JsonObject) array.get(i));
        } else {
            result = new FluidStack[]{FluidStack.fromJson((JsonObject) json.get(singularKey))};
        }

        return result;
    }

}
