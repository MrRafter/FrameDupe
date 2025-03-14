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

    private int mcVersion;

    @Override
    public void onEnable() {
        mcVersion = getMinecraftVersion();

        // Register events
        getServer().getPluginManager().registerEvents(new Events(this).new FrameAll(), this);

        // activate Glow_Frame_Dupe
        if (mcVersion >= 17) {
            getServer().getPluginManager().registerEvents(new Events(this).new FrameSpecific(), this);
        }

        // Commands
        if (getCommand("framedupe") != null) {
            getCommand("framedupe").setExecutor((CommandExecutor) new Commands(this));
            getCommand("framedupe").setTabCompleter((TabCompleter) new CommandCompleter());
        } else {
            getLogger().warning("The 'framedupe' command is not defined in plugin.yml");
        }

        // Metrics
        int pluginId = 17434;
        new Metrics(this, pluginId);

        // Guardar configuración
        saveDefaultConfig();

        Bukkit.getLogger().info("FrameDupe by MrRafter loaded successfully (MC Version: " + mcVersion + ")");
    }

   // Get Vercion on mc
    private int getMinecraftVersion() {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        String[] parts = version.split("\\.");
        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return 7;
        }
    }
}
