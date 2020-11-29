package net.flytre.mechanix.base.gui;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

@FunctionalInterface
public interface ButtonTooltipRenderer {

    void draw(MatrixStack matrices, List<Text> lines, int x, int y);
}
