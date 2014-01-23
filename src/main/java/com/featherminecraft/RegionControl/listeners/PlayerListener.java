package com.featherminecraft.RegionControl.listeners;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.RegionControl.Config;
import com.featherminecraft.RegionControl.DependencyManager;
import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.api.PlayerAPI;
import com.featherminecraft.RegionControl.api.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.api.events.ControlPointCaptureEvent;
import com.featherminecraft.RegionControl.api.events.ControlPointDefendEvent;
import com.featherminecraft.RegionControl.api.events.ControlPointNeutralizeEvent;
import com.featherminecraft.RegionControl.api.events.InfluenceOwnerChangeEvent;
import com.featherminecraft.RegionControl.api.events.RegionCaptureEvent;
import com.featherminecraft.RegionControl.api.events.RegionDefendEvent;
import com.featherminecraft.RegionControl.api.events.RegionInfluenceRateChangeEvent;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class PlayerListener implements Listener
{
    // Block removed from region. Needs to be added after region capture.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(!event.isCancelled())
        {
            for(CapturableRegion capturableRegion : ServerLogic.capturableRegions.values())
            {
                ProtectedRegion region = capturableRegion.getRegion();
                if(region.contains(toVector(event.getBlock().getLocation())))
                {
                    if(!capturableRegion.getBlocksPlaced().contains(event.getBlock().getState()))
                    {
                        capturableRegion.getBlocksDestroyed().add(event.getBlock().getState());
                    }
                }
            }
        }
    }
    
    // Block added to region. Needs to be removed after region capture.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(!event.isCancelled())
        {
            for(CapturableRegion capturableRegion : ServerLogic.capturableRegions.values())
            {
                ProtectedRegion region = capturableRegion.getRegion();
                if(region.contains(toVector(event.getBlock().getLocation())))
                {
                    if(!capturableRegion.getBlocksDestroyed().contains(event.getBlockPlaced().getState()))
                    {
                        capturableRegion.getBlocksPlaced().add(event.getBlockPlaced().getState());
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeRegion(ChangeRegionEvent event)
    {
        if(!DependencyManager.isSpoutCraftAvailable() || !event.getPlayer().hasSpout())
        {
            event.getPlayer().getBukkitPlayer().setScoreboard(event.getNewRegion().getRegionScoreboard().getScoreboard());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onControlPointCapture(ControlPointCaptureEvent event)
    {
        event.getRegion().getRegionScoreboard().updateControlPoints();
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onControlPointDefend(ControlPointDefendEvent event)
    {
        event.getRegion().getRegionScoreboard().updateControlPoints();
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onControlPointNeutralise(ControlPointNeutralizeEvent event)
    {
        event.getRegion().getRegionScoreboard().updateControlPoints();
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        if(Config.getMainConfig().getBoolean("players.enableFriendlyDamage"))
        {
            return;
        }
        RCPlayer playerdamager = null;
        RCPlayer damagedplayer = null;
        
        Entity damager = event.getDamager();
        Entity damagedentity = event.getEntity();
        
        if((damagedentity instanceof Player))
        {
            damagedplayer = PlayerAPI.getRCPlayerFromBukkitPlayer((Player) damagedentity);
        }
        
        if(damager instanceof Player)
        {
            playerdamager = PlayerAPI.getRCPlayerFromBukkitPlayer((Player) damager);
        }
        
        else if(damager instanceof Projectile)
        {
            Projectile projectile = (Projectile) damager;
            Entity entitydamager = projectile.getShooter();
            if(entitydamager instanceof Player && entitydamager != null)
            {
                playerdamager = PlayerAPI.getRCPlayerFromBukkitPlayer((Player) entitydamager);
            }
        }
        
        if(playerdamager != null && damagedplayer != null)
        {
            if(playerdamager.getFaction() == damagedplayer.getFaction())
            {
                event.setCancelled(true);
            }
        }
    }
    
    // Explosion. Blocks need to be added after region capture.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event)
    {
        if(!event.isCancelled())
        {
            for(CapturableRegion capturableRegion : ServerLogic.capturableRegions.values())
            {
                ProtectedRegion region = capturableRegion.getRegion();
                for(Block block : event.blockList())
                {
                    if(region.contains(toVector(block.getLocation())))
                    {
                        if(!capturableRegion.getBlocksPlaced().contains(block.getState()))
                        {
                            capturableRegion.getBlocksDestroyed().add(block.getState());
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInfluenceOwnerChange(InfluenceOwnerChangeEvent event)
    {
        event.getRegion().getRegionScoreboard().updateInfluenceRate();
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInfluenceRateChange(RegionInfluenceRateChangeEvent event)
    {
        event.getRegion().getRegionScoreboard().updateInfluenceRate();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        
        Faction faction = PlayerAPI.getFactionFromGroup(DependencyManager.getPermission().getPrimaryGroup(player));
        if(faction == null)
        {
            // if(DependencyManager.isSpoutCraftAvailable())
            // {
            // if(SpoutUtils.isSpoutPlayer(player))
            // {
            // player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2400, 0, false));
            // new FactionSelectScreen(player);
            // }
            // }
            
            player.kickPlayer("You currently do not belong to a valid faction!");
            return;
        }
        
        CapturableRegion currentRegion = faction.getFactionSpawnRegion(player.getWorld());
        
        RCPlayer rcPlayer = new RCPlayer(player, faction, currentRegion);
        
        ServerLogic.players.put(player.getName(), rcPlayer);
        currentRegion.getPlayers().add(rcPlayer);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        if(event.getPlayer().isInsideVehicle())
        {
            event.getPlayer().getVehicle().eject();
        }
        // If a player is kicked for not being in a faction, they do not have an
        // RCPlayer object
        try
        {
            RCPlayer player = PlayerAPI.getRCPlayerFromBukkitPlayer(event.getPlayer());
            CapturableRegion currentRegion = player.getCurrentRegion();
            if(currentRegion != null)
            {
                currentRegion.getPlayers().remove(player);
                Bukkit.getServer().getPluginManager().callEvent(new ChangeRegionEvent(null, currentRegion, player));
            }
            
            for(BukkitTask runnable : player.getClientRunnables().values())
            {
                runnable.cancel();
            }
            ServerLogic.players.remove(event.getPlayer().getName());
            
        }
        catch(NullPointerException e)
        {
            return;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        RCPlayer rcplayer = PlayerAPI.getRCPlayerFromBukkitPlayer(player);
        Location respawnLocation = rcplayer.getRespawnLocation();
        if(respawnLocation != null)
        {
            event.setRespawnLocation(respawnLocation);
        }
        
        else
        {
            event.setRespawnLocation(rcplayer.getFaction().getFactionSpawnRegion(player.getWorld()).getSpawnPoint().getLocation());
        }
        
        rcplayer.setRespawnLocation(null);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionCapture(RegionCaptureEvent event)
    {
        event.getRegion().getRegionScoreboard().updateOwner();
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionDefend(RegionDefendEvent event)
    {
        event.getRegion().getRegionScoreboard().updateOwner();
    }
}