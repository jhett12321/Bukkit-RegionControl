package com.featherminecraft.regioncontrol;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.featherminecraft.regioncontrol.utils.ConfigUtils;
import com.featherminecraft.regioncontrol.utils.Utils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Config {
    static Map<String, ProtectedRegion> regions;
    static List<World> worlds;

    public static Map<String, ProtectedRegion> getRegionsForWorld(World world)
    {
        ConfigUtils configutils = new ConfigUtils();
        List<String> regionnames = configutils.getConfigValues(world + ".controllableregions");
        for(String region : regionnames)
        {
            regions.put(region, Utils.getWorldGuard().getRegionManager(world).getRegion(region));
        }
        return regions;
    }
    
    public static List<World> getWorlds()
    {
        ConfigUtils configutils = new ConfigUtils();
        List<String> worldnames = configutils.getConfigSectionValues("worlds");
        worlds = null;
        for(String world : worldnames)
        {
            worlds.add(Bukkit.getWorld(world));
        }
        return worlds;
    }
}
