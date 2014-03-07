package com.featherminecraft.RegionControl.data;

public enum Table
{
    regioncontrol_characters("character");
    // regioncontrol_regions ("region");
    
    private final String lookupColumn;
    
    private Table(String lookupColumn)
    {
        this.lookupColumn = lookupColumn;
    }
    
    public String getLookupColumn()
    {
        return lookupColumn;
    }
}