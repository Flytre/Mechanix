package net.flytre.mechanix.api.fluid.mixin;


import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.mechanix.api.client.FluidHandledScreen;
import net.flytre.mechanix.api.fluid.FluidHandler;
import net.flytre.mechanix.api.fluid.FluidSlot;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin<T extends ScreenHandler> extends Screen {

    @Shadow
    @Final
    protected T handler;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/util/math/MatrixStack;II)V", shift = At.Shift.BEFORE))
    public void mechanix$fluidHandledScreenRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if ((Object) this instanceof FluidHandledScreen) {
            FluidHandler handler = (FluidHandler) this.handler;
            FluidHandledScreen<?> me = (FluidHandledScreen<?>) (Object) this;
            int r;
            for (int m = 0; m < handler.fluidSlots.size(); ++m) {
                FluidSlot slot = handler.fluidSlots.get(m);
                if (slot.doDrawHoveringEffect()) {
                    me.drawFluidSlot(matrices, slot);
                }

                if (me.isPointOverFluidSlot(slot, mouseX, mouseY) && slot.doDrawHoveringEffect()) {
                    me.focusedFluidSlot = slot;
                    RenderSystem.disableDepthTest();
                    int n = slot.x;
                    r = slot.y;
                    RenderSystem.colorMask(true, true, true, false);
                    this.fillGradient(matrices, slot.compact ? n : n + 1, slot.compact ? r : r + 1, n + (slot.compact ? 16 : 29), r + (slot.compact ? 16 : 59), -2130706433, -2130706433);
                    RenderSystem.colorMask(true, true, true, true);
                    RenderSystem.enableDepthTest();
                }
            }
        }
    }
}
