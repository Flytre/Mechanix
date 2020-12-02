package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class MachineType<T extends BlockWithEntity, E extends BlockEntity, H extends ScreenHandler> {
    private final String id;
    private final T block;
    private final BlockEntityType<E> entityType;
    private final ScreenHandlerType<H> handlerType;

    public MachineType(T block, String id, IconMaker<Block> blockItem, Supplier<E> blockEntityCreator, ScreenHandlerRegistry.ExtendedClientHandlerFactory<H> factory) {
        this.block = block;
        this.id = id;
        Registry.register(Registry.BLOCK, new Identifier("mechanix", id), block);
        Registry.register(Registry.ITEM, new Identifier("mechanix", id), blockItem.create(block));
        entityType = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("mechanix", id), BlockEntityType.Builder.create(blockEntityCreator, block).build(null));
        handlerType = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix",id), factory);
    }

    public String getId() {
        return id;
    }

    public T getBlock() {
        return block;
    }

    public BlockEntityType<E> getEntityType() {
        return entityType;
    }

    public ScreenHandlerType<H> getHandlerType() {
        return handlerType;
    }
}
