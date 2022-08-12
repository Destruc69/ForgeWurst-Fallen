/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;

public final class ElytraBAOT extends Hack {

	double startY = 0;


	ArrayList<BlockPos> targetPos = new ArrayList<>();


	ArrayList<BlockPos> positivePos = new ArrayList<>();
	ArrayList<BlockPos> negativePos = new ArrayList<>();

	private final SliderSetting radiuss =
			new SliderSetting("Radius", "How far from the player will the module look for nodes", 4, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting maxnodes =
			new SliderSetting("MaxNodes", "if the node count is greater than this value we clear them", 4, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting stop =
			new CheckboxSetting("Stop", "Stop all movement",
					false);

	public ElytraBAOT() {
		super("AutoElytra", "Addition pathfinding for ElytraFly.");
		setCategory(Category.MOVEMENT);
		addSetting(radiuss);
		addSetting(maxnodes);
		addSetting(stop);
	}

	@Override
	protected void onEnable() {
		try {
			MinecraftForge.EVENT_BUS.register(this);
			startY = mc.player.posY;
			if (targetPos.size() > 0) {
				targetPos.clear();
			}
			if (positivePos.size() > 0) {
				positivePos.clear();
			}
			if (negativePos.size() > 0) {
				negativePos.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDisable() {
		try {
			MinecraftForge.EVENT_BUS.unregister(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (ForgeWurst.getForgeWurst().getHax().elytraFlight.isEnabled()) {
			if (!stop.isChecked()) {
				try {
					if (targetPos.size() > maxnodes.getValueF()) {
						targetPos.clear();
					}
					assert targetPos != null;
					assert radiuss != null;
					assert maxnodes != null;
					assert positivePos != null;
					assert negativePos != null;
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					try {
						try {
							assert targetPos != null;
							try {
								if (targetPos.size() > maxnodes.getValueF())
									targetPos.clear();
							} catch (Exception e) {
								e.printStackTrace();
							}
							int radius = radiuss.getValueI();
							for (int aimX = -radius; aimX < radius; aimX++) {
								for (int aimY = -radius; aimY < radius; aimY++) {
									for (int aimZ = -radius; aimZ < radius; aimZ++) {
										BlockPos node = new BlockPos(mc.player.getPosition().add(aimX, aimY, aimZ).getX(), startY, mc.player.getPosition().add(aimX, aimY, aimZ).getZ());
										if (mc.world.getBlockState(node).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(node.add(0, -1, 0)).getBlock().equals(Blocks.AIR) && PlayerUtils.CanSeeBlock(node)) {
											targetPos.add(node);
											aimX = node.getX();
											aimY = (int) (node.getY() + 0.5f);
											aimZ = node.getZ();
											positivePos.add(node);
										} else {
											if (PlayerUtils.CanSeeBlock(node)) {
												negativePos.add(node);
											}
										}
									}
								}
							}

							KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);

							for (BlockPos thePos : targetPos) {
								if (thePos.getY() > mc.player.posY) {
									KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
									KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, true);
								} else if (mc.player.posY > thePos.getY()) {
									KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
								}
							}

							if (mc.player.ticksExisted % 10 == 0) {
								for (BlockPos pos : targetPos) {
									assert pos != null;
									AxisAlignedBB bb = BlockUtils.getBoundingBox(pos);
									assert bb != null;
									double dd = RotationUtils.getEyesPos().distanceTo(
											bb.getCenter());
									double posXX = pos.getX() + (0) * dd
											- mc.player.posX;
									double posZZ = pos.getZ() + (0) * dd
											- mc.player.posZ;

									mc.player.rotationYaw = (float) Math.toDegrees(Math.atan2(posZZ, posXX)) - 90;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		try {
			try {
				try {
					// GL settings
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glEnable(GL11.GL_LINE_SMOOTH);
					GL11.glLineWidth(1);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glEnable(GL11.GL_CULL_FACE);
					GL11.glDisable(GL11.GL_DEPTH_TEST);

					GL11.glPushMatrix();
					GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX,
							-TileEntityRendererDispatcher.staticPlayerY,
							-TileEntityRendererDispatcher.staticPlayerZ);

					for (BlockPos posPos : positivePos) {
						assert posPos != null;
						GL11.glColor4f(0, 1, 0, 0.3F);
						GL11.glBegin(GL11.GL_QUADS);
						RenderUtils.drawSolidBox(Objects.requireNonNull(BlockUtils.getBoundingBox(posPos.add(0, -1, 0))));
						GL11.glEnd();
					}

					for (BlockPos negPos : negativePos) {
						assert negPos != null;
						GL11.glColor4f(1, 0, 0, 0.3F);
						GL11.glBegin(GL11.GL_QUADS);
						RenderUtils.drawSolidBox(Objects.requireNonNull(BlockUtils.getBoundingBox(negPos.add(0, -1, 0))));
						GL11.glEnd();
					}

					GL11.glPopMatrix();

					// GL resets
					GL11.glColor4f(1, 1, 1, 1);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glDisable(GL11.GL_LINE_SMOOTH);
















					assert targetPos != null;
					// GL settings
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glEnable(GL11.GL_LINE_SMOOTH);
					GL11.glLineWidth(2);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glEnable(GL11.GL_CULL_FACE);
					GL11.glDisable(GL11.GL_DEPTH_TEST);

					GL11.glPushMatrix();
					GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX,
							-TileEntityRendererDispatcher.staticPlayerY,
							-TileEntityRendererDispatcher.staticPlayerZ);

					for (BlockPos bpos : targetPos) {
						assert bpos != null;
						GL11.glColor4f(0, 0, 1, 1F);
						GL11.glBegin(GL11.GL_LINES);
						RenderUtils.drawArrow(new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ), new Vec3d(bpos.getX(), bpos.getY(), bpos.getZ()));
						GL11.glEnd();
					}

					GL11.glPopMatrix();

					// GL resets
					GL11.glColor4f(1, 1, 1, 1);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glDisable(GL11.GL_LINE_SMOOTH);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}