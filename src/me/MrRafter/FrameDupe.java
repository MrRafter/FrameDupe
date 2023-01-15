package me.MrRafter;

import me.MrRafter.framedupe.Metrics;
import me.MrRafter.framedupe.CommandCompleter;
import me.MrRafter.framedupe.Commands;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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

    private final class FrameAll implements Listener {
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

    private final class FrameSpecific implements Listener {
        @EventHandler
        private void onFrameBreak(EntityDamageByEntityEvent event) {
            if (event.getEntityType() == EntityType.GLOW_ITEM_FRAME) {
                int rng = (int)Math.round(Math.random() * 100);
                if (rng < getConfig().getInt("probability-percentage")) {
                    event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), ((ItemFrame) event.getEntity()).getItem());
                }
            }
        }

    }
    public void onEnable() {
        //Frame Dupe
        if (Bukkit.getVersion().contains("1.8")){
            Bukkit.getLogger().info("Server on 1.8.x applying the changes by version...");
            Bukkit.getLogger().info("Changes by version applied correctly");
            getServer().getPluginManager().registerEvents(new FrameAll(), this);
        }
        if (Bukkit.getVersion().contains("1.9")){
            Bukkit.getLogger().info("Server on 1.9.x applying the changes by version...");
            Bukkit.getLogger().info("Changes by version applied correctly");
            getServer().getPluginManager().registerEvents(new FrameAll(), this);
        }
        if (Bukkit.getVersion().contains("1.10")){
            Bukkit.getLogger().info("Server on 1.10.x applying the changes by version...");
            Bukkit.getLogger().info("Changes by version applied correctly");
            getServer().getPluginManager().registerEvents(new FrameAll(), this);
        }
        if (Bukkit.getVersion().contains("1.11")){
            Bukkit.getLogger().info("Server on 1.11.x applying the changes by version...");
            Bukkit.getLogger().info("Changes by version applied correctly");
            getServer().getPluginManager().registerEvents(new FrameAll(), this);
        }
        if (Bukkit.getVersion().contains("1.12")){
            Bukkit.getLogger().info("Server on 1.12.x applying the changes by version...");
            Bukkit.getLogger().info("Changes by version applied correctly");
            getServer().getPluginManager().registerEvents(new FrameAll(), this);
        }
        if (Bukkit.getVersion().contains("1.13")){
            Bukkit.getLogger().info("Server on 1.13.x applying the changes by version...");
            Bukkit.getLogger().info("Changes by version applied correctly");
            getServer().getPluginManager().registerEvents(new FrameAll(), this);
        }
        if (Bukkit.getVersion().contains("1.14")){
            Bukkit.getLogger().info("Server on 1.14.x applying the changes by version...");
            Bukkit.getLogger().info("Changes by version applied correctly");
            getServer().getPluginManager().registerEvents(new FrameAll(), this);
        }
        if (Bukkit.getVersion().contains("1.15")){
            Bukkit.getLogger().info("Server on 1.15.x applying the changes by version...");
            Bukkit.getLogger().info("Changes by version applied correctly");
            getServer().getPluginManager().registerEvents(new FrameAll(), this);
        }
        if (Bukkit.getVersion().contains("1.16")){
            Bukkit.getLogger().info("Server on 1.16.x applying the changes by version...");
            Bukkit.getLogger().info("Changes by version applied correctly");
            getServer().getPluginManager().registerEvents(new FrameAll(), this);
        }
        if (Bukkit.getVersion().contains("1.17")){
            Bukkit.getLogger().info("Server on 1.17.x applying the changes by version...");
            Bukkit.getLogger().info("Changes by version applied correctly");
            getServer().getPluginManager().registerEvents(new FrameAll(), this);
            getServer().getPluginManager().registerEvents(new FrameSpecific(), this);
        }
        if (Bukkit.getVersion().contains("1.18")){
            Bukkit.getLogger().info("Server on 1.18.x applying the changes by version...");
            Bukkit.getLogger().info("Changes by version applied correctly");
            getServer().getPluginManager().registerEvents(new FrameAll(), this);
            getServer().getPluginManager().registerEvents(new FrameSpecific(), this);
        }
        if (Bukkit.getVersion().contains("1.19")){
            Bukkit.getLogger().info("Server on 1.19.x applying the changes by version...");
            Bukkit.getLogger().info("Changes by version applied correctly");
            getServer().getPluginManager().registerEvents(new FrameAll(), this);
            getServer().getPluginManager().registerEvents(new FrameSpecific(), this);
        }

        getCommand("framedupe").setExecutor((CommandExecutor)new Commands(this));
        getCommand("framedupe").setTabCompleter((TabCompleter)new CommandCompleter());

        int pluginId = 17434;
        Metrics metrics = new Metrics(this, pluginId);

        saveDefaultConfig();
        Bukkit.getLogger().info(Color.GREEN+ "FrameDupe by MrRafter loaded succesfully");
    }
}
