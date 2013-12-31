package com.featherminecraft.RegionControl;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.listeners.GenericListener;
import com.featherminecraft.RegionControl.listeners.PlayerListener;
import com.featherminecraft.RegionControl.listeners.SpoutPlayerListener;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;
import com.featherminecraft.RegionControl.utils.PlayerUtils;
import com.featherminecraft.RegionControl.utils.SpoutUtils;

public final class RegionControl extends JavaPlugin
{
    // Utilities Begin
    private PlayerUtils playerUtils = new PlayerUtils();
    private SpoutUtils spoutUtils = new SpoutUtils();
    // Utilities End
    
    public static RegionControl plugin;
    public static boolean isfirstrun;
    
    @Override
    public void onDisable()
    {
        new Config().saveAll();
        for( RCPlayer player : ServerLogic.players.values())
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
    
    @Override
    public void onEnable()
    {
        RegionControl.plugin = this;
        
        PluginManager pluginManager = getServer().getPluginManager();
        if(!DependencyManager.init())
        {
            setEnabled(false);
        }
        
        // Server Setup
        Config config = new Config();
        config.reloadFactionConfig();
        config.reloadRegionConfigs();
        
        ServerLogic.init();
        
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new GenericListener(), this);
        
        if(DependencyManager.isSpoutCraftAvailable())
        {
            SpoutClientLogic.init();
            pluginManager.registerEvents(new SpoutPlayerListener(), this);
        }
        
        else
        {
            for(Player player : getServer().getOnlinePlayers())
            {
                Faction faction = playerUtils.getPlayerFaction(player);
                CapturableRegion currentRegion = faction.getFactionSpawnRegion(player.getWorld());
                
                RCPlayer rcPlayer = new RCPlayer(player, faction, currentRegion);
                
                ServerLogic.players.put(player.getName(), rcPlayer);
                currentRegion.getPlayers().add(rcPlayer);
            }
        }
        
        // Register Command Handler (NYI - TODO)
        // getCommand("regioncontrol").setExecutor(new CommandHandler(isfirstrun));
        
        // Server may have been reloaded, so setup all current online players.
    }

    public PlayerUtils getPlayerUtils()
    {
        return playerUtils;
    }

    public SpoutUtils getSpoutUtils()
    {
        return spoutUtils;
    }
}