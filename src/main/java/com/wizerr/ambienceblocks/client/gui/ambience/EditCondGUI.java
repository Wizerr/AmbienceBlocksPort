package com.wizerr.ambienceblocks.client.gui.ambience;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wizerr.ambienceblocks.Main;
import com.wizerr.ambienceblocks.ambience.conds.AbstractCond;
import com.wizerr.ambienceblocks.ambience.util.AmbienceWidgetHolder;
import com.wizerr.ambienceblocks.ambience.util.messenger.*;
import com.wizerr.ambienceblocks.client.gui.widgets.CheckboxWidget;
import com.wizerr.ambienceblocks.client.gui.widgets.ScrollListWidget;
import com.wizerr.ambienceblocks.client.gui.widgets.TextInstance;
import com.wizerr.ambienceblocks.client.gui.widgets.presets.textfield.CustomTextField;
import com.wizerr.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditCondGUI extends AmbienceScreen implements IFetchCond {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/gui/ambience_gui.png");

    public static final int texWidth = 256;
    public static final int texHeight = 184;
    public int xTopLeft, yTopLeft;

    protected static final int offset = 4;//8;
    protected static final int separation = 8;//16;
    protected static final int rowHeight = 20;

    //private AmbienceGUI prevScreen;
    private AmbienceScreen prevScreen;
    private IFetchCond condFetcher;
    private AbstractCond oldCond;
    private AbstractCond newCond;
    /*private CondTab tab;
    private AbstractCond cond;
    private int index;*/

    //private boolean offToEditSoundLol = false;

    private final List<AmbienceWidgetHolder> condWidgets = new ArrayList<>();
    //the internal reference to get the values to and pass back to the cond
    //private List<AbstractAmbienceWidgetMessenger> widgetMessengers = new ArrayList<>();
    private final HashMap<AbstractAmbienceWidgetMessenger, AmbienceWidgetHolder> widgetLink = new HashMap<>();

    private Button editCond;
    private Button confirm;
    private Button back;

    //boolean closing = false;

    //private boolean condInit = false;

    private boolean isFieldBeingEdited = false;
    private CustomTextField fieldBeingEdited;

    public EditCondGUI(AmbienceScreen prevScreen, IFetchCond condFetcher, AbstractCond oldCond) {
        super(Component.translatable("narrator.screen.editcond"));
        this.prevScreen = prevScreen;
        this.condFetcher = condFetcher;
        this.oldCond = oldCond;
        this.newCond = oldCond.copy();
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

        widgetLink.clear();
        condWidgets.clear();

        editCond = addCustomWidget(Button.builder(Component.literal("Edit"), button -> {
                    collectData();
                    minecraft.setScreen(new ChooseCondGUI(this));
                })
                .bounds(xTopLeft + offset, yTopLeft + offset, 80, 20)
                .build());

        confirm = addCustomWidget(Button.builder(Component.literal("Confirm"), button -> {
                    collectData();
                    condFetcher.fetch(newCond, oldCond);
                    minecraft.setScreen(prevScreen);
                })
                .bounds(xTopLeft + 4, yTopLeft + texHeight + 4, 100, 20)
                .build());

        back = addCustomWidget(Button.builder(Component.literal("Back"), button -> {
                    minecraft.setScreen(prevScreen);
                })
                .bounds(xTopLeft + texWidth - 4 - 100, yTopLeft + texHeight + 4, 100, 20)
                .build());

        condWidgets.clear();

        addCondWidgets(newCond.getWidgets());
        updateCondWidgetPos();
    }

    private void collectData() {
        List<AbstractAmbienceWidgetMessenger> data = new ArrayList<>();
        for(AbstractAmbienceWidgetMessenger messenger : widgetLink.keySet()) {
            if(messenger instanceof AmbienceWidgetString) {
                String value = ((CustomTextField) widgetLink.get(messenger).get()).getValue();
                AmbienceWidgetString widget = (AmbienceWidgetString) messenger;
                widget.setValue(value);
            }
            if(messenger instanceof AmbienceWidgetSound) {
                String value = ((CustomTextField) widgetLink.get(messenger).get()).getValue();
                AmbienceWidgetSound widget = (AmbienceWidgetSound) messenger;
                widget.setValue(value);
            }
            if(messenger instanceof AmbienceWidgetCheckbox) {
                boolean value = ((CheckboxWidget) widgetLink.get(messenger).get()).isChecked();
                AmbienceWidgetCheckbox widget = (AmbienceWidgetCheckbox) messenger;
                widget.setValue(value);
            }
            data.add(messenger);
        }
        newCond.getDataFromWidgets(data);
    }

    private void addCondWidgets(List<AbstractAmbienceWidgetMessenger> widgets) {
        for(int i = 0; i < widgets.size(); i++) {
            AbstractAmbienceWidgetMessenger widget = widgets.get(i);

            if(!widget.getLabel().isEmpty()) {
                AmbienceWidgetHolder label = new AmbienceWidgetHolder("", new TextInstance(0, (rowHeight - font.lineHeight) / 2, 0xFFFFFF, widget.getLabel(), font));
                addWidget(label);
            }

            if(widget instanceof AmbienceWidgetString) {
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new CustomTextField(0, 0, widget.getWidth(), 20, ""));
                CustomTextField text = (CustomTextField) holder.get();
                if(((AmbienceWidgetString) widget).getCharLimit() > 0) {
                    text.setMaxLength(((AmbienceWidgetString) widget).getCharLimit());
                }
                text.setValue(((AmbienceWidgetString) widget).getValue());
                text.setFilter(((AmbienceWidgetString) widget).getValidator());
                addWidget(holder);
                widgetLink.put(widget, holder);
            }
            if(widget instanceof AmbienceWidgetEnum) {
                AmbienceWidgetEnum wEnum = (AmbienceWidgetEnum) widget;
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(),
                        Button.builder(Component.literal(ParsingUtil.getCachedEnumName(wEnum.getValue())), button -> {
                                    wEnum.next();
                                    button.setMessage(Component.literal(ParsingUtil.getCachedEnumName(wEnum.getValue())));
                                })
                                .bounds(0, 0, widget.getWidth(), 20)
                                .build()
                );
                addWidget(holder);
                widgetLink.put(widget, holder);
            }
            if(widget instanceof AmbienceWidgetCond) {
                AmbienceWidgetCond wCond = (AmbienceWidgetCond) widget;
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(),
                        Button.builder(Component.literal(wCond.getCond().getListDescription()), button -> {
                                    Minecraft.getInstance().setScreen(new EditCondGUI(this, this, wCond.getCond()));
                                })
                                .bounds(0, 0, widget.getWidth(), 20)
                                .build()
                );
                addWidget(holder);
                widgetLink.put(widget, holder);
            }
            if(widget instanceof AmbienceWidgetSound) {
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new CustomTextField(0, 0, widget.getWidth() - separation * 2 - 20, 20, ""));
                AmbienceWidgetHolder button = new AmbienceWidgetHolder(widget.getKey(),
                        Button.builder(Component.literal("..."), b -> {
                                    isFieldBeingEdited = true;
                                    fieldBeingEdited = (CustomTextField) holder.get();
                                    Minecraft.getInstance().setScreen(new ChooseSoundGUI(this, (CustomTextField) holder.get()));
                                })
                                .bounds(0, 0, 20, 20)
                                .build()
                );
                CustomTextField text = (CustomTextField) holder.get();
                text.setMaxLength(50);
                if(isFieldBeingEdited) {
                    text.setValue(fieldBeingEdited.getValue());
                } else {
                    text.setValue(((AmbienceWidgetSound) widget).getValue());
                }
                addWidget(holder);
                addWidget(button);
                widgetLink.put(widget, holder);
            }
            if(widget instanceof AmbienceWidgetScroll) {
                AmbienceWidgetScroll wScroll = (AmbienceWidgetScroll) widget;
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new ScrollListWidget(0, 0, widget.getWidth(), 20, 4, 16, 5, wScroll.getValues(), font, new ScrollListWidget.IPressable() {
                    @Override
                    public void onChange(ScrollListWidget list, int index, String name) {
                        wScroll.setValue(name);
                    }
                }));
                ScrollListWidget scrollListWidget = (ScrollListWidget) holder.get();
                scrollListWidget.setSelectionByString(wScroll.getValue());
                addWidget(holder);
                widgetLink.put(widget, holder);
            }
            if(widget instanceof AmbienceWidgetCheckbox) {
                AmbienceWidgetCheckbox wCheck = (AmbienceWidgetCheckbox) widget;
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new CheckboxWidget(0, 0, widget.getWidth(), 20, "", wCheck.getValue()));
                addWidget(holder);
                widgetLink.put(widget, holder);
            }
        }
    }

    private void updateCondWidgetPos() {
        int indexX = offset + separation;
        int indexY = offset + rowHeight + separation;
        for(AmbienceWidgetHolder element : condWidgets) {
            AbstractWidget widget = element.get();
            //new line if the widget that is about to be added leaks out of the main window
            if(indexX + widget.getWidth() + separation > texWidth - offset) {
                indexX = offset + separation;
                indexY += rowHeight + separation;
            }
            widget.setX(xTopLeft + indexX);
            widget.setY(yTopLeft + indexY + (rowHeight - widget.getHeight())/2);
            indexX += widget.getWidth() + separation;
            if(widget instanceof ScrollListWidget) ((ScrollListWidget)widget).updateWidgetPosition();
        }
    }

    @Override
    public void render(GuiGraphics matrix, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(matrix, p_render_1_, p_render_2_, p_render_3_);

        drawMainBackground(matrix);

        for(AmbienceWidgetHolder widget : condWidgets) {
            widget.get().render(matrix, p_render_1_, p_render_2_, p_render_3_);

            if(widget.get() instanceof ScrollListWidget) ((ScrollListWidget)widget.get()).render(matrix, p_render_1_, p_render_2_, p_render_3_);
        }

        editCond.render(matrix, p_render_1_, p_render_2_, p_render_3_);

        matrix.drawString(font, newCond.getName(), editCond.getX() + editCond.getWidth() + separation, editCond.getY() + 6, 0xFFFFFF);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public void setCond(AbstractCond cond) {
        this.newCond = cond;
    }

    private void addWidget(AmbienceWidgetHolder widget) {
        this.condWidgets.add(widget);
        addCustomWidget(widget.get());
        if(widget.get() instanceof ScrollListWidget)
            ((ScrollListWidget) widget.get()).addWidget(this);
    }

    public void drawMainBackground(GuiGraphics matrix) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        matrix.blit(BACKGROUND_TEXTURE, xTopLeft, yTopLeft, 0, 0, texWidth, texHeight);
    }

    @Override
    public void fetch(AbstractCond newCond, AbstractCond oldCond) {
        //Upon returning to this screen after "fetching" a condition, this happens
        for(AbstractAmbienceWidgetMessenger widget : widgetLink.keySet()) {
            if(widget instanceof AmbienceWidgetCond) {
                AmbienceWidgetCond wc = (AmbienceWidgetCond) widget;
                if(wc.getCond() == oldCond/*wc.getCond().equals(oldCond)*/) {
                    wc.setCond(newCond);
                    widgetLink.get(widget).get().setMessage(Component.literal(newCond.getListDescription()));
                    collectData();
                }
            }
        }
    }
}
