package com.wizerr.ambienceblocks.client.gui.widgets.presets.textfield;

import com.wizerr.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class CustomTextField extends EditBox {
    public CustomTextField(int xIn, int yIn, int widthIn, int heightIn, String msg) {
        super(Minecraft.getInstance().font, xIn, yIn, widthIn, heightIn, Component.literal(msg));
    }

    public void filterDecimalNumberFilter() {
        this.setFilter(ParsingUtil.decimalNumberFilter);
    }
}
