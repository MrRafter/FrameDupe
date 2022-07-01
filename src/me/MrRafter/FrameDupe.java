package me.MrRafter;

import me.MrRafter.framedupe.Metrics;
import me.MrRafter.framedupe.CommandCompleter;
import me.MrRafter.framedupe.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public final class FrameDupe extends JavaPlugin {
    public Map<Item, Player> droppers = new HashMap<>();

    private final class FrameDupeListener implements Listener {

        @EventHandler
        private void onFrameBreak(EntityDamageByEntityEvent event) {
            if (event.getEntityType() == EntityType.ITEM_FRAME) {
                int rng = (int)Math.round(Math.random() * 100);
                if (rng < getConfig().getInt("probability-percentage")) {
                    event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), ((ItemFrame) event.getEntity()).getItem());
                }
            }
        }

    }


    public static boolean usingPAPI = false;
    public void onEnable() {
        //Frame Dupe
        getServer().getPluginManager().registerEvents(new FrameDupeListener(), this);
        getCommand("framedupe").setExecutor((CommandExecutor)new Commands(this));
        getCommand("framedupe").setTabCompleter((TabCompleter)new CommandCompleter());
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            usingPAPI = true;

        }

        int pluginId = 10837;
        Metrics metrics = new Metrics(this, pluginId);

        saveDefaultConfig();
        registerCommands();
        registerEvents();
        Bukkit.getLogger().info("FrameDupe by MrRafter loaded succesfully");
    }

    public void registerEvents(){
    }

    public void registerCommands(){
    }

    public static FrameDupe getPlugin(){
        return getPlugin(FrameDupe.class);
    }
}
