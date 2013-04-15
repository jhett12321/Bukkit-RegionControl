package com.featherminecraft.regioncontrol;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Config {
    static Map<String, ProtectedRegion> regions;

    public static Map<String, ProtectedRegion> getRegions()
    {
        Utils utils = new Utils();
        List<String> worlds = utils.getConfigSectionValues("worlds");
        List<String> regionnames;
        for(String world : worlds)
        {
            regionnames = utils.getConfigValues(world + ".controllableregions");
            for(String region : regionnames)
            {
                regions.put(region, Utils.getWorldGuard().getRegionManager(Bukkit.getWorld(world)).getRegion(region));
            }
        }
        return regions;
    }
}
