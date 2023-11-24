/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public final class FriendsModule extends Hack {

	private ArrayList<EntityPlayer> entityPlayersWithinRenderDistance;

	private final CheckboxSetting antiDamage =
			new CheckboxSetting("Anti-Damage", "Prevents you from damaging your friends",
					false);

	private final CheckboxSetting tabList =
			new CheckboxSetting("TabList", "Highlights your friends on the tablist with the color green \n" +
					"to differentiate from non-friends.",
					false);

	private final CheckboxSetting notify =
			new CheckboxSetting("Notify", "Notifies you if your friend has ventured within your\n" +
					"render distance.",
					false);

	public FriendsModule() {
		super("Friends", "A module just for your friends.");
		setCategory(Category.WORLD);
		addSetting(antiDamage);
		addSetting(tabList);
		addSetting(notify);
	}

	private final boolean isSingleplayer = mc.isSingleplayer();

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		entityPlayersWithinRenderDistance = new ArrayList<>();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		entityPlayersWithinRenderDistance.clear();
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			if (!isSingleplayer) {
				if (tabList.isChecked()) {
					// Assuming mc is your Minecraft instance
					TreeSet<String> friendsSet = ForgeWurst.getForgeWurst().getFriendsList().getFriends();

					for (NetworkPlayerInfo playerInfo : mc.getConnection().getPlayerInfoMap()) {
						String targetName = playerInfo.getGameProfile().getName().toLowerCase();

						if (friendsSet != null && friendsSet.contains(targetName)) {
							// Apply text formatting only once if the player is a friend
							ITextComponent greenName = new TextComponentString(TextFormatting.GREEN + playerInfo.getDisplayName().getFormattedText());
							playerInfo.setDisplayName(greenName);
						}
					}
				}
				if (notify.isChecked()) {
					for (Entity entity : mc.world.loadedEntityList) {
						if (entity instanceof EntityPlayer) {
							EntityPlayer player = (EntityPlayer) entity;

							// Check if the player is within the render distance
							if (mc.player.getDistanceSq(player) < mc.gameSettings.renderDistanceChunks * 16) {
								String targetName = player.getGameProfile().getName().toLowerCase();

								// Check if the player is a friend
								TreeSet<String> friendsSet = ForgeWurst.getForgeWurst().getFriendsList().getFriends();
								List<String> friendsList = new ArrayList<>(friendsSet);

								for (String friend : friendsList) {
									if (targetName.contains(friend.toLowerCase())) {
										// Check if the player is not already in the list
										if (!entityPlayersWithinRenderDistance.contains(player)) {
											entityPlayersWithinRenderDistance.add(player);
											ChatUtils.message("Player " + player.getName() + " (friend) entered render distance.");
										}
									} else {
										// Check if the player is in the list and remove them
										if (entityPlayersWithinRenderDistance.contains(player)) {
											entityPlayersWithinRenderDistance.remove(player);
											ChatUtils.message("Player " + player.getName() + " (friend) left render distance.");
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onAttackEntity(AttackEntityEvent event) {
		if (!isSingleplayer) {
			if (antiDamage.isChecked()) {
				String targetName = event.getTarget().getName().toLowerCase();

				TreeSet<String> friendsSet = ForgeWurst.getForgeWurst().getFriendsList().getFriends();
				List<String> friendsList = new ArrayList<>(friendsSet);

				for (String friend : friendsList) {
					if (targetName.toLowerCase().contains(friend.toLowerCase())) {
						event.setCanceled(true);
					}
				}
			}
		}
	}
}