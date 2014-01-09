package com.featherminecraft.RegionControl;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.dynmap.DynmapImpl;
import com.featherminecraft.RegionControl.listeners.DynmapListener;
import com.featherminecraft.RegionControl.listeners.PlayerListener;
import com.featherminecraft.RegionControl.listeners.ServerListener;
import com.featherminecraft.RegionControl.listeners.SpoutPlayerListener;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;
import com.featherminecraft.RegionControl.utils.PlayerUtils;

public final class RegionControl extends JavaPlugin
{
    public static Config config = new Config();
    public static RegionControl plugin;
    public static boolean isfirstrun;
    private static boolean pluginLoaded = false;
    
    @Override
    public void onDisable()
    {
        if(pluginLoaded)
        {
            Config.saveAll(true);
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
        
        ServerLogic.init();
        
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new ServerListener(), this);
        
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
                Faction faction = PlayerUtils.getPlayerFaction(player);
                CapturableRegion currentRegion = faction.getFactionSpawnRegion(player.getWorld());
                
                RCPlayer rcPlayer = new RCPlayer(player, faction, currentRegion);
                
                ServerLogic.players.put(player.getName(), rcPlayer);
                currentRegion.getPlayers().add(rcPlayer);
            }
        }
        
        // Register Command Handler (NYI - TODO)
        // getCommand("regioncontrol").setExecutor(new CommandHandler(isfirstrun));
        
        pluginLoaded = true;
    }
}