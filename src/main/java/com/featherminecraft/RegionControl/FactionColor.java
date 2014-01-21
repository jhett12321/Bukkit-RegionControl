package com.featherminecraft.RegionControl;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

public class FactionColor
{
    private DyeColor woolColor;
    private Color color;
    private ChatColor chatColor;
    
    private String factionIcon;
    private File controlPointIcon;
    
    public FactionColor(Faction faction, String factionColor)
    {
        boolean spoutEnabled = DependencyManager.isSpoutCraftAvailable();
        boolean dynmapEnabled = DependencyManager.isDynmapAvailable();
        
        // try
        // {
        // InputStream in = new FileInputStream(controlPointIcon);
        // }
        // catch(FileNotFoundException e)
        // {
        // e.printStackTrace();
        // }
        
        if(spoutEnabled)
        {
            factionIcon = Config.getFactionConfig().getString("factions." + faction.getId() + ".factionIcon");
        }
        
        switch(factionColor)
        {
            case "red" :
                woolColor = DyeColor.RED;
                color = Color.RED;
                chatColor = ChatColor.RED;
                break;
            case "green" :
                woolColor = DyeColor.GREEN;
                color = Color.GREEN;
                chatColor = ChatColor.GREEN;
                break;
            case "blue" :
                woolColor = DyeColor.BLUE;
                color = Color.BLUE;
                chatColor = ChatColor.DARK_BLUE;
                break;
            case "orange" :
                woolColor = DyeColor.ORANGE;
                color = Color.ORANGE;
                chatColor = ChatColor.GOLD;
                break;
            case "magenta" :
            case "pink" :
                woolColor = DyeColor.MAGENTA;
                color = Color.FUCHSIA;
                chatColor = ChatColor.LIGHT_PURPLE;
                break;
            case "purple" :
                woolColor = DyeColor.PURPLE;
                color = Color.PURPLE;
                chatColor = ChatColor.DARK_PURPLE;
                break;
            case "yellow" :
                woolColor = DyeColor.YELLOW;
                color = Color.YELLOW;
                chatColor = ChatColor.YELLOW;
                break;
            case "gray" :
                woolColor = DyeColor.GRAY;
                color = Color.GRAY;
                chatColor = ChatColor.GRAY;
                break;
            case "black" :
                woolColor = DyeColor.BLACK;
                color = Color.BLACK;
                chatColor = ChatColor.BLACK;
                break;
            case "white" :
            default :
                woolColor = DyeColor.WHITE;
                color = Color.WHITE;
                chatColor = ChatColor.WHITE;
                break;
        }
    }
    
    public ChatColor getChatColor()
    {
        return chatColor;
    }
    
    public Color getColor()
    {
        return color;
    }
    
    public String getFactionIcon()
    {
        return factionIcon;
    }
    
    public DyeColor getWoolColor()
    {
        return woolColor;
    }
}
