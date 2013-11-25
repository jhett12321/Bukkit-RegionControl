package com.featherminecraft.RegionControl.capturableregion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.events.InfluenceRateChangeEvent;
import com.featherminecraft.RegionControl.events.RegionCaptureEvent;
import com.featherminecraft.RegionControl.events.RegionCaptureStatusChangeEvent;
import com.featherminecraft.RegionControl.events.RegionDefendEvent;

public class InfluenceManager {

    // Region Variable
    private CapturableRegion region;

    // Private Vars
    // private HashMap<Faction, Float> percentageOwned;

    public InfluenceManager(CapturableRegion cregion) {
        this.region = cregion;
        this.region.setInfluenceManager(this);
    }

    public Faction CalculateInfluenceOwner() {
        /*
         * Influence Owner Calculations
         */

        Map<Faction, Float> influenceMap = this.region.getInfluenceMap();
        Faction influenceOwner = null;
        for (Entry<Faction, Float> influence : influenceMap.entrySet()) {
            if (influence.getValue() > 0F) {
                influenceOwner = influence.getKey();
                break;
            }
        }

        // if(region.getInfluenceOwner() != influenceOwner && influenceOwner !=
        // null)
        // {
        // Bukkit.getServer().getPluginManager().callEvent(new
        // InfluenceRateChangeEvent(region, region.getInfluenceRate(),
        // region.getInfluenceRate()));
        // }
        //
        // if(region.getInfluenceOwner() != influenceOwner)
        // {
        // region.setInfluenceOwner(influenceOwner); //Migrate to Event
        // }

        return influenceOwner;
    }

    public Float CalculateInfluenceRate() {
        /*
         * Influence Rate Calculations, Take away influence for this loop. Rate
         * 1: 1% Diff. + Rate 2: 33% Diff. + Rate 3: 66% Diff. + Rate 4: 100%
         * Diff.
         */
        List<ControlPoint> controlPoints = this.region.getControlPoints();
        float effectiveControlPointCount = 0F;
        for (ControlPoint controlPoint : controlPoints) {
            if (controlPoint.getOwner() == this.region.getMajorityController()
                    && !controlPoint.isCapturing()) {
                effectiveControlPointCount += 1F;
            } else if (controlPoint.getOwner() != this.region
                    .getMajorityController() && !controlPoint.isCapturing()) {
                effectiveControlPointCount -= 1F;
            }
        }

        float percentageOwned = effectiveControlPointCount
                / ((Integer) controlPoints.size()).floatValue();

        Float influenceRate = 0F;
        if (this.region.getMajorityController() != null) {
            if (percentageOwned >= 1F) {
                influenceRate = 4F;
            }

            else if (percentageOwned > 0.66) {
                influenceRate = 3F;
            }

            else if (percentageOwned > 0.33) {
                influenceRate = 2F;
            }

            else if (percentageOwned > 0.01F) {
                influenceRate = 1F;
            }
        }
        return influenceRate;
    }

    public Faction CalculateMajorityController() {
        /*
         * Majority Controller Calculations
         */
        List<ControlPoint> controlPoints = this.region.getControlPoints();
        Map<Faction, Float> ownedControlPoints = new HashMap<Faction, Float>();
        for (Entry<String, Faction> faction : ServerLogic.factions.entrySet()) {
            if (faction.getValue() != null) {
                ownedControlPoints.put(faction.getValue(), 0F);
            }
        }

        for (ControlPoint controlPoint : controlPoints) {
            if (controlPoint.getOwner() != null && !controlPoint.isCapturing()) {
                ownedControlPoints.put(controlPoint.getOwner(),
                        ownedControlPoints.get(controlPoint.getOwner()) + 1F);
            }
        }

        Faction majorityController = null;
        Float majorityAmount = 0F;
        for (Entry<Faction, Float> faction : ownedControlPoints.entrySet()) {
            if (faction.getValue() > majorityAmount) {
                majorityController = faction.getKey();
                majorityAmount = faction.getValue();
            }

            else if (faction.getValue() == majorityAmount) {
                majorityController = null;
            }
        }

        return majorityController;
    }

