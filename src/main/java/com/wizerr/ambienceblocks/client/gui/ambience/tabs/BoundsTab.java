package com.wizerr.ambienceblocks.client.gui.ambience.tabs;

import com.wizerr.ambienceblocks.ambience.AmbienceData;
import com.wizerr.ambienceblocks.ambience.bounds.*;
import com.wizerr.ambienceblocks.ambience.util.AmbienceAxis;
import com.wizerr.ambienceblocks.ambience.util.AmbienceWorldSpace;
import com.wizerr.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.wizerr.ambienceblocks.client.gui.widgets.CheckboxWidget;
import com.wizerr.ambienceblocks.client.gui.widgets.TextInstance;
import com.wizerr.ambienceblocks.util.BoundsUtil;
import com.wizerr.ambienceblocks.util.ParsingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BoundsTab extends AbstractTab {
    private int boundType;

    TextInstance textBounds = new TextInstance(getBaseX(), getRowY(0) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.type") + " :", font);
    Button buttonBounds = Button.builder(
                    Component.literal("no"),
                    button -> moveToNextBoundType()
            )
            .bounds(getNeighbourX(textBounds), getRowY(0) + getOffsetY(20), 60, 20)
            .build();

    CheckboxWidget isGlobal = new CheckboxWidget(getNeighbourX(buttonBounds), getRowY(0), 20 + font.width("Global"), 20, "Global", false);
    CheckboxWidget isLocatable = new CheckboxWidget(getNeighbourX(buttonBounds), getRowY(0), 20 + font.width("Locatable"), 20, "Locatable", false);

    TextInstance textSphereRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.radius"), font);
    EditBox sphereRadius = new EditBox(font, getNeighbourX(textSphereRadius), getRowY(1), 40, 20, Component.empty());

    TextInstance textCylRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.radius"), font);
    EditBox cylRadius = new EditBox(font, getNeighbourX(textCylRadius), getRowY(1), 40, 20, Component.empty());
    TextInstance textCylLength = new TextInstance(getNeighbourX(cylRadius), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.length"), font);
    EditBox cylLength = new EditBox(font, getNeighbourX(textCylLength), getRowY(1), 40, 20, Component.empty());
    TextInstance textCylAxis = new TextInstance(getNeighbourX(cylLength), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.axis"), font);
    Button cylAxisButton = Button.builder(Component.literal("X"), button -> {
        moveToNextAxisCylinder();
    }).bounds(
            getNeighbourX(textCylAxis),
            getRowY(1) + getOffsetY(20),
            20,
            20
    ).build();
    /*guiRef.addButton(new Button(getNeighbourX(textCylAxis), getRowY(1) + getOffsetY(20), 20, 20, new TextComponent("X"), button -> {
        moveToNextAxisCylinder();
    }));*/
    AmbienceAxis cylAxis;

    TextInstance textCapRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.radius"), font);
    EditBox capRadius = new EditBox(font, getNeighbourX(textCapRadius), getRowY(1), 40, 20, Component.empty());
    TextInstance textCapLength = new TextInstance(getNeighbourX(capRadius), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.length"), font);
    EditBox capLength = new EditBox(font, getNeighbourX(textCapLength), getRowY(1), 40, 20, Component.empty());
    TextInstance textCapAxis = new TextInstance(getNeighbourX(capLength), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.axis"), font);
    /*Button capAxisButton = guiRef.addButton(new Button(getNeighbourX(textCapAxis), getRowY(1) + getOffsetY(20), 20, 20, new TextComponent("X"), button -> {
        System.out.println(button.visible);
        System.out.println(button.active);
        System.out.println(this.isActive());
        moveToNextAxisCapsule();
    }));*/
    Button capAxisButton = Button.builder(Component.literal("X"), button -> {
        moveToNextAxisCapsule();
    }).bounds(
            getNeighbourX(textCapAxis),
            getRowY(1) + getOffsetY(20),
            20,
            20
    ).build();
    AmbienceAxis capAxis;

    TextInstance textCubicX = new TextInstance(getBaseX(), getRowY(1) + getOffsetFontY(), 0xFFFFFF, "X", font);
    EditBox cubicX = new EditBox(font, getNeighbourX(textCubicX), getRowY(1), 40, 20, Component.empty());
    TextInstance textCubicY = new TextInstance(getNeighbourX(cubicX), getRowY(1) + getOffsetFontY(), 0xFFFFFF, "Y", font);
    EditBox cubicY = new EditBox(font, getNeighbourX(textCubicY), getRowY(1), 40, 20, Component.empty());
    TextInstance textCubicZ = new TextInstance(getNeighbourX(cubicY), getRowY(1) + getOffsetFontY(), 0xFFFFFF, "Z", font);
    EditBox cubicZ = new EditBox(font, getNeighbourX(textCubicZ), getRowY(1), 40, 20, Component.empty());

    TextInstance textOffset = new TextInstance(getBaseX(), getRowY(2) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.offset"), font);
    AmbienceWorldSpace offsetPos;
    Button offsetPosButton = Button.builder(Component.literal("X"), button -> {
        offsetPos = offsetPos.next();
        button.setMessage(Component.literal(offsetPos.getName()));
    }).bounds(
            getNeighbourX(textOffset),
            getRowY(2) + getOffsetY(20),
            20,
            20
    ).build();
    TextInstance textOffsetX = new TextInstance(getNeighbourX(offsetPosButton), getRowY(2) + getOffsetFontY(), 0xFFFFFF, "X", font);
    EditBox offsetX = new EditBox(font, getNeighbourX(textOffsetX), getRowY(2), 40, 20, Component.empty());
    TextInstance textOffsetY = new TextInstance(getNeighbourX(offsetX), getRowY(2) + getOffsetFontY(), 0xFFFFFF, "Y", font);
    EditBox offsetY = new EditBox(font, getNeighbourX(textOffsetY), getRowY(2), 40, 20, Component.empty());
    TextInstance textOffsetZ = new TextInstance(getNeighbourX(offsetY), getRowY(2) + getOffsetFontY(), 0xFFFFFF, "Z", font);
    EditBox offsetZ = new EditBox(font, getNeighbourX(textOffsetZ), getRowY(2), 40, 20, Component.empty());

    private List<AbstractWidget> sphereWidgets;// = new ArrayList<>();
    private List<AbstractWidget> cylinderWidgets;// = new ArrayList<>();
    private List<AbstractWidget> capsuleWidgets;
    private List<AbstractWidget> cubicWidgets;// = new ArrayList<>();
    private List<AbstractWidget> noneWidgets;// = new ArrayList<>();

    public BoundsTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Bounds";
    }

    @Override
    public String getShortName() {
        return "Bnds";
    }

    @Override
    public void initialInit() {
        sphereWidgets = new ArrayList<>();
        cylinderWidgets = new ArrayList<>();
        capsuleWidgets = new ArrayList<>();
        cubicWidgets = new ArrayList<>();
        noneWidgets = new ArrayList<>();

        addButton(buttonBounds);
        addWidget(isGlobal);
        addWidget(isLocatable);

        sphereWidgets.add(textSphereRadius);
        sphereWidgets.add(sphereRadius);
        sphereRadius.setFilter(ParsingUtil.decimalNumberFilter);
        sphereRadius.setMaxLength(6);
        addWidget(sphereRadius);

        cylinderWidgets.add(textCylRadius);
        cylinderWidgets.add(cylRadius);
        cylRadius.setFilter(ParsingUtil.decimalNumberFilter);
        cylRadius.setMaxLength(6);
        addWidget(cylRadius);
        cylinderWidgets.add(textCylLength);
        cylinderWidgets.add(cylLength);
        cylLength.setFilter(ParsingUtil.decimalNumberFilter);
        cylLength.setMaxLength(6);
        addWidget(cylLength);
        cylinderWidgets.add(textCylAxis);
        cylinderWidgets.add(cylAxisButton);
        addButton(cylAxisButton);

        capsuleWidgets.add(textCapRadius);
        capsuleWidgets.add(capRadius);
        capRadius.setFilter(ParsingUtil.decimalNumberFilter);
        capRadius.setMaxLength(6);
        addWidget(capRadius);
        capsuleWidgets.add(textCapLength);
        capsuleWidgets.add(capLength);
        capLength.setFilter(ParsingUtil.decimalNumberFilter);
        capLength.setMaxLength(6);
        addWidget(capLength);
        capsuleWidgets.add(textCapAxis);
        capsuleWidgets.add(capAxisButton);
        addButton(capAxisButton);

        cubicWidgets.add(textCubicX);
        cubicWidgets.add(cubicX);
        cubicX.setFilter(ParsingUtil.decimalNumberFilter);
        cubicX.setMaxLength(6);
        addWidget(cubicX);
        cubicWidgets.add(textCubicY);
        cubicWidgets.add(cubicY);
        cubicY.setFilter(ParsingUtil.decimalNumberFilter);
        cubicY.setMaxLength(6);
        addWidget(cubicY);
        cubicWidgets.add(textCubicZ);
        cubicWidgets.add(cubicZ);
        cubicZ.setFilter(ParsingUtil.decimalNumberFilter);
        cubicZ.setMaxLength(6);
        addWidget(cubicZ);

        addButton(offsetPosButton);
        offsetX.setFilter(ParsingUtil.negativeDecimalNumberFilter);
        offsetX.setMaxLength(10);
        addWidget(offsetX);
        offsetY.setFilter(ParsingUtil.negativeDecimalNumberFilter);
        offsetY.setMaxLength(10);
        addWidget(offsetY);
        offsetZ.setFilter(ParsingUtil.negativeDecimalNumberFilter);
        offsetZ.setMaxLength(10);
        addWidget(offsetZ);

        resetBoundFields();
        setBoundType(0);

        resetShownFields();
    }

    @Override
    public void updateWidgetPosition() {
        textBounds.setPosition(getBaseX(), getRowY(0) + getOffsetFontY());
        buttonBounds.setPosition(getNeighbourX(textBounds), getRowY(0) + getOffsetY(20));

        isGlobal.setPosition(getNeighbourX(buttonBounds), getRowY(0));
        isLocatable.setPosition(getNeighbourX(isGlobal) + horizontalSeparation, getRowY(0));

        textSphereRadius.setPosition(getBaseX(), getRowY(1) + getOffsetFontY());
        sphereRadius.setPosition(getNeighbourX(textSphereRadius), getRowY(1));

        textCylRadius.setPosition(getBaseX(), getRowY(1) + getOffsetFontY());
        cylRadius.setPosition(getNeighbourX(textCylRadius), getRowY(1));
        textCylLength.setPosition(getNeighbourX(cylRadius), getRowY(1) + getOffsetFontY());
        cylLength.setPosition(getNeighbourX(textCylLength), getRowY(1));
        textCylAxis.setPosition(getNeighbourX(cylLength), getRowY(1) + getOffsetFontY());
        cylAxisButton.setPosition(getNeighbourX(textCylAxis), getRowY(1) + getOffsetY(20));

        textCapRadius.setPosition(getBaseX(), getRowY(1) + getOffsetFontY());
        capRadius.setPosition(getNeighbourX(textCapRadius), getRowY(1));
        textCapLength.setPosition(getNeighbourX(capRadius), getRowY(1) + getOffsetFontY());
        capLength.setPosition(getNeighbourX(textCapLength), getRowY(1));
        textCapAxis.setPosition(getNeighbourX(capLength), getRowY(1) + getOffsetFontY());
        capAxisButton.setPosition(getNeighbourX(textCapAxis), getRowY(1) + getOffsetY(20));

        textCubicX.setPosition(getBaseX(), getRowY(1) + getOffsetFontY());
        cubicX.setPosition(getNeighbourX(textCubicX), getRowY(1));
        textCubicY.setPosition(getNeighbourX(cubicX), getRowY(1) + getOffsetFontY());
        cubicY.setPosition(getNeighbourX(textCubicY), getRowY(1));
        textCubicZ.setPosition(getNeighbourX(cubicY), getRowY(1) + getOffsetFontY());
        cubicZ.setPosition(getNeighbourX(textCubicZ), getRowY(1));

        textOffset.setPosition(getBaseX(), getRowY(2) + getOffsetFontY());
        offsetPosButton.setPosition(getNeighbourX(textOffset), getRowY(2) + getOffsetY(20));
        textOffsetX.setPosition(getNeighbourX(offsetPosButton), getRowY(2) + getOffsetFontY());
        offsetX.setPosition(getNeighbourX(textOffsetX), getRowY(2));
        textOffsetY.setPosition(getNeighbourX(offsetX), getRowY(2) + getOffsetFontY());
        offsetY.setPosition(getNeighbourX(textOffsetY), getRowY(2));
        textOffsetZ.setPosition(getNeighbourX(offsetY), getRowY(2) + getOffsetFontY());
        offsetZ.setPosition(getNeighbourX(textOffsetZ), getRowY(2));
    }

    private void moveToNextAxisCapsule() {
        capAxis = capAxis.next();
        capAxisButton.setMessage(Component.literal(capAxis.toString()));
    }

    private void moveToNextAxisCylinder() {
        cylAxis = cylAxis.next();
        cylAxisButton.setMessage(Component.literal(cylAxis.toString()));
    }

    @Override
    public void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks) {
        textBounds.render(matrix, mouseX, mouseY);
        buttonBounds.render(matrix, mouseX, mouseY, partialTicks);
        isGlobal.render(matrix, mouseX, mouseY, partialTicks);
        isLocatable.render(matrix, mouseX, mouseY, partialTicks);

        textSphereRadius.render(matrix, mouseX, mouseY);
        sphereRadius.render(matrix, mouseX, mouseY, partialTicks);

        textCylRadius.render(matrix, mouseX, mouseY);
        cylRadius.render(matrix, mouseX, mouseY, partialTicks);
        textCylLength.render(matrix, mouseX, mouseY);
        cylLength.render(matrix, mouseX, mouseY, partialTicks);
        textCylAxis.render(matrix, mouseX, mouseY);
        cylAxisButton.render(matrix, mouseX, mouseY, partialTicks);

        textCapRadius.render(matrix, mouseX, mouseY);
        capRadius.render(matrix, mouseX, mouseY, partialTicks);
        textCapLength.render(matrix, mouseX, mouseY);
        capLength.render(matrix, mouseX, mouseY, partialTicks);
        textCapAxis.render(matrix, mouseX, mouseY);
        capAxisButton.render(matrix, mouseX, mouseY, partialTicks);

        textCubicX.render(matrix, mouseX, mouseY);
        cubicX.render(matrix, mouseX, mouseY, partialTicks);
        textCubicY.render(matrix, mouseX, mouseY);
        cubicY.render(matrix, mouseX, mouseY, partialTicks);
        textCubicZ.render(matrix, mouseX, mouseY);
        cubicZ.render(matrix, mouseX, mouseY, partialTicks);

        textOffset.render(matrix, mouseX, mouseY);
        offsetPosButton.render(matrix, mouseX, mouseY, partialTicks);
        textOffsetX.render(matrix, mouseX, mouseY);
        offsetX.render(matrix, mouseX, mouseY, partialTicks);
        textOffsetY.render(matrix, mouseX, mouseY);
        offsetY.render(matrix, mouseX, mouseY, partialTicks);
        textOffsetZ.render(matrix, mouseX, mouseY);
        offsetZ.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(GuiGraphics matrix, int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if (buttonBounds.isHoveredOrFocused())
        {
            if(boundType == SphereBounds.id) {
                list.add(ChatFormatting.RED + "Sphere Bounds");
                list.add(ChatFormatting.WHITE + "Sphere bounds that cover a radius all around it.");
                list.add(ChatFormatting.GRAY + "(If the sound isn't global, the volume will be scaled by the distance of the player from the source)");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
            }

            if(boundType == CylinderBounds.id) {
                list.add(ChatFormatting.RED + "Cylinder Bounds");
                list.add(ChatFormatting.WHITE + "Cylinder bounds that cover a radius all around it except the height which can be changed.");
                list.add(ChatFormatting.GRAY + "(If the sound isn't global, the volume will be unaffected by height and only scaled by horizontal distance)");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
            }

            if(boundType == CapsuleBounds.id) {
                list.add(ChatFormatting.RED + "Capsule Bounds");
                list.add(ChatFormatting.WHITE + "Capsule bounds that work almost exactly like the cylinder variant, it adds two sphere detection to each end.");
                list.add(ChatFormatting.GRAY + "(If the sound isn't global, the volume will be unaffected by height and only scaled by horizontal distance (and regular sphere stuff on each ends))");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
            }

            if(boundType == CubicBounds.id) {
                list.add(ChatFormatting.RED + "Cubic Bounds");
                list.add(ChatFormatting.WHITE + "Cubic bounds that cover a cubic area of origin by the value of x, y and z.");
                list.add(ChatFormatting.GRAY + "(If the sound isn't global, the volume will be scaled as if this was a Sphere Bounds which won't cover the whole area)");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
            }

            if(boundType == NoneBounds.id) {
                list.add(ChatFormatting.RED + "None Bounds");
                list.add(ChatFormatting.WHITE + "Will always play as long as the tile is loaded on the client.");
                list.add(ChatFormatting.GRAY + "(The volume will not change with distance regardless of if the tile is set to be global)");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
            }
        }

        if(isGlobal.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Global");
            list.add("If global is checked the volume won't scale with distance, otherwise it will.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }

        if(isLocatable.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Locatable");
            list.add("If locatable is checked the sound will play in a 3D space, otherwise it plays globally like menu sounds.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }

        if(textOffset.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Offset");
            list.add("Shifts the origin of the bounds to a certain position.");
            list.add(ChatFormatting.GRAY + "(Useful for hiding the block)");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
    }

    @Override
    public void tick() {
        sphereRadius.tick();

        cylRadius.tick();
        cylLength.tick();

        cubicX.tick();
        cubicY.tick();
        cubicZ.tick();
    }

    @Override
    public void setFieldFromData(AmbienceData data) {
        //setCheckBoxChecked(isGlobal, data.isGlobal());
        isGlobal.setChecked(data.isGlobal());
        isLocatable.setChecked(data.isLocatable());

        offsetPos = data.getSpace();
        offsetPosButton.setMessage(Component.literal(offsetPos.getName()));

        loadBoundType(data);

        Vec3 pos = data.getOffset();
        offsetX.setValue(String.valueOf(pos.x()));
        offsetY.setValue(String.valueOf(pos.y()));
        offsetZ.setValue(String.valueOf(pos.z()));
    }

    @Override
    public void setDataFromField(AmbienceData data) {
        data.setGlobal(isGlobal.isChecked());
        data.setLocatable(isLocatable.isChecked());

        data.setSpace(offsetPos);

        if(boundType == SphereBounds.id) {
            SphereBounds bounds = new SphereBounds();

            bounds.setRadius(ParsingUtil.tryParseDouble(sphereRadius.getValue()));

            data.setBounds(bounds);
        }

        if(boundType == CylinderBounds.id) {
            CylinderBounds bounds = new CylinderBounds();

            bounds.setRadius(ParsingUtil.tryParseDouble(cylRadius.getValue()));
            bounds.setLength(ParsingUtil.tryParseDouble(cylLength.getValue()));
            bounds.setAxis(cylAxis);

            data.setBounds(bounds);
        }

        if(boundType == CapsuleBounds.id) {
            CapsuleBounds bounds = new CapsuleBounds();

            bounds.setRadius(ParsingUtil.tryParseDouble(capRadius.getValue()));
            bounds.setLength(ParsingUtil.tryParseDouble(capLength.getValue()));
            bounds.setAxis(capAxis);

            data.setBounds(bounds);
        }

        if(boundType == CubicBounds.id) {
            CubicBounds bounds = new CubicBounds();

            bounds.setxSize(ParsingUtil.tryParseDouble(cubicX.getValue()));
            bounds.setySize(ParsingUtil.tryParseDouble(cubicY.getValue()));
            bounds.setzSize(ParsingUtil.tryParseDouble(cubicZ.getValue()));

            data.setBounds(bounds);
        }

        if(boundType == NoneBounds.id) {
            data.setBounds(new NoneBounds());
        }

        data.setOffset(new Vec3(
                ParsingUtil.tryParseDouble(offsetX.getValue()),
                ParsingUtil.tryParseDouble(offsetY.getValue()),
                ParsingUtil.tryParseDouble(offsetZ.getValue())
        ));

        //data.setOffset(new BlockPos(offsetX.getValue()));
    }

    @Override
    public void onActivate() {
        //refresh boundType widgets
        updateBoundsField();
    }

    @Override
    public void onDeactivate() {
        resetShownFields();
    }

    public void moveToNextBoundType() {
        if(boundType == BoundsUtil.lastBoundType)
        {
            setBoundType(0);
        }
        else
        {
            setBoundType(boundType + 1);
        }
    }

    public void loadBoundType(AmbienceData data) {
        setBoundType(data.getBounds().getID());
        if(data.getBounds() instanceof SphereBounds) {
            SphereBounds bounds = (SphereBounds) data.getBounds();
            sphereRadius.setValue(String.valueOf(bounds.getRadius()));
        }
        if(data.getBounds() instanceof CylinderBounds) {
            CylinderBounds bounds = (CylinderBounds) data.getBounds();
            cylRadius.setValue(String.valueOf(bounds.getRadius()));
            cylLength.setValue(String.valueOf(bounds.getLength()));
            cylAxisButton.setMessage(Component.literal(bounds.getAxis().toString()));
            cylAxis = bounds.getAxis();
        }
        if(data.getBounds() instanceof CapsuleBounds) {
            CapsuleBounds bounds = (CapsuleBounds) data.getBounds();
            capRadius.setValue(String.valueOf(bounds.getRadius()));
            capLength.setValue(String.valueOf(bounds.getLength()));
            capAxisButton.setMessage(Component.literal(bounds.getAxis().toString()));
            capAxis = bounds.getAxis();
        }
        if(data.getBounds() instanceof CubicBounds) {
            CubicBounds bounds = (CubicBounds) data.getBounds();
            cubicX.setValue(String.valueOf(bounds.getxSize()));
            cubicY.setValue(String.valueOf(bounds.getySize()));
            cubicZ.setValue(String.valueOf(bounds.getzSize()));
        }
        resetShownFields();
    }

    public void setBoundType(int type) {
        boundType = type;
        buttonBounds.setMessage(Component.literal(BoundsUtil.getBoundsFromType(boundType).getName()));
        updateBoundsField();
    }

    public void updateBoundsField() {
        resetShownFields();
        //System.out.println(BoundsUtil.getBoundsFromType(boundType).getName());
        //System.out.println(sphereWidgets.size());
        if(boundType == SphereBounds.id) for(AbstractWidget widget : sphereWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == CylinderBounds.id) for(AbstractWidget widget : cylinderWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == CapsuleBounds.id) for(AbstractWidget widget : capsuleWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == CubicBounds.id) for(AbstractWidget widget : cubicWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == NoneBounds.id) for(AbstractWidget widget : noneWidgets) { widget.active = true; widget.visible = true; }
    }

    public void resetShownFields() {
        for(AbstractWidget widget : sphereWidgets) { widget.active = false; widget.visible = false; }
        for(AbstractWidget widget : cylinderWidgets) { widget.active = false; widget.visible = false; }
        for(AbstractWidget widget : capsuleWidgets) { widget.active = false; widget.visible = false; }
        for(AbstractWidget widget : cubicWidgets) { widget.active = false; widget.visible = false; }
        for(AbstractWidget widget : noneWidgets) { widget.active = false; widget.visible = false; }
    }

    public void resetBoundFields() {
        sphereRadius.setValue(String.valueOf(0));

        cylRadius.setValue(String.valueOf(0));
        cylLength.setValue(String.valueOf(0));
        cylAxis = AmbienceAxis.Y;
        cylAxisButton.setMessage(Component.literal(cylAxis.toString()));

        capRadius.setValue(String.valueOf(0));
        capLength.setValue(String.valueOf(0));
        capAxis = AmbienceAxis.Y;
        capAxisButton.setMessage(Component.literal(capAxis.toString()));

        cubicX.setValue(String.valueOf(0));
        cubicY.setValue(String.valueOf(0));
        cubicZ.setValue(String.valueOf(0));
    }
}
