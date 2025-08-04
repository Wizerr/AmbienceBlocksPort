package com.wizerr.ambienceblocks.client.gui.ambience.tabs;

import com.wizerr.ambienceblocks.ambience.AmbienceData;
import com.wizerr.ambienceblocks.ambience.conds.AbstractCond;
import com.wizerr.ambienceblocks.ambience.conds.AlwaysTrueCond;
import com.wizerr.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.wizerr.ambienceblocks.client.gui.ambience.EditCondGUI;
import com.wizerr.ambienceblocks.client.gui.ambience.IFetchCond;
import com.wizerr.ambienceblocks.client.gui.widgets.StringListWidget;
import com.wizerr.ambienceblocks.config.AmbienceConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CondTab extends AbstractTab implements IFetchCond {
    List<AbstractCond> condList = new ArrayList<>();

    //widgets
    Button edit = Button.builder(Component.literal("Edit"), button -> buttonEdit())
            .bounds(0, 0, 30, 20)
            .build();
    Button add = Button.builder(Component.literal("Add"), button -> buttonAdd())
            .bounds(0, 0, 30, 20)
            .build();
    Button copy = Button.builder(Component.literal("Copy"), button -> buttonCopy())
            .bounds(0, 0, 30, 20)
            .build();
    Button remove = Button.builder(Component.literal("Del"), button -> buttonRemove())
            .bounds(0, 0, 30, 20)
            .build();
    Button up = Button.builder(Component.literal("Up"), button -> buttonUp())
            .bounds(0, 0, 30, 20)
            .build();
    Button down = Button.builder(Component.literal("Down"), button -> buttonDown())
            .bounds(0, 0, 30, 20)
            .build();

    StringListWidget condGuiList = new StringListWidget(getBaseX(), getRowY(0), getEndX() - getBaseX(), getRowY(5) - getRowY(0) - verticalSeparation, 4, 16, font, null);

    public CondTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Condition";
    }

    @Override
    public String getShortName() {
        return "Cond";
    }

    @Override
    public void initialInit() {
        updateGuiCondList();

        addButton(edit);
        addButton(add);
        addButton(copy);
        addButton(remove);
        addButton(up);
        addButton(down);
        addWidget(condGuiList);
    }

    @Override
    public void updateWidgetPosition() {
        edit.setPosition(getBaseX(), getRowY(5));
        add.setPosition(getNeighbourX(edit), getRowY(5));
        copy.setPosition(getNeighbourX(add), getRowY(5));
        remove.setPosition(getNeighbourX(copy), getRowY(5));
        up.setPosition(getNeighbourX(remove), getRowY(5));
        down.setPosition(getNeighbourX(up), getRowY(5));
        condGuiList.setPosition(getBaseX(), getRowY(0));
    }

    @Override
    public void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks) {
        edit.render(matrix, mouseX, mouseY,partialTicks);
        add.render(matrix, mouseX, mouseY,partialTicks);
        copy.render(matrix, mouseX, mouseY,partialTicks);
        remove.render(matrix, mouseX, mouseY,partialTicks);
        up.render(matrix, mouseX, mouseY,partialTicks);
        down.render(matrix, mouseX, mouseY,partialTicks);

        condGuiList.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(GuiGraphics matrix, int mouseX, int mouseY) {
    }

    @Override
    public void tick() {

    }

    @Override
    public void setFieldFromData(AmbienceData data) {
        condList.clear();
        condList.addAll(data.getConditions());
        updateGuiCondList();
    }

    @Override
    public void setDataFromField(AmbienceData data) {
        data.setConditions(condList);
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }

    private void buttonEdit() {
        if(condGuiList.getSelectionIndex() < condGuiList.getAmountOfElements() && condGuiList.getSelectionIndex() >= 0) {
            Minecraft.getInstance().setScreen(new EditCondGUI(this.guiRef, this, condList.get(condGuiList.getSelectionIndex())));
        }
    }

    private void buttonAdd() {
        addCond(new AlwaysTrueCond());
    }

    private void buttonCopy() {
        if(condGuiList.getSelectionIndex() < condGuiList.getAmountOfElements() && condGuiList.getSelectionIndex() >= 0) {
            addCond(condList.get(condGuiList.getSelectionIndex()).copy());
        }
    }

    private void buttonRemove() {
        if(condGuiList.getSelectionIndex() < condGuiList.getAmountOfElements() && condGuiList.getSelectionIndex() >= 0) {
            removeCond(condGuiList.getSelectionIndex());
        }
    }

    private void buttonUp() {
        if(condGuiList.getSelectionIndex() < condGuiList.getAmountOfElements() && condGuiList.getSelectionIndex() >= 1) {
            Collections.swap(condList, condGuiList.getSelectionIndex(), condGuiList.getSelectionIndex() - 1);
            updateGuiCondList();
            condGuiList.setSelectionIndex(condGuiList.getSelectionIndex() - 1);
        }
    }

    private void buttonDown() {
        if(condGuiList.getSelectionIndex() < condGuiList.getAmountOfElements() - 1 && condGuiList.getSelectionIndex() >= 0) {
            Collections.swap(condList, condGuiList.getSelectionIndex(), condGuiList.getSelectionIndex() + 1);
            updateGuiCondList();
            condGuiList.setSelectionIndex(condGuiList.getSelectionIndex() + 1);
        }
    }

    public void addCond(AbstractCond cond) {
        if(condList.size() <= AmbienceConfig.maxAmountOfConditions) {
            condList.add(cond);
            updateGuiCondList();
            condGuiList.setSelectionIndexToLast();
        }
    }

    public void removeCond(int index) {
        if(index >= condList.size()) return;

        condList.remove(index);
        updateGuiCondList();
        if(condGuiList.getSelectionIndex() >= condGuiList.getAmountOfElements())
            condGuiList.setSelectionIndexToLast();
    }

    public void replaceCond(int index, AbstractCond cond) {

        removeCond(index);
        condList.add(index, cond);
        updateGuiCondList();
        condGuiList.setSelectionIndex(index);
    }

    public void updateGuiCondList() {
        condGuiList.clearList();
        for(AbstractCond cond : condList) {
            condGuiList.addElement(cond.getListDescription());
        }
    }

    @Override
    public void fetch(AbstractCond newCond, AbstractCond oldCond) {
        for (int i = 0; i < condList.size(); i++) {
            AbstractCond cond = condList.get(i);
            if (cond.equals(oldCond)) {
                replaceCond(i, newCond.copy());
                return;
            }
        }
    }
}
