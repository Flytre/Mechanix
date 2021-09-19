package net.flytre.mechanix.block.item_collector;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.flytre_lib.client.gui.CoordinateProvider;
import net.flytre.flytre_lib.client.gui.ToggleButton;
import net.flytre.flytre_lib.common.inventory.filter.Filtered;
import net.flytre.flytre_lib.common.inventory.filter.FilteredScreen;
import net.flytre.mechanix.util.Packets;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class ItemCollectorScreen extends FilteredScreen<ItemCollectorHandler> {

    protected final ItemCollectorHandler handler;
    private final Identifier background;
    private boolean synced;


    public ItemCollectorScreen(ItemCollectorHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        this.background = new Identifier("mechanix:textures/gui/container/item_collector.png");
        synced = false;
    }

    private void onSynced() {
        addButton(handler.getFilterType(), 0, MODE_BUTTON, Filtered.BLOCK_FILTER_MODE, handler::getPos, new TranslatableText("block.mechanix.fluid_pipe.whitelist"), new TranslatableText("block.mechanix.fluid_pipe.blacklist"));
        addButton(handler.getModMatch(), 1, MOD_BUTTON, Filtered.BLOCK_MOD_MATCH, handler::getPos, new TranslatableText("block.mechanix.fluid_pipe.mod_match.false"), new TranslatableText("block.mechanix.fluid_pipe.mod_match.true"));
        addButton(handler.getNbtMatch(), 2, NBT_BUTTON, Filtered.BLOCK_NBT_MATCH, handler::getPos, new TranslatableText("block.mechanix.item_collector.nbt_match.false"), new TranslatableText("block.mechanix.item_collector.nbt_match.true"));
    }

    @Override
    public void tick() {
        super.tick();

        if (!synced && handler.getSynced()) {
            onSynced();
            synced = true;
        }
    }

    @Override
    protected void init() {
        synced = false;
        super.init();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.client != null;

        this.client.getTextureManager().bindTexture(this.background);
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}