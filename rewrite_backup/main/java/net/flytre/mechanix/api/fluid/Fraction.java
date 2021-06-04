package net.flytre.mechanix.api.fluid;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Fractions are IMMUTABLE!
 * A simple representation of a standardized fraction object
 */
public class Fraction {

    public static final Fraction ONE = new Fraction(1, 1);
    public static final Fraction ZERO = new Fraction(0, 1);

    public static final Fraction ONE_THOUSANDTH = new Fraction(1, 1000);


    private static final String[] NUMERATOR_ALIASES = new String[]{"n", "num", "numerator"};
    private static final String[] DENOMINATOR_ALIASES = new String[]{"d", "den", "denominator"};
    private static final Map<Fraction, Map<Integer, Fraction>> CACHE = new HashMap<>();
    private final int numerator;
    private final int denominator;

    public Fraction(int n, int d) {

        /*
        Proper standardization of fractions:
        -Denominator is always positive
        -Fractions are always fully simplified
         */

        int gcd = gcd(n, d);
        if (d < 0) {
            n *= -1;
            d *= -1;
        }
        if (d == 0)
            throw new AssertionError("Fraction denominator cannot be 0!");
        numerator = n / gcd;
        denominator = d / gcd;
    }

    private static int gcd(int a, int b) {
        while (b != 0) {
            int t = a;
            a = b;
            b = t % b;
        }
        return a;
    }

    public static Fraction add(Fraction a, Fraction b) {

        if (a.denominator == b.denominator)
            return new Fraction(a.numerator + b.numerator, a.denominator);

        int aTop = b.denominator * a.numerator;
        int bTop = a.denominator * b.numerator;
        return new Fraction(aTop + bTop, a.denominator * b.denominator);
    }

    public static Fraction divide(Fraction a, Fraction b) {
        return new Fraction(a.numerator * b.denominator, a.denominator * b.numerator);
    }

    public static Fraction multiply(Fraction a, Fraction b) {
        return new Fraction(a.numerator * b.numerator, a.denominator * b.denominator);
    }

    public static Fraction subtract(Fraction a, Fraction b) {
        return add(a, b.invert());
    }

    public static Fraction fromTag(CompoundTag frac) {
        int n = frac.getInt("n");
        int d = frac.getInt("d");
        return new Fraction(n, d);
    }

    public static Fraction fromJson(JsonObject object) {
        int n = 0, d = 1;
        for (String key : NUMERATOR_ALIASES)
            if (object.has(key))
                n = object.get(key).getAsInt();
        for (String key : DENOMINATOR_ALIASES)
            if (object.has(key))
                d = object.get(key).getAsInt();
        return new Fraction(n, d);
    }

    public static Fraction fromPacket(PacketByteBuf buf) {
        return new Fraction(buf.readInt(), buf.readInt());
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("n", numerator);
        tag.putInt("d", denominator);
        return tag;
    }

    public void toPacket(PacketByteBuf buf) {
        buf.writeInt(numerator);
        buf.writeInt(denominator);
    }

    public double toDouble() {
        return ((double) numerator) / ((double) denominator);
    }


    public boolean isPositive() {
        return numerator > 0;
    }

    public boolean isNegative() {
        return numerator < 0;
    }


    public Fraction min(Fraction b) {
        if (denominator == b.denominator)
            return numerator <= b.numerator ? this : b;
        return toDouble() < b.toDouble() ? this : b;
    }

    public boolean isLess(Fraction b) {
        return Fraction.subtract(b, this).numerator > 0;
    }

    public boolean isLessOrEqual(Fraction b) {
        return isLess(b) || equals(b);
    }

    public boolean isGreaterOrEqual(Fraction b) {
        return isGreater(b) || equals(b);
    }

    public boolean isGreater(Fraction b) {
        return Fraction.subtract(b, this).numerator < 0;
    }

    public Fraction max(Fraction b) {
        if (denominator == b.denominator)
            return numerator >= b.numerator ? this : b;
        return toDouble() >= b.toDouble() ? this : b;
    }

    @Override
    public String toString() {
        return numerator + " / " + denominator;
    }


    public boolean isZero() {
        return numerator == 0;
    }


    public Fraction invert() {
        return new Fraction(this.numerator * -1, this.denominator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fraction fraction = (Fraction) o;
        return numerator == fraction.numerator && denominator == fraction.denominator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }


    public Fraction getPipeTransferAmount(Fraction target) {
        if (CACHE.containsKey(target) && CACHE.get(target).containsKey(denominator)) {
            return CACHE.get(target).get(denominator);
        }

        if (target.isGreaterOrEqual(new Fraction(1, denominator)))
            return new Fraction(1, denominator);

        int bestNum = 0;
        double bestDist = Integer.MAX_VALUE;

        for (int i = 0; i <= denominator; i++) {
            double dist = getPipeHelper(i, target.toDouble());
            if (dist < bestDist) {
                bestNum = i;
                bestDist = dist;
            }
        }

        Fraction result = new Fraction(bestNum, denominator);
        if (!CACHE.containsKey(target))
            CACHE.put(target, new HashMap<>());
        CACHE.get(target).put(denominator, result);
        return result;
    }

    private double getPipeHelper(int num, double targetDouble) {
        Fraction frac = new Fraction(num, denominator);
        return Math.abs(frac.toDouble() - targetDouble);
    }
}
