package com.featherminecraft.RegionControl.spout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bukkit.Location;

import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.Container;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericListView;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.WidgetAnchor;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.RegionControl;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class RespawnScreen
{
    private RespawnListModel listModel;
    private GenericListView listWidget;
    List<ListWidgetItem> respawnList = new ArrayList<ListWidgetItem>();
    private GenericPopup popup;
    private Button respawnButton;
    private SortedMap<Integer, CapturableRegion> distances;
    private Location playerLoc;
    
    public RespawnScreen(InGameHUD mainscreen, RCPlayer player)
    {
        // Container
        Container respawnContainer = (Container) new GenericContainer().setLayout(ContainerType.VERTICAL).setAnchor(WidgetAnchor.TOP_LEFT).setX(5).setY(40);
        
        // Respawn Button
        Label deploymentTitle = (Label) ((Label) new GenericLabel("Deployment").setTextColor(new Color(1F, 1F, 1F, 1F)).setMargin(0, 3)).setShadow(false).setScale(2F).setResize(true).setFixed(true);
        respawnButton = (Button) new GenericButton("Respawn: Ancestria Southern NetherGate").setWidth(200).setHeight(20).setFixed(true).setPriority(RenderPriority.Lowest);
        
        // Get Any Regions that are owned, and adjacent to the player.
        List<CapturableRegion> spawnableRegions = new ArrayList<CapturableRegion>();
        
        List<CapturableRegion> regions = player.getCurrentRegion().getAdjacentRegions();
        regions.add(player.getCurrentRegion());
        for(CapturableRegion region : regions)
        {
            if(region.getOwner() == player.getFaction())
            {
                spawnableRegions.add(region);
            }
        }
        
        if(!spawnableRegions.contains(player.getFaction().getFactionSpawnRegion(player.getBukkitPlayer().getWorld())))
        {
            spawnableRegions.add(player.getFaction().getFactionSpawnRegion(player.getBukkitPlayer().getWorld()));
        }
        
        playerLoc = player.getBukkitPlayer().getLocation();
        distances = new TreeMap<Integer, CapturableRegion>();
        
        for(CapturableRegion region : spawnableRegions)
        {
            addRegionToSpawnList(region);
        }
        
        List<ListWidgetItem> respawnList = new ArrayList<ListWidgetItem>();
        for(Entry<Integer, CapturableRegion> listEntry : distances.entrySet())
        {
            ListWidgetItem item = new ListWidgetItem(listEntry.getKey().toString() + "m", listEntry.getValue().getDisplayName());
            respawnList.add(item);
        }
        
        listModel = new RespawnListModel(this, player, respawnList);
        listWidget = new GenericListView(listModel);
        listWidget.setWidth(200).setHeight(200).setFixed(true).setPriority(RenderPriority.Lowest);
        respawnContainer.addChildren(deploymentTitle, listWidget, respawnButton);
        
        popup = new GenericPopup();
        
        popup.attachWidgets(RegionControl.plugin, respawnContainer, listWidget, deploymentTitle, respawnButton);
        
        mainscreen.attachPopupScreen(popup);
    }
    
    public void addRegionToSpawnList(CapturableRegion region)
    {
        Location spawnLoc = region.getSpawnPoint().getLocation();
        Integer distance = ((Double) playerLoc.distance(spawnLoc)).intValue();
        distances.put(distance, region);
    }
    
    public void removeRegionFromSpawnList(CapturableRegion region)
    {
        Location spawnLoc = region.getSpawnPoint().getLocation();
        Integer distance = ((Double) playerLoc.distance(spawnLoc)).intValue();
        distances.remove(distance);
    }
    
    public void updateRegions()
    {
        listWidget.clear();
        List<ListWidgetItem> respawnList = new ArrayList<ListWidgetItem>();
        for(Entry<Integer, CapturableRegion> listEntry : distances.entrySet())
        {
            ListWidgetItem item = new ListWidgetItem(listEntry.getKey().toString() + "m", listEntry.getValue().getDisplayName());
            listWidget.addItem(item);
            respawnList.add(item);
        }
        listModel.updateSpawnPoints(respawnList);
        listModel.onSelected(0, false);
    }
    
    public GenericListView getListWidget()
    {
        return listWidget;
    }
    
    public GenericPopup getPopup()
    {
        return popup;
    }
    
    public Button getRedeployButton()
    {
        return respawnButton;
    }
}