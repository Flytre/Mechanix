package net.flytre.mechanix.util;

import net.flytre.mechanix.fluid.MetallicFluid;

public class FluidRegistry {

    public static FluidType PERLIUM;
    public static FluidType GOLD;
    public static FluidType IRON;
    public static FluidType BRONZE;
    public static FluidType COPPER;
    public static FluidType ELECTRUM;
    public static FluidType INVAR;
    public static FluidType LEAD;
    public static FluidType NICKEL;
    public static FluidType PLATINUM;
    public static FluidType SILVER;
    public static FluidType TIN;
    public static FluidType RAW_PERLIUM;
    public static FluidType RAW_GOLD;
    public static FluidType RAW_IRON;
    public static FluidType RAW_COPPER;
    public static FluidType RAW_LEAD;
    public static FluidType RAW_NICKEL;
    public static FluidType RAW_PLATINUM;
    public static FluidType RAW_SILVER;
    public static FluidType RAW_TIN;
    public static FluidType LIQUID_XP;

    public static void init() {
        PERLIUM = new FluidType("molten_perlium", () -> (new MetallicFluid.Still(() -> PERLIUM)), () -> (new MetallicFluid.Flowing(() -> PERLIUM)));
        PERLIUM.setBlock();
        GOLD = new FluidType("molten_gold", () -> (new MetallicFluid.Still(() -> GOLD)), () -> (new MetallicFluid.Flowing(() -> GOLD)));
        GOLD.setBlock();
        IRON = new FluidType("molten_iron", () -> (new MetallicFluid.Still(() -> IRON)), () -> (new MetallicFluid.Flowing(() -> IRON)));
        IRON.setBlock();
        BRONZE = new FluidType("molten_bronze", () -> (new MetallicFluid.Still(() -> BRONZE)), () -> (new MetallicFluid.Flowing(() -> BRONZE)));
        BRONZE.setBlock();
        COPPER = new FluidType("molten_copper", () -> (new MetallicFluid.Still(() -> COPPER)), () -> (new MetallicFluid.Flowing(() -> COPPER)));
        COPPER.setBlock();
        ELECTRUM = new FluidType("molten_electrum", () -> (new MetallicFluid.Still(() -> ELECTRUM)), () -> (new MetallicFluid.Flowing(() -> ELECTRUM)));
        ELECTRUM.setBlock();
        INVAR = new FluidType("molten_invar", () -> (new MetallicFluid.Still(() -> INVAR)), () -> (new MetallicFluid.Flowing(() -> INVAR)));
        INVAR.setBlock();
        LEAD = new FluidType("molten_lead", () -> (new MetallicFluid.Still(() -> LEAD)), () -> (new MetallicFluid.Flowing(() -> LEAD)));
        LEAD.setBlock();
        NICKEL = new FluidType("molten_nickel", () -> (new MetallicFluid.Still(() -> NICKEL)), () -> (new MetallicFluid.Flowing(() -> NICKEL)));
        NICKEL.setBlock();
        PLATINUM = new FluidType("molten_platinum", () -> (new MetallicFluid.Still(() -> PLATINUM)), () -> (new MetallicFluid.Flowing(() -> PLATINUM)));
        PLATINUM.setBlock();
        SILVER = new FluidType("molten_silver", () -> (new MetallicFluid.Still(() -> SILVER)), () -> (new MetallicFluid.Flowing(() -> SILVER)));
        SILVER.setBlock();
        TIN = new FluidType("molten_tin", () -> (new MetallicFluid.Still(() -> TIN)), () -> (new MetallicFluid.Flowing(() -> TIN)));
        TIN.setBlock();
        
        RAW_PERLIUM = new FluidType("raw_perlium", () -> (new MetallicFluid.Still(() -> RAW_PERLIUM)), () -> (new MetallicFluid.Flowing(() -> RAW_PERLIUM)));
        RAW_PERLIUM.setBlock();
        RAW_GOLD = new FluidType("raw_gold", () -> (new MetallicFluid.Still(() -> RAW_GOLD)), () -> (new MetallicFluid.Flowing(() -> RAW_GOLD)));
        RAW_GOLD.setBlock();
        RAW_IRON = new FluidType("raw_iron",() -> (new MetallicFluid.Still(() -> RAW_IRON)), () -> (new MetallicFluid.Flowing(() -> RAW_IRON)));
        RAW_IRON.setBlock();
        RAW_COPPER = new FluidType("raw_copper", () -> (new MetallicFluid.Still(() -> RAW_COPPER)), () -> (new MetallicFluid.Flowing(() -> RAW_COPPER)));
        RAW_COPPER.setBlock();
        RAW_LEAD = new FluidType("raw_lead", () -> (new MetallicFluid.Still(() -> RAW_LEAD)), () -> (new MetallicFluid.Flowing(() -> RAW_LEAD)));
        RAW_LEAD.setBlock();
        RAW_NICKEL = new FluidType("raw_nickel", () -> (new MetallicFluid.Still(() -> RAW_NICKEL)), () -> (new MetallicFluid.Flowing(() -> RAW_NICKEL)));
        RAW_NICKEL.setBlock();
        RAW_PLATINUM = new FluidType("raw_platinum", () -> (new MetallicFluid.Still(() -> RAW_PLATINUM)), () -> (new MetallicFluid.Flowing(() -> RAW_PLATINUM)));
        RAW_PLATINUM.setBlock();
        RAW_SILVER = new FluidType("raw_silver", () -> (new MetallicFluid.Still(() -> RAW_SILVER)), () -> (new MetallicFluid.Flowing(() -> RAW_SILVER)));
        RAW_SILVER.setBlock();
        RAW_TIN = new FluidType("raw_tin", () -> (new MetallicFluid.Still(() -> RAW_TIN)), () -> (new MetallicFluid.Flowing(() -> RAW_TIN)));
        RAW_TIN.setBlock();
        LIQUID_XP = new FluidType("liquid_xp", () -> (new MetallicFluid.Still(() -> LIQUID_XP)), () -> (new MetallicFluid.Flowing(() -> LIQUID_XP)));
        LIQUID_XP.setBlock();
    }
}
