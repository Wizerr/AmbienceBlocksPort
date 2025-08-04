package com.wizerr.ambienceblocks.client.gui.ambience.tabs;

import com.wizerr.ambienceblocks.ambience.AmbienceData;
import com.wizerr.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.wizerr.ambienceblocks.client.gui.widgets.CheckboxWidget;
import com.wizerr.ambienceblocks.client.gui.widgets.TextInstance;
import com.wizerr.ambienceblocks.util.ParsingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class DelayTab extends AbstractTab {
    private static final String MIN_DELAY = I18n.get("ui.ambienceblocks.min") + " " + I18n.get("ui.ambienceblocks.delay");
    private static final String MAX_DELAY = I18n.get("ui.ambienceblocks.max") + " " + I18n.get("ui.ambienceblocks.delay");

    private static final String MIN_VOLUME = I18n.get("ui.ambienceblocks.min") + " " + I18n.get("ui.ambienceblocks.volume");
    private static final String MAX_VOLUME = I18n.get("ui.ambienceblocks.max") + " " + I18n.get("ui.ambienceblocks.volume");

    private static final String MIN_PITCH = I18n.get("ui.ambienceblocks.min") + " " + I18n.get("ui.ambienceblocks.pitch");
    private static final String MAX_PITCH = I18n.get("ui.ambienceblocks.max") + " " + I18n.get("ui.ambienceblocks.pitch");

    public TextInstance textMinDelay = new TextInstance(getBaseX(), getRowY(0) + getOffsetFontY(), 0xFFFFFF, MIN_DELAY + " :", font);
    public EditBox minDelay = new EditBox(font, getNeighbourX(textMinDelay), getRowY(0), 50, 20, Component.empty());

    public TextInstance textMaxDelay = new TextInstance(getNeighbourX(minDelay), getRowY(0) + getOffsetFontY(), 0xFFFFFF, MAX_DELAY + " :", font);
    public EditBox maxDelay = new EditBox(font, getNeighbourX(textMaxDelay), getRowY(0), 50, 20, Component.empty());

    public TextInstance textMinVolume = new TextInstance(0, 0, 0xFFFFFF, MIN_VOLUME + " :", font);
    public EditBox minVolume = new EditBox(font, 0, 0, 50, 20, Component.empty());

    public TextInstance textMaxVolume = new TextInstance(0, 0, 0xFFFFFF, MAX_VOLUME + " :", font);
    public EditBox maxVolume = new EditBox(font, 0, 0, 50, 20, Component.empty());

    public TextInstance textMinPitch = new TextInstance(0, 0, 0xFFFFFF, MIN_PITCH + " :", font);
    public EditBox minPitch = new EditBox(font, 0, 0, 50, 20, Component.empty());

    public TextInstance textMaxPitch = new TextInstance(0, 0, 0xFFFFFF, MAX_PITCH + " :", font);
    public EditBox maxPitch = new EditBox(font, 0, 0, 50, 20, Component.empty());


    public CheckboxWidget canPlayOverSelf = new CheckboxWidget(getBaseX(), getRowY(3), 20 + font.width("Plays over itself"), 20, "Plays over itself", false);
    public CheckboxWidget shouldStopPrevious = new CheckboxWidget(getBaseX(), getRowY(4), 20 + font.width("Stop previous"), 20, "Stop previous", false);

    public DelayTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Delay";
    }

    @Override
    public String getShortName() {
        return "Dly";
    }

    @Override
    public void initialInit() {
        minDelay.setFilter(ParsingUtil.numberFilter);
        minDelay.setMaxLength(8);
        minDelay.setValue(String.valueOf(0));
        maxDelay.setFilter(ParsingUtil.numberFilter);
        maxDelay.setMaxLength(8);
        maxDelay.setValue(String.valueOf(0));

        minVolume.setFilter(ParsingUtil.decimalNumberFilter);
        minVolume.setMaxLength(8);
        minVolume.setValue(String.valueOf(0));
        maxVolume.setFilter(ParsingUtil.decimalNumberFilter);
        maxVolume.setMaxLength(8);
        maxVolume.setValue(String.valueOf(0));

        minPitch.setFilter(ParsingUtil.decimalNumberFilter);
        minPitch.setMaxLength(8);
        minPitch.setValue(String.valueOf(0));
        maxPitch.setFilter(ParsingUtil.decimalNumberFilter);
        maxPitch.setMaxLength(8);
        maxPitch.setValue(String.valueOf(0));

        shouldStopPrevious.active = false;
        shouldStopPrevious.visible = false;

        addWidget(minDelay);
        addWidget(maxDelay);

        addWidget(minVolume);
        addWidget(maxVolume);

        addWidget(minPitch);
        addWidget(maxPitch);

        addWidget(canPlayOverSelf);
        addWidget(shouldStopPrevious);
    }

    @Override
    public void updateWidgetPosition() {
        textMinDelay.setPosition(getBaseX(), getRowY(0) + getOffsetFontY());
        minDelay.setPosition(getNeighbourX(textMinDelay), getRowY(0));
        textMaxDelay.setPosition(getNeighbourX(minDelay), getRowY(0) + getOffsetFontY());
        maxDelay.setPosition(getNeighbourX(textMaxDelay), getRowY(0));

        textMinVolume.setPosition(getBaseX(), getRowY(1) + getOffsetFontY());
        minVolume.setPosition(getNeighbourX(textMinVolume), getRowY(1));
        textMaxVolume.setPosition(getNeighbourX(minVolume), getRowY(1) + getOffsetFontY());
        maxVolume.setPosition(getNeighbourX(textMaxVolume), getRowY(1));

        textMinPitch.setPosition(getBaseX(), getRowY(2) + getOffsetFontY());
        minPitch.setPosition(getNeighbourX(textMinPitch), getRowY(2));
        textMaxPitch.setPosition(getNeighbourX(minPitch), getRowY(2) + getOffsetFontY());
        maxPitch.setPosition(getNeighbourX(textMaxPitch), getRowY(2));


        canPlayOverSelf.setPosition(getBaseX(), getRowY(3));
        shouldStopPrevious.setPosition(getNeighbourX(canPlayOverSelf), getRowY(3));
    }

    @Override
    public void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks) {
        textMinDelay.render(matrix, mouseX, mouseY);
        minDelay.render(matrix, mouseX, mouseY, partialTicks);

        textMaxDelay.render(matrix, mouseX, mouseY);
        maxDelay.render(matrix, mouseX, mouseY, partialTicks);

        textMinVolume.render(matrix, mouseX, mouseY);
        minVolume.render(matrix, mouseX, mouseY, partialTicks);
        textMaxVolume.render(matrix, mouseX, mouseY);
        maxVolume.render(matrix, mouseX, mouseY, partialTicks);
        textMinPitch.render(matrix, mouseX, mouseY);
        minPitch.render(matrix, mouseX, mouseY, partialTicks);
        textMaxPitch.render(matrix, mouseX, mouseY);
        maxPitch.render(matrix, mouseX, mouseY, partialTicks);

        canPlayOverSelf.render(matrix, mouseX, mouseY, partialTicks);
        shouldStopPrevious.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(GuiGraphics matrix, int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(textMinDelay.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Minimum Delay");
            list.add("Should be smaller than max.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textMaxDelay.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Maximum Delay");
            list.add("Ticking for the delay will keep going as long as the block is loaded, so don't hesitate making this long.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }

        if(textMinVolume.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Minimum Volume");
            list.add("If the volume is random between two bounds then the lower bound is <tile's volume> - <value>");
            list.add(ChatFormatting.GRAY + "0 on both min and max means that the sound won't have a random volume.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textMaxVolume.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Maximum Volume");
            list.add("If the volume is random between two bounds then the upper bound is <tile's volume> + <value>");
            list.add(ChatFormatting.GRAY + "0 on both min and max means that the sound won't have a random volume.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }

        if(textMinPitch.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Minimum Pitch");
            list.add("If the pitch is random between two bounds then the lower bound is <tile's pitch> - <value>");
            list.add(ChatFormatting.GRAY + "0 on both min and max means that the sound won't have a random pitch.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textMaxPitch.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Maximum Pitch");
            list.add("If the pitch is random between two bounds then the upper bound is <tile's pitch> + <value>");
            list.add(ChatFormatting.GRAY + "0 on both min and max means that the sound won't have a random pitch.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }

        if(canPlayOverSelf.isHoveredOrFocused()) {
            list.add("If this block's delay reaches the end before a previous sound finishes playing, it won't care and play anyway when this is checked.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(shouldStopPrevious.isHoveredOrFocused()) {
            list.add("If this block plays over a previous sound it will stop the previous instance and play the new one.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
    }

    @Override
    public void tick() {
        minDelay.tick();
        maxDelay.tick();

        minVolume.tick();
        maxVolume.tick();

        minPitch.tick();
        maxPitch.tick();

        shouldStopPrevious.active = canPlayOverSelf.isChecked();
        shouldStopPrevious.visible = canPlayOverSelf.isChecked();
    }

    @Override
    public void setFieldFromData(AmbienceData data) {
        minDelay.setValue(String.valueOf(data.getMinDelay()));
        maxDelay.setValue(String.valueOf(data.getMaxDelay()));

        minVolume.setValue(String.valueOf(data.getMinRandVolume()));
        maxVolume.setValue(String.valueOf(data.getMaxRandVolume()));

        minPitch.setValue(String.valueOf(data.getMinRandPitch()));
        maxPitch.setValue(String.valueOf(data.getMaxRandPitch()));

        canPlayOverSelf.setChecked(data.canPlayOverSelf());
        shouldStopPrevious.setChecked(data.shouldStopPrevious());
    }

    @Override
    public void setDataFromField(AmbienceData data) {
        data.setMinDelay(ParsingUtil.tryParseInt(minDelay.getValue()));
        data.setMaxDelay(ParsingUtil.tryParseInt(maxDelay.getValue()));

        data.setMinRandVolume(ParsingUtil.tryParseFloat(minVolume.getValue()));
        data.setMaxRandVolume(ParsingUtil.tryParseFloat(maxVolume.getValue()));

        data.setMinRandPitch(ParsingUtil.tryParseFloat(minPitch.getValue()));
        data.setMaxRandPitch(ParsingUtil.tryParseFloat(maxPitch.getValue()));

        data.setCanPlayOverSelf(canPlayOverSelf.isChecked());
        if(data.canPlayOverSelf()) data.setShouldStopPrevious(shouldStopPrevious.isChecked());
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }
}
