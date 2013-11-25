package com.featherminecraft.RegionControl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.World;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class Faction {

    private String name;
    private Map<World, CapturableRegion> spawnRegion = new HashMap<World, CapturableRegion>();
    private String permissionGroup;
    private Color factionColor;
    private String factionIconUrl;

    public Faction(String name, String permissiongroup, Color factioncolor) {
        this.name = name;
        this.permissionGroup = permissiongroup;
        this.factionColor = factioncolor;
    }

    public void addFactionSpawnRegion(CapturableRegion spawnRegion) {
        this.spawnRegion.put(spawnRegion.getWorld(), spawnRegion);
    }

    public Color getFactionColor() {
        return this.factionColor;
    }

    public String getFactionIconUrl() {
        return this.factionIconUrl;
    }

    public CapturableRegion getFactionSpawnRegion(World world) {
        return this.spawnRegion.get(world);
    }

    public String getName() {
        return this.name;
    }

    public String getPermissionGroup() {
        return this.permissionGroup;
    }

    public void setFactionIconUrl(String factionIconUrl) {
        this.factionIconUrl = factionIconUrl;
    }
}
