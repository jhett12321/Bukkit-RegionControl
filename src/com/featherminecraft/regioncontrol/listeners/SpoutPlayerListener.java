package com.featherminecraft.regioncontrol.listeners;

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
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.gui.Button;

import com.featherminecraft.regioncontrol.CapturableRegion;
import com.featherminecraft.regioncontrol.ClientRunnables;
import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.SpawnPoint;
import com.featherminecraft.regioncontrol.spout.SpoutClientLogic;
import com.featherminecraft.regioncontrol.utils.PlayerUtils;
import com.featherminecraft.regioncontrol.utils.RegionUtils;
import com.featherminecraft.regioncontrol.utils.Utils;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class SpoutPlayerListener implements Listener {
    private SpoutClientLogic spoutclientlogic;
    private Location respawnlocation;
    private ClientRunnables clientrunnables;
    

    @EventHandler
    public void onSpoutcraftEnable(SpoutCraftEnableEvent event) {
        spoutclientlogic = new SpoutClientLogic(event.getPlayer());
        clientrunnables = new ClientRunnables(event.getPlayer().getPlayer());
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
                CapturableRegion capturableregion = new RegionUtils().getCapturableRegion(region, event.getPlayer().getWorld());
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
    }
}