package net.wurstclient.forge.hacks.player;

import net.minecraftforge.common.MinecraftForge;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;

public final class AutoJoinModule extends Hack {

	public static SliderSetting timeToEngage =
			new SliderSetting("TimeToEngage", "0 == 12 am", 0, 0, 23, 1, SliderSetting.ValueDisplay.INTEGER);

	public static SliderSetting indexToJoin =
			new SliderSetting("IndexToJoin", "Index 0 = The first server on the multiplayer menu", 0, 0, 5, 1, SliderSetting.ValueDisplay.INTEGER);

	public static CheckboxSetting enable =
			new CheckboxSetting("Enable", "Once this checkbox is enabled, if \n" +
					"you have configured, you can now go to the multiplayer list.",
					false);

	public static CheckboxSetting instant =
			new CheckboxSetting("Instant", "Dismisses everything and just joins instantly",
					false);

	public AutoJoinModule() {
		super("AutoJoin", "Joins servers from the multiplayer server list.");
		setCategory(Category.PLAYER);
		addSetting(indexToJoin);
		addSetting(timeToEngage);
		addSetting(enable);
		addSetting(instant);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}
}