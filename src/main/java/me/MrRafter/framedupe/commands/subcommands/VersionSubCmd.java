package me.MrRafter.framedupe.commands.subcommands;


import me.MrRafter.framedupe.FrameDupe;
import me.MrRafter.framedupe.commands.SubCommand;
import me.MrRafter.framedupe.enums.Permissions;
import org.bukkit.ChatColor;
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
        if (sender.hasPermission(Permissions.CMD_VERSION.get())) {
            final PluginDescriptionFile pluginyml = FrameDupe.getInstance().getDescription();
            sender.sendMessage("\n");
            sender.sendMessage(
                    ChatColor.GOLD+pluginyml.getName()+" "+pluginyml.getVersion()+
                    ChatColor.GRAY+" by "+ChatColor.DARK_AQUA+pluginyml.getAuthors().get(0)+
                    ChatColor.GRAY+" & "+ChatColor.DARK_AQUA+pluginyml.getAuthors().get(1)
            );
            sender.sendMessage("\n");
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }
    }
}
