package com.featherminecraft.RegionControl.utils;

import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import com.featherminecraft.RegionControl.Config;
import com.featherminecraft.RegionControl.RegionControl;

public class Utils
{
    public static WorldGuardPlugin getWorldGuard()
    {
        Plugin plugin = RegionControl.plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        if(WorldGuardAvailable())
        {
            return (WorldGuardPlugin) plugin;
        }
        else
        {
            return null;
        }
    }
    
    public static boolean ProtocolLibAvailable()
    {
        Plugin protocolLib = RegionControl.plugin.getServer().getPluginManager().getPlugin("ProtocolLib");
        
        if(protocolLib == null)
        {
            return false;
        }
        else if(!protocolLib.isEnabled())
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    public static boolean SpoutAvailable()
    {
        Plugin spoutplugin = RegionControl.plugin.getServer().getPluginManager().getPlugin("Spout");
        if(!new Config().getMainConfig().getBoolean("spout.enabled"))
        {
            return false;
        }
        else if(spoutplugin == null)
        {
            return false;
        }
        else if(!spoutplugin.isEnabled())
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    public static boolean VaultAvailable()
    {
        Plugin vault = RegionControl.plugin.getServer().getPluginManager().getPlugin("Vault");
        
        if(vault == null)
        {
            return false;
        }
        else if(!vault.isEnabled())
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    public static boolean WorldGuardAvailable()
    {
        Plugin worldguard = RegionControl.plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        
        if(worldguard == null)
        {
            return false;
        }
        else if(!worldguard.isEnabled())
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
