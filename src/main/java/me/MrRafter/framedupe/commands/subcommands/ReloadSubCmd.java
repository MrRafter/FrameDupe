package me.MrRafter.framedupe.commands.subcommands;

import me.MrRafter.framedupe.FrameDupe;
import me.MrRafter.framedupe.commands.SubCommand;
import me.MrRafter.framedupe.enums.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCmd extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }
    @Override
    public String getDescription() {
        return "Reload the plugin configuration.";
    }
    @Override
    public String getSyntax() {
        return "/framedupe reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission(Permissions.CMD_RELOAD.get())) {
            sender.sendMessage(ChatColor.WHITE + "Reloading FrameDupe config...");
            FrameDupe.getFoliaLib().getImpl().runAsync(reload -> {
                FrameDupe.getInstance().reloadConfiguration();
                sender.sendMessage(ChatColor.GREEN + "Reload complete.");
            });
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }
    }
}
