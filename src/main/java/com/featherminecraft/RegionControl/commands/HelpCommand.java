package com.featherminecraft.RegionControl.commands;

import org.bukkit.command.CommandSender;

@CommandInfo(
        name = "help",
        aliases = "help",
        usage = "/regioncontrol help",
        desc = "Provides a list of RegionControl commands.",
        permission = "regioncontrol.help")
public class HelpCommand implements Command
{
    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        sender.sendMessage("______o|RegionControl Help|o______");
        sender.sendMessage("/rc addRegion <WorldGuard Id> <World> <'Display Name'> <Base Influence> <ControlPoint Count> <Faction Owner> <AdjacentRegion1,AdjancentRegion2...> <ControlPoint x> <ControlPoint y> <ControlPoint z> <SpawnPoint x> <SpawnPoint y> <SpawnPoint z>");
        sender.sendMessage("    - Adds a new CapturableRegion with the specified variables. Regions with more than 1 ControlPoint can only be created manually in the config file.");
        sender.sendMessage("/rc removeRegion <WorldGuard Id>");
        sender.sendMessage("    - Removes an Existing CapturableRegion (Note: Does not delete the Worldguard Region)");
        sender.sendMessage("__________________________________");
        return true;
    }
}
