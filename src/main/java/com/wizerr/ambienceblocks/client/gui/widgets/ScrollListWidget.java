package com.wizerr.ambienceblocks.client.gui.widgets;

import com.wizerr.ambienceblocks.client.gui.ambience.AmbienceScreen;
import com.wizerr.ambienceblocks.client.gui.ambience.tabs.AbstractTab;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class ScrollListWidget extends AbstractWidget {
    public ArrayList<String> list;
    public int index;

    protected final IPressable onChange;
    private State state = State.NEUTRAL;

    //internal
    int internalWidth;
    int internalHeight;

    //list options
    int separation;
    int optionHeight;
    Font font;

    public Button main;

    public StringListWidget scrollList;

    public ScrollListWidget(int xIn, int yIn, int widthIn, int heightIn, int separation, int optionHeight, int listLength, ArrayList<String> list, Font font, IPressable onChange) {
        super(xIn, yIn, widthIn, heightIn, Component.literal(""));
        internalWidth = widthIn;
        internalHeight = heightIn;
        this.separation = separation;
        if(optionHeight < font.lineHeight) optionHeight = font.lineHeight;
        this.optionHeight = optionHeight;
        this.font = font;
        this.onChange = onChange;
        this.list = list;

        setWidth(0);
        setHeight(0);

        main = Button.builder(Component.literal(""), button -> {
                    mainPressed();
                })
                .bounds(0, 0, 30, 20)
                .build();

        if(list.size()!=0)
            main.setMessage(Component.literal(list.get(0)));
        main.setPosition(xIn, yIn);
        main.setWidth(internalWidth);
        main.setHeight(internalHeight);

        this.width = widthIn;
        this.height = heightIn;

        scrollList = new StringListWidget(getX() + 1, getY() + internalHeight, internalWidth - 2, internalHeight * listLength + separation - 1, separation, optionHeight, font, new StringListWidget.IPressable() {
            @Override
            public void onClick(StringListWidget list, int index, String name) {

            }

            @Override
            public void onDoubleClick(StringListWidget list, int index, String name) {
                if(scrollList.visible)
                    setIndex(index);
            }
        });
        for (String s : list) scrollList.addElement(s);

        index = 0;

        hideMenu();
    }

    public void mainPressed() {
        if(!this.visible) return;

        if(state.equals(State.NEUTRAL) && list.size()!=0) {
            showMenu();
        }
        else if(state.equals(State.LIST_UP)) {
            hideMenu();
        }
    }

    public void showMenu() {
        state = State.LIST_UP;
        scrollList.visible = true;
        scrollList.active = true;
        scrollList.setSelectionByString(list.get(index));
    }

    public void hideMenu() {
        state = State.NEUTRAL;
        scrollList.visible = false;
        scrollList.active = false;
    }

    public void setIndex(int index) {
        if(index < 0 || index >= list.size())
            return;

        if(this.index != index) {
            main.setMessage(Component.literal(list.get(index)));
            hideMenu();
            this.index = index;
            onChange.onChange(this, index, list.get(index));
        } else {
            hideMenu();
        }
    }

    public void setSelectionByString(String name) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(name)) {
                setIndex(i);
            }
        }
    }

    public void updateWidgetPosition() {
        main.setX(this.getX());
        main.setY(this.getY());
        scrollList.setX(this.getX() + 1);
        scrollList.setY(this.getY() + internalHeight + 1);
    }

    public void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks) {
        this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

        if(!this.visible)
            return;

        main.render(matrix, mouseX, mouseY, partialTicks);

        if(state.equals(State.LIST_UP) && scrollList != null) {
            scrollList.render(matrix, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.isHovered = mouseX >= getX() && mouseY >= getY() &&
                mouseX < getX() + this.width && mouseY < getY() + this.height;

        if (!this.visible) return;

        main.render(guiGraphics, mouseX, mouseY, partialTicks);

        if (state == State.LIST_UP && scrollList != null) {
            scrollList.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double pX, double pY, int pType) {
        if(state.equals(State.LIST_UP) && scrollList != null && this.visible) {
        }
        return false;
    }

    public void addWidget(AbstractTab gui) {
        gui.addButton(main);
        gui.addWidget(this);
    }

    public void addWidget(AmbienceScreen gui) {
        gui.addCustomWidget(main);
        gui.addCustomWidget(scrollList);
        gui.addCustomWidget(this);
    }

    public String getSelectedString() {
        return list.get(index);
    }

    public void updateWidgetNarration(NarrationElementOutput narration) {
    }

    @OnlyIn(Dist.CLIENT)
    public interface IPressable {
        void onChange(ScrollListWidget list, int index, String name);
    }

    private enum State {
        NEUTRAL,
        LIST_UP;
    }
}