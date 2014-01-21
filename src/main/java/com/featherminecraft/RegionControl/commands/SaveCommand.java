package com.featherminecraft.RegionControl.commands;

import org.bukkit.command.CommandSender;

import com.featherminecraft.RegionControl.Config;

@CommandInfo(
        name = "save",
        aliases = "save|saveall",
        usage = "/regioncontrol save",
        desc = "Saves all loaded regions and data",
        permission = "regioncontrol.save")
public class SaveCommand implements Command
{
    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        Config.saveAll(true);
        return true;
    }
    
}
