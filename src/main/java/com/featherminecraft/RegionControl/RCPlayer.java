package com.featherminecraft.RegionControl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.data.Data;
import com.featherminecraft.RegionControl.data.Table;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;

public class RCPlayer
{
    private String playerName;
    private CapturableRegion currentRegion;
    private Faction faction;
    private UI ui;
    private Boolean hasSpout = false;
    private Location respawnLocation;
    private Boolean visible = true;
    
    // Player Classes/Runnables
    private SpoutClientLogic spoutClientLogic;
    private Map<String, BukkitTask> clientRunnables = new HashMap<String, BukkitTask>();
    
    private Map<RCPlayer, Double> damageSources = new LinkedHashMap<RCPlayer, Double>();
    
    // Player Session Stats
    private int sessionKills = 0;
    private int sessionDeaths = 0;
    private int sessionAssists = 0;
    private int sessionExperience = 0;
    private int sessionRegionCaptures = 0;
    private int sessionRegionDefends = 0;
    private int sessionBlocksDestroyed = 0;
    private int sessionBlocksPlaced = 0;
    
    // Player All-Time Stats
    private Integer kills = null;
    private Integer deaths = null;
    private Integer assists = null;
    private Integer experience = null;
    private Integer regionCaptures = null;
    private Integer regionDefends = null;
    private Integer blocksDestroyed = null;
    private Integer blocksPlaced = null;
    
    public RCPlayer(Player player, Faction faction, CapturableRegion currentRegion)
    {
        playerName = player.getName();
        this.faction = faction;
        this.currentRegion = currentRegion;
        this.ui = new UI(this);
        
        Data.getPlayerStats(this);
    }
    
    public UI getUi()
    {
        return ui;
    }

    public Player getBukkitPlayer()
    {
        return Bukkit.getPlayer(playerName);
    }
    
    public BukkitTask getClientRunnable(String id)
    {
        return clientRunnables.get(id);
    }
    
    public Map<String, BukkitTask> getClientRunnables()
    {
        return clientRunnables;
    }
    
    public CapturableRegion getCurrentRegion()
    {
        return currentRegion;
    }
    
    public Faction getFaction()
    {
        return faction;
    }
    
    public void hidePlayer()
    {
        this.visible = false;
        ServerLogic.updateVisibility(this);
    }
    
    public void showPlayer()
    {
        this.visible = true;
        ServerLogic.updateVisibility(this);
    }
    
    public Boolean isVisible()
    {
        return visible;
    }
    
    public Location getRespawnLocation()
    {
        return respawnLocation;
    }
    
    public SpoutClientLogic getSpoutClientLogic()
    {
        return spoutClientLogic;
    }
    
    public Boolean hasSpout()
    {
        return hasSpout;
    }
    
    public void setCurrentRegion(CapturableRegion currentRegion)
    {
        this.currentRegion = currentRegion;
    }
    
    public void setFaction(Faction faction)
    {
        this.faction = faction;
    }
    
    public void setHasSpout(Boolean hasSpout)
    {
        this.hasSpout = hasSpout;
    }
    
    public void setRespawnLocation(Location respawnLocation)
    {
        this.respawnLocation = respawnLocation;
    }
    
    public void setSpoutClientLogic(SpoutClientLogic spoutClientLogic)
    {
        this.spoutClientLogic = spoutClientLogic;
    }
    
    public int getSessionKills()
    {
        return sessionKills;
    }
    
    public void setSessionKills(int sessionKills)
    {
        this.sessionKills = sessionKills;
    }
    
    public int getSessionDeaths()
    {
        return sessionDeaths;
    }
    
    public void setSessionDeaths(int sessionDeaths)
    {
        this.sessionDeaths = sessionDeaths;
    }
    
    public Float getSessionKDR()
    {
        Float fkills = (float) sessionKills;
        Float fdeaths = (float) sessionDeaths;
        if(fdeaths == 0F)
        {
            fdeaths = 1F;
        }
        return fkills / fdeaths;
    }
    
    public int getSessionAssists()
    {
        return sessionAssists;
    }
    
    public void setSessionAssists(int sessionAssists)
    {
        this.sessionAssists = sessionAssists;
    }
    
    public int getSessionExperience()
    {
        return sessionExperience;
    }
    
    public void setSessionExperience(int sessionExperience)
    {
        this.sessionExperience = sessionExperience;
    }
    
    public int getSessionRegionCaptures()
    {
        return sessionRegionCaptures;
    }
    
    public void setSessionRegionCaptures(int sessionRegionCaptures)
    {
        this.sessionRegionCaptures = sessionRegionCaptures;
    }
    
    public int getSessionRegionDefends()
    {
        return sessionRegionDefends;
    }
    
