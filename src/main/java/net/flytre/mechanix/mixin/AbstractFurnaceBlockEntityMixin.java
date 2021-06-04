package net.flytre.mechanix.mixin;


import net.flytre.mechanix.util.ItemRegistry;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {

    @Inject(method = "createFuelTimeMap", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void mechanix$cokeFuel(CallbackInfoReturnable<Map<Item, Integer>> cir, Map<Item, Integer> map) {
        addFuel(map, ItemRegistry.COKE, 6400);
    }

    @Shadow
    private static void addFuel(Map<Item, Integer> map, ItemConvertible item, int fuelTime) {
        throw new AssertionError();
    }

}
