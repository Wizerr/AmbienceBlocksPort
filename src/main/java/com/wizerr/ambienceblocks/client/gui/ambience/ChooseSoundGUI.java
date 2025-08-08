package com.wizerr.ambienceblocks.client.gui.ambience;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wizerr.ambienceblocks.Main;
import com.wizerr.ambienceblocks.client.gui.widgets.StringListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ChooseSoundGUI extends AmbienceScreen implements StringListWidget.IPressable {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/gui/ambience_gui.png");

    public static final int texWidth = 256;
    public static final int texHeight = 184;
    public int xTopLeft, yTopLeft;

    private EditBox targetField;
    public Font font;

    protected static final int yOffset = 8;
    protected static final int separation = 16;

    Minecraft mc = Minecraft.getInstance();

    StringListWidget list;
    Button play;
    Button stop;
    Button cancel;

    private String selectedDomain = "";
    private String selected = "";
    private List<String> activeList = new ArrayList<>();
    private String biasSelectionName = "";

    SimpleSoundInstance previewSound;
    //boolean closing = false;

    public ChooseSoundGUI(Screen prevScreen, EditBox targetField) {
        super(Component.translatable("narrator.screen.choosesound"));

        this.font = mc.font;
        setPreviousScreen(prevScreen);
        this.targetField = targetField;

        String previousString = targetField.getValue();

        if(previousString.split(":").length <= 2) {
            if(previousString.split(":").length == 1) {
                selectedDomain = "minecraft";

                selected = previousString;
                boolean validate = false;

                for (ResourceLocation element : Minecraft.getInstance().getSoundManager().getAvailableSounds()) {
                    if(element.getNamespace().equals(selectedDomain) && element.getPath().equals(selected)) validate = true;
                }

                if(validate) {
                    int firstIndex = selected.lastIndexOf(".") + 1;

                    biasSelectionName = selected.substring(firstIndex);

                    selected = selected.replaceAll(selected.substring(firstIndex), "");
                } else {
                    selectedDomain = ""; selected = "";
                }
            } else {
                selectedDomain = previousString.split(":")[0];
                if(!selectedDomain.equals("")) {
                    selected = previousString.split(":")[1];

                    boolean validate = false;
                    for (ResourceLocation element : Minecraft.getInstance().getSoundManager().getAvailableSounds()) {
                        if(element.getNamespace().equals(selectedDomain) && element.getPath().equals(selected)) validate = true;
                    }

                    if(validate) {
                        int firstIndex = selected.lastIndexOf(".") + 1;

                        biasSelectionName = selected.substring(firstIndex);

                        selected = selected.replaceAll(selected.substring(firstIndex), "");
                    } else {
                        selectedDomain = ""; selected = "";
                    }
                }
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        play = Button.builder(Component.literal("Play"), button -> {
                    playSoundPreview();
                })
                .bounds(xTopLeft + separation / 4, yTopLeft + texHeight + separation / 4, 60, 20)
                .build();
        addCustomWidget(play);

        stop = Button.builder(Component.literal("Stop"), button -> {
                    stopSoundPreview();
                })
                .bounds(xTopLeft + 60 + separation / 2, yTopLeft + texHeight + separation / 4, 60, 20)
                .build();
        addCustomWidget(stop);

        cancel = Button.builder(Component.literal("Cancel"), button -> {
                    stopSoundPreview();
                    quit();
                })
                .bounds(xTopLeft + texWidth - 80 - separation / 4, yTopLeft + texHeight + 4, 80, 20)
                .build();
        addCustomWidget(cancel);
        addCustomWidget(cancel);

        addCustomWidget(list = new StringListWidget(xTopLeft + separation, yTopLeft + separation + yOffset, texWidth - separation * 2, texHeight - separation * 2 - yOffset, 4, 16, font, new StringListWidget.IPressable() {
            @Override
            public void onClick(StringListWidget list, int index, String name) {

            }

            @Override
            public void onDoubleClick(StringListWidget list, int index, String name) {
                if(selectedDomain.isEmpty()) {
                    selectedDomain = name;
                    updateList();
                } else {
                    if(name.equals("<...>")) {
                        if(selected.isEmpty()) {
                            selectedDomain = "";
                        } else {
                            int firstIndex = selected.lastIndexOf(".", selected.lastIndexOf(".") - 1) + 1;
                            int lastIndex = selected.lastIndexOf(".") + 1;

                            selected = selected.replaceAll(selected.substring(firstIndex, lastIndex), "");
                        }
                        updateList();
                    } else {
                        if(name.contains("<")) {
                            name = name.replace("<", "").replace(">", "");
                            selected += name + ".";
                            updateList();
                        } else {
                            selected += name;
                            onConfirm();
                        }
                    }
                }
            }
        }));

        updateList();
    }

    public void playSoundPreview() {
        if(!selectedDomain.equals("") && !list.getSelectionContent().contains("<")) {
            String resultSound = selectedDomain + ":" + selected + list.getSelectionContent();
            stopSoundPreview();
            ResourceLocation soundResource = ResourceLocation.parse(resultSound);
            SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(soundResource);
            previewSound = SimpleSoundInstance.forUI(soundEvent, 1.0f, 0.75f);
            mc.getSoundManager().play(previewSound);
        }
    }

    public void stopSoundPreview() {
        if(previewSound != null) {
            mc.getSoundManager().stop(previewSound);
            previewSound = null;
        }
    }

    public void updateList() {
        activeList.clear();
        list.clearList();
        list.setSelectionIndex(0);
        list.resetScroll();
        if(selectedDomain.equals("")) {
            for (ResourceLocation element : Minecraft.getInstance().getSoundManager().getAvailableSounds()) {
                if(!activeList.contains(element.getNamespace())) activeList.add(element.getNamespace());
            }
        } else {
            for (ResourceLocation element : Minecraft.getInstance().getSoundManager().getAvailableSounds()) {
                if(selectedDomain.equals(element.getNamespace())) {
                    String name = element.getPath().replaceAll("\\b" + selected + "\\b", "").split("\\.")[0];
                    if(element.getPath().replace(selected,"").chars().filter(ch -> ch == '.').count() > 0) {
                        //is a folder
                        name = "<" + name + ">";
                    }  //is not a folder

                    if(element.getPath().contains(selected) && !activeList.contains(name)) activeList.add(name);
                }
            }
            Collections.sort(activeList);
            activeList.add(0, "<...>");
        }

        list.addElements(activeList);

        if(!biasSelectionName.equals("")) {
            list.setSelectionByString(biasSelectionName);
            biasSelectionName = "";
        }
    }

    @Override
    public void render(GuiGraphics matrix, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(matrix, p_render_1_, p_render_2_, p_render_3_);

        drawMainBackground(matrix);

        matrix.drawCenteredString(font, selectedDomain + ":" + selected, xTopLeft + texWidth/2, yTopLeft + separation / 2, 0xFFFFFF);

        list.render(matrix, p_render_1_, p_render_2_, p_render_3_);

        play.render(matrix, p_render_1_, p_render_2_, p_render_3_);
        stop.render(matrix, p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public void onClose() {
        mc.getSoundManager().stop(previewSound);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void onClick(StringListWidget list, int index, String name) {
    }

    @Override
    public void onDoubleClick(StringListWidget list, int index, String name) {
        targetField.setValue(name);
        mc.getSoundManager().stop(previewSound);
        quit();
    }

    public void onConfirm() {
        targetField.setValue(selectedDomain + ":" + selected);
        quit();
        mc.getSoundManager().stop(previewSound);
        targetField.setValue(selectedDomain + ":" + selected);
    }

    public void drawMainBackground(GuiGraphics matrix) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        matrix.blit(BACKGROUND_TEXTURE, xTopLeft, yTopLeft, 0, 0, texWidth, texHeight);
    }
}

