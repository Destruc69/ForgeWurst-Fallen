/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

public final class FriendsModule extends Hack {

	private final CheckboxSetting antiDamage =
			new CheckboxSetting("Anti-Damage", "Prevents you from damaging your friends",
					false);

	private final CheckboxSetting highlight =
			new CheckboxSetting("Highlight", "Highlights your friends around there body.",
					false);

	public FriendsModule() {
		super("Friends", "A module just for your friends.");
		setCategory(Category.WORLD);
		addSetting(antiDamage);
		addSetting(highlight);
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
	public void onAttackEntity(AttackEntityEvent event) {
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

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (highlight.isChecked()) {
			if (!mc.isSingleplayer()) {
				if (getFriends().size() > 0) {
					for (EntityPlayer friend : getFriends()) {
						renderEntityOutline(friend, 0xFF0000);
					}
				}
			}
		}
	}

	private ArrayList<EntityPlayer> getFriends() {
		TreeSet<String> friendsSet = ForgeWurst.getForgeWurst().getFriendsList().getFriends();
		List<String> friendsList = new ArrayList<>(friendsSet);

		ArrayList<EntityPlayer> friendEntities = new ArrayList<>();

		for (String friend : friendsList) {
			for (EntityPlayer playerMP : Objects.requireNonNull(mc.player.getServer()).getPlayerList().getPlayers()) {
				if (playerMP.getName().toLowerCase().equals(friend.toLowerCase())) {
					friendEntities.add(playerMP);
					break; // No need to continue searching if the friend is found
				}
			}
		}

		return friendEntities;
	}

	private void renderEntityOutline(EntityPlayer player, int rgb) {
		// Save the current OpenGL state
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();

		// Enable blending for the glow effect
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// Disable lighting to make the outline visible
		GL11.glDisable(GL11.GL_LIGHTING);

		// Set the outline color
		float red = ((rgb >> 16) & 0xFF) / 255.0f;
		float green = ((rgb >> 8) & 0xFF) / 255.0f;
		float blue = (rgb & 0xFF) / 255.0f;

		// Set the outline width
		GL11.glLineWidth(2.0f);

		// Render the outline
		GL11.glColor4f(red, green, blue, 0.5f); // Adjust alpha for glow effect
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

		// Translate to the player's position
		double x = player.posX - mc.getRenderManager().viewerPosX;
		double y = player.posY - mc.getRenderManager().viewerPosY;
		double z = player.posZ - mc.getRenderManager().viewerPosZ;
		GL11.glTranslated(x, y, z);

		// Render the player's model
		mc.getRenderManager().renderEntity(player, 0, 0, 0, 0, 0, true);

		// Reset OpenGL states
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glLineWidth(1.0f);

		// Restore the previous OpenGL state
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
}