package net.flytre.mechanix.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ToggleButton extends ButtonWidget {

    private final int textureWidth;
    private final int textureHeight;
    private final Identifier texture;
    private int frame;
    private final char id;


    public ToggleButton(int x, int y, int width, int height, int state, Identifier texture, PressAction onPress, char id) {
        super(x, y, width, height, LiteralText.EMPTY, onPress);
        this.textureHeight = height * 4;
        this.textureWidth = width;
        this.texture = texture;
        this.frame = state * 2;
        this.id = id;
    }


    public boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }


    public void toggleState() {
        this.frame += 2;
        this.frame %= 4;
    }

    public int getState() {
        return frame < 2 ? 0 : 1;
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (MinecraftClient.getInstance() == null)
            return;

        MinecraftClient mc = MinecraftClient.getInstance();
        mc.getTextureManager().bindTexture(texture);
        this.hovered = isHovering(mouseX, mouseY);
        int y = this.height * frame;
        if (hovered)
            y += this.height;

        drawTexture(matrices, x, this.y, 0, y, width, height, textureWidth, textureHeight);

        TextRenderer renderer = mc.textRenderer;
        OrderedText orderedText =  Text.of(id + "").asOrderedText();
        renderer.draw(matrices, orderedText, (float)(x + width/2 - renderer.getWidth(orderedText) / 2) + 0.5f, (float)this.y + 4.5f, 0);

        if (isHovering(mouseX, mouseY))
            renderToolTip(matrices, mouseX, mouseY);
    }


}

