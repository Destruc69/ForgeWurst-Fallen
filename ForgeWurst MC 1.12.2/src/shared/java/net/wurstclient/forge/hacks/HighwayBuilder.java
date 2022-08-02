package net.wurstclient.forge.hacks;

import com.mojang.authlib.legacy.LegacyMinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WEntityPlayerJumpEvent;
import net.wurstclient.fmlevents.WPlayerMoveEvent;
import net.wurstclient.fmlevents.WRenderBlockModelEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WPlayer;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public final class HighwayBuilder extends Hack {

	ArrayList<AxisAlignedBB> bbs = new ArrayList<>();

	ArrayList<AxisAlignedBB> bbsTo = new ArrayList<>();

	ArrayList<BlockPos> toBreak = new ArrayList<>();

	private int greenBox;

	public static double aimYaw;
	public static double aimY;

	public static boolean rea;

	private final SliderSetting width =
			new SliderSetting("Width", "width of the highway", 4, 1, 8, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting debug =
			new CheckboxSetting("Debug", "Prints what its doing",
					false);

	public HighwayBuilder() {
		super("HighwayBuilder", "Builds highways.");
		setCategory(Category.PATHING);
		addSetting(width);
		addSetting(debug);
	}

	@Override
	public String getRenderName()
	{
		return getName() + " [" + width.getValueString() + "]";
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		for (AxisAlignedBB p : bbs) {
			greenBox = GL11.glGenLists(1);
			GL11.glNewList(greenBox, GL11.GL_COMPILE);
			GL11.glColor4f(0, 1, 0, 0.25F);
			GL11.glBegin(GL11.GL_QUADS);
			RenderUtils.drawSolidBox(p);
			GL11.glEnd();
			GL11.glColor4f(0, 1, 0, 0.5F);
			GL11.glBegin(GL11.GL_LINES);
			RenderUtils.drawOutlinedBox(p);
			GL11.glEnd();
			GL11.glEndList();
		}

		try {
			ChatUtils.message("Bot might get stuck, Please keep your eye on the TPS and the screen.");
			aimYaw = mc.player.rotationYaw;
			aimY = mc.player.posY;
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
		taskToBreak();

		mc.player.rotationYaw = (float) Math.round(aimYaw);
		mc.player.rotationPitch = 0;
		mc.gameSettings.autoJump = false;
		if (mc.player.posY != aimY) {
			mc.player.setPosition(mc.player.posX, aimY, mc.player.posZ);
		}
		int radius = width.getValueI();
		for (int x = (int) -radius; x <= radius; x++) {
			for (int z = (int) -radius; z <= radius; z++) {
				BlockPos blockPos = new BlockPos(mc.player.getPosition().add(x, -1, z));
				BlockPos blockPosY0 = new BlockPos(mc.player.getPosition().add(x, 0, z));
				BlockPos blockPosY1 = new BlockPos(mc.player.getPosition().add(x, 1, z));
				BlockPos blockPosY2 = new BlockPos(mc.player.getPosition().add(x, 2, z));
				BlockPos blockPosY3 = new BlockPos(mc.player.getPosition().add(x, 3, z));

				IBlockState getState = mc.world.getBlockState(blockPos);
				IBlockState getStateY0 = mc.world.getBlockState(blockPosY0);
				IBlockState getStateY1 = mc.world.getBlockState(blockPosY1);
				IBlockState getStateY2 = mc.world.getBlockState(blockPosY2);
				IBlockState getStateY3 = mc.world.getBlockState(blockPosY3);

				bbs.add(BlockUtils.getBoundingBox(blockPos));
				bbs.add(BlockUtils.getBoundingBox(blockPosY0));
				bbs.add(BlockUtils.getBoundingBox(blockPosY1));
				bbs.add(BlockUtils.getBoundingBox(blockPosY2));
				bbs.add(BlockUtils.getBoundingBox(blockPosY3));

				if (TimerUtils.hasReached(1)) {
					bbs.clear();
					bbsTo.clear();
					TimerUtils.reset();
				}

				if (getState.getBlock().equals(Blocks.LAVA) || getStateY0.getBlock().equals(Blocks.LAVA) || getStateY1.getBlock().equals(Blocks.LAVA) || getStateY2.getBlock().equals(Blocks.LAVA) || getStateY3.getBlock().equals(Blocks.LAVA) && mc.player.getDistanceSq(blockPos) < 8) {
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindBack, true);
				} else {
					TimerUtils.sleep(1000);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindBack, false);
				}

				if (getState.getBlock().equals(Blocks.LAVA) || getStateY0.getBlock().equals(Blocks.LAVA) || getStateY1.getBlock().equals(Blocks.LAVA) || getStateY2.getBlock().equals(Blocks.LAVA) || getStateY3.getBlock().equals(Blocks.LAVA))
					return;

				if (!getState.getBlock().equals(Blocks.FLOWING_LAVA) &&
						!getStateY0.getBlock().equals(Blocks.FLOWING_LAVA) && !getStateY1.getBlock().equals(Blocks.FLOWING_LAVA) &&
						!getStateY2.getBlock().equals(Blocks.FLOWING_LAVA) && !getStateY3.getBlock().equals(Blocks.FLOWING_LAVA)) {

					if (getState.getBlock().equals(Blocks.AIR)) {
						if (mc.player.onGround) {
							mc.player.setVelocity(0, 0, 0);
						}
						mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, mc.player.getHorizontalFacing().getOpposite(), mc.objectMouseOver.hitVec, EnumHand.MAIN_HAND);
						lookAtPacket(blockPos.getX(), blockPos.getY(), blockPos.getZ(), mc.player);
						lookAtPacket(blockPos.getX(), blockPos.getY(), blockPos.getZ(), mc.player);
						lookAtPacket(blockPos.getX(), blockPos.getY(), blockPos.getZ(), mc.player);
						lookAtPacket(blockPos.getX(), blockPos.getY(), blockPos.getZ(), mc.player);
						mc.player.swingArm(EnumHand.MAIN_HAND);
					}

					if (!(getState.getBlock() instanceof Block) && !getState.getBlock().equals(Blocks.AIR) && getStateY0.getBlock().equals(Blocks.AIR)) {
						if (mc.player.onGround) {
							mc.player.setVelocity(0, 0, 0);
						}
						toBreak.add(blockPos);
						mc.player.swingArm(EnumHand.MAIN_HAND);
						if (debug.isChecked()) {
							ChatUtils.message("[HB] Clearing path...");
							TimerUtils.sleep(1000);
						}
					}

					if (!getStateY0.getBlock().equals(Blocks.AIR) && getStateY1.getBlock().equals(Blocks.AIR)) {
						if (mc.player.onGround) {
							mc.player.setVelocity(0, 0, 0);
						}
						toBreak.add(blockPosY0);
						mc.player.swingArm(EnumHand.MAIN_HAND);
						if (debug.isChecked()) {
							ChatUtils.message("[HB] Clearing tunnel... Y-0");
							TimerUtils.sleep(1000);
						}
					}
					if (!getStateY1.getBlock().equals(Blocks.AIR) && getStateY2.getBlock().equals(Blocks.AIR)) {
						if (mc.player.onGround) {
							mc.player.setVelocity(0, 0, 0);
						}
						toBreak.add(blockPosY1);
						mc.player.swingArm(EnumHand.MAIN_HAND);
						if (debug.isChecked()) {
							ChatUtils.message("[HB] Clearing tunnel... Y-1");
							TimerUtils.sleep(1000);
						}
					}
					if (!getStateY2.getBlock().equals(Blocks.AIR) && getStateY3.getBlock().equals(Blocks.AIR)) {
						if (mc.player.onGround) {
							mc.player.setVelocity(0, 0, 0);
						}
						toBreak.add(blockPosY2);
						mc.player.swingArm(EnumHand.MAIN_HAND);
						if (debug.isChecked()) {
							ChatUtils.message("[HB] Clearing tunnel... Y-2");
							TimerUtils.sleep(1000);
						}
					}
					if (!getStateY3.getBlock().equals(Blocks.AIR)) {
						if (mc.player.onGround) {
							mc.player.setVelocity(0, 0, 0);
						}
						toBreak.add(blockPosY3);
						mc.player.swingArm(EnumHand.MAIN_HAND);
						if (debug.isChecked()) {
							ChatUtils.message("[HB] Clearing tunnel... Y-3");
							TimerUtils.sleep(1000);
						}
					}
				}
				if (getState.getBlock().equals(Blocks.AIR) || !getStateY0.getBlock().equals(Blocks.AIR) || !getStateY1.getBlock().equals(Blocks.AIR) || !getStateY2.getBlock().equals(Blocks.AIR) || !getStateY3.getBlock().equals(Blocks.AIR)) {
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, true);
				} else {
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
				}
			}
		}
	}

	public void taskToBreak() {
		for (BlockPos pos : toBreak) {
			BlockUtils.breakBlockSimple(pos);
			lookAtPacket(pos.getX(), pos.getY(), pos.getZ(), mc.player);
			lookAtPacket(pos.getX(), pos.getY(), pos.getZ(), mc.player);
			lookAtPacket(pos.getX(), pos.getY(), pos.getZ(), mc.player);
			lookAtPacket(pos.getX(), pos.getY(), pos.getZ(), mc.player);
		}
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
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

		Vec3d start = RotationUtils.getClientLookVec()
				.addVector(0, 0, 0)
				.addVector(TileEntityRendererDispatcher.staticPlayerX,
						TileEntityRendererDispatcher.staticPlayerY,
						TileEntityRendererDispatcher.staticPlayerZ);

		for (AxisAlignedBB alignedBB : bbs) {
			GL11.glColor4f(0, 1, 0, 0.2F);
			GL11.glBegin(GL11.GL_LINES);
			RenderUtils.drawOutlinedBox(alignedBB);
			GL11.glEnd();
		}

		for (AxisAlignedBB alignedBB : bbs) {
			GL11.glColor4f(1, 0, 0, 0.2F);
			GL11.glBegin(GL11.GL_LINES);
			RenderUtils.drawNode(alignedBB);
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

		GL11.glColor4f(1, 0, 0, 0.5F);
		GL11.glBegin(GL11.GL_LINES);
		RenderUtils.drawArrow(start, new Vec3d(start.x, aimY, start.z));
		GL11.glEnd();

		// GL resets
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

	public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
		double dirx = me.posX - px;
		double diry = me.posY - py;
		double dirz = me.posZ - pz;

		double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

		dirx /= len;
		diry /= len;
		dirz /= len;

		double pitch = Math.asin(diry);
		double yaw = Math.atan2(dirz, dirx);

		pitch = pitch * 180.0d / Math.PI;
		yaw = yaw * 180.0d / Math.PI;

		yaw += 90f;

		return new double[]{yaw, pitch};
	}

	private static void setYawAndPitch(float yaw1, float pitch1) {
		RotationUtils.faceVectorPacket(new Vec3d(yaw1, pitch1, 0));
	}

	private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
		double[] v = calculateLookAt(px, py, pz, me);
		setYawAndPitch((float) v[0], (float) v[1]);
	}
}