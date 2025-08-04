package com.wizerr.ambienceblocks.client.gui.ambience.tabs;

import com.wizerr.ambienceblocks.ambience.AmbienceData;
import com.wizerr.ambienceblocks.ambience.util.AmbienceType;
import com.wizerr.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.wizerr.ambienceblocks.client.gui.ambience.ChooseSoundGUI;
import com.wizerr.ambienceblocks.client.gui.widgets.CheckboxWidget;
import com.wizerr.ambienceblocks.client.gui.widgets.ScrollListWidget;
import com.wizerr.ambienceblocks.client.gui.widgets.TextInstance;
import com.wizerr.ambienceblocks.util.ParsingUtil;
import com.wizerr.ambienceblocks.util.StaticUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MainTab extends AbstractTab {
    public TextInstance textSoundName = new TextInstance(getBaseX(), getRowY(0) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.music_id") + " :", font);
    public EditBox soundName = new EditBox(font, getNeighbourX(textSoundName), getRowY(0), getEndX() - getNeighbourX(textSoundName) - 20 - horizontalSeparation, 20, Component.literal(""));
    public Button soundButton = Button.builder(Component.literal("..."), button -> {
                Minecraft.getInstance().setScreen(new ChooseSoundGUI(this.guiRef, soundName));
            })
            .bounds(getNeighbourX(soundName), getRowY(0) + getOffsetY(20), 20, 20)
            .build();

    public TextInstance textCategory = new TextInstance(0, 0, 0xFFFFFF, I18n.get("ui.ambienceblocks.category") + " :", font);
    public ScrollListWidget listCategory = new ScrollListWidget(0, 0, 60, 20, 4, 16, 4, StaticUtil.getListOfSoundCategories(), font, new ScrollListWidget.IPressable() {
        @Override
        public void onChange(ScrollListWidget list, int index, String name) {

        }
    });

    public TextInstance textType = new TextInstance(0, 0, 0xFFFFFF, I18n.get("ui.ambienceblocks.type") + " :", font);
    public ScrollListWidget listType = new ScrollListWidget(0, 0, 60, 20, 4, 16, 2, StaticUtil.getListOfAmbienceType(), font, new ScrollListWidget.IPressable() {
        @Override
        public void onChange(ScrollListWidget list, int index, String name) {

        }
    });

    public TextInstance textVolume = new TextInstance(getBaseX(), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.volume") + " :", font);
    public EditBox soundVolume = new EditBox(font, getNeighbourX(textVolume), getRowY(1), 40, 20, Component.empty());

    public TextInstance textPitch = new TextInstance(getNeighbourX(soundVolume), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.pitch") + " :", font);
    public EditBox soundPitch = new EditBox(font, getNeighbourX(textPitch), getRowY(1), 40, 20, Component.empty());

    public TextInstance textFadeIn = new TextInstance(0, 0, 0xFFFFFF, "Fade In :", font);
    public EditBox soundFadeIn = new EditBox(font, 0, 0, 40, 20, Component.empty());

    public TextInstance textFadeOut = new TextInstance(0, 0, 0xFFFFFF, "Fade Out :", font);
    public EditBox soundFadeOut = new EditBox(font, 0, 0, 40, 20, Component.empty());

    public TextInstance textTag = new TextInstance(0, 0, 0xFFFFFF, "Tag :", font);
    public EditBox tag = new EditBox(font, 0, 0, 40, 20, Component.empty());
    //public CheckboxWidget needRedstone = new CheckboxWidget(getBaseX(), getRowY(3), 20 + font.getStringWidth("Needs redstone") + checkboxOffset, 20, "Needs redstone", false);
    public CheckboxWidget usePriority = new CheckboxWidget(0, getRowY(2), 20 + font.width("Priority") + checkboxOffset, 20, "Priority", false);
    public CheckboxWidget useCondition = new CheckboxWidget(getNeighbourX(usePriority), getRowY(2), 20 + font.width("Condition") + checkboxOffset, 20, "Condition", false);
    public CheckboxWidget shouldFuse = new CheckboxWidget(getBaseX(), getRowY(2), 20 + font.width("Fuse") + checkboxOffset, 20, "Fuse", false);
    public CheckboxWidget useDelay = new CheckboxWidget(getBaseX(), getRowY(3), 20 + font.width("Delay") + checkboxOffset, 20, "Delay", false);

    public MainTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Main";
    }

    @Override
    public String getShortName() {
        return "Main";
    }

    @Override
    public void initialInit() {
        soundName.setMaxLength(StaticUtil.LENGTH_SOUND);

        soundVolume.setFilter(ParsingUtil.decimalNumberFilter);
        soundVolume.setMaxLength(6);

        soundPitch.setFilter(ParsingUtil.decimalNumberFilter);
        soundPitch.setMaxLength(6);

        soundFadeIn.setFilter(ParsingUtil.numberFilter);
        soundFadeIn.setMaxLength(6);

        soundFadeOut.setFilter(ParsingUtil.numberFilter);
        soundFadeOut.setMaxLength(6);

        tag.setMaxLength(5);

        //add widgets to the list
        addWidget(soundName);
        addButton(soundButton);
        listCategory.addWidget(this);
        listCategory.addWidget(this.guiRef);
        listType.addWidget(this);
        listType.addWidget(this.guiRef);
        addWidget(soundVolume);
        addWidget(soundPitch);
        addWidget(soundFadeIn);
        addWidget(soundFadeOut);
        addWidget(tag);
        //addWidget(needRedstone);
        addWidget(shouldFuse);
        addWidget(usePriority);
        addWidget(useDelay);
        addWidget(useCondition);
    }

    @Override
    public void updateWidgetPosition() {
        textSoundName.setPosition(getBaseX(), getRowY(0) + getOffsetFontY());
        soundName.setPosition(getNeighbourX(textSoundName), getRowY(0));
        soundName.setWidth(getEndX() - getNeighbourX(textSoundName) - 20 - horizontalSeparation);
        soundButton.setPosition(getNeighbourX(soundName), getRowY(0) + getOffsetY(20));

        textCategory.setPosition(getBaseX(), getRowY(1) + getOffsetFontY());
        listCategory.setPosition(getNeighbourX(textCategory), getRowY(1) + getOffsetY(20));
        listCategory.updateWidgetPosition();

        textType.setPosition(getNeighbourX(listCategory), getRowY(1) + getOffsetFontY());
        listType.setPosition(getNeighbourX(textType), getRowY(1) + getOffsetY(20));
        listType.updateWidgetPosition();

        textVolume.setPosition(getBaseX(), getRowY(2) + getOffsetFontY());
        soundVolume.setPosition(getNeighbourX(textVolume), getRowY(2));

        textPitch.setPosition(getNeighbourX(soundVolume), getRowY(2) + getOffsetFontY());
        soundPitch.setPosition(getNeighbourX(textPitch), getRowY(2));

        textFadeIn.setPosition(getBaseX(), getRowY(3) + getOffsetFontY());
        soundFadeIn.setPosition(getNeighbourX(textFadeIn), getRowY(3));

        textFadeOut.setPosition(getNeighbourX(soundFadeIn), getRowY(3) + getOffsetFontY());
        soundFadeOut.setPosition(getNeighbourX(textFadeOut), getRowY(3));

//needRedstone.setPosition(getBaseX(), getRowY(3));
        textTag.setPosition(getBaseX(), getRowY(4) + getOffsetFontY());
        tag.setPosition(getNeighbourX(textTag), getRowY(4));

        usePriority.setPosition(getNeighbourX(tag), getRowY(4));
        useCondition.setPosition(getNeighbourX(usePriority), getRowY(4));
        shouldFuse.setPosition(getBaseX(), getRowY(5));
        useDelay.setPosition(getBaseX(), getRowY(5));

    }

    @Override
    public void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks) {
        boolean isMusic = AmbienceType.MUSIC.equals(getType());
        shouldFuse.active = isMusic;
        shouldFuse.visible = isMusic;
        useDelay.active = !isMusic;
        useDelay.visible = !isMusic;

        textSoundName.render(matrix, mouseX, mouseY);
        soundName.render(matrix, mouseX, mouseY, partialTicks);
        soundButton.render(matrix, mouseX, mouseY, partialTicks);

        textVolume.render(matrix, mouseX, mouseY);
        soundVolume.render(matrix, mouseX, mouseY, partialTicks);

        textPitch.render(matrix, mouseX, mouseY);
        soundPitch.render(matrix, mouseX, mouseY, partialTicks);

        textFadeIn.render(matrix, mouseX, mouseY);
        soundFadeIn.render(matrix, mouseX, mouseY, partialTicks);
        textFadeOut.render(matrix, mouseX, mouseY);
        soundFadeOut.render(matrix, mouseX, mouseY, partialTicks);

        textTag.render(matrix, mouseX, mouseY, partialTicks);
        tag.render(matrix, mouseX, mouseY, partialTicks);

        //needRedstone.render(mouseX, mouseY, partialTicks);
        shouldFuse.render(matrix, mouseX, mouseY, partialTicks);
        useDelay.render(matrix, mouseX, mouseY, partialTicks);
        usePriority.render(matrix, mouseX, mouseY, partialTicks);
        useCondition.render(matrix, mouseX, mouseY, partialTicks);

        textCategory.render(matrix, mouseX, mouseY);

        matrix.pose().pushPose();
        matrix.pose().translate(0, 0, 300);
        listCategory.render(matrix, mouseX, mouseY, partialTicks);
        matrix.pose().popPose();

        textType.render(matrix, mouseX, mouseY);
        listType.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(GuiGraphics matrix, int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(textSoundName.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Sound ID");
            list.add("Choose the sound to play.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(soundButton.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Tree selection");
            list.add("Select the sound in the list of loaded sounds.");
            list.add(ChatFormatting.GRAY + "You first choose the namespace of the sound.");
            list.add(ChatFormatting.GRAY + "Sounds are separated into folders by '.' (dot), folders are annotated with <>.");
            list.add(ChatFormatting.GRAY + "Anything that isn't a folder is an actual sound, double clicking it will select it.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textCategory.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Category");
            list.add(ChatFormatting.GRAY + "Category the sound should play as.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textType.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Type");
            list.add(ChatFormatting.GRAY + "What kind of sound is it?");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textVolume.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Sound Volume");
            list.add(ChatFormatting.DARK_GRAY + "0 to 1");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textPitch.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Sound Pitch");
            list.add(ChatFormatting.DARK_GRAY + "0.5 to 2");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textFadeIn.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Fade In Time");
            list.add(ChatFormatting.GRAY + "How long should it take for this sound to reach max volume when it begins?");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textFadeOut.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Fade Out Time");
            list.add(ChatFormatting.GRAY + "How long should it take for this sound to reach min volume when it stops?");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textTag.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Tag");
            list.add(ChatFormatting.GRAY + "Can identify one or multiple blocks with conditions.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }

        /*if(needRedstone.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Needs Redstone");
            list.add("This block will not play without a redstone signal.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }*/
        if(shouldFuse.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Should Fuse");
            list.add("If another block is playing the same sound, the ownership will be transferred to whichever is closer.");
            list.add(ChatFormatting.DARK_GRAY + "Volume is summed and pitch is averaged.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(usePriority.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Using Priority");
            list.add("If another block is playing at a higher priority, this one will not play, they won't interact if they're on different channels.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(useDelay.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Using Delay");
            list.add("This block will wait a random amount of time before playing again, not very compatible with fusing and priority.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(useCondition.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Using Condition");
            list.add("This block will only play when a condition is met.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
    }

    @Override
    public void tick() {
        soundName.tick();

        soundVolume.tick();

        soundPitch.tick();

        soundFadeIn.tick();
        soundFadeOut.tick();

        tag.tick();
    }

    @Override
    public void setFieldFromData(AmbienceData data) {
        soundName.setValue(data.getSoundName());
        listCategory.setSelectionByString(data.getCategory());
        listType.setSelectionByString(data.getType());
        NumberFormat df = DecimalFormat.getInstance();
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(4);
        df.setRoundingMode(RoundingMode.HALF_EVEN);

        //soundVolume.setValue(df.format(data.getVolume()));
        //soundPitch.setValue(df.format(data.getPitch()));
        soundVolume.setValue(String.valueOf(data.getVolume()));
        soundPitch.setValue(String.valueOf(data.getPitch()));

        soundFadeIn.setValue(String.valueOf(data.getFadeIn()));
        soundFadeOut.setValue(String.valueOf(data.getFadeOut()));

        tag.setValue(data.getTag());

        //needRedstone.setChecked(data.needsRedstone());
        if(AmbienceType.MUSIC.equals(getType()))
            shouldFuse.setChecked(data.shouldFuse());
        if(AmbienceType.AMBIENT.equals(getType()))
            useDelay.setChecked(data.isUsingDelay());

        usePriority.setChecked(data.isUsingPriority());
        useCondition.setChecked(data.isUsingCondition());
    }

    @Override
    public void setDataFromField(AmbienceData data) {
        data.setSoundName(soundName.getValue());
        //data.setCategory(SoundCategory.valueOf(listCategory.getSelectedString()).getName());
        data.setCategory(ParsingUtil.tryParseEnum(listCategory.getSelectedString().toUpperCase(), SoundSource.MASTER).getName());
        data.setType(ParsingUtil.tryParseEnum(listType.getSelectedString().toUpperCase(), AmbienceType.AMBIENT).getName());
        data.setVolume(ParsingUtil.tryParseFloat(soundVolume.getValue()));
        data.setPitch(ParsingUtil.tryParseFloat(soundPitch.getValue()));
        data.setFadeIn(ParsingUtil.tryParseInt(soundFadeIn.getValue()));
        data.setFadeOut(ParsingUtil.tryParseInt(soundFadeOut.getValue()));

        data.setTag(tag.getValue());

        //data.setNeedRedstone(needRedstone.isChecked());
        if(AmbienceType.MUSIC.equals(getType()))
            data.setShouldFuse(shouldFuse.isChecked());
        if(AmbienceType.AMBIENT.equals(getType()))
            data.setUseDelay(useDelay.isChecked());
        data.setUsePriority(usePriority.isChecked());
        data.setUseCondition(useCondition.isChecked());
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }

    public AmbienceType getType() {
        return AmbienceType.valueOf(listType.getSelectedString().toUpperCase());
    }
}
