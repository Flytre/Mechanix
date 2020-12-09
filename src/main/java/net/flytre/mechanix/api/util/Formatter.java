package net.flytre.mechanix.api.util;

import com.google.common.collect.Maps;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to format text and numbers in various situations
 */
public class Formatter {

    private static final String[] PREFIX_VALUES = new String[]{"","k","M","G","T","P"};
    private static final Map<String, String> modNameCache = Maps.newHashMap();


    /**
     * Convert a hash map to an int
     *
     * @param hash the hash
     * @return the int
     */
    public static int hashToInt(HashMap<Direction, Boolean> hash) {
        boolean[] array = new boolean[]{
                hash.get(Direction.NORTH),
                hash.get(Direction.WEST),
                hash.get(Direction.EAST),
                hash.get(Direction.UP),
                hash.get(Direction.DOWN),
                hash.get(Direction.SOUTH)};
        return booleansToInt(array);
    }

    /**
     * Convert an int to a hashmap
     *
     * @param n the n
     * @return the hash map
     */
    public static HashMap<Direction, Boolean> intToHash(int n) {
        boolean[] array = intsToBoolean(n,6);
        HashMap<Direction,Boolean> result = new HashMap<>();
        result.put(Direction.NORTH,array[0]);
        result.put(Direction.WEST,array[1]);
        result.put(Direction.EAST,array[2]);
        result.put(Direction.UP,array[3]);
        result.put(Direction.DOWN,array[4]);
        result.put(Direction.SOUTH,array[5]);
        return result;
    }

    /**
     * Convery a boolean array to an int
     *
     * @param arr the arr
     * @return the int
     */
    public static int booleansToInt(boolean[] arr){
        int n = 0;
        for(int i = -1; i < arr.length - 1; n += arr[++i] ? 1 << i : 0);
        return n;
    }

    /**
     * Convert an int to a boolean array
     *
     * @param n    the n
     * @param size the size
     * @return the boolean [ ]
     */
    public static boolean[] intsToBoolean(int n, int size) {
        boolean[] result = new boolean[size];
        for(int i = size - 1, c = 1 << i; i >= 0; c = 1 << --i) {
            result[i] = n >= c;
            n = n >= c ? n - c : n;
        }
        return result;
    }

    /**
     * Split an int into mock-scientific notation
     *
     * @param x the x
     * @return the int [ ]
     */
    public static int[] splitInt(int x) {
        int[] result = new int[]{x, x == 0 ? 0 : Math.max((int)Math.log10(x) - 3, 0)};
        while(result[0] >= 10000)
            result[0] /= 10;
        return result;
    }

    /**
     * Unsplit int from mock-scientific notation.
     *
     * @param split the split
     * @return the int
     */
    public static int unsplit(int[] split) {
        return (int) (split[0] * Math.pow(10,split[1]));
    }

    /**
     * Get energy from delegate (hardcoded indices 3 and 4)
     *
     * @param propertyDelegate the property delegate
     * @return the int
     */
    public static int energy(PropertyDelegate propertyDelegate) {
            return Formatter.unsplit(new int[]{propertyDelegate.get(3),propertyDelegate.get(4)});
    }

    /**
     * Get max energy from delegate (hardcoded indices 5 and 6)
     *
     * @param propertyDelegate the property delegate
     * @return the int
     */
    public static int maxEnergy(PropertyDelegate propertyDelegate) {
        return Math.max(Formatter.unsplit(new int[]{propertyDelegate.get(5),propertyDelegate.get(6)}),1);
    }


    /**
     * Format a number to a String, for example 1000B = 1kB.
     *
     * @param num    the num
     * @param suffix the suffix
     * @return the string
     */
    public static String formatNumber(double num, String suffix) {
        int suffixIndex = 0;
        while(num >= 1000.0) {
            suffixIndex++;
            num /= 1000.0;
        }
        if(suffix.equals("J"))
            suffixIndex++;

        int format = num >= 100 ? 1 : 2;
        return String.format("%." + format + "f", num) + PREFIX_VALUES[suffixIndex] + suffix;
    }


    public static String getModFromModId(String modid) {
        if (modid == null)
            return "";
        String any = modNameCache.getOrDefault(modid, null);
        if (any != null)
            return any;
        String s = FabricLoader.getInstance().getModContainer(modid).map(ModContainer::getMetadata).map(ModMetadata::getName).orElse(modid);
        modNameCache.put(modid, s);
        return s;
    }

    public static Text getModNameToolTip(String modid) {
        MutableText name = new LiteralText(getModFromModId(modid)).append(new LiteralText(""));
        name.setStyle(Style.EMPTY.withColor(Formatting.BLUE).withItalic(true));
        return name;
    }
}
