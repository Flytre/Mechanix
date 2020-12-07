package net.flytre.mechanix.api.gui;


import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public abstract class PanelledScreen<T extends ScreenHandler> extends HandledScreen<T> {
    public PanelledScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
