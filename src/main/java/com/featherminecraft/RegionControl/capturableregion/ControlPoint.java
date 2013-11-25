package com.featherminecraft.RegionControl.capturableregion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.events.ControlPointCaptureEvent;
import com.featherminecraft.RegionControl.events.ControlPointDefendEvent;
import com.featherminecraft.RegionControl.events.ControlPointNeutraliseEvent;

public class ControlPoint {

    // Region Variable
    private CapturableRegion region;

    // Control Point Info
    private String identifier;
    private Location location;
    private Double captureRadius;

    // Capture Influence
    private Map<Faction, Float> influenceMap = new HashMap<Faction, Float>();
    private Faction influenceOwner;
    private Faction majorityPopulation;
    private Float baseInfluence;
    private Float captureRate;

    // Public Variables
    private Faction owner;
    private boolean capturing;

    public ControlPoint(String identifier, Faction owner, Location location,
            Double captureRadius, Float baseInfluence, Float influence,
            Faction influenceOwner)

    {
        this.identifier = identifier;
        this.owner = owner;
        this.location = location;
        this.captureRadius = captureRadius;
        this.baseInfluence = baseInfluence;

        this.influenceOwner = influenceOwner;
        for (Entry<String, Faction> faction : ServerLogic.factions.entrySet()) {
            if (faction.getValue() == influenceOwner && influenceOwner != null) {
                this.influenceMap.put(influenceOwner, baseInfluence);
            }

            else if (faction.getValue() != null) {
                this.influenceMap.put(faction.getValue(), 0F);
            }
        }

        this.location.getBlock().setTypeId(85, false);
        this.location.setY(location.getY() + 1);
        this.location.getBlock().setTypeId(85, false);
        this.location.setY(location.getY() + 1);

        if (this.influenceMap.get(influenceOwner) == baseInfluence) {
            this.location.getBlock().setTypeIdAndData(Material.WOOL.getId(),
                    DyeColor.getByColor(owner.getFactionColor()).getWoolData(),
                    false);
            this.capturing = false;
        } else {
            this.location.getBlock().setTypeIdAndData(Material.WOOL.getId(),
                    DyeColor.getByColor(Color.WHITE).getWoolData(), false);
            this.capturing = true;
        }
    }

    private Faction CalculateInfluenceOwner() {
        /*
         * Influence Owner Calculations
         */

        Faction influenceOwner = null;
        for (Entry<Faction, Float> influence : this.influenceMap.entrySet()) {
            if (influence.getValue() > 0F) {
                influenceOwner = influence.getKey();
                break;
            }
        }

        return influenceOwner;
    }

    private Faction CalculateMajorityPopulation() {
        /*
         * Majority Population on Point Calculations
         */

        List<RCPlayer> players = this.region.getPlayers();
        Map<Faction, Integer> factionInfluence = new HashMap<Faction, Integer>();

        for (Entry<String, Faction> faction : ServerLogic.factions.entrySet()) {
            factionInfluence.put(faction.getValue(), 0);
        }

        for (RCPlayer player : players) {
            if (player.getBukkitPlayer().getLocation()
                    .distanceSquared(this.location) <= this.captureRadius
                    * this.captureRadius) {
                Faction playersFaction = player.getFaction();
                factionInfluence.put(playersFaction,
                        factionInfluence.get(playersFaction) + 1);
            }
        }

        int majorityPopulationAmount = 0;
        Faction majorityPopulation = null;
        for (Entry<Faction, Integer> faction : factionInfluence.entrySet()) {
            if (faction.getValue() > majorityPopulationAmount) {
                majorityPopulation = faction.getKey();
                majorityPopulationAmount = faction.getValue().intValue();
            }

            else if (faction.getValue() == majorityPopulationAmount) {
                majorityPopulation = null;
            }
        }

        if (majorityPopulation != null) {
            int populationAgainstAmount = 0;
            for (Entry<Faction, Integer> faction : factionInfluence.entrySet()) {
                if (faction.getKey() != majorityPopulation) {
                    populationAgainstAmount += faction.getValue();
                }
            }

            if (majorityPopulationAmount - populationAgainstAmount > 0) {
                this.captureRate = ((Integer) (majorityPopulationAmount - populationAgainstAmount))
                        .floatValue();
            }
        }

        return majorityPopulation;
    }

