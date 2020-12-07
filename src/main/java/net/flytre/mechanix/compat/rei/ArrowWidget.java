package net.flytre.mechanix.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.REIHelper;
import me.shedaniel.rei.gui.widget.WidgetWithBounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ArrowWidget extends WidgetWithBounds {
    private int x;
    private int y;
    private double time = 2000.0D;
    private boolean animated;

    public ArrowWidget(int x, int y, boolean animated) {
        this.x = x;
        this.y = y;
        this.animated = animated;
    }

    public ArrowWidget(int x, int y, boolean animated, double time) {
        this(x,y,animated);
        this.time = time * 50; //1 tick = 20 ms
    }


    public static ArrowWidget create(Point point, boolean animated) {
        return new ArrowWidget(point.x, point.y, animated);
    }

    public static ArrowWidget create(Point point, boolean animated, double time) {
        return new ArrowWidget(point.x, point.y, animated, time);
    }


    public ArrowWidget time(double time) {
        this.time = time;
        return this;
    }

    @NotNull
    public Rectangle getBounds() {
        return new Rectangle(this.x, this.y, 24, 17);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(REIHelper.getInstance().getDefaultDisplayTexture());
        this.drawTexture(matrices, this.x, this.y, 106, 91, 24, 17);
        if (this.animated) {
            int width = MathHelper.ceil((double)System.currentTimeMillis() / (this.time / 24.0D) % 24.0D / 1.0D);
            this.drawTexture(matrices, this.x, this.y, 82, 91, width, 17);
        }

    }

    public List<? extends Element> children() {
        return Collections.emptyList();
    }
}


