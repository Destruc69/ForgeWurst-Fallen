package net.wurstclient.forge.hacks;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.RenderUtils;
import net.wurstclient.forge.utils.RotationUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public final class HighwayNav extends Hack {

	ArrayList<AxisAlignedBB> bbs = new ArrayList<>();
	ArrayList<AxisAlignedBB> bbsPost = new ArrayList<>();

	public static int aimY;

	BlockPos blockPos;
	BlockPos node11;

	public static int x;
	public static int z;

	public static int xx;
	public static int zz;

	public HighwayNav() {
		super("HighwayNav", "Path through Highways.");
		setCategory(Category.PATHING);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		try {
			aimY = (int) mc.player.posY;
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
		try {
			mc.gameSettings.autoJump = false;
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindBack, false);

			int radius = 2;
			for (x = -radius; x <= radius; x++) {
				for (z = -radius; z <= radius; z++) {
					if (bbs.size() > 1)
						bbs.clear();
					BlockPos node = new BlockPos(mc.player.getPosition().add(x, 0, z).getX(), aimY - 1, mc.player.getPosition().add(x, 0, z).getZ());
					if (mc.world.getBlockState(node).getBlock().equals(Blocks.OBSIDIAN)) {
						x = node.getX();
						z = node.getZ();
						bbs.add(BlockUtils.getBoundingBox(node));
						blockPos = node;
						KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
					} else {
						bbs.clear();
						KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
					}
					for (AxisAlignedBB bb : bbs) {
						double dd = RotationUtils.getEyesPos().distanceTo(
								bb.getCenter());
						double posXX = blockPos.getX() + (0) * dd
								- mc.player.posX;
						double posZZ = blockPos.getZ() + (0) * dd
								- mc.player.posZ;

						mc.player.rotationYaw = (float) Math.toDegrees(Math.atan2(posZZ, posXX)) - 100;
					}
				}
			}

			int radius1 = 20;
			for (xx = -radius1; xx <= radius1; xx++) {
				for (zz = -radius1; zz <= radius1; zz++) {
					BlockPos node1 = new BlockPos(mc.player.getPosition().add(xx, aimY, zz).getX(), aimY - 1, mc.player.getPosition().add(xx, aimY, zz).getZ());
					if (mc.world.getBlockState(node1).getBlock().equals(Blocks.OBSIDIAN) && node1 != blockPos && mc.player.getDistanceSq(node1) > mc.player.getDistanceSq(blockPos)) {
						bbsPost.add(BlockUtils.getBoundingBox(node1));
						node11 = node1;
						xx = node1.getX();
						zz = node1.getZ();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		try {
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

			Vec3d start = RotationUtils.getClientLookVec()
					.addVector(0, 0, 0)
					.addVector(TileEntityRendererDispatcher.staticPlayerX,
							TileEntityRendererDispatcher.staticPlayerY,
							TileEntityRendererDispatcher.staticPlayerZ);

			for (AxisAlignedBB alignedBB : bbs) {
				GL11.glColor4f(1, 0, 0, 1F);
				GL11.glBegin(GL11.GL_LINES);
				RenderUtils.drawNode(alignedBB);
				GL11.glEnd();
			}

			for (AxisAlignedBB axisAlignedBB : bbsPost) {
				GL11.glColor4f(0, 0, 1, (float) 0.3);
				GL11.glBegin(GL11.GL_LINES);
				RenderUtils.drawOutlinedBox(axisAlignedBB);
				GL11.glEnd();
			}

			GL11.glPopMatrix();

			// GL resets
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);


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

			for (AxisAlignedBB alignedBB : bbs) {
				GL11.glColor4f(1, 0, 0, 0.5F);
				GL11.glBegin(GL11.GL_LINES);
				RenderUtils.drawArrow(start, alignedBB.getCenter());
				GL11.glEnd();
			}
			// GL resets
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
