package com.wizerr.ambienceblocks.client.gui.ambience.tabs;

import com.wizerr.ambienceblocks.ambience.AmbienceData;
import com.wizerr.ambienceblocks.client.ambience.AmbienceController;
import com.wizerr.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.wizerr.ambienceblocks.client.gui.widgets.TextInstance;
import com.wizerr.ambienceblocks.client.rendering.RenderingEventHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class MiscTab extends AbstractTab {
    public static final Component ON = Component.literal("ON");
    public static final Component OFF = Component.literal("OFF");

    Button copy = Button.builder(Component.literal("Copy"), button -> {
                AmbienceController.instance.setClipboard(guiRef.getData());
            })
            .bounds(getBaseX(), getRowY(0) + getOffsetY(20), 60, 20)
            .build();

    Button paste = Button.builder(Component.literal("Paste"), button -> {
                guiRef.setData(AmbienceController.instance.getClipboard());
            })
            .bounds(getNeighbourX(copy), getRowY(0) + getOffsetY(20), 60, 20)
            .build();

    TextInstance tDebug = new TextInstance(getBaseX(), getRowY(0) + getOffsetFontY(), 0xFFFFFF, "Debug :", font);
    Button bDebug = Button.builder(AmbienceController.debugMode ? ON : OFF, button -> {
                AmbienceController.debugMode = !AmbienceController.debugMode;
                button.setMessage(AmbienceController.debugMode ? ON : OFF);
                RenderingEventHandler.clearEvent();
            })
            .bounds(getNeighbourX(copy), getRowY(0) + getOffsetY(20), 40, 20)
            .build();

    public MiscTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Misc";
    }

    @Override
    public String getShortName() {
        return "Misc";
    }

    @Override
    public void initialInit() {
        addButton(copy);
        addButton(paste);

        addButton(bDebug);
    }

    @Override
    public void updateWidgetPosition() {
        copy.setPosition(getBaseX(), getRowY(0));
        paste.setPosition(getNeighbourX(copy), getRowY(0));

        tDebug.setPosition(getBaseX(), getRowY(1) + getOffsetFontY());
        bDebug.setPosition(getNeighbourX(tDebug), getRowY(1) + getOffsetY(20));
    }

    @Override
    public void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks) {
        copy.render(matrix, mouseX, mouseY, partialTicks);
        paste.render(matrix, mouseX, mouseY, partialTicks);

        tDebug.render(matrix, mouseX, mouseY, partialTicks);
        bDebug.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(GuiGraphics matrix, int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(copy.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Copy");
            list.add("Copy the current GUIs parameters.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(paste.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Paste");
            list.add("Paste the copied parameters onto this block.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }

        if(bDebug.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Debug");
            list.add("Turns on or off debug mode.");
            list.add("It highlights some GUI elements and will show you details on the main component of the mod.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
    }

    @Override
    public void tick() {
    }

    @Override
    public void setFieldFromData(AmbienceData data) {
        if(AmbienceController.debugMode)
            bDebug.setMessage(ON);
        else
            bDebug.setMessage(OFF);
    }

    @Override
    public void setDataFromField(AmbienceData data) {

    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }
}
