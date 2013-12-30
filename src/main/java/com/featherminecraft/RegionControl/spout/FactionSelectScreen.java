package com.featherminecraft.RegionControl.spout;

import org.bukkit.entity.Player;

import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.RegionControl.ServerLogic;

public class FactionSelectScreen
{
    public FactionSelectScreen(Player player)
    {
        SpoutPlayer sPlayer = (SpoutPlayer) player;
        Screen screen = sPlayer.getMainScreen();
        
        Color elderTyrants = new Color(ServerLogic.factions.get("elderTyrants").getFactionColor().getRed(),ServerLogic.factions.get("elderTyrants").getFactionColor().getGreen(),ServerLogic.factions.get("elderTyrants").getFactionColor().getBlue());
        Color relkanaForces = new Color(ServerLogic.factions.get("relkanaForces").getFactionColor().getRed(),ServerLogic.factions.get("relkanaForces").getFactionColor().getGreen(),ServerLogic.factions.get("relkanaForces").getFactionColor().getBlue());
        //Color theReturned = new Color(ServerLogic.factions.get("theReturned").getFactionColor().getRed(),ServerLogic.factions.get("elderTyrants").getFactionColor().getGreen(),ServerLogic.factions.get("elderTyrants").getFactionColor().getBlue());
        
        Button faction1 = (Button) new GenericButton("The Elder Tyrants").setColor(elderTyrants).setTooltip("");
        Button faction2 = (Button) new GenericButton("Relkana Forces").setColor(elderTyrants).setTooltip("");
        //Button faction3 = (Button) new GenericButton("The Returned").setColor(elderTyrants).setTooltip("");
    }
}