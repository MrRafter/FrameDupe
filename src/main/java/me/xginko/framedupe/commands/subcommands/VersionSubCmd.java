package me.xginko.framedupe.commands.subcommands;


import me.xginko.framedupe.FrameDupe;
import me.xginko.framedupe.commands.SubCommand;
import me.xginko.framedupe.enums.PluginPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class VersionSubCmd extends SubCommand {

    @Override
    public String getName() {
        return "version";
    }
    @Override
    public String getDescription() {
        return "Show the plugin version.";
    }
    @Override
    public String getSyntax() {
        return "/framedupe version";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PluginPermission.CMD_VERSION.get())) {
            sender.sendMessage("You don't have permission to use this command.");
            return;
        }

        PluginDescriptionFile pluginyml = FrameDupe.getInstance().getDescription();

        sender.sendMessage("\n");
        sender.sendMessage(pluginyml.getName()+" "+pluginyml.getVersion() +
                " by "+ String.join(", ", pluginyml.getAuthors()));
        sender.sendMessage("\n");
    }
}
