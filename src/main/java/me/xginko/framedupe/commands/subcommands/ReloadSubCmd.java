package me.xginko.framedupe.commands.subcommands;

import me.xginko.framedupe.FrameDupe;
import me.xginko.framedupe.commands.SubCommand;
import me.xginko.framedupe.enums.PluginPermission;
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
        if (!sender.hasPermission(PluginPermission.CMD_RELOAD.get())) {
            sender.sendMessage("You don't have permission to use this command.");
            return;
        }

        sender.sendMessage("Reloading FrameDupe config...");
        FrameDupe.scheduling().asyncScheduler().run(() -> {
            FrameDupe.getInstance().reloadConfiguration();
            sender.sendMessage("Reload complete.");
        });
    }
}
