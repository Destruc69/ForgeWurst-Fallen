/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.utils;

import com.mojang.text2speech.Narrator;
import com.mojang.text2speech.NarratorWindows;

public class TTS {

	public static Narrator narrator = new NarratorWindows();

	public void TTS() {

	}

	public static void say(String message) {
		narrator.say(message);
	}

	public static void clear() {
		narrator.clear();
	}

	public static void active() {
		narrator.active();
	}
}

