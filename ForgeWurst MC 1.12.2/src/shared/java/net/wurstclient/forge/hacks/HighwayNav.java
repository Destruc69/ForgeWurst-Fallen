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
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;

public final class HighwayNav extends Hack {

	ArrayList<BlockPos> targetPos = new ArrayList<>();


	ArrayList<BlockPos> positivePos = new ArrayList<>();
	ArrayList<BlockPos> negativePos = new ArrayList<>();

	private final SliderSetting radiuss =
			new SliderSetting("Radius", "How far from the player will the module look for nodes", 4, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting maxnodes =
			new SliderSetting("MaxNodes", "if the node count is greater than this value we clear them", 4, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting flymode =
			new CheckboxSetting("FlyMode", "A flying bot",
					false);

	private final CheckboxSetting stop =
			new CheckboxSetting("Stop", "Stop all movement",
					false);

	public HighwayNav() {
		super("HighwayNav", "Path through Highways.");
		setCategory(Category.PATHING);
		addSetting(radiuss);
		addSetting(maxnodes);
		addSetting(flymode);
		addSetting(stop);
	}
	@Override
	protected void onEnable() {
		try {
			MinecraftForge.EVENT_BUS.register(this);
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
									BlockPos node = new BlockPos(mc.player.getPosition().add(aimX, aimY, aimZ).getX(), mc.player.getPosition().add(aimX, aimY, aimZ).getY(), mc.player.getPosition().add(aimX, aimY, aimZ).getZ());
									if (mc.world.getBlockState(node.add(0, -1, 0)).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(node.add(0, 0, 0)).getBlock().equals(Blocks.AIR) &&!mc.world.getBlockState(node).getBlock().equals(Blocks.LAVA) && !mc.world.getBlockState(node).getBlock().equals(Blocks.FLOWING_LAVA) && PlayerUtils.CanSeeBlock(node)) {
										targetPos.add(node);
										aimX = node.getX();
										aimY = node.getY();
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

						if (mc.player.isInWater()) {
							KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, true);
						} else {
							KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
						}

						mc.player.setSprinting(true);
						KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);

						if (TimerUtils.hasReached(1000)) {
							TimerUtils.reset();
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

								mc.player.rotationYaw = (float) Math.toDegrees(Math.atan2(posZZ, posXX)) - 100;
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
				if (flymode.isChecked()) {
					for (BlockPos blockPoss : targetPos) {
						if (mc.player.posY < blockPoss.getY()) {
							mc.player.motionY = 0.405;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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