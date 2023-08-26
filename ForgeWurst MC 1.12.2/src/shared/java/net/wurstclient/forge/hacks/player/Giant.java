/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import org.lwjgl.opengl.GL11;

public final class Giant extends Hack {
	private final SliderSetting x =
			new SliderSetting("X", 2, 1, 30, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting y =
			new SliderSetting("Y", 2, 1, 30, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting z =
			new SliderSetting("z", 2, 1, 30, 1, SliderSetting.ValueDisplay.DECIMAL);

	public Giant() {
		super("Giant", "Makes you really big.");
		setCategory(Category.PLAYER);
		addSetting(x);
		addSetting(y);
		addSetting(z);
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
	public void onRenderPlayer(RenderPlayerEvent event) {
		GL11.glScalef(x.getValueF(), y.getValueF(), z.getValueF());
	}
}