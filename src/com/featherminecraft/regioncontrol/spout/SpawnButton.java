package com.featherminecraft.regioncontrol.spout;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.getspout.spoutapi.gui.Button;

import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.ServerLogic;
import com.featherminecraft.regioncontrol.capturableregion.CapturableRegion;

public class SpawnButton {
    
    private Button button;
    private CapturableRegion region;

    public SpawnButton(Button button, CapturableRegion region)
    {
        this.button = button;
        this.region = region;
        Map<Faction,Boolean> isValidSpawn = new HashMap<Faction,Boolean>();
        for(Entry<String, Faction> faction : ServerLogic.factions.entrySet())
        {
            isValidSpawn.put(faction.getValue(), false);
        }
        
        isValidSpawn.put(region.getOwner(), true);
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public CapturableRegion getRegion() {
        return region;
    }

    public void setRegion(CapturableRegion region) {
        this.region = region;
    }
}
