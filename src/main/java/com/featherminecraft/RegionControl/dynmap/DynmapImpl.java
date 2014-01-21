package com.featherminecraft.RegionControl.dynmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.World;

import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PolyLineMarker;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.RegionControl.DependencyManager;
import com.featherminecraft.RegionControl.RegionControl;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

public class DynmapImpl
{
    private static final String DEF_INFOWINDOW = "<div class=\"infowindow\"><span style=\"font-size:120%;\"><strong>%regionname%</strong></span><br>Currently Owned by <span style=\"font-weight:bold;\">%regionowner%</span><br><div style=\"text-align:center;font-weight:bold;margin:4px 0;\">%ControlPointDefs%</div></div>";
    private static final String DEF_CONTROLPOINT = "<span style=\"color:rgb(%red%,%green%,%blue%);padding:5px;\">[ %controlPointId% ]</span>";
    
    private static MarkerSet territoryControlMarkerSet;
    private static MarkerSet controlPointsMarkerSet;
    private static MarkerSet latticeLineMarkerSet;
    
    private static Map<CapturableRegion, AreaMarker> territoryControlMarkers = new HashMap<CapturableRegion, AreaMarker>();
    private static Map<ControlPoint, Marker> controlPointMarkers = new HashMap<ControlPoint, Marker>();
    private static Map<String, PolyLineMarker> latticeLineMarkers = new HashMap<String, PolyLineMarker>();
    
    public static void init()
    {
        latticeLineMarkerSet = DependencyManager.getDynmapAPI().getMarkerAPI().getMarkerSet("regioncontrol.latticeLines");
        if(latticeLineMarkerSet == null)
        {
            latticeLineMarkerSet = DependencyManager.getDynmapAPI().getMarkerAPI().createMarkerSet("regioncontrol.latticeLines", "Region Connections", null, false);
        }
        else
        {
            latticeLineMarkerSet.setMarkerSetLabel("Region Connections");
        }
        if(latticeLineMarkerSet == null)
        {
            RegionControl.plugin.getLogger().severe("Error creating marker set");
            return;
        }
        latticeLineMarkerSet.setLayerPriority(8);
        latticeLineMarkerSet.setHideByDefault(false);
        
        updateLatticeLinks();
        
        territoryControlMarkerSet = DependencyManager.getDynmapAPI().getMarkerAPI().getMarkerSet("regioncontrol.territorycontrol");
        
        if(territoryControlMarkerSet == null)
        {
            territoryControlMarkerSet = DependencyManager.getDynmapAPI().getMarkerAPI().createMarkerSet("regioncontrol.territorycontrol", "Territory Control", null, false);
        }
        else
        {
            territoryControlMarkerSet.setMarkerSetLabel("Territory Control");
        }
        if(territoryControlMarkerSet == null)
        {
            RegionControl.plugin.getLogger().severe("Error creating Territory Control marker set");
            return;
        }
        territoryControlMarkerSet.setLayerPriority(10);
        territoryControlMarkerSet.setHideByDefault(false);
        
        updateRegions();
        
        controlPointsMarkerSet = DependencyManager.getDynmapAPI().getMarkerAPI().getMarkerSet("regioncontrol.controlpoints");
        if(controlPointsMarkerSet == null)
        {
            controlPointsMarkerSet = DependencyManager.getDynmapAPI().getMarkerAPI().createMarkerSet("regioncontrol.controlpoints", "Control Points", null, false);
        }
        else
        {
            controlPointsMarkerSet.setMarkerSetLabel("Control Points");
        }
        if(controlPointsMarkerSet == null)
        {
            RegionControl.plugin.getLogger().severe("Error creating marker set");
            return;
        }
        controlPointsMarkerSet.setLayerPriority(8);
        controlPointsMarkerSet.setHideByDefault(false);
        
        updateControlPoints();
    }
    
