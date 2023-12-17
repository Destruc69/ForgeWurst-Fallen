/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import org.lwjgl.opengl.GL11;

public final class Animations extends Hack {

	private static final SliderSetting rhxv =
			new SliderSetting("RightHand-X", 0, -2, 2, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting rhyv =
			new SliderSetting("RightHand-Y", 0, -2, 2, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting rhzv =
			new SliderSetting("RightHand-Z", 0, -2, 2, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting lhxv =
			new SliderSetting("LeftHand-X", 0, -2, 2, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting lhyv =
			new SliderSetting("LeftHand-Y", 0, -2, 2, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting lhzv =
			new SliderSetting("LeftHand-Z", 0, -2, 2, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	public Animations() {
		super("Animations", "Animations, explains its self.");
		setCategory(Category.PLAYER);
		addSetting(rhxv);
		addSetting(rhyv);
		addSetting(rhzv);
		addSetting(lhxv);
		addSetting(lhyv);
		addSetting(lhzv);
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
		if (event.getHand() == EnumHand.MAIN_HAND) {
			GL11.glTranslated(rhxv.getValueF(), rhyv.getValueF(), rhzv.getValueF());
		}

		if (event.getHand() == EnumHand.OFF_HAND) {
			GL11.glTranslated(lhxv.getValueF(), lhyv.getValueF(), lhzv.getValueF());
		}
	}
}