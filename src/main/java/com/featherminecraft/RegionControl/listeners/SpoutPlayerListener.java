package com.featherminecraft.RegionControl.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutcraftFailedEvent;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.api.PlayerAPI;
import com.featherminecraft.RegionControl.api.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.spout.RespawnScreen;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;

public class SpoutPlayerListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onButtonClick(ButtonClickEvent event)
    {
        RCPlayer player = PlayerAPI.getRCPlayerFromBukkitPlayer(event.getPlayer());
        if(event.getButton().getText().contains("Redeploy:"))
        {
            if(player.getBukkitPlayer().isDead())
            {
                PlayerAPI.respawnPlayer(player);
            }
            else
            {
                player.getBukkitPlayer().teleport(player.getRespawnLocation());
            }
            event.getPlayer().getMainScreen().closePopup();
            player.getClientRunnable("spoutRespawnTooltip").cancel();
            player.showPlayer();
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeRegion(ChangeRegionEvent event)
    {
        RCPlayer player = event.getPlayer();
        
        CapturableRegion newRegion = event.getNewRegion();
        if(player.hasSpout())
        {
            SpoutClientLogic spoutClientLogic = player.getSpoutClientLogic();
            spoutClientLogic.updateRegion(newRegion);
        }
        
        if(event.getOldRegion() != null)
        {
            List<RCPlayer> oldPlayerList = event.getOldRegion().getPlayers();
            
            for(RCPlayer rcPlayer : oldPlayerList)
            {
                if(rcPlayer.hasSpout())
                {
                    rcPlayer.getSpoutClientLogic().updatePlayersDetected();
                }
            }
        }
        
        if(event.getNewRegion() != null)
        {
            List<RCPlayer> newPlayerList = event.getNewRegion().getPlayers();
            
            for(RCPlayer rcPlayer : newPlayerList)
            {
                if(rcPlayer.hasSpout())
                {
                    rcPlayer.getSpoutClientLogic().updatePlayersDetected();
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKeyPressed(KeyPressedEvent event)
    {
        if(event.getKey().equals(Keyboard.KEY_ESCAPE))
        {
            SpoutPlayer splayer = event.getPlayer();
            RCPlayer rcplayer = PlayerAPI.getRCPlayerFromBukkitPlayer(splayer);
            
            if(splayer.isDead() || !rcplayer.isVisible())
            {
                splayer.getMainScreen().closePopup();
                splayer.getMainScreen().attachPopupScreen(rcplayer.getSpoutClientLogic().getRespawnScreen().getPopup());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        final SpoutPlayer splayer = (SpoutPlayer) event.getEntity();
        RCPlayer rcplayer = PlayerAPI.getRCPlayerFromBukkitPlayer(splayer);
        if(rcplayer.hasSpout())
        {
            InGameHUD mainscreen = splayer.getMainScreen();
            rcplayer.getSpoutClientLogic().setRespawnScreen(new RespawnScreen(mainscreen, rcplayer));
        }
        else
        {
            // TODO
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onSpoutcraftEnable(SpoutCraftEnableEvent event)
    {
        SpoutPlayer splayer = event.getPlayer();
        
        RCPlayer rcPlayer = PlayerAPI.getRCPlayerFromBukkitPlayer(splayer.getPlayer());
        rcPlayer.setHasSpout(true);
        
        rcPlayer.setSpoutClientLogic(new SpoutClientLogic());
        rcPlayer.getSpoutClientLogic().setupClientElements(rcPlayer);
        
        if(rcPlayer.getBukkitPlayer().getScoreboard() != Bukkit.getServer().getScoreboardManager().getMainScoreboard())
        {
            rcPlayer.getBukkitPlayer().setScoreboard(Bukkit.getServer().getScoreboardManager().getMainScoreboard());
        }
        
        InGameHUD mainscreen = splayer.getMainScreen();
        rcPlayer.getSpoutClientLogic().setRespawnScreen(new RespawnScreen(mainscreen, rcPlayer));
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onSpoutCraftFailedEvent(SpoutcraftFailedEvent event)
    {
        RCPlayer rcPlayer = PlayerAPI.getRCPlayerFromBukkitPlayer(event.getPlayer().getPlayer());
        CapturableRegion currentRegion = rcPlayer.getCurrentRegion();
        List<RCPlayer> playerList = currentRegion.getPlayers();
        for(RCPlayer rPlayer : playerList)
        {
            if(rPlayer.hasSpout())
            {
                rPlayer.getSpoutClientLogic().updatePlayersDetected();
            }
        }
    }
}