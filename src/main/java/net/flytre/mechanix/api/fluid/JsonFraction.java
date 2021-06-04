package net.flytre.mechanix.api.fluid;

import com.google.gson.JsonObject;

public class JsonFraction {

    private static final String[] NUMERATOR_ALIASES = new String[]{"n", "num", "numerator"};
    private static final String[] DENOMINATOR_ALIASES = new String[]{"d", "den", "denominator"};

    private final int numerator;
    private final int denominator;

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public JsonFraction(int n, int d) {

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

    public static JsonFraction fromJson(JsonObject object) {
        int n = 0, d = 1;
        for (String key : NUMERATOR_ALIASES)
            if (object.has(key))
                n = object.get(key).getAsInt();
        for (String key : DENOMINATOR_ALIASES)
            if (object.has(key))
                d = object.get(key).getAsInt();
        return new JsonFraction(n, d);
    }
}
