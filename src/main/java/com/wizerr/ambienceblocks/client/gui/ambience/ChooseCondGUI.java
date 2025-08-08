package com.wizerr.ambienceblocks.client.gui.ambience;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wizerr.ambienceblocks.Main;
import com.wizerr.ambienceblocks.ambience.conds.AbstractCond;
import com.wizerr.ambienceblocks.ambience.conds.AlwaysTrueCond;
import com.wizerr.ambienceblocks.client.gui.widgets.StringListWidget;
import com.wizerr.ambienceblocks.util.CondsUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ChooseCondGUI extends AmbienceScreen {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/gui/ambience_gui.png");

    public static final int texWidth = 256;
    public static final int texHeight = 184;
    public int xTopLeft, yTopLeft;

    protected static final int yOffset = 8;
    protected static final int separation = 16;

    private StringListWidget list;

    private Button cancel;

    boolean closing = false;

    public ChooseCondGUI(EditCondGUI prevScreen) {
        super(Component.translatable("narrator.screen.choosecond"));

        setPreviousScreen(prevScreen);
    }

    @Override
    public void render(GuiGraphics matrix, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(matrix, p_render_1_, p_render_2_, p_render_3_);

        drawMainBackground(matrix);

        matrix.drawCenteredString(font, "Choose a condition", xTopLeft + texWidth/2, yTopLeft + separation / 2, 0xFFFFFF);

        list.render(matrix, p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public void drawMainBackground(GuiGraphics matrix) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        matrix.blit(BACKGROUND_TEXTURE, xTopLeft, yTopLeft, 0, 0, texWidth, texHeight);
    }

    @Override
    protected void init() {
        super.init();

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        list = new StringListWidget(xTopLeft + separation, yTopLeft + separation + yOffset, texWidth - separation * 2, texHeight - separation * 2 - yOffset, 4, 16, font, new StringListWidget.IPressable() {
            @Override
            public void onClick(StringListWidget list, int index, String name) {

            }

            @Override
            public void onDoubleClick(StringListWidget list, int index, String name) {
                AbstractCond returnCond = new AlwaysTrueCond();
                CondsUtil.CondList[] conds = CondsUtil.CondList.values();
                for (CondsUtil.CondList cond : conds) {
                    if(cond.getDefault().getName().equals(name)) returnCond = cond.getDefault();
                }
                if(previousScreen instanceof EditCondGUI)
                    ((EditCondGUI) previousScreen).setCond(returnCond);
                displayOriginalScreen();
            }
        });
        addCustomWidget(list);

        cancel = addCustomWidget(Button.builder(Component.literal("Cancel"), button -> {
                    displayOriginalScreen();
                })
                .bounds(xTopLeft + texWidth - 80 - 4, yTopLeft + texHeight + 4, 80, 20)
                .build());

        CondsUtil.CondList[] conds = CondsUtil.CondList.values();
        for (CondsUtil.CondList cond : conds) {
            list.addElement(cond.getDefault().getName());
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


    private void displayOriginalScreen() {
        if(!closing) {
            closing = true;
            quit();
        }
    }


}
