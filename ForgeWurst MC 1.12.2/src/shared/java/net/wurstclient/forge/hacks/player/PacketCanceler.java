package net.wurstclient.forge.hacks.player;

import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.Setting;

public final class PacketCanceler extends Hack {
	private final CheckboxSetting Abilities = new CheckboxSetting("CPacketPlayerAbilities", false);

	private final CheckboxSetting generic = new CheckboxSetting("GenericPackets", false);

	private final CheckboxSetting Rotation = new CheckboxSetting("S16PacketEntityLook", false);

	private final CheckboxSetting Animation = new CheckboxSetting("CPacketAnimation", false);

	private final CheckboxSetting useEntity = new CheckboxSetting("CPacketUseEntity", false);

	private final CheckboxSetting teleport = new CheckboxSetting("CPacketConfirmTeleport", false);

	private final CheckboxSetting digging = new CheckboxSetting("CPacketPlayerDigging", false);

	public PacketCanceler() {
		super("PacketCanceler", "Cancel packets that you desire!.");
		setCategory(Category.PLAYER);
		addSetting((Setting)this.Abilities);
		addSetting((Setting)this.Animation);
		addSetting((Setting)this.Rotation);
		addSetting((Setting)this.useEntity);
		addSetting((Setting)this.teleport);
		addSetting((Setting)this.digging);
		addSetting(generic);
	}

	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onPacketInput(WPacketOutputEvent event) {
		if (this.Animation.isChecked() &&
				event.getPacket() instanceof net.minecraft.network.play.client.CPacketAnimation)
			event.setCanceled(true);
		if (this.Rotation.isChecked() &&
				event.getPacket() instanceof net.minecraft.network.play.server.SPacketEntity.S16PacketEntityLook)
			event.setCanceled(true);
		if (this.Abilities.isChecked() &&
				event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerAbilities)
			event.setCanceled(true);
		if (this.useEntity.isChecked() &&
				event.getPacket() instanceof net.minecraft.network.play.client.CPacketUseEntity)
			event.setCanceled(true);
		if (this.teleport.isChecked() &&
				event.getPacket() instanceof net.minecraft.network.play.client.CPacketConfirmTeleport)
			event.setCanceled(true);
		if (this.digging.isChecked() &&
				event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerDigging)
			event.setCanceled(true);

		if (generic.isChecked()) {
			if (event.getPacket() instanceof CPacketPlayer) {
				event.setCanceled(true);
			}
		}
	}
}