    public static void updateLatticeLink(CapturableRegion region, CapturableRegion adjacentRegion)
    {
        String id = region.getWorld().getName() + "_" + region.getRegionId() + "_" + adjacentRegion.getRegionId();
        
        PolyLineMarker marker = latticeLineMarkers.get(id);
        
        if(marker != null)
        {
            if(region.getOwner() == adjacentRegion.getOwner())
            {
                marker.setLineStyle(3, 1.0, region.getOwner().getFactionColor().getColor().asRGB());
            }
            
            else
            {
                marker.setLineStyle(3, 1.0, Color.YELLOW.asRGB());
            }
        }
        else
        {
            id = adjacentRegion.getWorld().getName() + "_" + adjacentRegion.getRegionId() + "_" + region.getRegionId();
            marker = latticeLineMarkers.get(id);
            
            if(region.getOwner() == adjacentRegion.getOwner())
            {
                marker.setLineStyle(3, 1.0, region.getOwner().getFactionColor().getColor().asRGB());
            }
            
            else
            {
                marker.setLineStyle(3, 1.0, Color.YELLOW.asRGB());
            }
        }
    }
    
    public static void updateRegion(CapturableRegion region)
    {
        AreaMarker marker = territoryControlMarkers.get(region);
        
        marker.setFillStyle(0.6, region.getOwner().getFactionColor().getColor().asRGB());
        updateRegionControlPoints(region);
    }
    
    public static void updateRegionControlPoints(CapturableRegion region)
    {
        String desc = formatInfoWindow(region);
        territoryControlMarkers.get(region).setDescription(desc);
    }
    
    /* Update region information */
    @Deprecated
    public static void updateRegions()
    {
        Map<CapturableRegion, AreaMarker> newmap = new HashMap<CapturableRegion, AreaMarker>(); /* Build new map */
        
        /* Loop through worlds */
        for(CapturableRegion region : ServerLogic.capturableRegions.values())
        {
            handleRegion(region.getWorld(), region, newmap);
        }
        /* Now, review old map - anything left is gone */
        for(AreaMarker oldm : territoryControlMarkers.values())
        {
            oldm.deleteMarker();
        }
        /* And replace with new map */
        territoryControlMarkers = newmap;
    }
    
    private static String formatInfoWindow(CapturableRegion region)
    {
        String infoDiv = "<div class=\"regioninfo\">" + DEF_INFOWINDOW + "</div>";
        infoDiv = infoDiv.replace("%regionname%", region.getDisplayName());
        infoDiv = infoDiv.replace("%regionowner%", region.getOwner().getDisplayName());
        
        for(ControlPoint controlPoint : region.getControlPoints())
        {
            String controlpointdef = DEF_CONTROLPOINT.replace("%controlPointId%", controlPoint.getIdentifier().toUpperCase());
            controlpointdef = controlpointdef.replace("%red%", ((Integer) controlPoint.getOwner().getFactionColor().getColor().getRed()).toString());
            controlpointdef = controlpointdef.replace("%green%", ((Integer) controlPoint.getOwner().getFactionColor().getColor().getGreen()).toString());
            controlpointdef = controlpointdef.replace("%blue%", ((Integer) controlPoint.getOwner().getFactionColor().getColor().getBlue()).toString());
            infoDiv = infoDiv.replace("%ControlPointDefs%", controlpointdef + "%ControlPointDefs%");
        }
        
        infoDiv = infoDiv.replace("%ControlPointDefs%", "");
        
        return infoDiv;
    }
    
