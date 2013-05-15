package com.featherminecraft.regioncontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.featherminecraft.regioncontrol.utils.ConfigUtils;
import com.featherminecraft.regioncontrol.utils.Utils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


//TODO: Incomplete.
public class Config {
    static Map<String, ProtectedRegion> regions;
    static List<World> worlds;
    
    private File data = null;
    
    public void createConfigFiles()
    {
        //W.I.P
        FileConfiguration mainconfig = RegionControl.plugin.getConfig();
        data = new File(RegionControl.plugin.getDataFolder(), "data.yml");
    }

    public Map<String, ProtectedRegion> getRegionsForWorld(World world)
    {
        List<String> regionnames = new ConfigUtils().getConfigValues(world + ".controllableregions");
        for(String region : regionnames)
        {
            regions.put(region, Utils.getWorldGuard().getRegionManager(world).getRegion(region));
        }
        return regions;
    }
    
    public List<World> getWorlds()
    {
        List<String> worldnames = new ConfigUtils().getConfigSectionValues("worlds");
        worlds = null;
        for(String world : worldnames)
        {
            worlds.add(Bukkit.getWorld(world));
        }
        return worlds;
    }

    public Map<String, Faction> getFactions() {
        List<String> factionnamelist = new ConfigUtils().getConfigValues("factionnames");
        Map<String, Faction> factions = new HashMap<String, Faction>();
        for(String factionname : factionnamelist)
        {
            Faction faction = new Faction(factionname,null/*Replace with permissions group type*/);
            factions.put(factionname, faction);
        }
        return factions;
    }

    public static Faction getDefaultFaction() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, Location> getControlPointsForRegion(CapturableRegion capturableregion) {
        // TODO Auto-generated method stub
        return null;
    }

    public Location getSpawnPointForRegion(CapturableRegion capturableregion) {
        // TODO Auto-generated method stub
        return null;
    }
}
