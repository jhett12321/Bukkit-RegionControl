package com.featherminecraft.RegionControl.spout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bukkit.Location;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
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

public class RespawnScreen {
    private RespawnListModel ListModel;
    private GenericListView listWidget;
    List<ListWidgetItem> respawnList = new ArrayList<ListWidgetItem>();
    private GenericPopup popup;
    private Button redeployButton;

    public RespawnScreen(InGameHUD mainscreen, RCPlayer player) {
        // General Elements
        Label redeployTitle = (Label) ((Label) new GenericLabel("Deployment")
                .setTextColor(new Color(1F, 1F, 1F, 1F)).setMargin(0, 3))
                .setShadow(false).setScale(2F).setResize(true).setFixed(true)
                .setX(5).setY(20).setAnchor(WidgetAnchor.TOP_LEFT);

        this.redeployButton = (Button) new GenericButton("Respawn")
                .setScale(1F).setWidth(170).setHeight(20).setMargin(0, 3)
                .setFixed(true).setX(5).setY(60)
                .setAnchor(WidgetAnchor.CENTER_LEFT)
                .setPriority(RenderPriority.Lowest);

        // Get Any Regions that are owned, and adjacent to the player.
        List<CapturableRegion> regions = player.getCurrentRegion()
                .getAdjacentRegions();
        regions.add(player.getCurrentRegion());
        regions.add(player.getFaction().getFactionSpawnRegion(
                player.getBukkitPlayer().getWorld()));

        List<CapturableRegion> friendlyRegions = new ArrayList<>();
        for (CapturableRegion region : regions) {
            if (region.getOwner() == player.getFaction()) {
                friendlyRegions.add(region);
            }
        }

        SortedMap<Integer, CapturableRegion> distances = new TreeMap<Integer, CapturableRegion>();
        for (CapturableRegion region : friendlyRegions) {
            Location playerLoc = player.getBukkitPlayer().getLocation();
            Location spawnLoc = region.getSpawnPoint().getLocation();
            Integer distance = ((Double) playerLoc.distance(spawnLoc))
                    .intValue();
            distances.put(distance, region);
        }

        List<ListWidgetItem> respawnList = new ArrayList<ListWidgetItem>();
        for (Entry<Integer, CapturableRegion> listEntry : distances.entrySet()) {
            ListWidgetItem item = new ListWidgetItem(listEntry.getKey()
                    .toString() + "m", listEntry.getValue().getDisplayName());
            respawnList.add(item);
        }

        this.ListModel = new RespawnListModel(this, player, respawnList);
        GenericContainer listContainer = new GenericContainer();
        listContainer.setAnchor(WidgetAnchor.TOP_LEFT).setX(5).setY(40);
        this.listWidget = new GenericListView(this.ListModel);
        this.listWidget.setAnchor(WidgetAnchor.TOP_LEFT).setWidth(170)
                .setHeight(200).setFixed(true)
                .setPriority(RenderPriority.Lowest);
        listContainer.addChild(this.listWidget);

        this.popup = new GenericPopup();

        this.popup.attachWidgets(RegionControl.plugin, listContainer,
                this.listWidget, redeployTitle, this.redeployButton);

        mainscreen.attachPopupScreen(this.popup);
    }

    public GenericPopup getPopup() {
        return this.popup;
    }

    public Button getRedeployButton() {
        return this.redeployButton;
    }
}
