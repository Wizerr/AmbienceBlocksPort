package com.wizerr.ambienceblocks.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.wizerr.ambienceblocks.ambience.AmbienceData;
import com.wizerr.ambienceblocks.ambience.bounds.*;
import com.wizerr.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;

public class BoundRenderer {
    public static void renderBounds(PoseStack poseStack, AmbienceTileEntity tile) {
        renderBounds(poseStack, tile.getData(), tile.getOrigin());
    }

    public static void renderBounds(PoseStack poseStack, AmbienceData data, Vec3 origin) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(origin.x - camPos.x, origin.y - camPos.y, origin.z - camPos.z);
        poseStack.scale(0.998f, 0.998f, 0.998f);

        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        poseStack.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180f));

        renderBoundsInternal(data.getBounds(), Vec3.ZERO, data.getColor());

        poseStack.popPose();
    }

    private static void renderBoundsInternal(AbstractBounds bounds, Vec3 origin, float[] color) {
        if(bounds instanceof CubicBounds) {
            renderCubic((CubicBounds) bounds, origin, color);
        } else if(bounds instanceof SphereBounds) {

        } else if(bounds instanceof CylinderBounds) {

        } else if(bounds instanceof CapsuleBounds) {

        }
    }

    private static void renderCubic(CubicBounds cubic, Vec3 origin, float[] color) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        renderCubicFilling(buffer, cubic, origin, color);
        tessellator.end();
        RenderSystem.disableDepthTest();

        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth(4.0F);
        buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        renderCubicOutline(buffer, cubic, origin, color);
        tessellator.end();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        RenderSystem.enableCull();
    }

    private static void renderCubicFilling(BufferBuilder buffer, CubicBounds cubic, Vec3 origin, float[] color) {
        float ix = (float) (origin.x() - cubic.getxSize() / 2f);
        float iy = (float) (origin.y() - cubic.getySize() / 2f);
        float iz = (float) (origin.z() - cubic.getzSize() / 2f);
        float ax = (float) (origin.x() + cubic.getxSize() / 2f);
        float ay = (float) (origin.y() + cubic.getySize() / 2f);
        float az = (float) (origin.z() + cubic.getzSize() / 2f);
        float a = color[3];
        float r = color[0];
        float g = color[1];
        float b = color[2];

        //-y
        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();

        //+y
        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();

        //-x
        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();

        //+x
        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        //-z
        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();

        //+z
        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
    }

    private static void renderCubicOutline(BufferBuilder buffer, CubicBounds cubic, Vec3 origin, float[] color) {
        float ix = (float) (origin.x() - cubic.getxSize() / 2f);
        float iy = (float) (origin.y() - cubic.getySize() / 2f);
        float iz = (float) (origin.z() - cubic.getzSize() / 2f);
        float ax = (float) (origin.x() + cubic.getxSize() / 2f);
        float ay = (float) (origin.y() + cubic.getySize() / 2f);
        float az = (float) (origin.z() + cubic.getzSize() / 2f);
        float a = color[3];
        float r = color[0];
        float g = color[1];
        float b = color[2];

        buffer.vertex(ix, iy, iz).color(r, g, b, a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ix, iy, iz).color(r, g, b, a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(ix, iy, iz).color(r, g, b, a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).normal(-1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).normal(-1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).normal(0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).normal(0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).normal(0.0F, 0.0F, -1.0F).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).normal(0.0F, 0.0F, -1.0F).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).normal(0.0F, 0.0F, 1.0F).endVertex();
    }
}
