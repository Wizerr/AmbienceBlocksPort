package com.wizerr.ambienceblocks.client.gui.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TextInstance extends AbstractWidget {
    public int color;
    public String text;

    public boolean active;

    private Font font;

    public TextInstance(int x, int y, int color, String text, Font font) {
        super(x, y, 0, 0, Component.literal(text));

        this.setX(x);
        this.setY(y);
        this.color = color;
        this.text = text;

        this.width = font.width(text);
        this.height = font.lineHeight;

        this.font = font;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void render(GuiGraphics matrix, int mouseX, int mouseY) {
        if(this.visible && text != null)
            matrix.drawString(font, text, getX(), getY(), color);

        this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        render(matrixStack, mouseX, mouseY);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible && text != null) {
            guiGraphics.drawString(font, text, getX(), getY(), color);
        }

        this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
    }

    @Override
    public int getWidth() {
        return font.width(text);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narration) {
    }
}
