package net.flytre.mechanix.api.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.flytre.mechanix.api.fluid.FluidHandler;
import net.flytre.mechanix.api.fluid.FluidPackets;
import net.flytre.mechanix.api.fluid.FluidSlot;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;


//FluidHandler::render handled by mixin to prevent messy code
public abstract class FluidHandledScreen<T extends FluidHandler> extends HandledScreen<T> {

    private static final Identifier BCKG = new Identifier("mechanix:textures/gui/container/fluid_cell.png");
    private static final Identifier OVERLAY = new Identifier("mechanix:textures/gui/container/fluid_cell_overlay.png");
    public FluidSlot focusedFluidSlot; //UNUSED

    public FluidHandledScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }


    public void drawFluidSlot(MatrixStack matrices, FluidSlot slot) {

        if (!slot.compact) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            minecraftClient.getTextureManager().bindTexture(BCKG);
            RenderSystem.enableDepthTest();
            drawTexture(matrices, slot.x, slot.y, 0, 0, 30, 60, 30, 60);

            if (slot.getStack().getAmount() > 0 && slot.getCapacity() != 0) {
                double percent = 1.0 - (double) slot.getStack().getAmount() / slot.getCapacity();

                percent = Math.min(0.95, percent);

                Fluid fluid = slot.getStack().getFluid();
                drawFluid(matrices, fluid, (int) Math.ceil(60 * (1 - percent) - 2), slot.x + 1, slot.y + 1, 30 - 2, 58);
//            SimpleFluidRenderer.render(fluid,matrices,x + 1, (int) (y + (height*(percent)) + 1),width - 2, (int) Math.ceil(height*(1 - percent) - 2),getZOffset());
            }

            minecraftClient.getTextureManager().bindTexture(OVERLAY);
            drawTexture(matrices, slot.x, slot.y, 0, 0, 30, 60, 30, 60);
        } else {
            if(!slot.getStack().isEmpty()) {
                Fluid fluid = slot.getStack().getFluid();
                drawFluid(matrices, fluid, 16, slot.x, slot.y, 16, 16);
            }
        }
    }


    //TODO: REFACTOR, COPIED FROM FLUID METER WIDGET
    private void drawFluid(MatrixStack matrixStack, Fluid fluid, int drawHeight, int x, int y, int width, int height) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        y += height;

        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);

        // If registry can't find it, don't render.
        if (handler == null) {
            return;
        }

        final Sprite sprite = handler.getFluidSprites(MinecraftClient.getInstance().world, BlockPos.ORIGIN, fluid.getDefaultState())[0];
        int color = FluidRenderHandlerRegistry.INSTANCE.get(fluid).getFluidColor(MinecraftClient.getInstance().world, BlockPos.ORIGIN, fluid.getDefaultState());

        final int iconHeight = sprite.getHeight();
        int offsetHeight = drawHeight;

        RenderSystem.color3f((color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F);

        int iteration = 0;
        while (offsetHeight != 0) {
            final int curHeight = Math.min(offsetHeight, iconHeight);

            DrawableHelper.drawSprite(matrixStack, x, y - offsetHeight, 0, width, curHeight, sprite);
            offsetHeight -= curHeight;
            iteration++;
            if (iteration > 50) {
                break;
            }
        }
    }


    @Nullable
    private FluidSlot getFluidSlotAt(double xPosition, double yPosition) {
        for (int i = 0; i < this.handler.fluidSlots.size(); ++i) {
            FluidSlot slot = this.handler.fluidSlots.get(i);
            if (this.isPointOverFluidSlot(slot, xPosition, yPosition) && slot.doDrawHoveringEffect()) {
                return slot;
            }
        }

        return null;
    }

    public boolean isPointOverFluidSlot(FluidSlot slot, double pointX, double pointY) {
        return this.isPointWithinBounds(slot.x, slot.y, slot.compact ? 16 : 30, slot.compact ? 16 : 60, pointX, pointY);
    }


    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y) {
        super.drawMouseoverTooltip(matrices, x, y);
        FluidSlot hover = getFluidSlotAt(x, y);
        if (hover != null) {
            renderTooltip(matrices, hover.getStack().toTooltip(true), x, y);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean val = super.mouseClicked(mouseX, mouseY, button);
        FluidSlot slot = getFluidSlotAt(mouseX, mouseY);
        boolean shifted = (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344));
        if (slot != null) {
            this.onMouseClick(slot, slot.id, button, shifted ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP);
        }
        return val;
    }

    protected void onMouseClick(FluidSlot slot, int invSlot, int clickData, SlotActionType actionType) {
        if (slot != null) {
            invSlot = slot.id;
        }

        PlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        short s = player.currentScreenHandler.getNextActionId(player.inventory);
        FluidStack fluidStack = ((FluidHandler) player.currentScreenHandler).onFluidSlotClick(invSlot, clickData, actionType, player);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeByte(player.currentScreenHandler.syncId);
        buf.writeShort(invSlot);
        buf.writeByte(clickData);
        buf.writeShort(s);
        buf.writeEnumConstant(actionType);
        fluidStack.toPacket(buf);
        ClientPlayNetworking.send(FluidPackets.CLICK_SLOT_C2S_PACKET, buf);
    }
}