    @Deprecated
    private static void handleRegion(World world, CapturableRegion region, Map<CapturableRegion, AreaMarker> newmap)
    {
        String name = region.getDisplayName();
        ProtectedRegion protectedRegion = region.getRegion();
        
        double[] x = null;
        double[] z = null;
        
        String typeName = protectedRegion.getTypeName();
        BlockVector l0 = protectedRegion.getMinimumPoint();
        BlockVector l1 = protectedRegion.getMaximumPoint();
        
        if(typeName.equalsIgnoreCase("cuboid"))
        { /* Cubiod region? */
            /* Make outline */
            x = new double[4];
            z = new double[4];
            x[0] = l0.getX();
            z[0] = l0.getZ();
            x[1] = l0.getX();
            z[1] = l1.getZ() + 1.0;
            x[2] = l1.getX() + 1.0;
            z[2] = l1.getZ() + 1.0;
            x[3] = l1.getX() + 1.0;
            z[3] = l0.getZ();
        }
        else if(typeName.equalsIgnoreCase("polygon"))
        {
            ProtectedPolygonalRegion polygonalRegion = (ProtectedPolygonalRegion) protectedRegion;
            List<BlockVector2D> points = polygonalRegion.getPoints();
            x = new double[points.size()];
            z = new double[points.size()];
            for(int i = 0; i < points.size(); i ++ )
            {
                BlockVector2D pt = points.get(i);
                x[i] = pt.getX();
                z[i] = pt.getZ();
            }
        }
        else
        { /* Unsupported type */
            return;
        }
        
        AreaMarker marker = territoryControlMarkers.remove(region); /* Existing area? */
        if(marker == null)
        {
            marker = territoryControlMarkerSet.createAreaMarker(region.getRegionId(), name, false, world.getName(), x, z, false);
            if(marker == null)
            {
                return;
            }
        }
        else
        {
            marker.setCornerLocations(x, z); /* Replace corner locations */
            marker.setLabel(name); /* Update label */
        }
        /* Set line and fill properties */
        marker.setFillStyle(0.3, region.getOwner().getFactionColor().getColor().asRGB());
        marker.setLineStyle(2, 1.0, Color.BLACK.asRGB());
        
        /* Build popup */
        String desc = formatInfoWindow(region);
        
        marker.setDescription(desc); /* Set popup */
        
        /* Add to map */
        newmap.put(region, marker);
    }
    
    private static void updateControlPoints()
    {
        for(CapturableRegion region : ServerLogic.capturableRegions.values())
        {
            for(ControlPoint controlPoint : region.getControlPoints())
            {
                String id = region.getWorld().getName() + "_" + region.getRegionId() + "_" + controlPoint.getIdentifier();
                
                double x = controlPoint.getLocation().getX();
                double y = controlPoint.getLocation().getY();
                double z = controlPoint.getLocation().getZ();
                
                Marker marker = controlPointsMarkerSet.createMarker(id, controlPoint.getIdentifier().toUpperCase(), region.getWorld().getName(), x, y, z, DependencyManager.getDynmapAPI().getMarkerAPI().getMarkerIcon("yellowflag"), false);
                
                controlPointMarkers.put(controlPoint, marker);
            }
        }
    }
    
    private static void updateLatticeLinks()
    {
        for(CapturableRegion region : ServerLogic.capturableRegions.values())
        {
            for(CapturableRegion adjacentRegion : region.getAdjacentRegions())
            {
                List<CapturableRegion> previousLink = new ArrayList<CapturableRegion>();
                previousLink.add(adjacentRegion);
                previousLink.add(region);
                
                if(!latticeLineMarkers.containsKey(adjacentRegion.getWorld().getName() + "_" + adjacentRegion.getRegionId() + "_" + region.getRegionId()))
                {
                    String id = region.getWorld().getName() + "_" + region.getRegionId() + "_" + adjacentRegion.getRegionId();
                    double[] x = new double[2];
                    double[] y = new double[2];
                    double[] z = new double[2];
                    
                    x[0] = region.getSpawnPoint().getLocation().getX();
                    y[0] = region.getSpawnPoint().getLocation().getY();
                    z[0] = region.getSpawnPoint().getLocation().getZ();
                    x[1] = adjacentRegion.getSpawnPoint().getLocation().getX();
                    y[1] = adjacentRegion.getSpawnPoint().getLocation().getY();
                    z[1] = adjacentRegion.getSpawnPoint().getLocation().getZ();
                    
                    PolyLineMarker marker = latticeLineMarkerSet.createPolyLineMarker(id, null, false, region.getWorld().getName(), x, y, z, false);
                    
                    if(region.getOwner() == adjacentRegion.getOwner())
                    {
                        marker.setLineStyle(3, 1.0, region.getOwner().getFactionColor().getColor().asRGB());
                    }
                    else
                    {
                        marker.setLineStyle(3, 1.0, Color.YELLOW.asRGB());
                    }
                    
                    latticeLineMarkers.put(id, marker);
                }
            }
        }
    }
}
