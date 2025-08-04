package com.wizerr.ambienceblocks.client.gui.ambience;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wizerr.ambienceblocks.Main;
import com.wizerr.ambienceblocks.ambience.AmbienceData;
import com.wizerr.ambienceblocks.ambience.compendium.BaseCompendium;
import com.wizerr.ambienceblocks.ambience.compendium.CompendiumEntry;
import com.wizerr.ambienceblocks.client.ambience.AmbienceController;
import com.wizerr.ambienceblocks.client.gui.widgets.StringListWidget;
import com.wizerr.ambienceblocks.packets.PacketCompendium;
import com.wizerr.ambienceblocks.util.PacketHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class CompendiumGUI extends AmbienceScreen {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");
    public static final int texWidth = 256;
    public static final int texHeight = 184;
    protected static final int outerOffset = 8;
    protected static final int offset = 2;
    private static final int buttonWidth = 30;
    private static final int buttonHeight = 20;

    private final BaseCompendium comp;
    private final BaseCompendium internalCompendium = new BaseCompendium();

    //keeps in memory the slot we were editing
    private int index = 0;

    public int xTopLeft, yTopLeft;

    private StringListWidget entriesList;
    private Button addEntry;
    private Button editEntry;
    private Button cloneEntry;
    private Button removeEntry;
    private Button upEntry;
    private Button downEntry;

    private Button confirmChanges;
    private Button cancel;

    public CompendiumGUI() {
        super(Component.translatable("narrator.screen.compendiumgui"));

        comp = AmbienceController.instance.compendium;

        internalCompendium.adopt(comp);
    }

    @Override
    public void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);

        drawMainBackground(matrix);

        entriesList.render(matrix, mouseX, mouseY, partialTicks);
        addEntry.render(matrix, mouseX, mouseY, partialTicks);
        editEntry.render(matrix, mouseX, mouseY, partialTicks);
        cloneEntry.render(matrix, mouseX, mouseY, partialTicks);
        removeEntry.render(matrix, mouseX, mouseY, partialTicks);
        upEntry.render(matrix, mouseX, mouseY, partialTicks);
        downEntry.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void init() {
        super.init();

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        addEntry = addCustomWidget(Button.builder(Component.literal("Add"), button -> {
                    addCompendiumEntry();
                    forceIndexUpdate();
                })
                .bounds(xTopLeft + outerOffset, getGenericButtonY(0), buttonWidth, buttonHeight)
                .build());

        editEntry = addCustomWidget(Button.builder(Component.literal("Edit"), button -> {
                    if(internalCompendium.getAllEntries().isEmpty())
                        return;
                    forceIndexUpdate();
                    AmbienceGUI gui = new AmbienceGUI(internalCompendium.getAllEntries().get(index));
                    gui.setPreviousScreen(this);
                    minecraft.setScreen(gui);
                })
                .bounds(xTopLeft + outerOffset, getGenericButtonY(1), buttonWidth, buttonHeight)
                .build());

        cloneEntry = addCustomWidget(Button.builder(Component.literal("Copy"), button -> {
                    cloneCompendiumEntry();
                    forceIndexUpdate();
                })
                .bounds(xTopLeft + outerOffset, getGenericButtonY(2), buttonWidth, buttonHeight)
                .build());

        removeEntry = addCustomWidget(Button.builder(Component.literal("Del"), button -> {
                    removeCompendiumEntry();
                    forceIndexUpdate();
                })
                .bounds(xTopLeft + outerOffset, getGenericButtonY(3), buttonWidth, buttonHeight)
                .build());

        upEntry = addCustomWidget(Button.builder(Component.literal("Up"), button -> {
                    moveCompendiumEntryUp();
                })
                .bounds(xTopLeft + outerOffset, getGenericButtonY(4), buttonWidth, buttonHeight)
                .build());

        downEntry = addCustomWidget(Button.builder(Component.literal("Down"), button -> {
                    moveCompendiumEntryDown();
                })
                .bounds(xTopLeft + outerOffset, getGenericButtonY(5), buttonWidth, buttonHeight)
                .build());

        confirmChanges = addCustomWidget(Button.builder(Component.literal("Confirm Changes"), button -> {
                    comp.adopt(internalCompendium);
                    PacketHandler.NET.sendToServer(new PacketCompendium(internalCompendium.getAllEntries()));
                    AmbienceController.instance.stopAllCompendium();
                    minecraft.setScreen(null);
                })
                .bounds(xTopLeft + 4, yTopLeft + texHeight + 4, 100, 20)
                .build());

        cancel = addCustomWidget(Button.builder(Component.literal("Cancel"), button -> {
                    minecraft.setScreen(null);
                })
                .bounds(xTopLeft + texWidth - 80 - 4, yTopLeft + texHeight + 4, 80, 20)
                .build());

        entriesList = new StringListWidget(xTopLeft + offset + outerOffset + buttonWidth, yTopLeft + outerOffset, texWidth - offset - outerOffset * 2 - buttonWidth, texHeight - outerOffset * 2, 4, 16, font, new StringListWidget.IPressable() {
            @Override
            public void onClick(StringListWidget list, int index, String name) {
                setIndex(index);
            }

            @Override
            public void onDoubleClick(StringListWidget list, int index, String name) {}
        });
        //this.children.add(entriesList);
        addCustomWidget(entriesList);

        updateCompendiumList();

        entriesList.setSelectionIndex(index);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void setIndex(int index) {
        this.index = index;
    }

    private void forceIndexUpdate() {
        setIndex(entriesList.getSelectionIndex());
    }

    private String getEntryText(AmbienceData data) {
        //return data.getSoundName() + " " + data.getVolume() + " " + data.;
        String text = !data.getSoundName().isEmpty() ? data.getSoundName() : "<No sound yet>";
        if(data.isUsingCondition()) text += " cond,";
        if(data.isUsingDelay()) text += " delay,";
        if(data.isUsingPriority()) text += " prio,";
        text += " " + data.getVolume();
        return text;
    }

    private void addCompendiumEntry() {
        //adds the default ambience data
        CompendiumEntry entry = new CompendiumEntry(new AmbienceData());
        internalCompendium.addEntry(entry);
        //entriesList.addElement(getEntryText(entry.getData()));
        updateCompendiumList();
        entriesList.setSelectionIndexToLast();
    }

    private void cloneCompendiumEntry() {
        if(index >= internalCompendium.size() || index < 0) return;

        List<CompendiumEntry> entries = internalCompendium.getAllEntries();
        entries.add(index + 1, entries.get(index).copy());
        entriesList.setSelectionIndex(entriesList.getSelectionIndex() + 1);
        index += 1;
        forceIndexUpdate();
        updateCompendiumList();
    }

    private void removeCompendiumEntry() {
        //remove the selected entry
        if(index >= internalCompendium.size() || index < 0) return;

        internalCompendium.removeEntry(index);
        updateCompendiumList();
        if(entriesList.getSelectionIndex() >= entriesList.getAmountOfElements())
            entriesList.setSelectionIndexToLast();

        forceIndexUpdate();
    }

    private void moveCompendiumEntryUp() {
        if(index < internalCompendium.size() && index >= 1) {
            Collections.swap(internalCompendium.getAllEntries(), index, index - 1);
            entriesList.setSelectionIndex(index - 1);
            updateCompendiumList();
            forceIndexUpdate();
            //condGuiList.setSelectionIndex(condGuiList.getSelectionIndex() - 1);
        }

    }

    private void moveCompendiumEntryDown() {
        if(index < internalCompendium.size() - 1 && index >= 0) {
            Collections.swap(internalCompendium.getAllEntries(), index, index + 1);
            entriesList.setSelectionIndex(index + 1);
            updateCompendiumList();
            forceIndexUpdate();
            //condGuiList.setSelectionIndex(condGuiList.getSelectionIndex() + 1);
        }
    }

    private void updateCompendiumList() {
        entriesList.clearList();
        internalCompendium.getAllEntries().forEach(entry -> {
            entriesList.addElement(getEntryText(entry.getData()));
        });
        //entriesList.addElements();
    }

    public void drawMainBackground(GuiGraphics matrix) {
        //RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        //this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        //this.blit(matrix, xTopLeft, yTopLeft, 0, 0, texWidth, texHeight);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        matrix.blit(BACKGROUND_TEXTURE, xTopLeft, yTopLeft, 0, 0, texWidth, texHeight);
    }

    public void applyData(AmbienceData data) {
        internalCompendium.getAllEntries().get(index).setData(data);
        updateCompendiumList();
    }

    private int getGenericButtonY(int index) {
        return yTopLeft + outerOffset + offset * index + buttonHeight * index;
    }
}
