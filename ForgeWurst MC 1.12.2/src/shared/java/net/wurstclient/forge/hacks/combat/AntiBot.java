/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import com.google.common.collect.Ordering;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.utils.ChatUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class AntiBot extends Hack {

	public static List<EntityPlayer> list;

	ArrayList<EntityPlayer> bots = new ArrayList<>();

	private final CheckboxSetting impMotionCheck = new CheckboxSetting(
			"ImpossibleMotionCheck", "Some servers make there bots faster than possible", true);

	private final CheckboxSetting tabCheck = new CheckboxSetting(
			"TabListCheck", "If there not on the tab list they shouldnt exist so there a bot", true);

	private final CheckboxSetting healthCheck = new CheckboxSetting(
			"HealthCheck", "Some servers try to trick anti-bots by making the health lower than the max health\n" +
			"when they only just spawned in", true);

	private final CheckboxSetting immuneCheck = new CheckboxSetting(
			"ImmuneCheck", "Some servers when they create there bots might make them immune to lava and fire, This will also check for invisible bots", true);

	private final CheckboxSetting impMotionY = new CheckboxSetting(
			"ImpossibleMotionYCheck", "If there airborne, there falldistance is greater than 1 but there motion y is greater than 0\n" +
			", we know there bots. A lot of bots go up and down around the player", true);

	private final CheckboxSetting inGroundCheck = new CheckboxSetting(
			"InGroundCheck (phasing)", "Some servers stupidly put there bots in the ground, making this check easy to catch the bots", true);

	public AntiBot() {
		super("AntiBot", "Helps the client know the difference between bot and a real player and remove the bot.");
		setCategory(Category.COMBAT);
		addSetting(impMotionCheck);
		addSetting(tabCheck);
		addSetting(healthCheck);
		addSetting(immuneCheck);
		addSetting(impMotionY);
		addSetting(inGroundCheck);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		bots.clear();
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			for (EntityPlayer theBots : bots) {
				mc.world.removeEntityDangerously(theBots);
				mc.world.removeEntity(theBots);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			for (Entity e : mc.world.loadedEntityList) {
				if (e instanceof EntityPlayer) {
					if (e != mc.player) {
						if (!(bots.contains(e))) {
							if (impMotionCheck.isChecked()) {
								if (e.motionX > 2.035 || e.motionX < -2.035 || e.motionY > 0.407 || e.motionY < -0.407 || e.motionZ > 2.035 || e.motionZ < -2.035) {
									if (((EntityPlayer) e).isPotionActive(MobEffects.SPEED) || ((EntityPlayer) e).isPotionActive(MobEffects.JUMP_BOOST))
										return;
									bots.add((EntityPlayer) e);
									ChatUtils.message("[AB] We removed a bot:" + " " + e.getName() + " " + "for ImpossibleMotion");
								}
							}
							if (healthCheck.isChecked()) {
								if (e.ticksExisted < 1 && ((EntityPlayer) e).getHealth() <= 19) {
									bots.add((EntityPlayer) e);
									ChatUtils.message("[AB] We removed a bot:" + " " + e.getName() + " "  + "for HeathCheck");
								}
							}
							if (immuneCheck.isChecked()) {
								if (e.isInvisible() || e.isImmuneToFire() || e.isImmuneToExplosions()) {
									bots.add((EntityPlayer) e);
									ChatUtils.message("[AB] We removed a bot:" + " " + e.getName());
								}
							}
							if (impMotionY.isChecked()) {
								if (e.isAirBorne && e.fallDistance > 1 && e.motionY > 0) {
									bots.add((EntityPlayer) e);
									ChatUtils.message("[AB] We removed a bot:" + " " + e.getName());
								}
							}
							if (inGroundCheck.isChecked()) {
								BlockPos leg = new BlockPos(e.posX, e.posY, e.posZ);
								BlockPos head = new BlockPos(e.posX, e.posY + 1, e.posZ);
								if (!mc.world.getBlockState(leg).getBlock().equals(Blocks.AIR)) {
									bots.add((EntityPlayer) e);
									ChatUtils.message("[AB] We removed a bot:" + " " + e.getName());
								}
								if (!mc.world.getBlockState(head).getBlock().equals(Blocks.AIR)) {
									bots.add((EntityPlayer) e);
									ChatUtils.message("[AB] We removed a bot:" + " " + e.getName());
								}
							}
							if (tabCheck.isChecked()) {
								if (!getTabPlayerList().contains(e.getName())) {
									bots.add((EntityPlayer) e);
									ChatUtils.message("[AB] We removed a bot:" + " " + e.getName());
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private List<EntityPlayer> getTabPlayerList() {
		Ordering<NetworkPlayerInfo> field_175252_a = field_175252_a();
		if (field_175252_a == null) {
			return list;
		}
		final List players = field_175252_a.sortedCopy(mc.player.connection.getPlayerInfoMap());
		for (final Object o : players) {
			final NetworkPlayerInfo info = (NetworkPlayerInfo) o;
			if (info == null) {
				continue;
			}
			list.add(mc.world.getPlayerEntityByName(info.getGameProfile().getName()));
		}
		return list;
	}
	private Ordering<NetworkPlayerInfo> field_175252_a() {
		try {
			final Class<GuiPlayerTabOverlay> c = GuiPlayerTabOverlay.class;
			final Field f = c.getField("field_175252_a");
			f.setAccessible(true);
			return (Ordering<NetworkPlayerInfo>)f.get(GuiPlayerTabOverlay.class);
		} catch (Exception e) {
			return null;
		}
	}
}
