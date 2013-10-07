package com.featherminecraft.RegionControl.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.getspout.spoutapi.event.screen.ScreenOpenEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.events.InfluenceRateChangeEvent;
import com.featherminecraft.RegionControl.spout.RespawnScreen;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;
import com.featherminecraft.RegionControl.utils.PlayerUtils;

public class SpoutPlayerListener implements Listener {

    @EventHandler
    public void onSpoutcraftEnable(SpoutCraftEnableEvent event)
    {
        SpoutPlayer splayer = event.getPlayer();
        
        RCPlayer rcPlayer = new PlayerUtils().getRCPlayerFromBukkitPlayer(splayer.getPlayer());
        rcPlayer.setHasSpout(true);
        
        rcPlayer.setSpoutClientLogic(new SpoutClientLogic());
        rcPlayer.getSpoutClientLogic().setupClientElements(splayer);
    }
    
    @EventHandler
    public void onChangeRegion(ChangeRegionEvent event)
    {
        RCPlayer player = event.getPlayer();
        SpoutClientLogic spoutClientLogic = player.getSpoutClientLogic();
        spoutClientLogic.updateRegion(event.getNewRegion());
    }
    
    @EventHandler
    public void onInfluenceRateChange(InfluenceRateChangeEvent event)
    {
        CapturableRegion region = event.getRegion();
        List<RCPlayer> affectedPlayers = event.getRegion().getPlayers();
        
        for(RCPlayer player : affectedPlayers)
        {
            SpoutClientLogic spoutClientLogic = player.getSpoutClientLogic();
            spoutClientLogic.updateRegion(event.getRegion());
        }
    }
    
    
    @EventHandler
    public void onScreenOpen(ScreenOpenEvent event)
    {
        SpoutPlayer splayer = event.getPlayer();
        RCPlayer rcplayer = new PlayerUtils().getRCPlayerFromBukkitPlayer((Player) splayer);
        
        if(event.getScreenType().equals(ScreenType.GAME_OVER_SCREEN))
        {
            InGameHUD mainscreen = splayer.getMainScreen();
            new RespawnScreen(mainscreen, rcplayer);
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        PlayerUtils playerUtils = new PlayerUtils();
        
        Player player = event.getPlayer();
        RCPlayer rcplayer = playerUtils.getRCPlayerFromBukkitPlayer(player);
        Location respawnLocation = rcplayer.getRespawnLocation();
        if(respawnLocation != null)
        {
            event.setRespawnLocation(respawnLocation);
        }
        
        else
        {
            event.setRespawnLocation(rcplayer.getFaction().getFactionSpawnRegion(player.getWorld()).getSpawnPoint().getLocation()); //TODO: Replace with config default value for per-faction spawn.
        }
    }
}