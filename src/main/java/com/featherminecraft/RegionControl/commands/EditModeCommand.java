package com.featherminecraft.RegionControl.commands;

import org.bukkit.command.CommandSender;

import com.featherminecraft.RegionControl.ServerLogic;

@CommandInfo(
             name = "editmode",
             aliases = "editmode|toggleedit",
             usage = "/regioncontrol editmode",
             desc = "Enables edit mode, allowing you to modify the world terrain, without it being reset.",
             permission = "regioncontrol.editmode")
public class EditModeCommand implements Command
{
    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        if(!ServerLogic.editMode)
        {
            ServerLogic.editMode = true;
            sender.sendMessage("Server is now in edit mode. Any blocks placed/destroyed will be permanent.");
        }
        else
        {
            ServerLogic.editMode = false;
            sender.sendMessage("Server is no-longer in edit mode. Any blocks placed/destroyed will be reset on region capture/server shutdown.");
        }
        return true;
    }
    
}
