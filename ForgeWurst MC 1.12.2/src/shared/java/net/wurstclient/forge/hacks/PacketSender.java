package net.wurstclient.forge.hacks;

import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.Setting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.TimerUtils;

public final class PacketSender extends Hack {
	private final SliderSetting delay = new SliderSetting("Delay [MS]", 1000.0D, 0.0D, 10000.0D, 1000.0D, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting onGround = new CheckboxSetting("CPacketPlayer [ON-GROUND]", false);

	private final CheckboxSetting notOnGround = new CheckboxSetting("CPacketPlayer [ [NOT] ON-GROUND]", false);

	public PacketSender() {
		super("PacketSender", "Send packets that you desire!.");
		setCategory(Category.PLAYER);
		addSetting((Setting) this.onGround);
		addSetting((Setting) this.notOnGround);
	}

	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onPacketInput(WPacketInputEvent event) {
		if (this.onGround.isChecked()) {
			if (TimerUtils.hasReached(delay.getValueI(), true)) {
				mc.player.connection.sendPacket(new CPacketPlayer(true));
			}
		}

		if (this.notOnGround.isChecked()) {
			if (TimerUtils.hasReached(delay.getValueI(), true)) {
				mc.player.connection.sendPacket(new CPacketPlayer(false));
			}
		}
	}
}