package me.MrRafter.framedupe;
import me.MrRafter.FrameDupe;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {
private FrameDupe plugin;

   public Commands(FrameDupe instance) {
     this.plugin = instance;
   }
   public static String PREFIX = "&b&l[FrameDupe] ";
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
     if (!cmd.getName().equalsIgnoreCase("FrameDupe"))
       return true;
     if (args.length == 0) {
       sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(PREFIX) + "&cUsage: &l/fd reload"));
       return true;
     }
     if (args[0].equalsIgnoreCase("reload")) {
       if (!sender.hasPermission("fd.reload")) {
         sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
               String.valueOf(PREFIX) + "&cYou don't have permissions to run this command."));
         return true;
       }
       this.plugin.reloadConfig();
       sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(PREFIX) + "&aConfig reloaded!"));
       return true;
     }
     return false;
   }
 }