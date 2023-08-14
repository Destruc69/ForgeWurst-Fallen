/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.forge.compatibility.WVec3d;

public final class RenderUtils
{
	public static void drawSolidBox(AxisAlignedBB bb)
	{
		GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
		
		GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
		
		GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
		
		GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
		GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
		
		GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
		GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
		
		GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
		GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
	}
	
	public static void drawOutlinedBox(AxisAlignedBB bb)
	{
		GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
		
		GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
		
		GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
		
		GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
		
		GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
		
		GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
		
		GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
		
		GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
		
		GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
		
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
		
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
		
		GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
	}
	
	public static void drawNode(AxisAlignedBB bb)
	{
		double midX = (bb.minX + bb.maxX) / 2;
		double midY = (bb.minY + bb.maxY) / 2;
		double midZ = (bb.minZ + bb.maxZ) / 2;
		
		GL11.glVertex3d(midX, midY, bb.maxZ);
		GL11.glVertex3d(bb.minX, midY, midZ);
		
		GL11.glVertex3d(bb.minX, midY, midZ);
		GL11.glVertex3d(midX, midY, bb.minZ);
		
		GL11.glVertex3d(midX, midY, bb.minZ);
		GL11.glVertex3d(bb.maxX, midY, midZ);
		
		GL11.glVertex3d(bb.maxX, midY, midZ);
		GL11.glVertex3d(midX, midY, bb.maxZ);
		
		GL11.glVertex3d(midX, bb.maxY, midZ);
		GL11.glVertex3d(bb.maxX, midY, midZ);
		
		GL11.glVertex3d(midX, bb.maxY, midZ);
		GL11.glVertex3d(bb.minX, midY, midZ);
		
		GL11.glVertex3d(midX, bb.maxY, midZ);
		GL11.glVertex3d(midX, midY, bb.minZ);
		
		GL11.glVertex3d(midX, bb.maxY, midZ);
		GL11.glVertex3d(midX, midY, bb.maxZ);
		
		GL11.glVertex3d(midX, bb.minY, midZ);
		GL11.glVertex3d(bb.maxX, midY, midZ);
		
		GL11.glVertex3d(midX, bb.minY, midZ);
		GL11.glVertex3d(bb.minX, midY, midZ);
		
		GL11.glVertex3d(midX, bb.minY, midZ);
		GL11.glVertex3d(midX, midY, bb.minZ);
		
		GL11.glVertex3d(midX, bb.minY, midZ);
		GL11.glVertex3d(midX, midY, bb.maxZ);
	}
	
