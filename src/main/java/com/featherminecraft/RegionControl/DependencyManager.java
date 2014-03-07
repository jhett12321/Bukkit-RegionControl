package com.featherminecraft.RegionControl;

import org.bukkit.plugin.Plugin;

import org.dynmap.DynmapAPI;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.milkbowl.vault.permission.Permission;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import com.featherminecraft.RegionControl.data.Config;

import com.herocraftonline.heroes.Heroes;

public final class DependencyManager
{
    protected static boolean areDependenciesAvailable()
    {
        return(isPluginAvailable(RegionControl.plugin.getServer().getPluginManager().getPlugin("Vault")) && isPluginAvailable(RegionControl.plugin.getServer().getPluginManager().getPlugin("WorldGuard")) && isPluginAvailable(RegionControl.plugin.getServer().getPluginManager().getPlugin("ProtocolLib")));
    }
    
    public static DynmapAPI getDynmapAPI()
    {
        if(isDynmapAvailable())
        {
            return (DynmapAPI) RegionControl.plugin.getServer().getPluginManager().getPlugin("dynmap");
        }
        
        return null;
    }
    
    public static Heroes getHeroes()
    {
        if(isHeroesAvailable())
        {
            return (Heroes) RegionControl.plugin.getServer().getPluginManager().getPlugin("Heroes");
        }
        
        return null;
    }
    
    public static Permission getPermission()
    {
        return RegionControl.plugin.getServer().getServicesManager().getRegistration(Permission.class).getProvider();
    }
    
    public static ProtocolManager getProtocolManager()
    {
        return ProtocolLibrary.getProtocolManager();
    }
    
    public static WorldGuardPlugin getWorldGuard()
    {
        return (WorldGuardPlugin) RegionControl.plugin.getServer().getPluginManager().getPlugin("WorldGuard");
    }
    
    public static boolean isDynmapAvailable()
    {
        return isOptionalPluginAvailable(RegionControl.plugin.getServer().getPluginManager().getPlugin("dynmap"), "hooks.dynmap.enabled");
    }
    
    public static boolean isHeroesAvailable()
    {
        return isOptionalPluginAvailable(RegionControl.plugin.getServer().getPluginManager().getPlugin("Heroes"), "hooks.heroes.enabled");
    }
    
    public static boolean isSpoutCraftAvailable()
    {
        return isOptionalPluginAvailable(RegionControl.plugin.getServer().getPluginManager().getPlugin("Spout"), "hooks.spout.enabled");
    }
    
    private static boolean isOptionalPluginAvailable(Plugin plugin, String mainConfigPath)
    {
        if(plugin == null || !plugin.isEnabled() || !Config.getMainConfig().getBoolean(mainConfigPath))
        {
            return false;
        }
        
        return true;
    }
    
    private static boolean isPluginAvailable(Plugin plugin)
    {
        if(plugin == null || !plugin.isEnabled())
        {
            return false;
        }
        
        return true;
    }
}