    public void setSessionRegionDefends(int sessionRegionDefends)
    {
        this.sessionRegionDefends = sessionRegionDefends;
    }
    
    public Integer getKills()
    {
        return kills;
    }
    
    public void setKills(Integer kills)
    {
        this.kills = kills;
    }
    
    public Integer getDeaths()
    {
        return deaths;
    }
    
    public void setDeaths(Integer deaths)
    {
        this.deaths = deaths;
    }
    
    public Float getKDR()
    {
        if(kills != null && deaths != null)
        {
            Float fkills = kills.floatValue();
            Float fdeaths = deaths.floatValue();
            if(fdeaths == 0F)
            {
                fdeaths = 1F;
            }
            return fkills / fdeaths;
        }
        else
        {
            return null;
        }
    }
    
    public Integer getAssists()
    {
        return assists;
    }
    
    public void setAssists(Integer assists)
    {
        this.assists = assists;
    }
    
    public Integer getExperience()
    {
        return experience;
    }
    
    public void setExperience(Integer experience)
    {
        this.experience = experience;
    }
    
    public Integer getRegionCaptures()
    {
        return regionCaptures;
    }
    
    public void setRegionCaptures(Integer regionCaptures)
    {
        this.regionCaptures = regionCaptures;
    }
    
    public Integer getRegionDefends()
    {
        return regionDefends;
    }
    
    public void setRegionDefends(Integer regionDefends)
    {
        this.regionDefends = regionDefends;
    }
    
    public int getSessionDestroyedBlocks()
    {
        return sessionBlocksDestroyed;
    }
    
    public void setSessionDestroyedBlocks(int sessionDestroyedBlocks)
    {
        this.sessionBlocksDestroyed = sessionDestroyedBlocks;
    }
    
    public int getSessionPlacedBlocks()
    {
        return sessionBlocksPlaced;
    }
    
    public void setSessionPlacedBlocks(int sessionPlacedBlocks)
    {
        this.sessionBlocksPlaced = sessionPlacedBlocks;
    }
    
    public Integer getBlocksDestroyed()
    {
        return blocksDestroyed;
    }
    
    public void setBlocksDestroyed(Integer destroyedBlocks)
    {
        this.blocksDestroyed = destroyedBlocks;
    }
    
    public Integer getBlocksPlaced()
    {
        return blocksPlaced;
    }
    
    public void setBlocksPlaced(Integer placedBlocks)
    {
        this.blocksPlaced = placedBlocks;
    }
    
    public void addKill()
    {
        this.sessionKills ++ ;
        this.kills ++ ;
        Data.addItemToQueue(Table.regioncontrol_characters, getBukkitPlayer().getName().toLowerCase(), "kills", getKills());
    }
    
    public void addDeath()
    {
        this.sessionDeaths ++ ;
        this.deaths ++ ;
        Data.addItemToQueue(Table.regioncontrol_characters, getBukkitPlayer().getName().toLowerCase(), "deaths", getDeaths());
    }
    
    public void addAssist()
    {
        this.sessionAssists ++ ;
        this.assists ++ ;
        Data.addItemToQueue(Table.regioncontrol_characters, getBukkitPlayer().getName().toLowerCase(), "assists", getAssists());
    }
    
    public void addExperience(int experience)
    {
        this.sessionExperience += experience;
        this.experience ++ ;
    }
    
    public void addRegionCapture()
    {
        this.sessionRegionCaptures ++ ;
        this.regionCaptures ++ ;
        Data.addItemToQueue(Table.regioncontrol_characters, getBukkitPlayer().getName().toLowerCase(), "region_captures", getRegionCaptures());
    }
    
    public void addRegionDefend()
    {
        this.sessionRegionDefends ++ ;
        this.regionDefends ++ ;
        Data.addItemToQueue(Table.regioncontrol_characters, getBukkitPlayer().getName().toLowerCase(), "region_defends", getRegionDefends());
    }
    
    public void addDestroyedBlock()
    {
        this.sessionBlocksDestroyed ++ ;
        this.blocksDestroyed ++ ;
        Data.addItemToQueue(Table.regioncontrol_characters, getBukkitPlayer().getName().toLowerCase(), "blocks_destroyed", getBlocksDestroyed());
    }
    
    public void addPlacedBlock()
    {
        this.sessionBlocksPlaced ++ ;
        this.blocksPlaced ++ ;
        Data.addItemToQueue(Table.regioncontrol_characters, getBukkitPlayer().getName().toLowerCase(), "blocks_placed", getBlocksPlaced());
    }
    
    public Map<RCPlayer, Double> getDamageSources()
    {
        return damageSources;
    }
}