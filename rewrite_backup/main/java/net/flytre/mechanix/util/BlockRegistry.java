package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class BlockRegistry {

    public static Block PERLIUM_ORE = new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F));
    public static Block HARDENED_GLASS = new GlassBlock(FabricBlockSettings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(BlockRegistry::never).solidBlock(BlockRegistry::never).suffocates(BlockRegistry::never).blockVision(BlockRegistry::never));
    public static Block VYSTERIUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(7.0F, 6.0F));
    public static Block NEPTUNIUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(10.0F, 10.0F));
    public static Block ENDALUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(6.0F, 6.0F));
    public static Block PERLIUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(5.0F, 5.0F));
    public static Block REINFORCED_IRON_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(7.0F, 6.0F));


    private static boolean never(BlockState state, BlockView blockView, BlockPos blockPos) {
        return false;
    }

    private static boolean never(BlockState state, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
        return false;
    };

    public static void init() {
        MachineRegistry.registerBlock(PERLIUM_ORE,"perlium_ore", IconMaker.STANDARD);
        MachineRegistry.registerBlock(HARDENED_GLASS,"hardened_glass", IconMaker.STANDARD);
        MachineRegistry.registerBlock(VYSTERIUM_BLOCK,"vysterium_block", IconMaker.STANDARD);
        MachineRegistry.registerBlock(NEPTUNIUM_BLOCK,"neptunium_block", IconMaker.STANDARD);
        MachineRegistry.registerBlock(ENDALUM_BLOCK,"endalum_block", IconMaker.STANDARD);
        MachineRegistry.registerBlock(PERLIUM_BLOCK,"perlium_block", IconMaker.STANDARD);
        MachineRegistry.registerBlock(REINFORCED_IRON_BLOCK,"reinforced_iron_block", IconMaker.STANDARD);

    }

}
