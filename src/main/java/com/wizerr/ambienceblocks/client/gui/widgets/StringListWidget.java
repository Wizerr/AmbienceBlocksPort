package com.wizerr.ambienceblocks.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
    public class StringListWidget extends AbstractWidget {
    protected final IPressable onPress;
    private final Font font;

    public ArrayList<String> list = new ArrayList<String>();

    public int selectionIndex = 0;
    public float scrollIndex;

    int separation;
    int optionHeight;

    public StringListWidget(int xIn, int yIn, int widthIn, int heightIn, int separation, int optionHeight, Font font, IPressable onPress) {
        super(xIn, yIn, widthIn, heightIn, Component.literal(""));
        this.separation = separation;
        if(optionHeight < font.lineHeight) optionHeight = font.lineHeight;
        this.optionHeight = optionHeight;
        this.font = font;
        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(GuiGraphics matrix, int mouseX, int mouseY, float partialTick) {
        this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

        if(!this.visible)
            return;

        renderGeneric(matrix, getX() - 1, getY() - 1, width + 2, height + 2, 0.62745f, 0.62745f, 0.62745f, 1f);

        renderBackground(matrix);

        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        RenderSystem.enableScissor((int)(this.getX()  * scale), (int)(Minecraft.getInstance().getWindow().getHeight() - ((this.getY() + this.height) * scale)),
                (int)(width * scale), (int)(height * scale));


        for(int i = 0; i < list.size(); i++) {
            if(selectionIndex == i)
                renderGeneric(matrix, getElementX(), getElementY(i), getElementWidth(), getElementHeight(), 0.8f, 0.8f, 0f, 1f);
            else
                renderGeneric(matrix, getElementX(), getElementY(i), getElementWidth(), getElementHeight(), 1f, 1f, 1f, 1f);
            renderGeneric(matrix, getElementX() + 1, getElementY(i) + 1, getElementWidth() - 2, getElementHeight() - 2, (1f - i/((float)list.size() - 1)) * 0.8f, i/((float)list.size() - 1) * 0.8f,  0.8f, 1f);
            drawElementString(matrix, i);
        }

        renderGeneric(matrix, getX() + width - 8, getY(), 8, height, 0.5f, 0.5f, 0.5f, 0.5f);
        renderGeneric(matrix, getScrollBarX(), getScrollBarY(), getScrollBarWidth(), getScrollBarHeight(), 0.9f, 0.9f, 0.9f, 1f);

        RenderSystem.disableScissor();
    }

    private void drawElementString(GuiGraphics matrix, int index) {
        int color = 0xFFFFFF;
        if(selectionIndex == index) color = 0xFFFF00;
        matrix.drawString(font, list.get(index),
                getElementX() + separation,
                getElementY(index) + getyFontOffset(),
                color, true);
    }

    public String getElementString() {
        return list.get(selectionIndex);
    }

    private int getElementX() {
        return getX() + separation;
    }

    private int getElementY(int index) {
        return (int) (getY() + separation + (optionHeight + separation) * index + getScrollOffsetY());
    }

    private int getElementWidth() {
        return width - separation * 2;
    }

    private int getElementHeight() {
        return optionHeight;
    }

    public boolean mouseScrolled(double x, double y, double amount) {
        if (amount > 1.0D) {
            amount = 1.0D;
        }

        if (amount < -1.0D) {
            amount = -1.0D;
        }

        addScroll(-amount);

        return true;
    }

    public void resetScroll() {
        scrollIndex = 0;
    }

    private void addScroll(double amount) {
        amount = (optionHeight / ((double) getTotalListHeight() - height)) * amount;
        amount *= 2D;
        if(scrollIndex + amount < 0f) {
            scrollIndex = 0f;
            return;
        }

        if(scrollIndex + amount > 1f) {
            scrollIndex = 1f;
            return;
        }

        scrollIndex += amount;
    }

    private void setScroll(float value) {
        scrollIndex = value;
        if(scrollIndex > 1f) scrollIndex = 1f;
        if(scrollIndex < 0f) scrollIndex = 0f;
    }

    private int getScrollBarX() {
        return getX() + width - getScrollBarWidth() - 1;
    }

    private int getScrollBarY() {
        return (int) (getY() + (height - getScrollBarHeight()) * scrollIndex);
    }

    private int getScrollBarWidth() {
        return 6;
    }

    private int getScrollBarHeight() {
        int value = 1;
        if(getTotalListHeight() < height)
            value = height;
        else
            value = (int) ((height / (float) getTotalListHeight()) * (float) height);

        if(value < 1) value = 1;
        return value;
    }

    private float getScrollOffsetY() {
        if(getTotalListHeight() < height)
            return 0;
        else
            return (getTotalListHeight() - height) * -scrollIndex;
    }

    private int getTotalListHeight() {
        return separation + list.size() * (optionHeight + separation);
    }

    private void renderBackground(GuiGraphics stack) {

        renderGeneric(stack, getX(), getY(), width, height, 0f, 0f, 0f, 1f);
    }

    private void renderGeneric(GuiGraphics graphics, int x, int y, int width, int height,
                               float red, float green, float blue, float alpha) {
        int color = ((int)(alpha * 255) << 24) |
                ((int)(red * 255) << 16) |
                ((int)(green * 255) << 8) |
                (int)(blue * 255);
        graphics.fill(x, y, x + width, y + height, color);
    }


    private float getyFontOffset() {
        return (optionHeight - font.lineHeight) / 2.0f;
    }

    public void addElement(String string) {
        list.add(string);
    }

    public void addElements(List<String> strings) {
        list.addAll(strings);
    }

    public void clearList() {
        list.clear();
    }

    public void clickElement(double pX, double pY) {
        if(pX > getElementX() && pX < getElementX() + getElementWidth()) {
            for (int i = 0; i < list.size(); i++) {
                if (pY > getElementY(i) && pY < getElementY(i) + getElementHeight()) {
                    playDownSound(Minecraft.getInstance().getSoundManager());

                    boolean onClick = false;
                    boolean onDoubleClick = false;

                    if(onPress != null) {
                        if(getSelectionIndex() != i)
                            onClick = true;
                        else
                            onDoubleClick = true;
                    }
                    setSelectionIndex(i);

                    if(onClick)
                        onPress.onClick(this, i, list.get(i));
                    if(onDoubleClick)
                        onPress.onDoubleClick(this, i, list.get(i));

                    return;
                }
            }
        }
    }

    public int getAmountOfElements() {
        return list.size();
    }

    public String getSelectionContent() {
        return list.get(selectionIndex);
    }

    public int getSelectionIndex() {
        return selectionIndex;
    }

    public void setSelectionIndex(int selectionIndex) {
        this.selectionIndex = selectionIndex;
    }

    public void setSelectionIndexToLast() {
        this.selectionIndex = list.size() - 1;
    }

    public void setSelectionByString(String name) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(name)) {
                setSelectionIndex(i);
                setScroll(0f);
                setScroll((float) ((getElementY(i) - getY() - separation) / ((double) getTotalListHeight() - height)));
            }
        }
    }

    @Override
    public boolean mouseClicked(double pX, double pY, int pType) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(pType)) {
                boolean flag = this.clicked(pX, pY);
                if (flag) {
                    clickElement(pX, pY);
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("narration.string_list"));
    }

    @OnlyIn(Dist.CLIENT)
    public interface IPressable {
        void onClick(StringListWidget list, int index, String name);
        void onDoubleClick(StringListWidget list, int index, String name);
    }
}
