/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;

public final class SinglePlayerFunner extends Hack {

	private final SliderSetting movementSpeed =
			new SliderSetting("MovementSpeed", 0.2, 0.5, 10, 0.5, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting armor =
			new SliderSetting("Armor", 1, 0.5, 10, 0.5, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting armorToughness =
			new SliderSetting("ArmorToughness", 1, 0.5, 10, 0.5, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting attackSpeed =
			new SliderSetting("AttackSpeed", 1, 0.5, 10, 0.5, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting attackDamage =
			new SliderSetting("AttackDamage", 1, 0.5, 10, 0.5, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting flyingSpeed =
			new SliderSetting("FlyingSpeed", 0.2, 0.5, 10, 0.5, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting followRange =
			new SliderSetting("FollowRange", 10, 0.5, 50, 0.5, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting knockBackResistance =
			new SliderSetting("KnockBackResistance", 1, 0.5, 10, 0.5, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting luck =
			new SliderSetting("Luck", 1, 0.5, 10, 0.5, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting maxHealth =
			new SliderSetting("MaxHealth", 20, 0.5, 50, 0.5, SliderSetting.ValueDisplay.DECIMAL);


	public SinglePlayerFunner() {
		super("SinglePlayerFunner", "Makes single-player more fun by allowing you to change shared monster attributes.");
		setCategory(Category.WORLD);
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
	public void onUpdate(WUpdateEvent event) {
		// Need to work on this
	//mc.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(movementSpeed.getValue());
	//mc.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ARMOR).setBaseValue(armor.getValue());
	//mc.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(armorToughness.getValue());
	//mc.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(attackSpeed.getValue());
	//mc.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(attackDamage.getValue());
	////mc.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(flyingSpeed.getValue());
	//mc.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(followRange.getValue());
	//mc.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(knockBackResistance.getValue());
	//mc.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.LUCK).setBaseValue(luck.getValue());
	//mc.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth.getValue());
	}
}