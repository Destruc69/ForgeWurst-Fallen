/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.hud;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;

import java.util.ArrayList;
import java.util.Objects;

public final class EntityListModule extends Hack {

	private final SliderSetting entityAnimal =
			new SliderSetting("EntityAnimal", 0, 0, 5000, 1, SliderSetting.ValueDisplay.INTEGER);

	private final SliderSetting entityMob =
			new SliderSetting("EntityMob", 0, 0, 5000, 1, SliderSetting.ValueDisplay.INTEGER);

	private final SliderSetting entityPlayer =
			new SliderSetting("EntityPlayer", 0, 0, 5000, 1, SliderSetting.ValueDisplay.INTEGER);

	private ArrayList<EntityAnimal> entityAnimals;
	private ArrayList<EntityMob> entityMobs;
	private ArrayList<EntityPlayer> entityPlayers;

	public EntityListModule() {
		super("EntityList", "Lists the current entitys loaded.");
		setCategory(Category.HUD);
		addSetting(entityAnimal);
		addSetting(entityMob);
		addSetting(entityPlayer);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		entityAnimals = new ArrayList<>();
		entityMobs = new ArrayList<>();
		entityPlayers = new ArrayList<>();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			for (Entity entity : mc.world.loadedEntityList) {
				if (entity instanceof EntityAnimal) {
					if (!entityAnimals.contains(entity)) {
						entityAnimals.add((EntityAnimal) entity);
					}
				}
				if (entity instanceof EntityMob) {
					if (!entityMobs.contains(entity)) {
						entityMobs.add((EntityMob) entity);
					}
				}
				if (entity instanceof EntityPlayer) {
					if (entity != mc.player) {
						if (!entityPlayers.contains(entity)) {
							entityPlayers.add((EntityPlayer) entity);
						}
					}
				}
			}
			entityPlayers.removeIf(entityPlayer -> mc.player.getDistance(entityPlayer.lastTickPosX, mc.player.lastTickPosY, entityPlayer.lastTickPosZ) > mc.gameSettings.renderDistanceChunks * 15);
			entityMobs.removeIf(entityMob1 -> mc.player.getDistance(entityMob1.lastTickPosX, mc.player.lastTickPosY, entityMob1.lastTickPosZ) > mc.gameSettings.renderDistanceChunks * 15);
			entityAnimals.removeIf(entityAnimal1 -> mc.player.getDistance(entityAnimal1.lastTickPosX, mc.player.lastTickPosY, entityAnimal1.lastTickPosZ) > mc.gameSettings.renderDistanceChunks * 15);

			entityAnimal.setValue(entityAnimals.size());
			entityMob.setValue(entityMobs.size());
			entityPlayer.setValue(entityPlayers.size());
		} catch (Exception ignored) {
		}
	}
}