    public Float getBaseInfluence() {
        return this.baseInfluence;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Map<Faction, Float> getInfluenceMap() {
        return this.influenceMap;
    }

    public Faction getInfluenceOwner() {
        return this.influenceOwner;
    }

    public Faction getMajorityPopulation() {
        return this.majorityPopulation;
    }

    public Faction getOwner() {
        return this.owner;
    }

    public CapturableRegion getRegion() {
        return this.region;
    }

    public boolean isCapturing() {
        return this.capturing;
    }

    public void Runnable() {
        Faction majorityPopulation = CalculateMajorityPopulation();
        Faction influenceOwner = CalculateInfluenceOwner();

        if (influenceOwner == null) {
            if (majorityPopulation != null && this.captureRate != null
                    && this.captureRate != 0F) {
                this.influenceMap.put(majorityPopulation, this.captureRate);
            }
        }

        else if (influenceOwner != majorityPopulation) {
            if (majorityPopulation != null && this.captureRate != null
                    && this.captureRate != 0F) {
                if (this.influenceMap.get(influenceOwner) - this.captureRate <= 0F) {
                    this.influenceMap.put(influenceOwner, 0F);
                }

                else {
                    this.influenceMap.put(influenceOwner,
                            this.influenceMap.get(influenceOwner)
                                    - this.captureRate);
                }
            }
        }

        else if (influenceOwner == majorityPopulation) {
            if (majorityPopulation != null
                    && this.captureRate != null
                    && this.captureRate != 0F
                    && this.influenceMap.get(influenceOwner) != this.baseInfluence) {
                if (this.influenceMap.get(influenceOwner) + this.captureRate >= this.baseInfluence) {
                    this.influenceMap.put(majorityPopulation,
                            this.baseInfluence);
                    this.location.getBlock().setTypeIdAndData(
                            Material.WOOL.getId(),
                            DyeColor.getByColor(
                                    influenceOwner.getFactionColor())
                                    .getWoolData(), false);

                    if (this.owner != influenceOwner) {
                        this.owner = influenceOwner;
                        this.capturing = false;
                        Bukkit.getServer()
                                .getPluginManager()
                                .callEvent(
                                        new ControlPointCaptureEvent(
                                                this.region, influenceOwner,
                                                this));
                    } else {
                        this.capturing = false;
                        Bukkit.getServer()
                                .getPluginManager()
                                .callEvent(
                                        new ControlPointDefendEvent(
                                                this.region, influenceOwner,
                                                this));
                    }
                }

                else {
                    this.influenceMap.put(influenceOwner,
                            this.influenceMap.get(influenceOwner)
                                    + this.captureRate);
                }
            }
        }

        if (this.influenceMap.get(influenceOwner) != this.baseInfluence
                && !this.capturing) {
            this.capturing = true;
            this.location.getBlock().setTypeIdAndData(Material.WOOL.getId(),
                    DyeColor.getByColor(Color.WHITE).getWoolData(), false);
            Bukkit.getServer()
                    .getPluginManager()
                    .callEvent(
                            new ControlPointNeutraliseEvent(this.region,
                                    influenceOwner, this));
        }
    }

    public void setBaseInfluence(Float baseInfluence) {
        this.baseInfluence = baseInfluence;
    }

    public void setCapturing(boolean capturing) {
        this.capturing = capturing;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setInfluenceMap(Map<Faction, Float> influenceMap) {
        this.influenceMap = influenceMap;
    }

    public void setInfluenceOwner(Faction influenceOwner) {
        this.influenceOwner = influenceOwner;
    }

    public void setMajorityPopulation(Faction majorityPopulation) {
        this.majorityPopulation = majorityPopulation;
    }

    public void setOwner(Faction owner) {
        this.owner = owner;
        this.capturing = false;
        for (Entry<Faction, Float> faction : this.influenceMap.entrySet()) {
            if (faction.getKey() == owner) {
                this.influenceMap.put(owner, this.baseInfluence);
            } else {
                this.influenceMap.put(faction.getKey(), 0F);
            }
        }
        this.location.getBlock().setTypeIdAndData(Material.WOOL.getId(),
                DyeColor.getByColor(owner.getFactionColor()).getWoolData(),
                false);
        Bukkit.getServer()
                .getPluginManager()
                .callEvent(
                        new ControlPointCaptureEvent(this.region,
                                this.influenceOwner, this));
    }

    public void setRegion(CapturableRegion region) {
        this.region = region;
    }
}
