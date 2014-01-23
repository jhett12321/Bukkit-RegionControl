package com.featherminecraft.RegionControl.listeners;

import java.util.List;

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
import com.featherminecraft.RegionControl.api.events.CaptureStatusChangeEvent;
import com.featherminecraft.RegionControl.api.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.api.events.ControlPointCaptureEvent;
import com.featherminecraft.RegionControl.api.events.ControlPointDefendEvent;
import com.featherminecraft.RegionControl.api.events.ControlPointInfluenceRateChangeEvent;
import com.featherminecraft.RegionControl.api.events.ControlPointNeutralizeEvent;
import com.featherminecraft.RegionControl.api.events.InfluenceOwnerChangeEvent;
import com.featherminecraft.RegionControl.api.events.RegionCaptureEvent;
import com.featherminecraft.RegionControl.api.events.RegionDefendEvent;
import com.featherminecraft.RegionControl.api.events.RegionInfluenceRateChangeEvent;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.spout.RespawnScreen;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;

public class SpoutPlayerListener implements Listener
{
    private RespawnScreen respawnScreen;
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onButtonClick(ButtonClickEvent event)
    {
        RCPlayer player = PlayerAPI.getRCPlayerFromBukkitPlayer(event.getPlayer());
        if(event.getButton().getText().contains("Redeploy:"))
        {
            PlayerAPI.respawnPlayer(player);
            event.getPlayer().getMainScreen().closePopup();
            player.getClientRunnable("spoutRespawnTooltip").cancel();
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
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onControlPointCapture(ControlPointCaptureEvent event)
    {
        List<RCPlayer> playerList = event.getRegion().getPlayers();
        
        for(RCPlayer player : playerList)
        {
            if(player.hasSpout())
            {
                player.getSpoutClientLogic().updateControlPoints(event.getRegion());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onControlPointDefend(ControlPointDefendEvent event)
    {
        List<RCPlayer> playerList = event.getRegion().getPlayers();
        
        for(RCPlayer player : playerList)
        {
            if(player.hasSpout())
            {
                player.getSpoutClientLogic().updateControlPoints(event.getRegion());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onControlPointNeutralise(ControlPointNeutralizeEvent event)
    {
        List<RCPlayer> playerList = event.getRegion().getPlayers();
        
        for(RCPlayer player : playerList)
        {
            if(player.hasSpout())
            {
                player.getSpoutClientLogic().updateControlPoints(event.getRegion());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onControlPointPlayerInfluenceChange(ControlPointInfluenceRateChangeEvent event)
    {
        List<RCPlayer> addedPlayers = event.getAddedPlayers();
        List<RCPlayer> removedPlayers = event.getRemovedPlayers();
        
        for(RCPlayer player : addedPlayers)
        {
            if(player.hasSpout())
            {
                player.getSpoutClientLogic().setControlPoint(event.getControlPoint());
                player.getSpoutClientLogic().showControlPointCaptureBar();
            }
        }
        
        for(RCPlayer player : removedPlayers)
        {
            if(player.hasSpout())
            {
                player.getSpoutClientLogic().setControlPoint(null);
                player.getSpoutClientLogic().hideControlPointCaptureBar();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInfluenceOwnerChange(InfluenceOwnerChangeEvent event)
    {
        CapturableRegion region = event.getRegion();
        List<RCPlayer> affectedPlayers = region.getPlayers();
        
        for(RCPlayer player : affectedPlayers)
        {
            if(player.hasSpout())
            {
                SpoutClientLogic spoutClientLogic = player.getSpoutClientLogic();
                spoutClientLogic.updateInfluenceOwnerIcon();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInfluenceRateChange(RegionInfluenceRateChangeEvent event)
    {
        CapturableRegion region = event.getRegion();
        List<RCPlayer> affectedPlayers = region.getPlayers();
        
        for(RCPlayer player : affectedPlayers)
        {
            if(player.hasSpout())
            {
                SpoutClientLogic spoutClientLogic = player.getSpoutClientLogic();
                spoutClientLogic.updateInfluenceRate(event.getNewInfluenceRate());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
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
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        final SpoutPlayer splayer = (SpoutPlayer) event.getEntity();
        RCPlayer rcplayer = PlayerAPI.getRCPlayerFromBukkitPlayer(splayer);
        if(rcplayer.hasSpout())
        {
            InGameHUD mainscreen = splayer.getMainScreen();
            respawnScreen = new RespawnScreen(mainscreen, rcplayer);
        }
        else
        {
            // TODO
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionCapture(RegionCaptureEvent event)
    {
        List<RCPlayer> playerList = event.getRegion().getPlayers();
        
        for(RCPlayer player : playerList)
        {
            if(player.hasSpout())
            {
                player.getSpoutClientLogic().updateRegion(event.getRegion());
            }
            // TODO Play Capture Sounds/Music
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionCaptureStatusChange(CaptureStatusChangeEvent event)
    {
        List<RCPlayer> playerList = event.getRegion().getPlayers();
        
        for(RCPlayer player : playerList)
        {
            if(player.hasSpout())
            {
                player.getSpoutClientLogic().setRegionCaptureStatus(event.isBeingCaptured());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionDefend(RegionDefendEvent event)
    {
        List<RCPlayer> playerList = event.getRegion().getPlayers();
        
        for(RCPlayer player : playerList)
        {
            if(player.hasSpout())
            {
                player.getSpoutClientLogic().updateRegion(event.getRegion());
            }
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