    public void Runnable() {
        Faction majorityController = CalculateMajorityController();
        this.region.setMajorityController(majorityController);

        Faction influenceOwner = CalculateInfluenceOwner();
        this.region.setInfluenceOwner(influenceOwner);

        Float influenceRate = CalculateInfluenceRate();
        Float oldInfluenceRate = this.region.getInfluenceRate();
        this.region.setInfluenceRate(influenceRate);

        if (influenceOwner == null) {
            if (majorityController != null && influenceRate != null
                    && influenceRate != 0F) {
                this.region.getInfluenceMap().put(majorityController,
                        influenceRate);
                Bukkit.getServer()
                        .getPluginManager()
                        .callEvent(
                                new InfluenceRateChangeEvent(this.region,
                                        oldInfluenceRate, influenceRate));
            }
        }

        else if (influenceOwner != majorityController) {
            if (majorityController != null && influenceRate != null
                    && influenceRate != 0F) {
                if (this.region.getInfluenceMap().get(influenceOwner)
                        - influenceRate <= 0F) {
                    this.region.getInfluenceMap().put(influenceOwner, 0F);
                }

                else {
                    this.region.getInfluenceMap().put(
                            influenceOwner,
                            this.region.getInfluenceMap().get(influenceOwner)
                                    - influenceRate);
                }
            }
        }

        else if (influenceOwner == majorityController) {
            if (this.region.getInfluenceMap().get(influenceOwner) != this.region
                    .getBaseInfluence()
                    && majorityController != null
                    && influenceRate != null && influenceRate != 0F) {
                if (this.region.getInfluenceMap().get(influenceOwner)
                        + influenceRate >= this.region.getBaseInfluence()) {
                    this.region.getInfluenceMap().put(influenceOwner,
                            this.region.getBaseInfluence());
                    if (this.region.getOwner() == influenceOwner) {
                        this.region.setBeingCaptured(false);
                        Bukkit.getServer()
                                .getPluginManager()
                                .callEvent(
                                        new RegionCaptureStatusChangeEvent(
                                                this.region, false));
                        Bukkit.getServer()
                                .getPluginManager()
                                .callEvent(
                                        new RegionDefendEvent(this.region,
                                                influenceOwner));
                    }

                    else if (this.region.getOwner() != influenceOwner) {
                        this.region.setOwner(influenceOwner);
                        this.region.setInfluenceRate(4F);
                        for (ControlPoint controlPoint : this.region
                                .getControlPoints()) {
                            for (Entry<Faction, Float> influence : controlPoint
                                    .getInfluenceMap().entrySet()) {
                                controlPoint.getInfluenceMap().put(
                                        influence.getKey(), 0F);
                            }
                            controlPoint.getInfluenceMap().put(influenceOwner,
                                    controlPoint.getBaseInfluence());
                            controlPoint.setInfluenceOwner(influenceOwner);
                            controlPoint.setOwner(influenceOwner);
                        }
                        this.region.setBeingCaptured(false);
                        Bukkit.getServer()
                                .getPluginManager()
                                .callEvent(
                                        new RegionCaptureStatusChangeEvent(
                                                this.region, false));
                        Bukkit.getServer()
                                .getPluginManager()
                                .callEvent(
                                        new RegionCaptureEvent(this.region,
                                                this.region.getOwner(),
                                                influenceOwner));
                    }
                }

                else {
                    this.region.getInfluenceMap().put(
                            influenceOwner,
                            this.region.getInfluenceMap().get(influenceOwner)
                                    + influenceRate);
                }
            }
        }

        // Capture Status Change events run here.
        if (this.region.getInfluenceMap().get(influenceOwner) != this.region
                .getBaseInfluence()
                && !this.region.isBeingCaptured()
                || this.region.getInfluenceRate() < 4F
                && !this.region.isBeingCaptured()) {
            this.region.setBeingCaptured(true);
            Bukkit.getServer()
                    .getPluginManager()
                    .callEvent(
                            new RegionCaptureStatusChangeEvent(this.region,
                                    true));
        }

        if (!this.region.getInfluenceRate().equals(oldInfluenceRate)) {
            Bukkit.getServer()
                    .getPluginManager()
                    .callEvent(
                            new InfluenceRateChangeEvent(this.region,
                                    oldInfluenceRate, influenceRate));
        }
    }
}
