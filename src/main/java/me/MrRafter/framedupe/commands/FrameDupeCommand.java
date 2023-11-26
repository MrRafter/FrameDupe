package me.MrRafter.framedupe.commands;

import me.MrRafter.framedupe.commands.subcommands.ReloadSubCmd;
import me.MrRafter.framedupe.commands.subcommands.VersionSubCmd;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class FrameDupeCommand implements TabCompleter, CommandExecutor {

    private final List<SubCommand> subCommands = new ArrayList<>(2);
    private final List<String> tabCompleter = new ArrayList<>(2);

    public FrameDupeCommand() {
        subCommands.add(new ReloadSubCmd());
        subCommands.add(new VersionSubCmd());
        subCommands.forEach(subCommand -> tabCompleter.add(subCommand.getName()));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length == 1 ? tabCompleter : null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            boolean cmdExists = false;
            for (SubCommand subCommand : subCommands) {
                if (args[0].equalsIgnoreCase(subCommand.getName())) {
                    subCommand.perform(sender, args);
                    cmdExists = true;
                    break;
                }
            }
            if (!cmdExists) showCommandOverviewTo(sender);
        } else {
            showCommandOverviewTo(sender);
        }
        return true;
    }

    private void showCommandOverviewTo(CommandSender sender) {
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