/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.NotiUtils;
import net.wurstclient.forge.utils.TextUtil;

import java.util.ArrayList;

public final class NoCom extends Hack {

	public static Vec3d ranPos;
	public static ArrayList<Vec3d> passedPoses = new ArrayList<>();
	public static ArrayList<Vec3d> goodPoses = new ArrayList<>();

	private final CheckboxSetting packet =
			new CheckboxSetting("EnforcePacket", "Sends packets to chunks aswell.",
					false);

	public NoCom() {
		super("NoCom", "A NoCom clone. \n" +
				"ONLY WORKS ON OLD VERSIONS OF SERVERS");
		setCategory(Category.WORLD);
		addSetting(packet);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		ranPos = new Vec3d(0, 0, 0);
		passedPoses.clear();
		goodPoses.clear();
		try {
			ChatUtils.warning("Firstly, This is as simple as it gets. We break a random block every few ticks to load the chunk\n" +
					"Server will only respond if the chunk is loaded by a player is if we find a responsive block then it means\n" +
					"the chunk is loaded. \n" +
					TextUtil.coloredString("This is a very computer intensive module", TextUtil.Color.RED) + "\n" +
					TextUtil.coloredString("If nothing is happening its because were still searching through blocks, Please be very patient, And save chat logs", TextUtil.Color.RED));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (packet.isChecked()) {
			double x = ranPos.x;
			double y = ranPos.y;
			double z = ranPos.z;
			mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(x, y, z), EnumFacing.DOWN));
		}

		if (mc.player.ticksExisted % 10 == 0) {
			ranPos = new Vec3d(Math.round(Math.random() * 30000000) - Math.round(Math.random() * 30000000), 20, Math.round(Math.random() * 30000000) - Math.round(Math.random() * 30000000));
		} else {
			if (!passedPoses.contains(ranPos)) {
				passedPoses.add(ranPos);
			}
		}
		if (!mc.world.getBlockState(new BlockPos(ranPos.x, ranPos.y, ranPos.z)).getBlock().equals(Blocks.AIR)) {
			boolean b = !(goodPoses.contains(ranPos));
			if (b) {
				goodPoses.add(ranPos);
			}
		}
		mc.playerController.onPlayerDamageBlock(new BlockPos(ranPos.x, ranPos.y, ranPos.z), EnumFacing.DOWN);
		mc.player.swingArm(EnumHand.MAIN_HAND);

		NotiUtils.render("NoCom - Fallen", "Poses Checked: " + passedPoses.size() + " | " + "Good Poses: " + goodPoses, true);
	}
}