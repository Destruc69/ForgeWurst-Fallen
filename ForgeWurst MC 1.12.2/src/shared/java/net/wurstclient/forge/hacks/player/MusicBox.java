/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import java.io.File;

public final class MusicBox extends Hack {

	Clip clip;
	AudioInputStream audioInputStream;
	File file;

	private final CheckboxSetting dupeGriefer =
			new CheckboxSetting("DupeGriefer", "",
					false);

	public MusicBox() {
		super("2b2tMusicBox", "Plays 2b2t classics.");
		setCategory(Category.PLAYER);
		addSetting(dupeGriefer);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		if (dupeGriefer.isChecked()) {

		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {

	}
}