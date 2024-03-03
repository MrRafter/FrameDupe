package me.MrRafter.framedupe;

import com.tcoded.folialib.FoliaLib;
import me.MrRafter.framedupe.commands.FrameDupeCommand;
import me.MrRafter.framedupe.modules.FrameDupeModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.logging.Logger;

public final class FrameDupe extends JavaPlugin {

    private static FrameDupe instance;
    private static DupeConfig config;
    private static Logger logger;
    private static Random random;
    private static Metrics metrics;

    @Override
    public void onEnable() {
        logger = getLogger();
        instance = this;
        random = new Random();

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
        logger.info("      ┏━━━━━━━━━━━┓      ");
        logger.info("      ┃           ┃      ");
        logger.info("      ┃           ┃      ");
        logger.info("      ┃           ┃      ");
        logger.info("      ┗━━━━━━━━━━━┛      ");
        logger.info("        FrameDupe        ");
        logger.info("                         ");

        logger.info("Loading Config");
        reloadConfiguration();
        logger.info("Registering Commands");
        getCommand("framedupe").setExecutor(new FrameDupeCommand());
        logger.info("Loading Metrics");
        metrics = new Metrics(this, 17434);
        logger.info("Done.");
    }

    @Override
    public void onDisable() {
        FrameDupeModule.modules.forEach(FrameDupeModule::disable);
        FrameDupeModule.modules.clear();
        if (metrics != null) {
            metrics.shutdown();
            metrics = null;
        }
        instance = null;
        logger = null;
        random = null;
    }

    public static FrameDupe getInstance() {
        return instance;
    }
    public static FoliaLib getFoliaLib() {
        return new FoliaLib(instance);
    }
    public static DupeConfig getConfiguration() {
        return config;
    }
    public static Random getRandom() {
        return random;
    }
    public static Logger getPrefixedLogger() {
        return logger;
    }

    public void reloadConfiguration() {
        try {
            config = new DupeConfig();
            FrameDupeModule.reloadModules();
            config.saveConfig();
        } catch (Exception e) {
            logger.severe("Error loading config! - " + e.getLocalizedMessage());
            logger.throwing("DupeConfig", "reloadConfiguration", e);
        }
    }
}