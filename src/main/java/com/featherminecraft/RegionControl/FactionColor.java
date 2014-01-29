package com.featherminecraft.RegionControl;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

public class FactionColor
{
    private DyeColor woolColor;
    private Color color;
    private ChatColor chatColor;
    
    private String factionIcon;
    private String controlPointIcon;
    
    public FactionColor(Faction faction, String factionColor)
    {
        boolean spoutEnabled = DependencyManager.isSpoutCraftAvailable();
        
        if(spoutEnabled)
        {
            factionIcon = Config.getFactionConfig().getString("factions." + faction.getId() + ".factionIcon");
        }
        Colors factionColors = Colors.valueOf(factionColor.toUpperCase());
        
        switch(factionColors)
        {
            case RED :
                woolColor = DyeColor.RED;
                color = Color.RED;
                chatColor = ChatColor.RED;
                controlPointIcon = "redflag";
                break;
            case GREEN :
                woolColor = DyeColor.GREEN;
                color = Color.GREEN;
                chatColor = ChatColor.GREEN;
                controlPointIcon = "greenflag";
                break;
            case BLUE :
                woolColor = DyeColor.BLUE;
                color = Color.BLUE;
                chatColor = ChatColor.DARK_BLUE;
                controlPointIcon = "blueflag";
                break;
            case ORANGE :
                woolColor = DyeColor.ORANGE;
                color = Color.ORANGE;
                chatColor = ChatColor.GOLD;
                controlPointIcon = "orangeflag";
                break;
            case MAGENTA :
            case PINK :
                woolColor = DyeColor.MAGENTA;
                color = Color.FUCHSIA;
                chatColor = ChatColor.LIGHT_PURPLE;
                controlPointIcon = "pinkflag";
                break;
            case PURPLE :
                woolColor = DyeColor.PURPLE;
                color = Color.PURPLE;
                chatColor = ChatColor.DARK_PURPLE;
                controlPointIcon = "purpleflag";
                break;
            case YELLOW :
                woolColor = DyeColor.YELLOW;
                color = Color.YELLOW;
                chatColor = ChatColor.YELLOW;
                controlPointIcon = "yellowflag";
                break;
            case GRAY :
                woolColor = DyeColor.GRAY;
                color = Color.GRAY;
                chatColor = ChatColor.GRAY;
                controlPointIcon = "pirateflag";
                break;
            case BLACK :
                woolColor = DyeColor.BLACK;
                color = Color.BLACK;
                chatColor = ChatColor.BLACK;
                controlPointIcon = "pirateflag";
                break;
            case WHITE :
            default :
                woolColor = DyeColor.WHITE;
                color = Color.WHITE;
                chatColor = ChatColor.WHITE;
                controlPointIcon = "pin";
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
    
    public String getControlPointIcon()
    {
        return controlPointIcon;
    }

    public DyeColor getWoolColor()
    {
        return woolColor;
    }
    
    private enum Colors
    {
        RED,BLUE,GREEN,ORANGE,MAGENTA,PINK,PURPLE,YELLOW,GRAY,BLACK,WHITE
    }
}
