package net.flytre.mechanix.mixin;

import net.flytre.mechanix.util.ItemRegistery;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin {

    @Shadow
    private static void registerCompostableItem(float levelIncreaseChance, ItemConvertible item) {};

    @Inject(method="registerDefaultCompostableItems", at = @At("RETURN"))
    private static void registerCustoms(CallbackInfo ci) {
        registerCompostableItem(0.3f, ItemRegistery.SAWDUST);
    }
}
