package net.flytre.mechanix.base.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collections;

public class ToggleButton extends ButtonWidget {

    private final int textureWidth;
    private final int textureHeight;
    private final Identifier texture;
    private int frame;
    private final String id;
    private Text[] tooltips;
    private ButtonTooltipRenderer tooltipRenderer;


    public ToggleButton(int x, int y, int width, int height, int state, Identifier texture, PressAction onPress, char id) {
        this(x,y,width,height,state,texture,onPress,id + "");
    }

    public ToggleButton(int x, int y, int width, int height, int state, Identifier texture, PressAction onPress, String id) {
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


    public void setTooltips(Text frame1, Text frame2) {
        this.tooltips = new Text[]{frame1,frame2};
    }

    public ButtonTooltipRenderer getTooltipRenderer() {
        return tooltipRenderer;
    }

    public void setTooltipRenderer(ButtonTooltipRenderer tooltipRenderer) {
        this.tooltipRenderer = tooltipRenderer;
    }


    public void toggleState() {
        this.frame += 2;
        this.frame %= 4;
    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {

        if(tooltips == null)
            return;

        if (getState() == 0)
            getTooltipRenderer().draw(matrices, Collections.singletonList(tooltips[0]),mouseX,mouseY);
        else
            getTooltipRenderer().draw(matrices, Collections.singletonList(tooltips[1]),mouseX,mouseY);

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

