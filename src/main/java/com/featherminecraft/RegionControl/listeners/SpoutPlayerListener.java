package com.featherminecraft.RegionControl.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.player.SpoutPlayer;


import com.featherminecraft.RegionControl.ClientRunnables;
import com.featherminecraft.RegionControl.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;


public class SpoutPlayerListener implements Listener {
    private SpoutClientLogic spoutclientlogic;
    private Location respawnlocation;
    private ClientRunnables clientrunnables;
    private SpoutPlayer splayer;

    @EventHandler
    public void onSpoutcraftEnable(SpoutCraftEnableEvent event) {
        SpoutPlayer splayer = event.getPlayer();
        this.splayer = splayer;
        
        spoutclientlogic = new SpoutClientLogic();
        spoutclientlogic.setupClientElements(splayer);
    }
    
    @EventHandler
    public void onChangeRegion(ChangeRegionEvent event) {
        spoutclientlogic.updateRegion(event.getNewRegion());
    }

    /*
    @EventHandler
    public void onScreenOpen(ScreenOpenEvent event)
    {
        ScreenType screentype = event.getScreenType();
        //Respawn Screen
        if(screentype == ScreenType.GAME_OVER_SCREEN)
        {
            Map<SpawnPoint,Button> respawnbuttons = spoutclientlogic.getBestSpawnPointsForPlayer(event.getPlayer());
            int positiony = 0;
            for(Entry<SpawnPoint, Button> button : respawnbuttons.entrySet())
            {
                positiony = positiony + 10;
                button.getValue().setVisible(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if(respawnlocation != null)
        {
            event.setRespawnLocation(respawnlocation);
        }
        
        else
        {
            event.setRespawnLocation(new PlayerUtils().getPlayerFaction(event.getPlayer()).getFactionSpawnPoint().getLocation()); //TODO: Replace with config default value for per-faction spawn.
        }
    }
    
    @EventHandler
    public void onButtonClick(ButtonClickEvent event) {
        Button button = event.getButton();
        Map<SpawnPoint,Button> respawnbuttons = spoutclientlogic.getSpawnButtons();
        for(Entry<SpawnPoint, Button> respawnbutton : respawnbuttons.entrySet())
        {
            if(respawnbutton == button)
            {
                respawnlocation = respawnbutton.getKey().getLocation();
            }
        }
    }*/
}