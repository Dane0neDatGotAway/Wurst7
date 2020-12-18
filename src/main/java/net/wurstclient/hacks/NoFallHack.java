/*
 * Copyright (c) 2014-2020 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.util.BlockUtils;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.settings.CheckboxSetting;

import net.wurstclient.hack.Hack;

@SearchTags({"no fall"})
public final class NoFallHack extends Hack implements UpdateListener
{
	private boolean resetSneak = false;
	private final CheckboxSetting noHorseDamage =
		new CheckboxSetting("dismounts before horse hits ground \n"+
							"if damage were fatal \u00a7c\u00a7lWARNING:\u00a7r does not protect horse", true);

	public NoFallHack()
	{
		super("NoFall", "Protects you from fall damage.");
		setCategory(Category.MOVEMENT);
		addSetting(noHorseDamage);
	}
	
	@Override
	public void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(resetSneak)
		{
			MC.options.keySneak.setPressed(false);
			resetSneak = false;
		}
		ClientPlayerEntity player = MC.player;
		if(player.fallDistance <= (player.isFallFlying() ? 1 : 2) && !player.hasVehicle())
			return;
		
		if(player.isFallFlying() && player.isSneaking()
			&& !isFallingFastEnoughToCauseDamage(player))
			return;
		//Prevents Player from taking damage
		player.networkHandler.sendPacket(new PlayerMoveC2SPacket(true));

		//Prevents Player From Taking Horse damage
		if (player.hasVehicle() && noHorseDamage.isChecked()&&!(player.getVehicle().getType() == EntityType.BOAT || player.getVehicle().getType() == EntityType.MINECART))
		{
			Entity vehicle = player.getVehicle();
			if((vehicle.fallDistance >= (player.getHealth() * 2 + 6) || BlockUtils.getBlock(new BlockPos(player.getPos()).down(5)) instanceof PointedDripstoneBlock) && !(BlockUtils.getBlock(new BlockPos(player.getPos()).down(5)) instanceof AirBlock))
			{
				MC.options.keySneak.setPressed(true);
				resetSneak = true;
			}
		}
	}
	
	private boolean isFallingFastEnoughToCauseDamage(ClientPlayerEntity player)
	{
		return player.getVelocity().y < -0.5;
	}
}
