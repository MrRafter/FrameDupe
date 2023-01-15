package me.MrRafter.framedupe;

import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.command.TabCompleter;

 public class CommandCompleter
   implements TabCompleter
 {
   private ArrayList<String> arguments = new ArrayList<>();


   public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
     if (this.arguments.isEmpty() && sender.hasPermission("fd.reload")) {
       this.arguments.add("reload");
     }
     ArrayList<String> results = new ArrayList<>();
     if (args.length >= 0 && args.length < 2) {
       for (String argument : this.arguments) {
         if (!argument.toLowerCase().startsWith(args[0].toLowerCase()))
           continue;
         results.add(argument);
       }
       return results;
     }
     return results;
   }
 }