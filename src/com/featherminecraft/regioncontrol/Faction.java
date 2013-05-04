package com.featherminecraft.regioncontrol;

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
}
