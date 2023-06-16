package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;

public final class Flight extends Hack
{
	private enum Mode {
		NORMAL("Normal", true, false),
		BYPASS("Bypass", false, true);

		private final String name;
		private final boolean normal;
		private final boolean bypass;

		private Mode(String name, boolean normal, boolean bypass) {
			this.name = name;
			this.normal = normal;
			this.bypass = bypass;
		}

		public String toString() {
			return name;
		}
	}

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NORMAL);

	private final SliderSetting speed =
			new SliderSetting("Speed", 1, 0.05, 5, 0.05, ValueDisplay.DECIMAL);

	public Flight()
	{
		super("Flight",
				"Allows you to fly.\n\n"
						+ "\u00a7c\u00a7lWARNING:\u00a7r You will take fall damage\n"
						+ "if you don't use NoFall.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(speed);
	}

	@Override
	public String getRenderName()
	{
		return getName() + " [" + speed.getValueString() + "]";
	}

	@Override
	protected void onEnable()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable()
	{
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode.getSelected().normal) {
			EntityPlayerSP player = event.getPlayer();

			player.capabilities.isFlying = false;
			player.motionX = 0;
			player.motionY = 0;
			player.motionZ = 0;
			player.jumpMovementFactor = speed.getValueF();

			if (mc.gameSettings.keyBindJump.isKeyDown())
				player.motionY += speed.getValue();
			if (mc.gameSettings.keyBindSneak.isKeyDown())
				player.motionY -= speed.getValue();
		} else if (mode.getSelected().bypass) {
			EntityPlayerSP player = event.getPlayer();

			player.capabilities.isFlying = false;
			player.motionX = Math.random() * 0.02 - Math.random() * 0.02;
			player.motionY = Math.random() * 0.02 - Math.random() * 0.02;
			player.motionZ = Math.random() * 0.02 - Math.random() * 0.02;
			player.jumpMovementFactor = (float) (speed.getValueF() + Math.random() * 0.02 - Math.random() * 0.02);

			if (mc.gameSettings.keyBindJump.isKeyDown())
				player.motionY += speed.getValue() - Math.random() * 0.1;
			if (mc.gameSettings.keyBindSneak.isKeyDown())
				player.motionY -= speed.getValue() + Math.random() * 0.1;
		}
	}
}