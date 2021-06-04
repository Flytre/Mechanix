package net.flytre.mechanix.api.fluid.mixin;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.flytre_lib.common.util.PacketUtils;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.fluid.screen.FluidHandler;
import net.flytre.mechanix.api.fluid.screen.FluidHandlerListener;
import net.flytre.mechanix.api.fluid.screen.FluidPackets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements FluidHandlerListener {
    @Shadow
    public ServerPlayNetworkHandler networkHandler;

    @Shadow
    public boolean skipPacketSlotUpdates;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Override
    public void onHandlerRegistered(FluidHandler handler, DefaultedList<FluidStack> stacks) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(handler.syncId);
        PacketUtils.toPacket(buf, stacks, FluidStack::toPacket);
        ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, FluidPackets.INVENTORY_S2C_PACKET, buf);
        this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
    }

    @Override
    public void onSlotUpdate(FluidHandler handler, int slotId, FluidStack stack) {
        if (!this.skipPacketSlotUpdates) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(handler.syncId);
            buf.writeInt(slotId);
            stack.toPacket(buf);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, FluidPackets.SLOT_UPDATE_S2C_PACKET, buf);
        }
    }

    @Inject(method = "refreshScreenHandler", at = @At("TAIL"))
    public void mechanix$refreshScreenHandler(ScreenHandler handler, CallbackInfo ci) {
        if (handler instanceof FluidHandler)
            this.onHandlerRegistered((FluidHandler) handler, ((FluidHandler) handler).getFluidStacks());
    }
}
