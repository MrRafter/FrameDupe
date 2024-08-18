package me.xginko.framedupe;

import me.xginko.framedupe.commands.FrameDupeCommand;
import me.xginko.framedupe.enums.PluginPermission;
import me.xginko.framedupe.modules.FrameDupeModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.morepaperlib.scheduling.GracefulScheduling;

import java.util.Random;
import java.util.logging.Logger;

public final class FrameDupe extends JavaPlugin {

    private static FrameDupe instance;
    private static GracefulScheduling scheduling;
    private static DupeConfig config;
    private static Logger logger;
    private static Random random;
    private static Metrics metrics;

    @Override
    public void onEnable() {
        // Check if plugin can be enabled in the first place
        if (!hasClass("org.bukkit.entity.ItemFrame")) {
            logger.severe("Your server does not have item frames. Plugin cannot enable.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        instance = this;
        logger = getLogger();
        random = new Random();
        scheduling = new MorePaperLib(this).scheduling();
        metrics = new Metrics(this, 17434);

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

        logger.info("Registering Permissions");
        PluginPermission.registerAll();

        logger.info("Registering Command");
        getCommand("framedupe").setExecutor(new FrameDupeCommand());

        logger.info("Done.");
    }

    @Override
    public void onDisable() {
        FrameDupeModule.MODULES.forEach(FrameDupeModule::disable);
        FrameDupeModule.MODULES.clear();
        if (scheduling != null) {
            scheduling.cancelGlobalTasks();
            scheduling = null;
        }
        if (metrics != null) {
            metrics.shutdown();
            metrics = null;
        }
        random = null;
        logger = null;
        instance = null;
    }

    public static FrameDupe getInstance() {
        return instance;
    }

    public static GracefulScheduling scheduling() {
        return scheduling;
    }

    public static DupeConfig config() {
        return config;
    }

    public static Random random() {
        return random;
    }

    public static Logger logger() {
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

    public static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}