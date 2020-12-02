package net.flytre.mechanix.api.gui;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Used to render the tooltip for a button. Just replace references of this with ScreenClass::renderTooltip
 */
@FunctionalInterface
public interface ButtonTooltipRenderer {

    void draw(MatrixStack matrices, List<Text> lines, int x, int y);
}
