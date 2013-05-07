package com.featherminecraft.regioncontrol;

import org.bukkit.entity.Player;

//W.I.P.
public class Faction {

    private String name;
    public Faction(String name, String factiongroup)
    {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public Faction getFaction(String name)
    {
        if(name == null)
        {
            //Name Can't be null
        }
        Faction faction = ServerLogic.factions.get(name);
        return faction;
    }

    public static Faction getPlayerFaction(Player player) {
        //Get Player Permission Group
        String group = null;
        Faction faction = ServerLogic.factions.get(group);
        return faction;
    }
}
