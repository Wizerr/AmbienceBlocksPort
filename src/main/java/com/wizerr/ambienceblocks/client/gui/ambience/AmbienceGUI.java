package com.wizerr.ambienceblocks.client.gui.ambience;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wizerr.ambienceblocks.Main;
import com.wizerr.ambienceblocks.ambience.AmbienceData;
import com.wizerr.ambienceblocks.ambience.IAmbienceSource;
import com.wizerr.ambienceblocks.ambience.util.AmbienceType;
import com.wizerr.ambienceblocks.client.gui.ambience.tabs.*;
import com.wizerr.ambienceblocks.packets.PacketUpdateAmbienceTE;
import com.wizerr.ambienceblocks.tileentity.AmbienceTileEntity;
import com.wizerr.ambienceblocks.util.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class AmbienceGUI extends AmbienceScreen {
    private final IAmbienceSource source;
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/gui/ambience_gui.png");

    public static final int texWidth = 256;
    public static final int texHeight = 184;
    public int xTopLeft, yTopLeft;

    public static final int tabEdgeWidth = 16, tabHeight = 16;

    private MainTab mainTab = new MainTab(this);
    private MusicTab musicTab = new MusicTab(this);
    private BoundsTab boundsTab = new BoundsTab(this);
    //private FuseTab fuseTab = new FuseTab(this);
    private PriorityTab priorityTab = new PriorityTab(this);
    private DelayTab delayTab = new DelayTab(this);
    private CondTab condTab = new CondTab(this);
    private MiscTab miscTab = new MiscTab(this);

    private AbstractTab highlightedTab;

    private Button confirmChanges;
    private Button cancel;

    private boolean help;
    private Button bHelp;

    private boolean initialized = false;
    //private boolean closing = false;

    public AmbienceGUI(IAmbienceSource source) {
        super(Component.translatable("narrator.screen.globalambiencegui"));

        this.source = source;
    }

    @Override
    public void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);

        drawMainBackground(matrix);

        drawTabs(matrix, mouseX, mouseY);

        if(highlightedTab != null)
            highlightedTab.render(matrix, mouseX, mouseY, partialTicks);

        drawHelp(matrix);

        if(help && highlightedTab != null)
            highlightedTab.renderToolTip(matrix, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        List<AbstractTab> tabs = getAllTabs();

        if(!initialized)
        {
            for(AbstractTab tab : tabs) {
                tab.updateMetaValues(this);
                tab.initialInit();
                tab.updateWidgetPosition();
                tab.deactivate();
            }

            setHighlightedTab(mainTab);

            loadDataFromTile();

            initialized = true;
        }
        else
        {
            for(AbstractTab tab : tabs) {
                tab.updateMetaValues(this);
                tab.refreshWidgets();
                tab.updateWidgetPosition();
            }
        }

        confirmChanges = addRenderableWidget(
                Button.builder(Component.literal("Confirm Changes"), button -> {
                    saveDataToTile();
                    quit();
                }).bounds(xTopLeft + 4, yTopLeft + texHeight + 4, 100, 20).build()
        );

        cancel = addRenderableWidget(
                Button.builder(Component.literal("Cancel"), button -> quit())
                        .bounds(xTopLeft + texWidth - 80 - 4, yTopLeft + texHeight + 4, 80, 20)
                        .build()
        );

        help = false;
        bHelp = addRenderableWidget(Button.builder(Component.literal(""), button -> {
                    clickHelp();
                })
                .bounds(xTopLeft + texWidth - 16 - 8, yTopLeft + texHeight - 16 - 8, 16, 16)
                .build());
    }

    private void clickHelp() {
        help = !help;
    }

    private void loadDataFromTile() {
        setData(source.getData());
    }

    private void saveDataToTile() {

        if(isSourceTypeTileEntity()) {
            PacketHandler.NET.sendToServer(new PacketUpdateAmbienceTE(((AmbienceTileEntity) source).getBlockPos(), getData()));
        } else {
            if(previousScreen instanceof CompendiumGUI) {
                CompendiumGUI gui = (CompendiumGUI) previousScreen;
                gui.applyData(getData());
            }
        }
    }

    public boolean isSourceTypeTileEntity() {
        return source instanceof AmbienceTileEntity;
    }

    public AmbienceData getData() {
        AmbienceData data = new AmbienceData();

        for(AbstractTab tab : getActiveTabs()) tab.setDataFromField(data);

        return data;
    }

    public void setData(AmbienceData data) {
        mainTab.setFieldFromData(data);

        for(AbstractTab tab : getActiveTabs()) if(!(tab instanceof MainTab)) tab.setFieldFromData(data);
    }

    @Override
    public void tick() {
        super.tick();

        if(highlightedTab != null)
            highlightedTab.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseType) {
        List<AbstractTab> tabs = getActiveTabs();
        for(int i = 0; i < tabs.size(); i++) {
            if(isMouseInTab((int)mouseX, (int)mouseY, i)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                setHighlightedTab(getActiveTabs().get(i));
                return super.mouseClicked(mouseX, mouseY, mouseType);
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseType);
    }

    private AbstractTab getHighlightedTab() {
        return highlightedTab;
    }

    private void setHighlightedTab(AbstractTab tab) {
        if(highlightedTab != null)
            highlightedTab.deactivate();

        highlightedTab = tab;
        highlightedTab.activate();
    }

    private List<AbstractTab> getAllTabs() {
        List<AbstractTab> list = new ArrayList<>();

        list.add(mainTab);

        list.add(musicTab);

        list.add(boundsTab);

        //list.add(fuseTab);

        list.add(priorityTab);

        list.add(delayTab);

        list.add(condTab);

        list.add(miscTab);

        return list;
    }

    private List<AbstractTab> getActiveTabs() {
        List<AbstractTab> list = new ArrayList<>();

        list.add(mainTab);

        try {
            if(AmbienceType.MUSIC.equals(mainTab.getType()))
                list.add(musicTab);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if(isSourceTypeTileEntity()) {
            list.add(boundsTab);
        }

        //if(mainTab.shouldFuse.isChecked())
        //    list.add(fuseTab);

        if(mainTab.usePriority.isChecked())
            list.add(priorityTab);

        if(mainTab.useDelay.isChecked() && AmbienceType.AMBIENT.equals(mainTab.getType()))
            list.add(delayTab);

        if(mainTab.useCondition.isChecked())
            list.add(condTab);

        list.add(miscTab);

        return list;
    }

    private int getTabWidth() {
        return texWidth/getActiveTabs().size();
    }

    private TabState getTabState(int mouseX, int mouseY, AbstractTab tab) {
        if(getHighlightedTab() == tab) return TabState.HIGHLIGHTED;
        if(getHoveredTab(mouseX, mouseY) == tab) return TabState.HOVERED;
        return TabState.NEUTRAL;
    }

    private AbstractTab getHoveredTab(int mouseX, int mouseY) {
        List<AbstractTab> list = getActiveTabs();
        for(int i = 0; i < list.size(); i++) {
            if(isMouseInTab(mouseX, mouseY, i))
                return list.get(i);
        }
        return null;
    }

    private boolean isMouseInTab(int mouseX, int mouseY, int index) {
        int size = getTabWidth();
        return mouseX > xTopLeft + index * size && mouseX < xTopLeft + index * size + size && mouseY > yTopLeft && mouseY < yTopLeft + tabHeight;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void drawMainBackground(GuiGraphics guiGraphics) {
        guiGraphics.blit(BACKGROUND_TEXTURE, xTopLeft, yTopLeft + tabHeight,
                0, tabHeight, texWidth, texHeight - tabHeight);
    }

    public void drawTab(GuiGraphics matrix, int x, int size, String text, TabState state) {
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        if(size < 32) size = 32;
        int spaceToFillOut = size - 32;
        int spaceCount = 0;

        matrix.blit(BACKGROUND_TEXTURE, xTopLeft + x, yTopLeft, 0, state.getySpriteOffset(), 16, 16);
        while (spaceToFillOut > spaceCount) {
            if (spaceToFillOut - spaceCount < 16) {
                matrix.blit(BACKGROUND_TEXTURE, xTopLeft + x + tabEdgeWidth + spaceCount, yTopLeft, 16, state.getySpriteOffset(), spaceToFillOut - spaceCount, 16);
                spaceCount = spaceToFillOut;
            } else {
                matrix.blit(BACKGROUND_TEXTURE, xTopLeft + x + tabEdgeWidth + spaceCount, yTopLeft, 16, state.getySpriteOffset(), 16, 16);
                spaceCount += 16;
            }
        }

        matrix.blit(BACKGROUND_TEXTURE, xTopLeft + x + size - tabEdgeWidth, yTopLeft, 32, state.getySpriteOffset(), 16, 16);

        matrix.drawCenteredString(font, text, xTopLeft + x + size/2, yTopLeft + (tabHeight - font.lineHeight) / 2 + 2, 0xFFFFFF);
    }

    private void drawTabs(GuiGraphics matrix, int mouseX, int mouseY) {
        List<AbstractTab> options = getActiveTabs();
        int tabSize = getTabWidth();
        String tabName;
        for(int i = 0; i < options.size(); i++) {

            if(font.width(options.get(i).getName()) < tabSize - 8)
                tabName = options.get(i).getName();
            else
                tabName = options.get(i).getShortName();

            if(i != options.size() - 1)
                drawTab(matrix, tabSize * i, tabSize, tabName, getTabState(mouseX, mouseY, options.get(i)));
            else
                drawTab(matrix, tabSize * i, texWidth - tabSize * i, tabName, getTabState(mouseX, mouseY, options.get(i)));
        }
    }

    private void drawHelp(GuiGraphics matrix) {
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        matrix.blit(BACKGROUND_TEXTURE, xTopLeft + texWidth - 16 - 8, yTopLeft + texHeight - 16 - 8, 48, getHelpState().getySpriteOffset(), 16, 16);
    }

    private TabState getHelpState() {
        if(help) return TabState.HIGHLIGHTED;
        if(bHelp.isHovered()) return TabState.HOVERED;
        return TabState.NEUTRAL;
    }

    //meta
    public void forceUpdateCondList() {
        condTab.updateGuiCondList();
    }


    enum TabState {
        NEUTRAL(AmbienceGUI.texHeight),
        HIGHLIGHTED(AmbienceGUI.texHeight + 16),
        HOVERED(AmbienceGUI.texHeight + 32);

        public int getySpriteOffset() {
            return ySpriteOffset;
        }

        private final int ySpriteOffset;

        TabState(int ySpriteOffset) {
            this.ySpriteOffset = ySpriteOffset;
        }
    }
}
