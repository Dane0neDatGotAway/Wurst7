/*
 * Copyright (c) 2014-2020 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */

 //Also contains Compass
package net.wurstclient.hud;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import io.netty.util.Constant;
import net.minecraft.block.Material;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.wurstclient.WurstClient;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.other_features.HackListOtf;
import net.wurstclient.other_features.HackListOtf.Mode;
import net.wurstclient.other_features.HackListOtf.Position;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import net.wurstclient.hacks.CompassHack;
import net.wurstclient.hacks.GuiHack;
import net.minecraft.util.Identifier;

public final class GuiHackHUD
{
    private static final Identifier compassback =
        new Identifier("wurst", "compass_bottem.png");
    private static final Identifier compassplayer =
        new Identifier("wurst", "player_facing_indicator.png");

    private static final Identifier marker_red = new Identifier("wurst", "compass_marker_red.png");
    private static final Identifier marker_green = new Identifier("wurst", "compass_marker_green.png");
    private static final Identifier marker_blue = new Identifier("wurst", "compass_marker_blue.png");
    private static final Identifier marker_lime = new Identifier("wurst", "compass_marker_lime.png");
    private static final Identifier marker_orange = new Identifier("wurst", "compass_marker_orange.png");
    private static final Identifier marker_pink = new Identifier("wurst", "compass_marker_pink.png");
    private static final Identifier marker_purple = new Identifier("wurst", "compass_marker_purple.png");
    private static final Identifier marker_yellow = new Identifier("wurst", "compass_marker_yellow.png");

