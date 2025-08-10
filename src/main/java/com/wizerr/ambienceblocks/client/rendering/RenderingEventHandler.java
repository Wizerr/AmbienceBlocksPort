package com.wizerr.ambienceblocks.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.wizerr.ambienceblocks.Main;
import com.wizerr.ambienceblocks.ambience.bounds.AbstractBounds;
import com.wizerr.ambienceblocks.ambience.bounds.SphereBounds;
import com.wizerr.ambienceblocks.client.ambience.AmbienceController;
import com.wizerr.ambienceblocks.tileentity.AmbienceTileEntity;
import com.wizerr.ambienceblocks.registry.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderingEventHandler {
    public final static int cWhite = 0xE0E0E0;//14737632;
    public final static int cRed = 0xE02020;//14680064;
    public final static int cGreen = 0x20E020;//57344;
    public final static int cBlue = 0x2020E0;//224;

    private final static float scale = 2f;
    private final static int eventListLimit = 20;
    private final static List<AmbienceEvent> eventList = new ArrayList<>();
    public static final float BOUND_SEPARATION = 0.998f;

    @SubscribeEvent
    public static void renderDebug(RenderGuiOverlayEvent.Post event) {
        if(!AmbienceController.debugMode)
            return;

        if(Minecraft.getInstance().options.renderDebug)
            return;

        if(!event.getOverlay().equals(VanillaGuiOverlay.DEBUG_TEXT.type()))
            return;

        RenderSystem.defaultBlendFunc();
        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = guiGraphics.pose();
        Font fontrenderer = Minecraft.getInstance().font;
        int width = event.getWindow().getGuiScaledWidth();

        poseStack.pushPose();
        poseStack.scale(1/scale, 1/scale, 1/scale);

        ArrayList<String> listL = new ArrayList<>(getLeft());

        int top = 2;
        for (String msg : listL) {
            if (msg == null) continue;
            int textWidth = fontrenderer.width(msg);
            // Draw background
            guiGraphics.fill(
                    1,
                    top - 1,
                    2 + textWidth + 1,
                    top + fontrenderer.lineHeight - 1,
                    -1873784752
            );
            // Draw text
            guiGraphics.drawString(
                    fontrenderer,
                    msg,
                    2,
                    top,
                    cWhite,
                    false
            );
            top += fontrenderer.lineHeight;
        }

        top = 2;
        for(AmbienceEvent e : eventList) {
            if (e.msg == null) continue;
            int textWidth = fontrenderer.width(e.msg);
            int left = (int) (width * scale - 2 - textWidth);
            // Draw background
            guiGraphics.fill(
                    left - 1,
                    top - 1,
                    left + textWidth + 1,
                    top + fontrenderer.lineHeight - 1,
                    -1873784752
            );
            // Draw text
            guiGraphics.drawString(
                    fontrenderer,
                    e.msg,
                    left,
                    top,
                    e.color,
                    false
            );
            top += fontrenderer.lineHeight;

            if (e.src == null) continue;
            textWidth = fontrenderer.width(e.src);
            left = (int) (width * scale - 2 - textWidth);
            // Draw background
            guiGraphics.fill(
                    left - 1,
                    top - 1,
                    left + textWidth + 1,
                    top + fontrenderer.lineHeight - 1,
                    -1873784752
            );
            // Draw text
            guiGraphics.drawString(
                    fontrenderer,
                    e.src,
                    left,
                    top,
                    e.color,
                    false
            );
            top += fontrenderer.lineHeight;
        }

        poseStack.popPose();
    }

    private static ArrayList<String> getLeft() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Sounds being played");
        AmbienceController.instance.soundsList.forEach(sound -> list.add(sound.toString()));
        list.add("Sounds being delayed");
        AmbienceController.instance.delayList.forEach(delay -> list.add(delay.toString()));
        return list;
    }


    public static void clearEvent() {
        eventList.clear();
    }

    public static void addEvent(String msg, AmbienceController.EventContext ctx) {
        if(eventList.size() >= eventListLimit)
            eventList.remove(0);

        eventList.add(new AmbienceEvent(msg, ctx.getComment(), ctx.getColor()));
    }

    private static class AmbienceEvent {
        private String msg;
        private String src;
        private int color;

        public AmbienceEvent(String msg, String src, int color) {
            this.msg = msg;
            this.src = src;
            this.color = color;
        }
    }

    @SubscribeEvent
    public static void renderOverlay(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        boolean holdingFinder = mc.player.isCreative() &&
                (mc.player.getMainHandItem().is(RegistryHandler.AMBIENCE_BLOCK_FINDER.get()) ||
                        mc.player.getOffhandItem().is(RegistryHandler.AMBIENCE_BLOCK_FINDER.get()));

        if (!holdingFinder) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        for (AmbienceTileEntity tile : AmbienceController.instance.getListOfLoadedAmbienceTiles()) {
            float[] c = tile.data.getColor();
            renderBlockOutlineAt(poseStack, bufferSource, tile.getBlockPos(), c);
        }

        bufferSource.endBatch();
    }

    private static void renderBlockBoundShape(BufferBuilder buffer, AmbienceTileEntity tile, float[] c) {
        AbstractBounds bounds = tile.getData().getBounds();
        if(!(bounds instanceof SphereBounds))
            return;

        float ox = (float) tile.getOrigin().x;
        float oy = (float) tile.getOrigin().y;
        float oz = (float) tile.getOrigin().z;

        float a = c[3] * 0.1f;
        float r = c[0];
        float g = c[1];
        float b = c[2];

        double radius = ((SphereBounds) bounds).getRadius();

        float ax, ay, az, ix, iy, iz, alpha, beta; // Storage for coordinates and angles
        int gradation = 10;
        float abx, aby, aplusbx, aplusby, abplusx, abplusy, aplusbplusx, aplusbplusy, abz, aplusbz;

        for (alpha = (float) 0.0; alpha + Math.PI/gradation < Math.PI; alpha += Math.PI/gradation)
        {
            for (beta = (float) 0.0; beta < 2*Math.PI; beta += Math.PI/gradation)
            {

                abx = (float) (radius * Math.cos(beta) * Math.sin(alpha));
                aby = (float) (radius * Math.sin(beta) * Math.sin(alpha));
                aplusbx = (float) (radius * Math.cos(beta) * Math.sin(alpha + Math.PI/gradation));
                aplusby = (float) (radius * Math.sin(beta) * Math.sin(alpha + Math.PI/gradation));
                abplusx = (float) (radius * Math.cos(beta + Math.PI/gradation) * Math.sin(alpha));
                abplusy = (float) (radius * Math.sin(beta + Math.PI/gradation) * Math.sin(alpha));
                aplusbplusx = (float) (radius * Math.cos(beta + Math.PI/gradation) * Math.sin(alpha + Math.PI/gradation));
                aplusbplusy = (float) (radius * Math.sin(beta + Math.PI/gradation) * Math.sin(alpha + Math.PI/gradation));

                abz = (float) (radius * Math.cos(alpha));
                aplusbz = (float) (radius * Math.cos(alpha + Math.PI/gradation));

                buffer.vertex(abplusx * BOUND_SEPARATION + ox, abplusy * BOUND_SEPARATION + oy, abz * BOUND_SEPARATION + oz).color(r, g, b, a).endVertex();
                buffer.vertex(aplusbplusx * BOUND_SEPARATION + ox, aplusbplusy * BOUND_SEPARATION + oy, aplusbz * BOUND_SEPARATION + oz).color(r, g, b, a).endVertex();
                buffer.vertex(aplusbx * BOUND_SEPARATION + ox, aplusby * BOUND_SEPARATION + oy, aplusbz * BOUND_SEPARATION + oz).color(r, g, b, a).endVertex();

                buffer.vertex(aplusbx * BOUND_SEPARATION + ox, aplusby * BOUND_SEPARATION + oy, aplusbz * BOUND_SEPARATION + oz).color(r, g, b, a).endVertex();
                buffer.vertex(abx * BOUND_SEPARATION + ox, aby * BOUND_SEPARATION + oy, abz * BOUND_SEPARATION + oz).color(r, g, b, a).endVertex();
                buffer.vertex(abplusx * BOUND_SEPARATION + ox, abplusy * BOUND_SEPARATION + oy, abz * BOUND_SEPARATION + oz).color(r, g, b, a).endVertex();

            }
        }
    }

    private static void renderBlockOutlineAt(PoseStack ms, MultiBufferSource.BufferSource lineBuffers, BlockPos pos, float[] c) {
        double renderPosX = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().x;
        double renderPosY = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y;
        double renderPosZ = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().z;

        ms.pushPose();

        ms.translate(-renderPosX, -renderPosY, -renderPosZ);

        RenderSystem.disableDepthTest();
        VertexConsumer vc = lineBuffers.getBuffer(RenderTypeHelper.LINES_NO_DEPTH);
        LevelRenderer.renderLineBox(ms, vc,
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX()+1, pos.getY()+1, pos.getZ()+1,
                c[0], c[1], c[2], c[3]
        );

        ms.popPose();
    }

    private static void renderBlockOutline(BufferBuilder buffer, BlockPos pos, float[] c) {
        float ix = (float) pos.getX();
        float iy = (float) pos.getY();
        float iz = (float) pos.getZ();
        float ax = (float) pos.getX() + 1;
        float ay = (float) pos.getY() + 1;
        float az = (float) pos.getZ() + 1;
        float a = c[3];
        float r = c[0];
        float g = c[1];
        float b = c[2];

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();

        //top / +y
        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();

        //west / -x
        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();

        //east / +x
        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        //west / -x
        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();

        //west / -x
        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
    }

}
