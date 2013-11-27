package com.featherminecraft.RegionControl.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.events.ControlPointCaptureEvent;
import com.featherminecraft.RegionControl.events.ControlPointDefendEvent;
import com.featherminecraft.RegionControl.events.ControlPointNeutraliseEvent;
import com.featherminecraft.RegionControl.events.ControlPointPlayerInfluenceChangeEvent;
import com.featherminecraft.RegionControl.events.RegionCaptureEvent;
import com.featherminecraft.RegionControl.events.RegionCaptureStatusChangeEvent;
import com.featherminecraft.RegionControl.events.RegionInfluenceRateChangeEvent;
import com.featherminecraft.RegionControl.spout.RespawnScreen;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;
import com.featherminecraft.RegionControl.utils.PlayerUtils;

public class SpoutPlayerListener implements Listener
{
    
    private RespawnScreen respawnScreen;
    
    @EventHandler
    public void onButtonClick(ButtonClickEvent event)
    {
        // Utilities
        PlayerUtils playerUtils = new PlayerUtils();
        
        RCPlayer player = playerUtils.getRCPlayerFromBukkitPlayer(event.getPlayer());
        if(event.getButton().getText().contains("Redeploy:"))
        {
            playerUtils.respawnPlayer(player);
            event.getPlayer().getMainScreen().closePopup();
        }
    }
    
    @EventHandler
    public void onChangeRegion(ChangeRegionEvent event)
    {
        RCPlayer player = event.getPlayer();
        SpoutClientLogic spoutClientLogic = player.getSpoutClientLogic();
        CapturableRegion newRegion = event.getNewRegion();
        spoutClientLogic.updateRegion(newRegion);
        
        if(event.getOldRegion() != null)
        {
            List<RCPlayer> oldPlayerList = event.getOldRegion().getPlayers();
            
            for(RCPlayer rcPlayer : oldPlayerList)
            {
                rcPlayer.getSpoutClientLogic().updatePlayersDetected();
            }
        }
        
        if(event.getNewRegion() != null)
        {
            List<RCPlayer> newPlayerList = event.getNewRegion().getPlayers();
            
            for(RCPlayer rcPlayer : newPlayerList)
            {
                rcPlayer.getSpoutClientLogic().updatePlayersDetected();
            }
        }
    }
    
    @EventHandler
    public void onControlPointCapture(ControlPointCaptureEvent event)
    {
        List<RCPlayer> playerList = event.getRegion().getPlayers();
        
        for(RCPlayer player : playerList)
        {
            player.getSpoutClientLogic().updateControlPoints(event.getRegion());
        }
    }
    
    @EventHandler
    public void onControlPointDefend(ControlPointDefendEvent event)
    {
        List<RCPlayer> playerList = event.getRegion().getPlayers();
        
        for(RCPlayer player : playerList)
        {
            player.getSpoutClientLogic().updateControlPoints(event.getRegion());
        }
    }
    
    @EventHandler
    public void onControlPointNeutralise(ControlPointNeutraliseEvent event)
    {
        List<RCPlayer> playerList = event.getRegion().getPlayers();
        
        for(RCPlayer player : playerList)
        {
            player.getSpoutClientLogic().updateControlPoints(event.getRegion());
        }
    }
    
    @EventHandler
    public void onControlPointPlayerInfluenceChange(ControlPointPlayerInfluenceChangeEvent event)
    {
        List<RCPlayer> addedPlayers = event.getAddedPlayers();
        List<RCPlayer> removedPlayers = event.getRemovedPlayers();
        
        for(RCPlayer player : addedPlayers)
        {
            player.getSpoutClientLogic().setControlPoint(event.getControlPoint());
            player.getSpoutClientLogic().showControlPointCaptureBar();
        }
        
        for(RCPlayer player : removedPlayers)
        {
            player.getSpoutClientLogic().setControlPoint(null);
            player.getSpoutClientLogic().hideControlPointCaptureBar();
        }
    }
    
    @EventHandler
    public void onInfluenceRateChange(RegionInfluenceRateChangeEvent event)
    {
        CapturableRegion region = event.getRegion();
        List<RCPlayer> affectedPlayers = region.getPlayers();
        
        for(RCPlayer player : affectedPlayers)
        {
            SpoutClientLogic spoutClientLogic = player.getSpoutClientLogic();
            spoutClientLogic.updateInfluenceRate(event.getNewInfluenceRate());
        }
    }
    
    @EventHandler
    public void onKeyPressed(KeyPressedEvent event)
    {
        if(event.getKey().equals(Keyboard.KEY_ESCAPE))
        {
            SpoutPlayer splayer = event.getPlayer();
            if(splayer.isDead())
            {
                splayer.getMainScreen().closePopup();
                splayer.getMainScreen().attachPopupScreen(respawnScreen.getPopup());
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        final SpoutPlayer splayer = (SpoutPlayer) event.getEntity();
        RCPlayer rcplayer = new PlayerUtils().getRCPlayerFromBukkitPlayer(splayer);
        
        InGameHUD mainscreen = splayer.getMainScreen();
        respawnScreen = new RespawnScreen(mainscreen, rcplayer);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        // Utilities Begin
        PlayerUtils playerUtils = new PlayerUtils();
        // Utilities End
        
        // If a player is kicked for not being in a faction, they do not have an
        // RCPlayer object
        try
        {
            RCPlayer player = playerUtils.getRCPlayerFromBukkitPlayer(event.getPlayer());
            player.getSpoutClientLogic().getRunnable().cancel();
        }
        catch(NullPointerException e)
        {
            return;
        }
    }
    
    @EventHandler
    public void onRegionCapture(RegionCaptureEvent event)
    {
        List<RCPlayer> playerList = event.getCapturableRegion().getPlayers();
        
        for(RCPlayer player : playerList)
        {
            player.getSpoutClientLogic().updateRegion(event.getCapturableRegion());
            // TODO Play Capture Sounds/Music
        }
    }
    
    @EventHandler
    public void onRegionCaptureStatusChange(RegionCaptureStatusChangeEvent event)
    {
        List<RCPlayer> playerList = event.getRegion().getPlayers();
        
        for(RCPlayer player : playerList)
        {
            player.getSpoutClientLogic().setRegionCaptureStatus(event.getCaptureStatus());
        }
    }
    
    @EventHandler
    public void onSpoutcraftEnable(SpoutCraftEnableEvent event)
    {
        SpoutPlayer splayer = event.getPlayer();
        
        RCPlayer rcPlayer = new PlayerUtils().getRCPlayerFromBukkitPlayer(splayer.getPlayer());
        rcPlayer.setHasSpout(true);
        
        rcPlayer.setSpoutClientLogic(new SpoutClientLogic());
        rcPlayer.getSpoutClientLogic().setupClientElements(rcPlayer);
    }
}