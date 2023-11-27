package me.MrRafter.framedupe;

import com.tcoded.folialib.FoliaLib;
import me.MrRafter.framedupe.commands.FrameDupeCommand;
import me.MrRafter.framedupe.modules.FrameModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class FrameDupe extends JavaPlugin {

    private static FrameDupe instance;
    private static FrameConfig config;
    private static Logger logger;

    public void onEnable() {
        // Check if plugin can be enabled in the first place
        try {
            Class.forName("org.bukkit.entity.ItemFrame");
        } catch (ClassNotFoundException e) {
            logger.severe("Your server version does not have item frames. Plugin cannot enable.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // Enable
        instance = this;
        logger = getLogger();
        logger.info("Loading config");
        reloadPlugin();
        logger.info("Registering commands");
        getCommand("framedupe").setExecutor(new FrameDupeCommand());
        logger.info("Loading Metrics");
        new Metrics(this, 17434);
        logger.info("Done.");
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

    public static boolean serverHasShulkers() {
        try {
            Class.forName("org.bukkit.block.ShulkerBox");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean serverHasGlowItemFrames() {
        try {
            Class.forName("org.bukkit.entity.GlowItemFrame");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean serverHasBundles() {
        try {
            Class.forName("org.bukkit.inventory.meta.BundleMeta");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}