package me.MrRafter;

import me.MrRafter.framedupe.Events;
import me.MrRafter.framedupe.Metrics;
import me.MrRafter.framedupe.CommandCompleter;
import me.MrRafter.framedupe.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class FrameDupe extends JavaPlugin {

    public void onEnable() {
        String versionString = Bukkit.getVersion();
        int versionNumber = Integer.parseInt(versionString.substring(versionString.indexOf('.') + 1, versionString.lastIndexOf('.')));

        if (versionNumber >= 17) {
            getServer().getPluginManager().registerEvents(new Events(this).new FrameSpecific(), this);
        }

        getServer().getPluginManager().registerEvents(new Events(this).new FrameAll(), this);

        getCommand("framedupe").setExecutor((CommandExecutor)new Commands(this));
        getCommand("framedupe").setTabCompleter((TabCompleter)new CommandCompleter());

        int pluginId = 17434;
        Metrics metrics = new Metrics(this, pluginId);

        saveDefaultConfig();
        Bukkit.getLogger().info("FrameDupe by MrRafter loaded successfully");
    }
}
