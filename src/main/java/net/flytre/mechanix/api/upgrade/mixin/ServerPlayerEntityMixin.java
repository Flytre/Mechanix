package net.flytre.mechanix.api.upgrade.mixin;


import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.flytre_lib.common.util.PacketUtils;
import net.flytre.mechanix.api.fluid.screen.FluidHandler;
import net.flytre.mechanix.api.upgrade.UpgradeHandler;
import net.flytre.mechanix.api.upgrade.UpgradeHandlerListener;
import net.flytre.mechanix.api.upgrade.UpgradePackets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements UpgradeHandlerListener {
    @Shadow
    public ServerPlayNetworkHandler networkHandler;

    @Shadow
    public boolean skipPacketSlotUpdates;


    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }


    @Override
    public void onHandlerRegistered(UpgradeHandler handler, DefaultedList<ItemStack> stacks) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(handler.syncId);
        PacketUtils.toPacket(buf, stacks, (stack, packet) -> packet.writeItemStack(stack));
        ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, UpgradePackets.INVENTORY_S2C_PACKET, buf);
        this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
    }


    @Override
    public void onSlotUpdate(UpgradeHandler handler, int slotId, ItemStack stack) {
        if (!this.skipPacketSlotUpdates) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(handler.syncId);
            buf.writeInt(slotId);
            buf.writeItemStack(stack);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, UpgradePackets.SLOT_UPDATE_S2C_PACKET, buf);
        }
    }

    @Inject(method = "refreshScreenHandler", at = @At("TAIL"))
    public void mechanix$refreshScreenHandler(ScreenHandler handler, CallbackInfo ci) {
        if (handler instanceof UpgradeHandler)
            this.onHandlerRegistered((UpgradeHandler) handler, ((UpgradeHandler) handler).getUpgradeStacks());
    }
}
