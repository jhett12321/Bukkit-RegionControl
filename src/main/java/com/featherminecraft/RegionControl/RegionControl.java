package com.featherminecraft.RegionControl;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.featherminecraft.RegionControl.api.PlayerAPI;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.commands.CommandHandler;
import com.featherminecraft.RegionControl.data.Config;
import com.featherminecraft.RegionControl.data.Data;
import com.featherminecraft.RegionControl.dynmap.DynmapImpl;
import com.featherminecraft.RegionControl.listeners.DynmapListener;
import com.featherminecraft.RegionControl.listeners.PlayerListener;
import com.featherminecraft.RegionControl.listeners.RegionListener;
import com.featherminecraft.RegionControl.listeners.ServerListener;
import com.featherminecraft.RegionControl.listeners.SpoutPlayerListener;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;

public final class RegionControl extends JavaPlugin
{
    public static RegionControl plugin;
    private static boolean pluginLoaded = false;
    private CommandHandler commandHandler;
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        commandHandler.handleCommand(sender, args);
        return true; // We handle all usage/permission messages.
    }
    
    @Override
    public void onDisable()
    {
        if(pluginLoaded)
        {
            Config.saveAll(true);
            Data.processQueue();
            for(RCPlayer player : ServerLogic.players.values())
            {
                for(BukkitTask runnable : player.getClientRunnables().values())
                {
                    runnable.cancel();
                }
            }
            
            for(BukkitTask runnable : ServerLogic.serverRunnables.values())
            {
                runnable.cancel();
            }
            
            for(CapturableRegion region : ServerLogic.capturableRegions.values())
            {
                for(BlockState block : region.getBlocksPlaced())
                {
                    block.getBlock().setType(Material.AIR);
                }
                for(BlockState block : region.getBlocksDestroyed())
                {
                    block.update(true, false);
                }
            }
        }
    }
    
    @Override
    public void onEnable()
    {
        RegionControl.plugin = this;
        
        PluginManager pluginManager = getServer().getPluginManager();
        if(!DependencyManager.areDependenciesAvailable())
        {
            setEnabled(false);
        }
        
        Config.reloadFactionConfig();
        Config.reloadRegionConfigs();
        Data.init();
        
        ServerLogic.init();
        
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new ServerListener(), this);
        pluginManager.registerEvents(new RegionListener(), this);
        
        if(DependencyManager.isSpoutCraftAvailable())
        {
            SpoutClientLogic.init();
            pluginManager.registerEvents(new SpoutPlayerListener(), this);
        }
        
        if(DependencyManager.isDynmapAvailable())
        {
            DynmapImpl.init();
            pluginManager.registerEvents(new DynmapListener(), this);
        }
        
        else
        {
            // Server may have been reloaded, so setup all current online players.
            for(Player player : getServer().getOnlinePlayers())
            {
                Faction faction = PlayerAPI.getFactionFromGroup(DependencyManager.getPermission().getPrimaryGroup(player));
                CapturableRegion currentRegion = faction.getFactionSpawnRegion(player.getWorld());
                
                RCPlayer rcPlayer = new RCPlayer(player, faction, currentRegion);
                
                ServerLogic.players.put(player.getName(), rcPlayer);
                currentRegion.getPlayers().add(rcPlayer);
            }
        }
        
        // Register Command Handler
        commandHandler = new CommandHandler();
        
        pluginLoaded = true;
    }
    
    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }
}