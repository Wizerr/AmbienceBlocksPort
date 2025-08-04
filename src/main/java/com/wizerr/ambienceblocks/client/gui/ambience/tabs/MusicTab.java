package com.wizerr.ambienceblocks.client.gui.ambience.tabs;

import com.wizerr.ambienceblocks.ambience.AmbienceData;
import com.wizerr.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.wizerr.ambienceblocks.client.gui.ambience.ChooseSoundGUI;
import com.wizerr.ambienceblocks.client.gui.widgets.TextInstance;
import com.wizerr.ambienceblocks.util.StaticUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class MusicTab extends AbstractTab {
    public TextInstance textIntroName = new TextInstance(getBaseX(), getRowY(0) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.intro") + " :", font);
    public EditBox introName = new EditBox(font, getNeighbourX(textIntroName), getRowY(0), getEndX() - getNeighbourX(textIntroName) - 20 - horizontalSeparation, 20, Component.empty());
    public Button introButton = Button.builder(Component.literal("..."), button -> {
                Minecraft.getInstance().setScreen(new ChooseSoundGUI(this.guiRef, introName));
            })
            .bounds(getNeighbourX(introName), getRowY(0) + getOffsetY(20), 20, 20)
            .build();

    public TextInstance textOutroName = new TextInstance(getBaseX(), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.outro") + " :", font);
    public EditBox outroName = new EditBox(font, getNeighbourX(textOutroName), getRowY(1), getEndX() - getNeighbourX(textOutroName) - 20 - horizontalSeparation, 20, Component.empty());
    public Button outroButton = Button.builder(Component.literal("..."), button -> {
                Minecraft.getInstance().setScreen(new ChooseSoundGUI(this.guiRef, outroName));
            })
            .bounds(getNeighbourX(outroName), getRowY(1) + getOffsetY(20), 20, 20)
            .build();

    public MusicTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Music";
    }

    @Override
    public String getShortName() {
        return "Mus";
    }

    @Override
    public void initialInit() {
        introName.setMaxLength(StaticUtil.LENGTH_SOUND);
        addWidget(introName);
        addButton(introButton);

        outroName.setMaxLength(StaticUtil.LENGTH_SOUND);
        addWidget(outroName);
        addButton(outroButton);
    }

    @Override
    public void updateWidgetPosition() {
        textIntroName.setPosition(getBaseX(), getRowY(0) + getOffsetFontY());
        introName.setPosition(getNeighbourX(textIntroName), getRowY(0));
        introName.setWidth(getEndX() - getNeighbourX(textIntroName) - 20 - horizontalSeparation);
        introButton.setPosition(getNeighbourX(introName), getRowY(0) + getOffsetY(20));

        textOutroName.setPosition(getBaseX(), getRowY(1) + getOffsetFontY());
        outroName.setPosition(getNeighbourX(textOutroName), getRowY(1));
        outroName.setWidth(getEndX() - getNeighbourX(textOutroName) - 20 - horizontalSeparation);
        outroButton.setPosition(getNeighbourX(outroName), getRowY(1) + getOffsetY(20));
    }

    @Override
    public void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks) {
        textIntroName.render(matrix, mouseX, mouseY);
        introName.render(matrix, mouseX, mouseY, partialTicks);
        introButton.render(matrix, mouseX, mouseY, partialTicks);

        textOutroName.render(matrix, mouseX, mouseY);
        outroName.render(matrix, mouseX, mouseY, partialTicks);
        outroButton.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(GuiGraphics matrix, int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();
    }

    @Override
    public void tick() {

    }

    @Override
    public void setFieldFromData(AmbienceData data) {
        introName.setValue(data.getIntroName());
        outroName.setValue(data.getOutroName());
    }

    @Override
    public void setDataFromField(AmbienceData data) {
        data.setIntroName(introName.getValue());
        data.setOutroName(outroName.getValue());
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }
}