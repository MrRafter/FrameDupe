package me.MrRafter.framedupe.commands;

import me.MrRafter.framedupe.commands.subcommands.ReloadSubCmd;
import me.MrRafter.framedupe.commands.subcommands.VersionSubCmd;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FrameDupeCommand implements TabCompleter, CommandExecutor {

    private final List<SubCommand> subCommands;
    private final List<String> tabCompleter;

    public FrameDupeCommand() {
        subCommands = Arrays.asList(new ReloadSubCmd(), new VersionSubCmd());
        tabCompleter = subCommands.stream().map(SubCommand::getName).collect(Collectors.toList());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length == 1 ? tabCompleter : Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendCommandOverview(sender);
            return true;
        }

        for (final SubCommand subCommand : subCommands) {
            if (args[0].equalsIgnoreCase(subCommand.getName())) {
                subCommand.perform(sender, args);
                return true;
            }
        }

        sendCommandOverview(sender);
        return true;
    }

    private void sendCommandOverview(CommandSender sender) {
        if (!sender.hasPermission("framedupe.cmd.*")) return;
        sender.sendMessage(ChatColor.GRAY+"-----------------------------------------------------");
        sender.sendMessage(ChatColor.WHITE+"FrameDupe Commands");
        sender.sendMessage(ChatColor.GRAY+"-----------------------------------------------------");
        subCommands.forEach(cmd ->
            sender.sendMessage(ChatColor.WHITE + cmd.getSyntax() + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + cmd.getDescription())
        );
        sender.sendMessage(ChatColor.GRAY+"-----------------------------------------------------");
    }
}