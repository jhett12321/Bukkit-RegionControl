package com.featherminecraft.RegionControl.data;

public class QueueItem
{
    private Table table;
    private String lookupKey;
    private Object lookupValue;
    private String column;
    private Object newValue;
    
    public QueueItem(Table table, Object lookupValue, String column, Object newValue)
    {
        this.table = table;
        this.lookupKey = table.getLookupColumn();
        this.lookupValue = lookupValue;
        this.column = column;
        this.newValue = newValue;
        
        String query = table.toString() + "_" + lookupValue.toString() + "_" + column;
        Data.getDatabaseQueue().put(query, this);
    }
    
    public Table getTable()
    {
        return table;
    }
    
    public String getLookupKey()
    {
        return lookupKey;
    }
    
    public String getColumn()
    {
        return column;
    }
    
    public Object getNewValue()
    {
        return newValue;
    }
    
    public Object getLookupValue()
    {
        return lookupValue;
    }
}