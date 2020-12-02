package net.flytre.mechanix.api.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collections;

/**
 * A toggle button takes a 16x64 image and displays 4 states from top to bottom: (state 0, state 0 hovered, state 1,
 * state 1 hovered) based on the circumstances. Predictably used to toggle between 2 states, ie input and output or
 * round robin mode and normal mode.
 */
public class ToggleButton extends ButtonWidget {

    private final int textureWidth;
    private final int textureHeight;
    private final Identifier texture;
    private int frame;
    private final String id;
    private Text[] tooltips;
    private ButtonTooltipRenderer tooltipRenderer;


    /**
     * Instantiates a new Toggle button.
     *
     * @param x       the x
     * @param y       the y
     * @param width   the width
     * @param height  the height
     * @param state   0 or 1 for 1st / 2nd frame
     * @param texture the texture location
     * @param onPress what to do when the button is pressed
     * @param id      text to overlay on button
     */
    public ToggleButton(int x, int y, int width, int height, int state, Identifier texture, PressAction onPress, char id) {
        this(x,y,width,height,state,texture,onPress,id + "");
    }

    /**
     * Instantiates a new Toggle button.
     *
     * @param x       the x
     * @param y       the y
     * @param width   the width
     * @param height  the height
     * @param state   0 or 1 for 1st / 2nd frame
     * @param texture the texture location
     * @param onPress what to do when the button is pressed
     * @param id      text to overlay on button
     */
    public ToggleButton(int x, int y, int width, int height, int state, Identifier texture, PressAction onPress, String id) {
        super(x, y, width, height, LiteralText.EMPTY, onPress);
        this.textureHeight = height * 4;
        this.textureWidth = width;
        this.texture = texture;
        this.frame = state * 2;
        this.id = id;
    }


    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }


    /**
     * Sets tooltips, make sure you set the renderer. See the ButtonTooltipRenderer for the easiest way
     * to do this
     *
     * @param frame1 the frame 1
     * @param frame2 the frame 2
     */
    public void setTooltips(Text frame1, Text frame2) {
        this.tooltips = new Text[]{frame1,frame2};
    }

    /**
     * Gets the tooltip renderer.
     *
     * @return the tooltip renderer
     */
    public ButtonTooltipRenderer getTooltipRenderer() {
        return tooltipRenderer;
    }

    /**
     * Sets the tooltip renderer. See ButtonTooltipRenderer's documentation.
     *
     * @param tooltipRenderer the tooltip renderer
     */
    public void setTooltipRenderer(ButtonTooltipRenderer tooltipRenderer) {
        this.tooltipRenderer = tooltipRenderer;
    }


    /**
     * Flip between state 0 / 1
     */
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

    /**
     * Gets the state (0 for frame 1, 1 for frme 2).
     *
     * @return the state
     */
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

