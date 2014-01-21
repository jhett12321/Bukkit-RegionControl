package com.featherminecraft.RegionControl.commands;

import org.bukkit.command.CommandSender;

@CommandInfo(
        name = "reload",
        aliases = "reload|reloadall|restart",
        usage = "/regioncontrol reload",
        desc = "Reloads the RegionControl config files.",
        permission = "regioncontrol.reload")
public class ReloadCommand implements Command
{
    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        // TODO Reload steps.
        sender.sendMessage("Reloaded RegionControl configs.");
        return true;
    }
}
