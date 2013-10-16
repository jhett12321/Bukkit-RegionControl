package com.featherminecraft.RegionControl.spout;

import java.util.List;

import org.getspout.spoutapi.gui.AbstractListModel;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class RespawnListModel extends AbstractListModel
{
    private SpoutPlayer splayer;
    private List<ListWidgetItem> spawnPoints;
    private RCPlayer rcplayer;

    public RespawnListModel(RCPlayer rcplayer, List<ListWidgetItem> spawnPoints)
    {
        this.rcplayer = rcplayer;
        this.splayer = (SpoutPlayer) rcplayer.getBukkitPlayer();
        this.spawnPoints = spawnPoints;
    }
    
    @Override
    public ListWidgetItem getItem(int i) {
        return (ListWidgetItem) this.spawnPoints.get(i);
    }

    @Override
    public int getSize() {
        return this.spawnPoints.size();
    }

    @Override
    public void onSelected(int arg0, boolean doubleClicked) {
        if (!doubleClicked) return;
        
        ListWidgetItem item = getItem(arg0);
        String displayName = item.getText();
        
        CapturableRegion regionSelected = null;
        for(CapturableRegion region :ServerLogic.capturableRegions.values())
        {
            if(region.getDisplayName().equalsIgnoreCase(displayName))
            {
                regionSelected = region;
                break;
            }
        }
        
        rcplayer.setRespawnLocation(regionSelected.getSpawnPoint().getLocation());
        this.splayer.getMainScreen().closePopup();
    }

}
