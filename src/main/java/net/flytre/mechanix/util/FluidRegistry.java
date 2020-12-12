package net.flytre.mechanix.util;

import net.flytre.mechanix.fluid.MetallicFluid;

public class FluidRegistry {

    public static FluidType PERLIUM;
    public static FluidType GOLD;
    public static FluidType IRON;
    public static FluidType RAW_PERLIUM;
    public static FluidType RAW_GOLD;
    public static FluidType RAW_IRON;

    public static void init() {
        PERLIUM = new FluidType("molten_perlium", () -> (new MetallicFluid.Still(() -> PERLIUM)), () -> (new MetallicFluid.Flowing(() -> PERLIUM)));
        PERLIUM.setBlock();
        GOLD = new FluidType("molten_gold", () -> (new MetallicFluid.Still(() -> GOLD)), () -> (new MetallicFluid.Flowing(() -> GOLD)));
        GOLD.setBlock();
        IRON = new FluidType("molten_iron", () -> (new MetallicFluid.Still(() -> IRON)), () -> (new MetallicFluid.Flowing(() -> IRON)));
        IRON.setBlock();
        RAW_PERLIUM = new FluidType("raw_perlium", () -> (new MetallicFluid.Still(() -> RAW_PERLIUM)), () -> (new MetallicFluid.Flowing(() -> RAW_PERLIUM)));
        RAW_PERLIUM.setBlock();
        RAW_GOLD = new FluidType("raw_gold", () -> (new MetallicFluid.Still(() -> RAW_GOLD)), () -> (new MetallicFluid.Flowing(() -> RAW_GOLD)));
        RAW_GOLD.setBlock();
        RAW_IRON = new FluidType("raw_iron",() -> (new MetallicFluid.Still(() -> RAW_IRON)), () -> (new MetallicFluid.Flowing(() -> RAW_IRON)));
        RAW_IRON.setBlock();
    }
}
