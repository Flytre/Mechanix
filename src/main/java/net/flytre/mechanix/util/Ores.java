package net.flytre.mechanix.util;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.*;

public class Ores {
    public static ConfiguredFeature<?, ?> PERLIUM_ORE = register("mechanix:ore_perlium_nether", Feature.ORE
            .configure(new OreFeatureConfig(
                    OreFeatureConfig.Rules.NETHERRACK,
                    BlockRegistry.PERLIUM_ORE.getDefaultState(), 10
            ))
            .decorate(ConfiguredFeatures.Decorators.NETHER_ORE)
            .spreadHorizontally()
            .repeat(8));


    private static <FC extends FeatureConfig> ConfiguredFeature<FC, ?> register(String id, ConfiguredFeature<FC, ?> configuredFeature) {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, configuredFeature);
    }
}