    private final GuiHack guiHack =
        WurstClient.INSTANCE.getHax().guiHack;
    private final CompassHack compassHack = 
        WurstClient.INSTANCE.getHax().compassHack;
    private int textColor;
    private List<int[]> indicators = new ArrayList<int[]>();
    //id called in a seperate file and runs EVERYTHING
    public void render(MatrixStack matrixStack, float partialTicks)
	{
        if(guiHack.isEnabled()){
            if(WurstClient.INSTANCE.getHax().rainbowUiHack.isEnabled())
	    	{
			    float[] acColor = WurstClient.INSTANCE.getGui().getAcColor();
		    	textColor = 0x04 << 24 | (int)(acColor[0] * 256) << 16
			    	| (int)(acColor[1] * 256) << 8 | (int)(acColor[2] * 256);
			
		    }else
                textColor = 0x04ffffff;
            float currentYPos = 0.0f;
            if(guiHack.showPosEnabled())
            {
                currentYPos = drawPos(matrixStack);
            }
            if(guiHack.showHandDurabilityEnabled())
            {
                drawHeldItem(matrixStack, currentYPos);
            }
            if(guiHack.showArmorDurabilityEnabled())
            {
                drawArmor(matrixStack);
            }
        }
        if(compassHack.isEnabled())
        {
            drawCompass(matrixStack);
        }
    }
    private void drawHeldItem(MatrixStack matrixStack, float currentHeightPos)
    {
        String damage = "";
        if(!WurstClient.MC.player.getStackInHand(Hand.MAIN_HAND).isEmpty())
        {
           if(WurstClient.MC.player.getStackInHand(Hand.MAIN_HAND).getMaxDamage() - WurstClient.MC.player.getStackInHand(Hand.MAIN_HAND).getDamage()>0)
           {
               damage = "        "+(WurstClient.MC.player.getStackInHand(Hand.MAIN_HAND).getMaxDamage() - WurstClient.MC.player.getStackInHand(Hand.MAIN_HAND).getDamage());//+" / "+WurstClient.MC.player.getStackInHand(Hand.MAIN_HAND).getMaxDamage()
           }
           else
           {
                damage = "         "+(WurstClient.MC.player.getStackInHand(Hand.MAIN_HAND).getCount());
           }
        }
        drawString(matrixStack, damage, 2.0f, currentHeightPos+2, 0.90f*guiHack.getSize(),2);
        //Draws Item in mainhand
        float itemscale = 1.8f*guiHack.getSize();
        GL11.glPushMatrix();
        GL11.glScaled(itemscale, itemscale, itemscale);
        WurstClient.MC.getItemRenderer().renderInGui(WurstClient.MC.player.getStackInHand(Hand.MAIN_HAND), (int)(2/itemscale), (int)((WurstClient.MC.getWindow().getScaledHeight()-(currentHeightPos+2))/itemscale-16));
        GL11.glPopMatrix();
    }
    private void drawArmor(MatrixStack matrixStack)
    {
        //Draws armor item
        float currentHeightPos = 2;
        float itemscale = 1.8f*guiHack.getSize();

        for(int armoritem=0;armoritem<4;armoritem++){
        GL11.glPushMatrix();
        GL11.glScaled(itemscale, itemscale, itemscale);
        WurstClient.MC.getItemRenderer().renderInGui(WurstClient.MC.player.getInventory().getArmorStack(armoritem),(int)((WurstClient.MC.getWindow().getScaledWidth()-2)/itemscale-16), (int)((WurstClient.MC.getWindow().getScaledHeight()-(currentHeightPos+2))/itemscale-16));
        GL11.glPopMatrix();
        String damage = "";
        if(!WurstClient.MC.player.getInventory().getArmorStack(armoritem).isEmpty())
        {
           if(WurstClient.MC.player.getInventory().getArmorStack(armoritem).getMaxDamage() - WurstClient.MC.player.getInventory().getArmorStack(armoritem).getDamage()>0)
           {
               damage = (WurstClient.MC.player.getInventory().getArmorStack(armoritem).getMaxDamage() - WurstClient.MC.player.getInventory().getArmorStack(armoritem).getDamage())+"        ";
           }
           else
           {
                damage = (WurstClient.MC.player.getInventory().getArmorStack(armoritem).getCount())+"         ";
           }
        }
        drawString(matrixStack, damage, 2, currentHeightPos+8, 0.90f*guiHack.getSize(),3);
        currentHeightPos = 2+currentHeightPos + 16*itemscale;
        }
    }
    private float drawPos(MatrixStack matrixStack)
    {
        String playerPos = "X:"+String.format("% 1.1f", WurstClient.MC.player.getX())+"/ Y:"+String.format("% 1.1f", WurstClient.MC.player.getY())+"/ Z:"+String.format("% 1.1f", WurstClient.MC.player.getZ());
        return drawString(matrixStack, playerPos, 2.0f, 2, 0.75f*guiHack.getSize(),2);
    }
    private void drawCompass(MatrixStack matrixStack)
    {
        GL11.glPushMatrix();
        GL11.glScaled(compassHack.getSize(), compassHack.getSize(), compassHack.getSize());
        GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_BLEND);
        WurstClient.MC.getTextureManager().bindTexture(compassback);
        DrawableHelper.drawTexture(matrixStack, (int)((WurstClient.MC.getWindow().getScaledWidth() - 3)/compassHack.getSize() - 64), 3, 0, 0, 64, 64, 64, 64);
        //Get Cords of block
        WurstClient.MC.getTextureManager().bindTexture(compassplayer);
        DrawableHelper.drawTexture(matrixStack, (int)((WurstClient.MC.getWindow().getScaledWidth() - 3)/compassHack.getSize() - 39), 28, 0, 0, 14, 14, 14, 14);
        GL11.glPopMatrix();
        if(compassHack.indicatorsHaveChanged())
        {
            indicators = new ArrayList<int[]>();
            for(String setCurrentIndicator : compassHack.currentIndicators())
            {
                String currentIndicator = setCurrentIndicator;
                int objectEnd = currentIndicator.indexOf("_");
                int[] currentpoint = new int[3]; 
                if(currentIndicator.length()>=objectEnd+1 && objectEnd!=-1)
                {
                    currentpoint[0] = 0;
                    if(currentIndicator.substring(0, objectEnd).equalsIgnoreCase("green"))
                    {
                        currentpoint[0] = 1;
                    } 
                    else if(currentIndicator.substring(0, objectEnd).equalsIgnoreCase("blue"))
                    {
                        currentpoint[0] = 2;
                    }
                    else if(currentIndicator.substring(0, objectEnd).equalsIgnoreCase("lime"))
                    {
                        currentpoint[0] = 3;
                    }
                    else if(currentIndicator.substring(0, objectEnd).equalsIgnoreCase("orange"))
                    {
                        currentpoint[0] = 4;
                    }
                    else if(currentIndicator.substring(0, objectEnd).equalsIgnoreCase("pink"))
                    {
                        currentpoint[0] = 5;
                    }
                    else if(currentIndicator.substring(0, objectEnd).equalsIgnoreCase("purple"))
                    {
                        currentpoint[0] = 6;
                    }
                    else if(currentIndicator.substring(0, objectEnd).equalsIgnoreCase("yellow"))
                    {
                        currentpoint[0] = 7;
                    }
                    currentIndicator = currentIndicator.substring(objectEnd+1, currentIndicator.length());
                    objectEnd = currentIndicator.indexOf("_");
                }
                if(currentIndicator.length()>=objectEnd+1 && objectEnd!=-1)
                {
                    try{
                    currentpoint[1] = Integer.parseInt(currentIndicator.substring(0, objectEnd));
                    currentpoint[2] = Integer.parseInt(currentIndicator.substring(objectEnd+1, currentIndicator.length()));
                    }
                    catch (NumberFormatException e)
                    {
                        System.err.println("Caugth Number Format Exception");
                    }
                }
                indicators.add(currentpoint);
            }
        }
        for(int[] currentIndicator : indicators)
        {
            addCompassIndicator(matrixStack, currentIndicator[1], currentIndicator[2], currentIndicator[0]);
        }
    }
    private void addCompassIndicator(MatrixStack matrixStack, float blockX, float blockZ, int color)
    {
        float angle = (float) Math.toDegrees(Math.atan2(blockZ - WurstClient.MC.player.getPos().getZ(), blockX - WurstClient.MC.player.getPos().getX()));

        if(angle < 0){
            angle += 360;
        }
        drawCompassIndicator(matrixStack, WurstClient.MC.player.headYaw-angle-90, color);
    }
    private void drawCompassIndicator(MatrixStack matrixStack,float rotation,int color)
    {
        GL11.glPushMatrix();
        GL11.glScaled(compassHack.getSize(), compassHack.getSize(), compassHack.getSize());
        GL11.glTranslatef((WurstClient.MC.getWindow().getScaledWidth()-3)/compassHack.getSize()-31.5f, 35, 0);
        GL11.glRotatef(rotation, 0, 0, -1);
        GL11.glTranslatef((-1.0f)*((WurstClient.MC.getWindow().getScaledWidth()-3)/compassHack.getSize()-31.5f), -35, 0f);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_BLEND);
        if(color==0)
        {
            WurstClient.MC.getTextureManager().bindTexture(marker_red);
        }
        else if(color==1)
        {
            WurstClient.MC.getTextureManager().bindTexture(marker_green);
        }
        else if(color==2)
        {
            WurstClient.MC.getTextureManager().bindTexture(marker_blue);
        }
        else if(color==3)
        {
            WurstClient.MC.getTextureManager().bindTexture(marker_lime);
        }
        else if(color==4)
        {
            WurstClient.MC.getTextureManager().bindTexture(marker_orange);
        }
        else if(color==5)
        {
            WurstClient.MC.getTextureManager().bindTexture(marker_pink);
        }
        else if(color==6)
        {
            WurstClient.MC.getTextureManager().bindTexture(marker_purple);
        }
        else if(color==7)
        {
            WurstClient.MC.getTextureManager().bindTexture(marker_yellow);
        }
        DrawableHelper.drawTexture(matrixStack, (int)((WurstClient.MC.getWindow().getScaledWidth()-3)/compassHack.getSize()-31.5f), 35, 0, 0, 5, 28, 5, 28);
        GL11.glPopMatrix();
    }
    private float drawString(MatrixStack matrixStack, String s, float posX, float posY, float scale, int corrner)
	{
        float posY2=0.0f;
        TextRenderer tr = WurstClient.MC.textRenderer;
        if(corrner==0){
        posX = posX/scale;
        posY2 = posY/scale;
        }
        else if(corrner==2)
        {
            posX = (posX)/scale; //* (WurstClient.MC.getWindow().getScaledWidth()/scale);
            posY2 = (WurstClient.MC.getWindow().getScaledHeight() - posY)/scale - tr.getWrappedLinesHeight(s, 9999);///100 * (WurstClient.MC.getWindow().getScaledHeight()/scale);
        }
        else if(corrner==3)
        {
            posX = (WurstClient.MC.getWindow().getScaledWidth() - posX)/scale - tr.getWidth(s); //* (WurstClient.MC.getWindow().getScaledWidth()/scale);
            posY2 = (WurstClient.MC.getWindow().getScaledHeight() - posY)/scale - tr.getWrappedLinesHeight(s, 9999);///100 * (WurstClient.MC.getWindow().getScaledHeight()/scale);
        }
        //does scale and prints text
        GL11.glPushMatrix();
        GL11.glScaled(scale, scale, scale);
		tr.draw(matrixStack, s, posX + 1, posY2 + 1, 0xff000000);
        tr.draw(matrixStack, s, posX, posY2, textColor | 0xff000000);
        GL11.glPopMatrix();
        return posY + tr.getWrappedLinesHeight(s, 9999)*scale;
        
    }
}
