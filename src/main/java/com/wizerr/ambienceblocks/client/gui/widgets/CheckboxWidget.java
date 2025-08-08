package com.wizerr.ambienceblocks.client.gui.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CheckboxWidget extends Checkbox {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("textures/gui/checkbox.png");

    public CheckboxWidget(int xIn, int yIn, int widthIn, int heightIn, String msg, boolean isChecked) {
        super(xIn, yIn, widthIn, heightIn, Component.literal(msg), isChecked);
        this.setAlpha(1f);
    }

    @Override
    public void renderWidget(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        matrix.blit(TEXTURE, this.getX(), this.getY(), this.isFocused() ? 20.0F : 0.0F, this.selected() ? 20.0F : 0.0F, 20, this.height, 64, 64);
        matrix.drawString(minecraft.font, this.getMessage(), this.getX() + 24, this.getY() + (this.height - 8)/2, 0xFFFFFF);
    }

    public void setChecked(boolean b) {
        if(b && !super.selected())
            super.onPress();
        if(!b && super.selected())
            super.onPress();
    }

    public boolean isChecked() {
        return super.selected();
    }
}