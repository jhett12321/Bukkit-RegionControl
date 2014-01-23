package com.featherminecraft.RegionControl.commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandHandler
{
    private Map<String, Command> commands;
    
    public CommandHandler()
    {
        commands = new LinkedHashMap<String, Command>();
        registerCommand(HelpCommand.class);
        registerCommand(ReloadCommand.class);
        registerCommand(SetFactionCommand.class);
        registerCommand(SaveCommand.class);
        registerCommand(EditModeCommand.class);
    }
    
    public Command getCommand(String commandName)
    {
        Command command = null;
        if(commandName == null)
        {
            commandName = "help";
        }
        else
        {
            for(Entry<String, Command> entry : commands.entrySet())
            {
                if(commandName.matches(entry.getKey()))
                {
                    command = entry.getValue();
                }
            }
        }
        return command;
    }
    
    public CommandInfo getCommandInfo(Command command)
    {
        CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);
        return info;
    }
    
    public boolean handleCommand(CommandSender sender, String[] args)
    {
        Command command = getCommand(args[0]);
        if(command == null)
        {
            sender.sendMessage("Unknown RegionControl Command. Type RegionControl help for a list of commands.");
            return false;
        }
        
        CommandInfo info = getCommandInfo(command);
        
        if(sender.hasPermission(info.permission()))
        {
            return command.execute(sender, args);
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return false;
        }
    }
    
    private void registerCommand(Class<? extends Command> command)
    {
        CommandInfo info = command.getAnnotation(CommandInfo.class);
        if(info == null)
        {
            return;
        }
        try
        {
            commands.put(info.aliases(), command.newInstance());
        }
        catch(InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}