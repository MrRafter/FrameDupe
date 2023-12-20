package me.MrRafter.framedupe;

import com.tcoded.folialib.FoliaLib;
import me.MrRafter.framedupe.commands.FrameDupeCommand;
import me.MrRafter.framedupe.modules.FrameDupeModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class FrameDupe extends JavaPlugin {

    private static FrameDupe instance;
    private static FrameConfig config;
    private static Logger logger;

    @Override
    public void onEnable() {
        logger = getLogger();
        instance = this;

        // Check if plugin can be enabled in the first place
        try {
            Class.forName("org.bukkit.entity.ItemFrame");
        } catch (ClassNotFoundException e) {
            logger.severe("Your server does not have item frames. Plugin cannot enable.");
            getServer().getPluginManager().disablePlugin(instance);
            return;
        }

        // Enable
        logger.info("                         ");
        logger.info("           /*\\           ");
        logger.info("      ┏╍╍╍╍╍╍╍╍╍╍╍┓      ");
        logger.info("      ┋           ┋      ");
        logger.info("      ┋           ┋      ");
        logger.info("      ┋           ┋      ");
        logger.info("      ┗╍╍╍╍╍╍╍╍╍╍╍┛      ");
        logger.info("        FrameDupe        ");
        logger.info("                         ");
        logger.info("Loading config");
        reloadConfiguration();
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

    public void reloadConfiguration() {
        try {
            config = new FrameConfig();
            FrameDupeModule.reloadModules();
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