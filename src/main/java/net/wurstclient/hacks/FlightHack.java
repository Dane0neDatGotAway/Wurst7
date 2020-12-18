/*
 * Copyright (c) 2014-2020 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.IsPlayerInWaterListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"FlyHack", "fly hack", "flying"})
public final class FlightHack extends Hack
	implements UpdateListener, IsPlayerInWaterListener
{
	private boolean moveDown = false;
	private double ySpeed = 0f;
	public final SliderSetting speed =
		new SliderSetting("Speed", 1, 0.05, 5, 0.05, ValueDisplay.DECIMAL);

	private final CheckboxSetting vanillaFlight =
        new CheckboxSetting("Vanilla Flight (prevents kicks)", false);
	
	public FlightHack()
	{
		super("Flight",
			"Allows you to you fly.\n\n" + "\u00a7c\u00a7lWARNING:\u00a7r"
				+ " You will take fall damage if you don't use NoFall.");
		setCategory(Category.MOVEMENT);
		addSetting(speed);
		addSetting(vanillaFlight);
	}
	
	@Override
	public void onEnable()
	{
		WURST.getHax().jetpackHack.setEnabled(false);
		
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(IsPlayerInWaterListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(IsPlayerInWaterListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		boolean isMovingDown = false;
		ClientPlayerEntity player = MC.player;
		
		player.getAbilities().flying = false;
		player.flyingSpeed = speed.getValueF();
		
		player.setVelocity(0, 0, 0);
		Vec3d velcity = player.getVelocity();
		
		if(MC.options.keyJump.isPressed())
		{
			player.setVelocity(velcity.add(0, speed.getValue(), 0));
			ySpeed = speed.getValue();
		}
		
		if(MC.options.keySneak.isPressed())
		{
			player.setVelocity(velcity.subtract(0, speed.getValue(), 0));
			ySpeed = -speed.getValue();
			isMovingDown = true;
		}
		if(vanillaFlight.isChecked() && !isMovingDown)
		{
			if(moveDown)
			{
				moveDown=false;
				player.setVelocity(velcity.add(0, velcity.y+ySpeed+0.05, 0));
			}
			else
			{
				moveDown=true;
				player.setVelocity(velcity.add(0, velcity.y+ySpeed-0.05, 0));
			}
		}
		ySpeed = 0;
	}
	
	@Override
	public void onIsPlayerInWater(IsPlayerInWaterEvent event)
	{
		event.setInWater(false);
	}
}
