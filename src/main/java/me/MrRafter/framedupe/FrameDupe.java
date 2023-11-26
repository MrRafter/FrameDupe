package me.MrRafter.framedupe;

import com.tcoded.folialib.FoliaLib;
import me.MrRafter.framedupe.modules.FrameModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class FrameDupe extends JavaPlugin {

    private static FrameDupe instance;
    private static FrameConfig config;
    private static Logger logger;
    public static boolean serverHasGlowItemFrames, serverHasShulkers;

    public void onEnable() {
        instance = this;
        logger = getLogger();
        new Metrics(this, 17434);

        // Compatibility checks
        try {
            Class.forName("org.bukkit.entity.ItemFrame");
        } catch (ClassNotFoundException e) {
            logger.severe("Your server version does not have item frames. Plugin cant enable.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        try {
            Class.forName("org.bukkit.entity.GlowItemFrame");
            serverHasGlowItemFrames = true;
        } catch (ClassNotFoundException e) {
            serverHasGlowItemFrames = false;
        }
        try {
            Class.forName("org.bukkit.block.ShulkerBox");
            serverHasShulkers = true;
        } catch (ClassNotFoundException e) {
            serverHasShulkers = false;
        }

        logger.info("Loading config");
        reloadPlugin();
        logger.info("Done.");
    }

    public void reloadPlugin() {
        try {
            config = new FrameConfig();
            FrameModule.reloadModules();
            config.saveConfig();
        } catch (Exception e) {
            logger.severe("Error loading config! - " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static FrameDupe getInstance() {
        return instance;
    }
    public static FoliaLib getFoliaLib() {
        return new FoliaLib(instance);
    }
    public static FrameConfig getConfiguration() {
        return config;
    }
    public static Logger getPrefixedLogger() {
        return logger;
    }
}