/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.FallenRenderUtils;
import net.wurstclient.forge.utils.PlayerUtils;

import java.awt.*;
import java.util.ArrayList;

public final class Trajectory extends Hack {

	private ArrayList<BlockPos> trajArray;

	private final SliderSetting accuracy =
			new SliderSetting("Accuracy", "For calculations", 100, 100, 2000, 50, SliderSetting.ValueDisplay.DECIMAL);


	public Trajectory() {
		super("Trajectory", "Renders the path at which items/entitys will fall/move.");
		setCategory(Category.RENDER);
		addSetting(accuracy);
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
	public void onUpdate(WUpdateEvent event) {
		try {
			for (Entity entity : mc.world.loadedEntityList) {
				if (PlayerUtils.CanSeeBlock(entity.getPosition().add(0, 1, 0))) {
					trajArray.addAll(getTrajectoryPath(entity, accuracy.getValueI()));
				}
			}
			trajArray.removeIf(blockPos -> mc.player.motionX == 0 && mc.player.motionZ == 0);
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		try {
			for (int a = 0; a < trajArray.size(); a++) {
				FallenRenderUtils.drawLine(new Vec3d(trajArray.get(a).getX(), trajArray.get(a).getY(), trajArray.get(a).getZ()), new Vec3d(trajArray.get(a + 1).getX(), trajArray.get(a + 1).getY(), trajArray.get(a + 1).getZ()), 1, Color.GREEN);
			}
		} catch (Exception ignored) {
		}
	}

	public ArrayList<BlockPos> getTrajectoryPath(Entity entity, int maxIterations) {
		double gravity = 0.08; // Adjust this value based on your needs
		double motionX = entity.motionX;
		double motionY = entity.motionY;
		double motionZ = entity.motionZ;
		double posX = entity.posX;
		double posY = entity.posY;
		double posZ = entity.posZ;

		double ticks = 0.0;
		Vec3d position = new Vec3d(posX, posY, posZ);
		ArrayList<BlockPos> trajectoryPath = new ArrayList<>();

		while (ticks < maxIterations) {
			position = position.addVector(motionX, motionY, motionZ);
			motionY -= gravity;

			BlockPos blockPos = new BlockPos(position.x, position.y, position.z);
			IBlockState blockState = entity.world.getBlockState(blockPos);

			if (!blockState.getBlock().isAir(blockState, entity.world, blockPos)) {
				trajectoryPath.add(blockPos);
				break; // Stop the trajectory when hitting a block
			}

			trajectoryPath.add(blockPos);
			ticks++;
		}

		return trajectoryPath;
	}
}