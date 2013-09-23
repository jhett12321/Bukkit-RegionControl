package com.featherminecraft.RegionControl.utils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.featherminecraft.RegionControl.Config;
import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RegionControl;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

public class ConfigUtils extends Config {
    public String getDefaultFaction() 
    {
        Set<String> factions = mainconfig.getConfigurationSection("factions").getKeys(false);
        for(String faction : factions)
        {
            if(mainconfig.getBoolean("factions." + faction + ".default"))
            {
                return faction;
            }
        }
        return null;
    }
    
    public void saveAll()
    {
        RegionControl.plugin.getLogger().info("Saving Region Data...");
        //Utilities Begin

        //Utilities End
        
        //Begin retrieving of Region Data
        Collection<CapturableRegion> regions = ServerLogic.capturableRegions.values();
        
        for(CapturableRegion region : regions)
        {
            //Not Here: DisplayName, Spawnpoint
            //Variables being set
//            Float baseInfluence = region.getBaseInfluence();
            Faction influenceOwner = region.getInfluenceOwner();
            String configInfluenceOwner = influenceOwner.getName();
            float configInfluence = region.getInfluenceMap().get(influenceOwner);
            String configOwner = region.getOwner().getName();
            
            //Key location variables
            String configWorld = region.getWorld().getName();
            String configId = region.getRegionId();
            
            //Saving of data
            //TODO Later Implementation: In-game setting of baseinfluence.
            //mainconfig.set("worlds." + configWorld + ".regions." + configId + ".baseinfluence", baseInfluence.intValue());
            
            data.set("worlds." + configWorld + ".regions." + configId + ".influenceowner", configInfluenceOwner);
            data.set("worlds." + configWorld + ".regions." + configId + ".influence", configInfluence);
            data.set("worlds." + configWorld + ".regions." + configId + ".owner", configOwner);
            
            //Control Points
            List<ControlPoint> controlPoints = region.getControlPoints();
            for(ControlPoint controlPoint : controlPoints)
            {
                Faction controlPointInfluenceOwner = controlPoint.getInfluenceOwner();
                String configControlPointInfluenceOwner = controlPointInfluenceOwner.getName();
                Float configControlPointInfluence = controlPoint.getInfluenceMap().get(controlPointInfluenceOwner);
                String configControlPointId = controlPoint.getIdentifier();
                String configControlPointOwner = controlPoint.getOwner().getName();
                
                data.set("worlds." + configWorld + ".regions." + configId + ".controlpoints." + configControlPointId + ".influenceowner", configControlPointInfluenceOwner);
                data.set("worlds." + configWorld + ".regions." + configId + ".controlpoints." + configControlPointId + ".influence", configControlPointInfluence);
                data.set("worlds." + configWorld + ".regions." + configId + ".controlpoints." + configControlPointId + ".owner", configControlPointOwner);
            }
        }
        
        if (saveDataFile() && saveMainConfig())
        {
            RegionControl.plugin.getLogger().info("Save Complete!");
        }
        else
        {
            RegionControl.plugin.getLogger().severe("Save Failed. Please check your plugin directory has write permissions.");
        }
    }
}
