/*    */ package me.MrRafter.framedupe;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.command.TabCompleter;
/*    */ 
/*    */ public class CommandCompleter
/*    */   implements TabCompleter
/*    */ {
/* 12 */   private ArrayList<String> arguments = new ArrayList<>();
/*    */ 
/*    */   
/*    */   public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
/* 16 */     if (this.arguments.isEmpty() && sender.hasPermission("fd.reload")) {
/* 17 */       this.arguments.add("reload");
/*    */     }
/* 19 */     ArrayList<String> results = new ArrayList<>();
/* 20 */     if (args.length >= 0 && args.length < 2) {
/* 21 */       for (String argument : this.arguments) {
/* 22 */         if (!argument.toLowerCase().startsWith(args[0].toLowerCase()))
/*    */           continue; 
/* 24 */         results.add(argument);
/*    */       } 
/* 26 */       return results;
/*    */     } 
/* 28 */     return results;
/*    */   }
/*    */ }