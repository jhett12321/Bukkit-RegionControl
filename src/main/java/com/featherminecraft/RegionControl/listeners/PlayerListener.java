package com.featherminecraft.RegionControl.listeners;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
//import org.bukkit.projectiles.ProjectileSource; //TODO 1.7
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.RegionControl.DependencyManager;
import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.RegionControl;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.api.PlayerAPI;
import com.featherminecraft.RegionControl.api.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.commands.Command;
import com.featherminecraft.RegionControl.commands.CommandInfo;
import com.featherminecraft.RegionControl.data.Config;
import com.featherminecraft.RegionControl.data.Data;

public class PlayerListener implements Listener
{
    // Block removed from region. Needs to be added after region capture.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(!event.isCancelled())
        {
            if(!ServerLogic.editMode)
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
                PlayerAPI.getRCPlayerFromBukkitPlayer(event.getPlayer()).addDestroyedBlock();
            }
            else
            {
                Command editCommand = RegionControl.plugin.getCommandHandler().getCommand("editmode");
                CommandInfo info = RegionControl.plugin.getCommandHandler().getCommandInfo(editCommand);
                if(!event.getPlayer().hasPermission(info.permission()))
                {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    // Block added to region. Needs to be removed after region capture.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(!event.isCancelled())
        {
            if(!ServerLogic.editMode)
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
                PlayerAPI.getRCPlayerFromBukkitPlayer(event.getPlayer()).addPlacedBlock();
            }
            else
            {
                Command editCommand = RegionControl.plugin.getCommandHandler().getCommand("editmode");
                CommandInfo info = RegionControl.plugin.getCommandHandler().getCommandInfo(editCommand);
                if(!event.getPlayer().hasPermission(info.permission()))
                {
                    event.setCancelled(true);
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
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        RCPlayer playerdamager = null;
        RCPlayer damagedplayer = null;
        
        if(!Config.getMainConfig().getBoolean("players.enableFriendlyDamage"))
        {
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
                Entity projectileSource = projectile.getShooter();
                //ProjectileSource projectileSource = projectile.getShooter(); //TODO 1.7
                
                if(projectileSource instanceof Player && projectileSource != null)
                {
                    playerdamager = PlayerAPI.getRCPlayerFromBukkitPlayer((Player) projectileSource);
                }
            }
            
            if(playerdamager != null && damagedplayer != null)
            {
                if(playerdamager.getFaction() == damagedplayer.getFaction())
                {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        
        if(playerdamager != null && damagedplayer != null)
        {
            if(damagedplayer.getDamageSources().containsKey(playerdamager))
            {
                damagedplayer.getDamageSources().put(playerdamager, damagedplayer.getDamageSources().get(playerdamager) + event.getDamage());
            }
            else
            {
                damagedplayer.getDamageSources().put(playerdamager, event.getDamage());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        RCPlayer deadPlayer = PlayerAPI.getRCPlayerFromBukkitPlayer(event.getEntity());
        RCPlayer killer = PlayerAPI.getRCPlayerFromBukkitPlayer(event.getEntity().getKiller());
        
        deadPlayer.hidePlayer();
        
        Map<RCPlayer, Double> damageSources = null;
        if(killer == null)
        {
            damageSources = deadPlayer.getDamageSources();
            if(damageSources != null && deadPlayer.getDamageSources().size() > 0)
            {
                while(damageSources.entrySet().iterator().next() != null)
                {
                    killer = damageSources.entrySet().iterator().next().getKey();
                }
            }
        }
        
        if(killer != null)
        {
            killer.addKill();
            if(damageSources != null)
            {
                damageSources.remove(killer);
                for(RCPlayer player : damageSources.keySet())
                {
                    player.addAssist();
                }
            }
        }
        
        deadPlayer.addDeath();
        deadPlayer.getDamageSources().clear();
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
                    if(block.getType() != Material.AIR)
                    {
                        if(region.contains(toVector(block.getLocation())))
                        {
                            if(!capturableRegion.getBlocksPlaced().contains(block.getState()))
                            {
                                capturableRegion.getBlocksDestroyed().add(block.getState());
                            }
                        }
                        if(event.getEntity() instanceof TNTPrimed)
                        {
                            TNTPrimed tntBlock = (TNTPrimed) event.getEntity();
                            if(tntBlock.getSource() != null && tntBlock.getSource() instanceof Player)
                            {
                                Player sourcePlayer = (Player) tntBlock.getSource();
                                PlayerAPI.getRCPlayerFromBukkitPlayer(sourcePlayer).addDestroyedBlock();
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        
        //Player Faction
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
        
        rcPlayer.hidePlayer();
        currentRegion.getPlayers().add(rcPlayer);
        
        event.getPlayer().setScoreboard(currentRegion.getRegionScoreboard().getScoreboard()); //This gets unset if player has Spout.
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
            //ServerLogic.players.remove(event.getPlayer().getUniqueId()); //TODO 1.7
            ServerLogic.players.remove(event.getPlayer().getName());
            
        }
        catch(NullPointerException e)
        {
            return;
        }
        
        Data.processQueue();
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
        
        if(!rcplayer.isVisible())
        {
            rcplayer.showPlayer();
        }
        
        rcplayer.setRespawnLocation(null);
    }
}