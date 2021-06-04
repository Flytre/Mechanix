package net.flytre.mechanix.api.upgrade.mixin;


import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ScreenHandler.class)
public interface ScreenHandlerAccessor {

    @Accessor("listeners")
    List<ScreenHandlerListener> getListeners();
}
