package com.featherminecraft.RegionControl.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.RegionControl;

public class Data
{
    private static Map<String, QueueItem> databaseQueue = new HashMap<String, QueueItem>();
    
    public static void init()
    {
        Connection connection;
        Statement statement;
        try
        {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + RegionControl.plugin.getDataFolder() + "/data.db");
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS regioncontrol_characters " +
                    "(character  TEXT PRIMARY KEY NOT NULL," +
                    " fsn_id          TEXT    NOT NULL, " +
                    " faction_id      TEXT    NOT NULL, " +
                    " kills           INT     NOT NULL, " +
                    " deaths          INT     NOT NULL, " +
                    " assists         INT     NOT NULL, " +
                    " region_captures INT     NOT NULL, " +
                    " region_defends  INT     NOT NULL, " +
                    " blocks_placed   INT     NOT NULL, " +
                    " blocks_destroyed INT    NOT NULL)";
            statement.executeUpdate(sql);
            
            statement.close();
            connection.close();
            
        }
        catch(SQLException | ClassNotFoundException e)
        {
            RegionControl.plugin.getLogger().log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    public static void addItemToQueue(Table table, Object lookupValue, String column, Object newValue)
    {
        String query = table.toString() + "_" + lookupValue.toString() + "_" + column;
        if(!databaseQueue.containsKey(query))
        {
            databaseQueue.put(query,new QueueItem(table, lookupValue, column, newValue));
        }
    }
    
    public static void processQueue()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + RegionControl.plugin.getDataFolder() + "/data.db");
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            
            for(QueueItem queueItem : databaseQueue.values())
            {
                if(queueItem.getTable() == Table.regioncontrol_characters)
                {
                    String sql = "UPDATE " + queueItem.getTable().toString() + " set " + queueItem.getColumn() + " = " + queueItem.getNewValue() + " where " + queueItem.getLookupKey() + "='" + queueItem.getLookupValue() + "';";
                    statement.executeUpdate(sql);
                }
            }
            
            databaseQueue.clear();
            
            statement.close();
            connection.commit();
            connection.close();
        }
        catch(SQLException | ClassNotFoundException e)
        {
            RegionControl.plugin.getLogger().log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    public static boolean getPlayerStats(RCPlayer player)
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + RegionControl.plugin.getDataFolder() + "/data.db");
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            
            String playername = player.getBukkitPlayer().getName();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM regioncontrol_characters WHERE character='" + playername + "';");
            if(!resultSet.isBeforeFirst())
            {
                // No Character Data, Generate Record now.
                String fsnId = playername;
                String factionID = player.getFaction().getId();
                String sql = "INSERT INTO regioncontrol_characters (character,fsn_id,faction_id,kills,deaths,assists,region_captures,region_defends,blocks_placed,blocks_destroyed) " +
                        "VALUES ('" + playername + "', '" + fsnId + "', '" + factionID + "', 0, 0, 0, 0, 0, 0, 0);";
                statement.executeUpdate(sql);
            }
            resultSet.close();
            resultSet = statement.executeQuery("SELECT * FROM regioncontrol_characters WHERE character='" + playername + "';");
            while(resultSet.next())
            {
                player.setKills(resultSet.getInt("kills"));
                player.setDeaths(resultSet.getInt("deaths"));
                player.setAssists(resultSet.getInt("assists"));
                player.setRegionCaptures(resultSet.getInt("region_captures"));
                player.setRegionDefends(resultSet.getInt("region_defends"));
                player.setBlocksPlaced(resultSet.getInt("blocks_placed"));
                player.setBlocksDestroyed(resultSet.getInt("blocks_destroyed"));
            }
            
            resultSet.close();
            statement.close();
            connection.commit();
            connection.close();
            return true;
        }
        catch(SQLException | ClassNotFoundException e)
        {
            RegionControl.plugin.getLogger().log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    protected static Map<String, QueueItem> getDatabaseQueue()
    {
        return databaseQueue;
    }
}
