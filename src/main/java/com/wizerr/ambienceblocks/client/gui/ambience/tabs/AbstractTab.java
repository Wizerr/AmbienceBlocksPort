package com.wizerr.ambienceblocks.client.gui.ambience.tabs;

import com.wizerr.ambienceblocks.ambience.AmbienceData;
import com.wizerr.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.wizerr.ambienceblocks.client.gui.widgets.ScrollListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractTab {
    protected Font font;
    protected AmbienceGUI guiRef;
    protected int x, y, width, height;

    private boolean active;
    public final List<AbstractWidget> widgets = new ArrayList<>();
    public final List<AbstractWidget> buttons = new ArrayList<>();

    protected static final int horizontalSeparation = 6;
    protected static final int verticalSeparation = 8;
    protected static final int rowHeight = 18;
    protected static final int checkboxOffset = 4;

    public AbstractTab(AmbienceGUI guiRef) {
        font = Minecraft.getInstance().font;
        this.guiRef = guiRef;
        updateMetaValues(guiRef);
    }

    public void refreshWidgets() {
        for(AbstractWidget widget : widgets) {
            if(!guiRef.children().contains(widget)) {
                guiRef.addCustomWidget(widget);

                if(widget instanceof ScrollListWidget) {
                    ((ScrollListWidget) widget).addWidget(guiRef);
                }
            }
        }
        for(AbstractWidget widget : buttons) {
            if(!guiRef.children().contains(widget)) {
                guiRef.addCustomWidget(widget);
            }
        }
    }

    public void updateMetaValues(AmbienceGUI guiRef) {
        this.x = guiRef.xTopLeft;
        this.y = guiRef.yTopLeft;
        this.width = AmbienceGUI.texWidth;
        this.height = AmbienceGUI.texHeight;
    }

    public void addWidget(AbstractWidget widget) {
        guiRef.addCustomWidget(widget);
        widgets.add(widget);
    }

    public void addButton(AbstractWidget widget) {
        guiRef.addCustomWidget(widget);
        buttons.add(widget);
    }

    public abstract String getName();

    public abstract String getShortName();

    public abstract void initialInit();

    public abstract void updateWidgetPosition();

    public abstract void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks);

    public abstract void renderToolTip(GuiGraphics matrix, int mouseX, int mouseY);

    public abstract void tick();

    /**
     * @param data to pass into the gui from the tile.
     */
    public abstract void setFieldFromData(AmbienceData data);
    /**
     * @param data to pass into the tile from the gui.
     */
    public abstract void setDataFromField(AmbienceData data);

    public void activate() {
        active = true;
        for(AbstractWidget widget : widgets) {
            if(widget != null) {
                widget.visible = true;
                widget.active = true;
            }
        }
        for(AbstractWidget widget : buttons) {
            if(widget != null) {
                widget.visible = true;
                widget.active = true;
            }
        }
        onActivate();
    }

    public abstract void onActivate();

    public void deactivate() {
        active = false;
        for(AbstractWidget widget : widgets) {
            if(widget != null) {
                widget.visible = false;
                widget.active = false;
            }
        }
        for(AbstractWidget widget : buttons) {
            if(widget != null) {
                widget.visible = false;
                widget.active = false;
            }
        }
        onDeactivate();
    }

    public abstract void onDeactivate();

    public boolean isActive() {
        return active;
    }

    //util stuff
    protected int getBaseX() {
        return x + horizontalSeparation;
    }

    protected int getEndX() {
        return x + width - horizontalSeparation;
    }

    protected int getEndY() {
        return y + height - verticalSeparation;
    }

    protected int getNeighbourX(AbstractWidget widget) {
        return widget.getX() + widget.getWidth() + horizontalSeparation;
    }

    protected int getRowY(int row) {
        return y + rowHeight * row + verticalSeparation * (row + 1) + AmbienceGUI.tabHeight;
    }

    protected int getOffsetY(int height) {
        return (rowHeight - height) / 2 + 1;
    }

    protected int getOffsetFontY() {
        return (rowHeight - font.lineHeight) / 2 + 1;
    }

    public void drawHoveringText(GuiGraphics matrix, List<String> textLines,
                                 int mouseX, int mouseY,
                                 int screenWidth, int screenHeight,
                                 int maxTextWidth) {
        List<Component> textComponentList = new ArrayList<>();
        for (String textLine : textLines) {
            textComponentList.add(Component.literal(textLine));
        }

        matrix.renderComponentTooltip(this.font, textComponentList, mouseX, mouseY);
    }

}
