package net.flytre.mechanix.api.upgrade.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.mechanix.api.client.PanelledFluidHandledScreenWithEnergy;
import net.flytre.mechanix.api.upgrade.UpgradeHandler;
import net.flytre.mechanix.api.upgrade.UpgradePackets;
import net.flytre.mechanix.compat.rei.UpgradeScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements UpgradeScreen {

    private static Identifier UPGRADE = new Identifier("mechanix:textures/gui/container/upgrade.png");


    @Shadow
    @Final
    protected T handler;


    @Shadow
    protected Slot focusedSlot;
    @Shadow
    protected int x;
    @Shadow
    protected int y;
    @Shadow
    protected int backgroundWidth;
    @Shadow
    protected int backgroundHeight;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    protected abstract void drawSlot(MatrixStack matrices, Slot slot);

    @Shadow
    protected abstract boolean isPointOverSlot(Slot slot, double pointX, double pointY);

    @Inject(method = "getSlotAt", at = @At("TAIL"), cancellable = true)
    public void mechanix$getUpgradeSlotAt(double xPosition, double yPosition, CallbackInfoReturnable<Slot> cir) {
        if (handler instanceof UpgradeHandler) {
            UpgradeHandler handler = (UpgradeHandler) this.handler;
            for (int i = 0; i < handler.upgradeSlots.size(); ++i) {
                Slot slot = handler.upgradeSlots.get(i);
                if (this.isPointOverSlot(slot, xPosition, yPosition) && slot.doDrawHoveringEffect()) {
                    cir.setReturnValue(slot);
                }
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void mechanix$renderQuadUpgradePanel(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (handler instanceof UpgradeHandler && ((UpgradeHandler) handler).upgradeSlots.size() == 4) {
            this.client.getTextureManager().bindTexture(UPGRADE);
            this.drawTexture(matrices, x + backgroundWidth, this.y + 70, 0, 0, 65, 65);

        }
    }


    @Inject(method = "isClickOutsideBounds", at = @At("HEAD"), cancellable = true)
    public void mechanix$inBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
        if (handler instanceof UpgradeHandler && ((UpgradeHandler) handler).upgradeSlots.size() == 4 && mouseX >= (double) left && mouseY >= (double) top + 70 && mouseX < (double) (left + this.backgroundWidth + 65) && mouseY <= (double) (top + 135)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/util/math/MatrixStack;II)V", shift = At.Shift.BEFORE))
    public void mechanix$upgradeHandledScreenRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (handler instanceof UpgradeHandler) {
            UpgradeHandler handler = (UpgradeHandler) this.handler;
            int r;
            for (int m = 0; m < handler.upgradeSlots.size(); ++m) {
                Slot slot = handler.upgradeSlots.get(m);
                if (slot.doDrawHoveringEffect()) {
                    drawSlot(matrices, slot);
                }

                if (isPointOverSlot(slot, mouseX, mouseY) && slot.doDrawHoveringEffect()) {
                    focusedSlot = slot;
                    RenderSystem.disableDepthTest();
                    int n = slot.x;
                    r = slot.y;
                    RenderSystem.colorMask(true, true, true, false);
                    this.fillGradient(matrices, n, r, n + 16, r + 16, -2130706433, -2130706433);
                    RenderSystem.colorMask(true, true, true, true);
                    RenderSystem.enableDepthTest();
                }
            }
        }
    }


    @Inject(method = "onMouseClick", at = @At("HEAD"), cancellable = true)
    public void mechanix$onUpgradeSlotClicked(Slot slot, int invSlot, int clickData, SlotActionType actionType, CallbackInfo ci) {
        if (handler instanceof UpgradeHandler && slot != null && ((UpgradeHandler) handler).upgradeSlots.stream().anyMatch(i -> i == slot)) {
            invSlot = slot.id;
            PlayerEntity player = MinecraftClient.getInstance().player;
            assert player != null;
            short s = player.currentScreenHandler.getNextActionId(player.inventory);
            ItemStack itemStack = ((UpgradeHandler) player.currentScreenHandler).onUpgradeSlotClick(invSlot, clickData, actionType, player);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeByte(player.currentScreenHandler.syncId);
            buf.writeShort(invSlot);
            buf.writeByte(clickData);
            buf.writeShort(s);
            buf.writeEnumConstant(actionType);
            buf.writeItemStack(itemStack);
            ClientPlayNetworking.send(UpgradePackets.CLICK_SLOT_C2S_PACKET, buf);
            ci.cancel();
        }
    }

    @Override
    public boolean excludeUpgrades() {
        return handler instanceof UpgradeHandler && ((UpgradeHandler) handler).upgradeSlots.size() == 4;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}
