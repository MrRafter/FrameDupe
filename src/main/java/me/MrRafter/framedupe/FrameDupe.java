package me.MrRafter.framedupe;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import com.tcoded.folialib.FoliaLib;
import me.MrRafter.framedupe.commands.FrameDupeCommand;
import me.MrRafter.framedupe.modules.FrameDupeModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class FrameDupe extends JavaPlugin {

    private static FrameDupe instance;
    private static FrameConfig config;
    private static Logger logger;
    public static Set<Material> SHULKER_BOXES;
    public static Material BUNDLE;

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
        logger.info("      ┏━━━━━━━━━━━┓      ");
        logger.info("      ┃           ┃      ");
        logger.info("      ┃           ┃      ");
        logger.info("      ┃           ┃      ");
        logger.info("      ┗━━━━━━━━━━━┛      ");
        logger.info("        FrameDupe        ");
        logger.info("                         ");
        // Cache ShulkerBox Materials
        SHULKER_BOXES = XTag.SHULKER_BOXES.getValues()
                .stream()
                .filter(XMaterial::isSupported)
                .map(XMaterial::parseMaterial)
                .collect(Collectors.toSet());
        // Cache Bundle Material
        BUNDLE = XMaterial.BUNDLE.parseMaterial();

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
        return XMaterial.SHULKER_BOX.isSupported();
    }

    public static boolean serverHasGlowItemFrames() {
        return XMaterial.GLOW_ITEM_FRAME.isSupported();
    }

    public static boolean serverHasBundles() {
        return XMaterial.BUNDLE.isSupported();
    }
}