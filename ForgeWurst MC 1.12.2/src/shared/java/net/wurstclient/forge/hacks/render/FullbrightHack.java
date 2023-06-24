package net.wurstclient.forge.hacks.render;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class FullbrightHack extends Hack
{
	public FullbrightHack()
	{
		super("Fullbright", "Allows you to see in the dark.");
		setCategory(Category.RENDER);
	}

	@Override
	protected void onEnable()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event)
	{
		if(isEnabled())
		{
			if(mc.gameSettings.gammaSetting < 16)
				mc.gameSettings.gammaSetting =
						Math.min(mc.gameSettings.gammaSetting + 0.5F, 16);

			return;
		}

		if(mc.gameSettings.gammaSetting > 0.5F)
			mc.gameSettings.gammaSetting =
					Math.max(mc.gameSettings.gammaSetting - 0.5F, 0.5F);
		else
			MinecraftForge.EVENT_BUS.unregister(this);
	}
}