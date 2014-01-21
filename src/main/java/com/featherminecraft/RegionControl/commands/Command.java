package com.featherminecraft.RegionControl.commands;

import org.bukkit.command.CommandSender;

public interface Command
{
    public boolean execute(CommandSender sender, String[] args);
}