	public static void drawArrow(Vec3d from, Vec3d to)
	{
		double startX = WVec3d.getX(from);
		double startY = WVec3d.getY(from);
		double startZ = WVec3d.getZ(from);
		
		double endX = WVec3d.getX(to);
		double endY = WVec3d.getY(to);
		double endZ = WVec3d.getZ(to);
		
		GL11.glPushMatrix();
		
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(startX, startY, startZ);
		GL11.glVertex3d(endX, endY, endZ);
		GL11.glEnd();
		
		GL11.glTranslated(endX, endY, endZ);
		GL11.glScaled(0.1, 0.1, 0.1);
		
		double angleX = Math.atan2(endY - startY, startZ - endZ);
		GL11.glRotated(Math.toDegrees(angleX) + 90, 1, 0, 0);
		
		double angleZ = Math.atan2(endX - startX,
			Math.sqrt(Math.pow(endY - startY, 2) + Math.pow(endZ - startZ, 2)));
		GL11.glRotated(Math.toDegrees(angleZ), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(0, 2, 1);
		GL11.glVertex3d(-1, 2, 0);
		
		GL11.glVertex3d(-1, 2, 0);
		GL11.glVertex3d(0, 2, -1);
		
		GL11.glVertex3d(0, 2, -1);
		GL11.glVertex3d(1, 2, 0);
		
		GL11.glVertex3d(1, 2, 0);
		GL11.glVertex3d(0, 2, 1);
		
		GL11.glVertex3d(1, 2, 0);
		GL11.glVertex3d(-1, 2, 0);
		
		GL11.glVertex3d(0, 2, 1);
		GL11.glVertex3d(0, 2, -1);
		
		GL11.glVertex3d(0, 0, 0);
		GL11.glVertex3d(1, 2, 0);
		
		GL11.glVertex3d(0, 0, 0);
		GL11.glVertex3d(-1, 2, 0);
		
		GL11.glVertex3d(0, 0, 0);
		GL11.glVertex3d(0, 2, -1);
		
		GL11.glVertex3d(0, 0, 0);
		GL11.glVertex3d(0, 2, 1);
		GL11.glEnd();
		
		GL11.glPopMatrix();
	}

	public static void renderNameTag(final String name, final Entity entity, final int borderWidth, final double scale, final int color) {
		final RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
		final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		final int stringWidth = fontRenderer.getStringWidth(name);
		final double distance = Minecraft.getMinecraft().player.getDistance(entity);
		GL11.glPushMatrix();
		enableRender3D(true);
		final double[] coords = EntityHelper.interpolate(entity);
		final double x = coords[0] - RenderManager.renderPosX;
		final double y = coords[1] - RenderManager.renderPosY + entity.height + 0.5;
		final double z = coords[2] - RenderManager.renderPosZ;
		GL11.glTranslated(x, y, z);
		GL11.glNormal3f(0.0f, 1.0f, 0.0f);
		GL11.glScaled(-0.027, -0.027, 0.027);
		final double scaleSize = 10.0 - scale;
		if (distance > scaleSize) {
			GL11.glScaled(distance / scaleSize, distance / scaleSize, distance / scaleSize);
		}
		if (Wolfram.getWolfram().storageManager.moduleSettings.getBoolean("nametags_resize")) {
			final double x2 = entity.posX;
			final double y2 = entity.posY;
			final double z2 = entity.posZ;
			final double diffX = x2 - WMinecraft.getPlayer().posX;
			final double diffY = y2 - WMinecraft.getPlayer().posY;
			final double diffZ = z2 - WMinecraft.getPlayer().posZ;
			final float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
			final float pitch = (float)(-(Math.atan2(diffY, distance) * 180.0 / 3.141592653589793));
			final float diffYaw = Math.abs(WMath.wrapDegrees(yaw - WMinecraft.getPlayer().rotationYaw));
			final float diffPitch = Math.abs(WMath.wrapDegrees(pitch - WMinecraft.getPlayer().rotationPitch));
			float factor = (float)((75.0 - Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch)) / 50.0);
			if (factor > 1.0f) {
				factor = 1.0f;
			}
			if (factor < 0.0f) {
				factor = 0.0f;
			}
			GL11.glScaled((double)factor, (double)factor, (double)factor);
		}
		GL11.glRotatef(renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(-renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
		GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.3f);
		GL11.glBegin(7);
		GL11.glVertex3d((double)(-stringWidth / 2 - 2), -2.0, 0.0);
		GL11.glVertex3d((double)(-stringWidth / 2 - 2), 9.0, 0.0);
		GL11.glVertex3d((double)(stringWidth / 2 + 2), 9.0, 0.0);
		GL11.glVertex3d((double)(stringWidth / 2 + 2), -2.0, 0.0);
		GL11.glEnd();
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glEnable(3553);
		fontRenderer.drawString(name, (float)(-stringWidth / 2), 0.0f, color);
		GL11.glDisable(3553);
		disableRender3D(true);
		GL11.glPopMatrix();
	}

	private static void enableRender3D(final boolean disableDepth) {
		if (disableDepth) {
			GL11.glDepthMask(false);
			GL11.glDisable(2929);
		}
		GL11.glDisable(3008);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glLineWidth(1.0f);
	}

	private static void disableRender3D(final boolean enableDepth) {
		if (enableDepth) {
			GL11.glDepthMask(true);
			GL11.glEnable(2929);
		}
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glEnable(3008);
		GL11.glDisable(2848);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	private static double[] interpolate(final Entity entity) {
		final double partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
		return new double[]{ entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks };
	}

	private static double wrapDegrees(final double value) {
		return MathHelper.wrapDegrees(value);
	}
}
