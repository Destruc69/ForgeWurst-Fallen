/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge;

import net.wurstclient.forge.commands.*;
import net.wurstclient.forge.compatibility.WCommandList;

public final class CommandList extends WCommandList
{
	public final BindsCmd bindsCmd = register(new BindsCmd());
	public final ClearCmd clearCmd = register(new ClearCmd());
	public final GmCmd gmCmd = register(new GmCmd());
	public final DupeCMD dupeCMD = register(new DupeCMD());
	public final GiveCMD giveCMD = register(new GiveCMD());
	public final PearlTPCmd pearlTPCmd = register(new PearlTPCmd());
	public final SetAP setAP = register(new SetAP());
	public final OPItemCMD opItemCMD = register(new OPItemCMD());
	public final AddPoint addPoint = register(new AddPoint());
	public final FaceCMD faceCMD = register(new FaceCMD());
	public final Damage damage = register(new Damage());
	public final HelpCmd helpCmd = register(new HelpCmd());
	public final SayCmd sayCmd = register(new SayCmd());
	public final SetCheckboxCmd setCheckboxCmd = register(new SetCheckboxCmd());
	public final SetEnumCmd setEnumCmd = register(new SetEnumCmd());
	public final SetSliderCmd setSliderCmd = register(new SetSliderCmd());
	public final TCmd tCmd = register(new TCmd());
	public final EnchantCMD enchantCMD = register(new EnchantCMD());
	public final tpCMD tpCMD = register(new tpCMD());
	public final TacoCmd tacoCmd = register(new TacoCmd());
	public final VClipCmd vClipCmd = register(new VClipCmd());
	public final VrTweaksCmd vrTweaksCmd = register(new VrTweaksCmd());
}
