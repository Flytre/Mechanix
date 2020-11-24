package net.flytre.mechanix.base;

import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.Direction;

import java.util.HashMap;

public class DelegateFixer {


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

    public static int booleansToInt(boolean[] arr){
        int n = 0;
        for(int i = -1; i < arr.length - 1; n += arr[++i] ? 1 << i : 0);
        return n;
    }

    public static boolean[] intsToBoolean(int n, int size) {
        boolean[] result = new boolean[size];
        for(int i = size - 1, c = 1 << i; i >= 0; c = 1 << --i) {
            result[i] = n >= c;
            n = n >= c ? n - c : n;
        }
        return result;
    }

    public static int[] splitInt(int x) {
        int[] result = new int[]{x, x == 0 ? 0 : Math.max((int)Math.log10(x) - 2, 0)};
        while(result[0] >= 1000)
            result[0] /= 10;
        return result;
    }

    public static int unsplit(int[] split) {
        return (int) (split[0] * Math.pow(10,split[1]));
    }

    public static int energy(PropertyDelegate propertyDelegate) {
            return DelegateFixer.unsplit(new int[]{propertyDelegate.get(3),propertyDelegate.get(4)});
    }
    public static int maxEnergy(PropertyDelegate propertyDelegate) {
        return Math.max(DelegateFixer.unsplit(new int[]{propertyDelegate.get(5),propertyDelegate.get(6)}),1);
    }
}
