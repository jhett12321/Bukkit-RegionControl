package com.featherminecraft.RegionControl.listeners;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenOpenEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;


import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.SpawnPoint;
import com.featherminecraft.RegionControl.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.ClientRunnables;
import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;
import com.featherminecraft.RegionControl.utils.PlayerUtils;
import com.featherminecraft.RegionControl.utils.RegionUtils;
import com.featherminecraft.RegionControl.utils.Utils;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


public class SpoutPlayerListener implements Listener {
    private SpoutClientLogic spoutclientlogic;
    private Location respawnlocation;
    private ClientRunnables clientrunnables;
    private SpoutPlayer splayer;

    @EventHandler
    public void onSpoutcraftEnable(SpoutCraftEnableEvent event) {
        spoutclientlogic = new SpoutClientLogic();
        spoutclientlogic.setupClientElements(splayer);
//        clientrunnables = new ClientRunnables(event.getPlayer().getPlayer()); //TODO
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeRegion(ChangeRegionEvent event) {
        if(splayer == SpoutManager.getPlayer(event.getPlayer()))
        {
            spoutclientlogic.updateRegion(event.getNewRegion());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        clientrunnables.cancel();
        
        Vector location = toVector(event.getPlayer().getLocation());
        WorldGuardPlugin worldguard = Utils.getWorldGuard();
        RegionManager regionmanager = worldguard.getRegionManager(event.getPlayer().getWorld());
        ApplicableRegionSet applicableregions = regionmanager.getApplicableRegions(location);

        if(applicableregions != null && applicableregions.size() == 1) {
            for(ProtectedRegion region : applicableregions)
            {
                CapturableRegion capturableregion = new RegionUtils().getCapturableRegionFromWorldGuardRegion(region, event.getPlayer().getWorld());
                    if(capturableregion.getRegion().contains(location))
                    {
                        RegionUtils.removePlayerFromRegion(event.getPlayer(), capturableregion);
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        Player playerdamager = null;
        Player damagedplayer = null;
        
        Entity damager = event.getDamager();
        Entity damagedentity = event.getEntity();
        
        if((damagedentity instanceof Player))
        {
            damagedplayer = (Player) damagedentity;
        }
        
        if(damager instanceof Player)
        {
            playerdamager = (Player) damager;
        }
        
        else if(damager instanceof Projectile)
        {
            Projectile projectile = (Projectile) damager;
            Entity entitydamager = projectile.getShooter();
            if(entitydamager instanceof Player && entitydamager != null)
            {
                playerdamager = (Player) entitydamager;
            }
        }
        
        if(playerdamager != null && damagedplayer != null)
        {
            PlayerUtils playerutils = new PlayerUtils();
            Faction damagerfaction = playerutils.getPlayerFaction(playerdamager);
            Faction damagedentityfaction = playerutils.getPlayerFaction(damagedplayer);
            if(damagerfaction == damagedentityfaction)
            {
                event.setCancelled(true);
            }
        }
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