package com.featherminecraft.RegionControl.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.api.PlayerAPI;

@CommandInfo(
        name = "setfaction",
        aliases = "setfaction|setplayerfaction",
        usage = "/regioncontrol setfaction [Player] <Faction>",
        desc = "Sets the provided players faction if provided, otherwise sets the senders faction.",
        permission = "regioncontrol.setfaction")
public class SetFactionCommand implements Command
{
    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        if((args.length == 2 || args.length == 3))
        {
            Faction faction = null;
            Player player = null;
            
            if(args.length == 3)
            {
                faction = ServerLogic.factions.get(args[2]);
                player = Bukkit.getPlayer(args[1]);
            }
            else
            {
                faction = ServerLogic.factions.get(args[1]);
                if(sender instanceof Player)
                {
                    player = (Player) sender;
                }
                else
                {
                    sender.sendMessage("You can't change the faction of console!");
                    sender.sendMessage("Console Command Syntax: /regioncontrol setfaction <Player> <Faction Id>");
                    return false;
                }
            }
            if(faction == null)
            {
                sender.sendMessage("Could not find a faction that matches that id!");
                sender.sendMessage("Command Syntax: /regioncontrol setfaction <Player> <Faction Id>");
                return false;
            }
            if(player == null)
            {
                sender.sendMessage("Invalid player specified.");
                sender.sendMessage("Command Syntax: /regioncontrol setfaction <Player> <Faction Id>");
                return false;
            }
            
            PlayerAPI.getRCPlayerFromBukkitPlayer(player).setFaction(faction);
            sender.sendMessage("Set " + player.getName() + "'s Faction to " + faction.getDisplayName());
            return true;
        }
        else
        {
            return false;
        }
    }
}
