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

    private final class FrameAll implements Listener {
        @EventHandler
        private void onFrameBreak(EntityDamageByEntityEvent event) {
            if (getConfig().getBoolean("FrameDupe.Enable") && event.getEntityType() == EntityType.ITEM_FRAME) {
                int rng = (int)Math.round(Math.random() * 100);
                if (rng < getConfig().getInt("FrameDupe.Probability-percentage")) {
                    event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), ((ItemFrame) event.getEntity()).getItem());
                }
            }
        }

    }

    private final class FrameSpecific implements Listener {
        @EventHandler
        private void onFrameBreak(EntityDamageByEntityEvent event) {
            if (getConfig().getBoolean("GLOW_FrameDupe.Enable") && event.getEntityType() == EntityType.GLOW_ITEM_FRAME) {
                int rng = (int)Math.round(Math.random() * 100);
                if (rng < getConfig().getInt("GLOW_FrameDupe.Probability-percentage")) {
                    event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), ((ItemFrame) event.getEntity()).getItem());
                }
            }
        }

    }

    public void onEnable() {

        if (Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18") || Bukkit.getVersion().contains("1.19") || Bukkit.getVersion().contains("1.20") || Bukkit.getVersion().contains("1.21") || Bukkit.getVersion().contains("1.22")){
            getServer().getPluginManager().registerEvents(new FrameSpecific(), this);
        }

        getServer().getPluginManager().registerEvents(new FrameAll(), this);

        getCommand("framedupe").setExecutor((CommandExecutor)new Commands(this));
        getCommand("framedupe").setTabCompleter((TabCompleter)new CommandCompleter());

        int pluginId = 17434;
        Metrics metrics = new Metrics(this, pluginId);

        saveDefaultConfig();
        Bukkit.getLogger().info("FrameDupe by MrRafter loaded successfully");
    }
}