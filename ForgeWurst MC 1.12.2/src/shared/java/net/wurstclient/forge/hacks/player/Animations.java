/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import org.lwjgl.opengl.GL11;

public final class Animations extends Hack {

	private static final Minecraft mc = Minecraft.getMinecraft();

	private static final SliderSetting rhxv =
			new SliderSetting("RightHand-X", 0, -2, 2, 1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting rhyv =
			new SliderSetting("RightHand-Y", 0, -2, 2, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting rhzv =
			new SliderSetting("RightHand-Z", 0, -2, 2, 1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting lhxv =
			new SliderSetting("LeftHand-X", 0, -2, 2, 1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting lhyv =
			new SliderSetting("LeftHand-Y", 0, -2, 2, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting lhzv =
			new SliderSetting("LeftHand-Z", 0, -2, 2, 1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting rotateAngle =
			new SliderSetting("RotateAngle", 0, 0, 360, 1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting rotateX =
			new SliderSetting("RotateX", 0, 0, 360, 1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting rotateY =
			new SliderSetting("RotateY", 0, 0, 360, 1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting rotateZ =
			new SliderSetting("RotateZ", 0, 0, 360, 1, SliderSetting.ValueDisplay.DECIMAL);


	public Animations() {
		super("Animations", "Animations, explains its self.");
		setCategory(Category.PLAYER);
		addSetting(rhxv);
		addSetting(rhyv);
		addSetting(rhzv);
		addSetting(lhxv);
		addSetting(lhyv);
		addSetting(lhzv);
		addSetting(rotateAngle);
		addSetting(rotateX);
		addSetting(rotateY);
		addSetting(rotateZ);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onRenderHand(RenderSpecificHandEvent event) {
		ItemRenderer ir = mc.getItemRenderer();
		if (event.getHand() == EnumHand.MAIN_HAND) {
			GlStateManager.translate(rhxv.getValueF(), rhyv.getValueF(), rhzv.getValueF());
			GlStateManager.rotate(rotateAngle.getValueF(), rotateX.getValueF(), rotateY.getValueF(), rotateZ.getValueF());
			ir.updateEquippedItem();
		}
		if (event.getHand() == EnumHand.OFF_HAND) {
			GlStateManager.translate(lhxv.getValueF(), lhyv.getValueF(), lhzv.getValueF());
			GlStateManager.rotate(rotateAngle.getValueF(), rotateX.getValueF(), rotateY.getValueF(), rotateZ.getValueF());
			ir.updateEquippedItem();
		}
	}
}