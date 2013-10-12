package com.featherminecraft.RegionControl.listeners;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.gui.InGameHUD;
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
//        RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: Added: " + event.getPlayer().getName() + " to the spout players list");
        SpoutPlayer splayer = event.getPlayer();
        
        RCPlayer rcPlayer = new PlayerUtils().getRCPlayerFromBukkitPlayer(splayer.getPlayer());
        rcPlayer.setHasSpout(true);
        
        rcPlayer.setSpoutClientLogic(new SpoutClientLogic());
        rcPlayer.getSpoutClientLogic().setupClientElements(rcPlayer);
    }
    
    @EventHandler
    public void onChangeRegion(ChangeRegionEvent event)
    {
//        RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: ChangeRegionEvent Listened");
        RCPlayer player = event.getPlayer();
        SpoutClientLogic spoutClientLogic = player.getSpoutClientLogic();
        CapturableRegion newRegion = event.getNewRegion();
        if(newRegion == null)
        {
            spoutClientLogic.updateRegion(null);
        }
        else
        {
//            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: " + newRegion.getDisplayName() + "'s Owner is " + newRegion.getOwner().getName());
//            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: " + newRegion.getDisplayName() + "'s Influence Owner is " + newRegion.getInfluenceOwner().getName());
//            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: " + newRegion.getDisplayName() + "'s Influence Rate is " + newRegion.getInfluenceRate());
//            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: " + newRegion.getDisplayName() + "'s Majority Controller is " + newRegion.getMajorityController().getName());
//            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: " + newRegion.getDisplayName() + "'s Influence is " + newRegion.getInfluenceMap().get(newRegion.getMajorityController()));
//            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: " + newRegion.getDisplayName() + "'s Base Influence is " + newRegion.getBaseInfluence());
            spoutClientLogic.updateRegion(newRegion);
        }
        
    }
    
    @EventHandler
    public void onInfluenceRateChange(InfluenceRateChangeEvent event)
    {
        CapturableRegion region = event.getRegion();
        List<RCPlayer> affectedPlayers = region.getPlayers();
        
        for(RCPlayer player : affectedPlayers)
        {
            SpoutClientLogic spoutClientLogic = player.getSpoutClientLogic();
            spoutClientLogic.updateRegion(event.getRegion());
        }
    }
    
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        SpoutPlayer splayer = (SpoutPlayer) event.getEntity();
        RCPlayer rcplayer = new PlayerUtils().getRCPlayerFromBukkitPlayer((Player) splayer);
        
        InGameHUD mainscreen = splayer.getMainScreen();
        new RespawnScreen(mainscreen, rcplayer);
    }
}