/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public final class MathUtils
{
	public static boolean isInteger(String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
			
		}catch(NumberFormatException e)
		{
			return false;
		}
	}
	
	public static boolean isDouble(String s)
	{
		try
		{
			Double.parseDouble(s);
			return true;
			
		}catch(NumberFormatException e)
		{
			return false;
		}
	}
	
	public static int floor(float value)
	{
		int i = (int)value;
		return value < i ? i - 1 : i;
	}
	
	public static int floor(double value)
	{
		int i = (int)value;
		return value < i ? i - 1 : i;
	}
	
	public static int clamp(int num, int min, int max)
	{
		return num < min ? min : num > max ? max : num;
	}
	
	public static float clamp(float num, float min, float max)
	{
		return num < min ? min : num > max ? max : num;
	}
	
	public static double clamp(double num, double min, double max)
	{
		return num < min ? min : num > max ? max : num;
	}

	private static float forward;
	private static float side;

	public static double[] directionSpeed(double speed) {
		try {
			final Minecraft mc = Minecraft.getMinecraft();
			if (mc.gameSettings.keyBindForward.isKeyDown()) {
				forward = 1;
			} else if (mc.gameSettings.keyBindBack.isKeyDown()) {
				forward = -1;
			} else {
				forward = 0;
			}
			if (mc.gameSettings.keyBindLeft.isKeyDown()) {
				side = 1;
			} else if (mc.gameSettings.keyBindRight.isKeyDown()) {
				side = -1;
			} else {
				side = 0;
			}
			float yaw = mc.player.prevRotationYaw
					+ (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

			if (forward != 0) {
				if (side > 0) {
					yaw += (forward > 0 ? -45 : 45);
				} else if (side < 0) {
					yaw += (forward > 0 ? 45 : -45);
				}
				side = 0;

				// forward = clamp(forward, 0, 1);
				if (forward > 0) {
					forward = 1;
				} else if (forward < 0) {
					forward = -1;
				}
			}

			final double sin = Math.sin(Math.toRadians(yaw + 90));
			final double cos = Math.cos(Math.toRadians(yaw + 90));
			final double posX = (forward * speed * cos + side * speed * sin);
			final double posZ = (forward * speed * sin - side * speed * cos);
			return new double[]
					{posX, posZ};
		} catch (Exception ignored) {
		}
		return new double[0];
	}

	public static void setSpeed(final double speed) {
		if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown() ||
				Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown() ||
				Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown() ||
				Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()) {
			Minecraft.getMinecraft().player.motionX = -MathHelper.sin(getDirection()) * speed;
			Minecraft.getMinecraft().player.motionZ = MathHelper.cos(getDirection()) * speed;
		}
	}

	public static float getDirection() {
		float yaw = Minecraft.getMinecraft().player.rotationYaw;
		final float forward = Minecraft.getMinecraft().player.moveForward;
		final float strafe = Minecraft.getMinecraft().player.moveStrafing;
		yaw += ((forward < 0.0f) ? 180 : 0);
		int i = (forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45);
		if (strafe < 0.0f) {
			yaw += i;
		}
		if (strafe > 0.0f) {
			yaw -= i;
		}
		return yaw * 0.017453292f;
	}
}
