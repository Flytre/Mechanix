package net.flytre.mechanix.mixin;

import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SaveLevelScreen.class)
public abstract class SaveLevelScreenMixin extends Screen {
    protected SaveLevelScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void mechanix(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int i = this.width / 2;
        int j = this.height / 2;
        drawCenteredText(matrices, this.textRenderer, new TranslatableText("gui.mechanix.world_load"), i, j - 9 / 2 + 100, 16777215);
    }
}
