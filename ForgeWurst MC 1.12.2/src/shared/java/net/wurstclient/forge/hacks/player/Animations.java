/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import org.lwjgl.util.vector.Quaternion;

public final class Animations extends Hack {

	private final SliderSetting xv =
			new SliderSetting("X", 0, -2, 2, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting yv =
			new SliderSetting("Y", 0, -2, 2, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting zv =
			new SliderSetting("Z", 0, -2, 2, 0.01, SliderSetting.ValueDisplay.DECIMAL);


	private final CheckboxSetting swing =
			new CheckboxSetting("OnlyOnSwing", "Only while hand is active",
					false);

	private final SliderSetting angle =
			new SliderSetting("Angle", 0, -2, 2, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting rx =
			new SliderSetting("Rotate-X", 0,-100, 100, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting ry =
			new SliderSetting("Rotate-Y", 0,-100, 100, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting rz =
			new SliderSetting("Rotate-Z", 0,-100, 100, 0.01, SliderSetting.ValueDisplay.DECIMAL);


	public Animations() {
		super("ArmModel", "Change arm model pos");
		setCategory(Category.PLAYER);
		addSetting(xv);
		addSetting(yv);
		addSetting(zv);
		addSetting(swing);
		addSetting(angle);
		addSetting(rx);
		addSetting(ry);
		addSetting(rz);
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
	public void onUpdate(RenderHandEvent event) {
		if (!swing.isChecked()) {
			ItemRenderer ir = mc.getItemRenderer();
			GlStateManager.translate(xv.getValueI(), yv.getValueF(), zv.getValueI());
			GlStateManager.rotate(angle.getValueF(), rx.getValueI(), ry.getValueF(), rz.getValueI());
			ir.updateEquippedItem();
		} else {
			if (mc.player.isSwingInProgress) {
				ItemRenderer ir = mc.getItemRenderer();
				GlStateManager.translate(xv.getValueI(), yv.getValueF(), zv.getValueI());
				GlStateManager.rotate(angle.getValueF(), rx.getValueI(), ry.getValueF(), rz.getValueI());
				ir.updateEquippedItem();
			}
		}
	}
}