package com.featherminecraft.RegionControl.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.featherminecraft.RegionControl.api.RegionAPI;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

@CommandInfo(
        name = "teleport",
        aliases = "tp|teleport",
        usage = "/regioncontrol teleport [player] [world] <regionId>",
        desc = "Teleports the sender (or player if provided) to the regions spawnroom.",
        permission = "regioncontrol.tp")
public class TeleportCommand implements Command
{
    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        if(args.length == 2 || args.length == 3 || args.length == 4)
        {
            Player player = null;
            String world = null;
            String region = null;
            
            if(args.length == 4)
            {
                player = Bukkit.getPlayer(args[1]);
                world = args[2];
                region = args[3];
            }
            
            else if(args.length == 3)
            {
                if(sender instanceof Player && Bukkit.getWorld(args[1]) != null)
                {
                    player = (Player) sender;
                    world = args[1];
                    region = args[2];
                }
                else
                {
                    player = Bukkit.getPlayer(args[1]);
                }
            }
            else if(args.length == 2)
            {
                if(sender instanceof Player)
                {
                    player = (Player) sender;
                    region = args[1];
                }
                else
                {
                    sender.sendMessage("You can't teleport console to a region!");
                    sender.sendMessage("Console Command Syntax: /regioncontrol teleport [player] [world] <regionId>");
                    return false;
                }
            }
            
            if(region == null)
            {
                sender.sendMessage("Invalid region specified.");
                sender.sendMessage("Command Syntax: /regioncontrol teleport [player] [world] <regionId>");
                return false;
            }
            
            if(player == null)
            {
                sender.sendMessage("Invalid player specified.");
                sender.sendMessage("Command Syntax: /regioncontrol teleport [player] [world] <regionId>");
                return false;
            }
            
            if(world == null)
            {
                world = player.getWorld().getName();
            }
            
            if(Bukkit.getWorld(world) == null)
            {
                sender.sendMessage("Invalid world specified.");
                sender.sendMessage("Command Syntax: /regioncontrol teleport [player] [world] <regionId>");
                return false;
            }
            
            CapturableRegion newRegion = RegionAPI.getRegionFromWorldGuardRegion(world, region);
            
            if(newRegion == null)
            {
                sender.sendMessage("Invalid world or region specified.");
                sender.sendMessage("Command Syntax: /regioncontrol teleport [player] [world] <regionId>");
                return false;
            }
            
            player.teleport(newRegion.getSpawnPoint().getLocation());
            
            sender.sendMessage("Teleported " + player.getName() + " to " + newRegion.getDisplayName());
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
