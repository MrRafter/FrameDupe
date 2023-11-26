package me.MrRafter.framedupe.modules;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.MrRafter.framedupe.FrameConfig;
import me.MrRafter.framedupe.FrameDupe;
import me.MrRafter.framedupe.ShulkerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class GlowFrames implements FrameModule, Listener {

    private final ServerImplementation scheduler;
    private final HashSet<Material> blacklist = new HashSet<>(); // HashSets are really fast
    private final HashSet<Material> whitelist = new HashSet<>();
    private EntityType glowItemFrame;
    private final double probability;
    private final boolean isFolia, blacklistEnabled, blacklistCheckShulkers, whitelistEnabled, whitelistCheckShulkers;

    public GlowFrames() {
        shouldEnable(); // make enable option appear on top
        FoliaLib foliaLib = FrameDupe.getFoliaLib();
        this.isFolia = foliaLib.isFolia();
        this.scheduler = isFolia ? foliaLib.getImpl() : null;
        try { this.glowItemFrame = EntityType.valueOf("GLOW_ITEM_FRAME"); } catch (IllegalArgumentException ignored) {}
        FrameConfig config = FrameDupe.getConfiguration();
        this.probability = config.getDouble("GLOW_FrameDupe.Probability-percentage", 50.0,
                "Value has to be greater than 0. Recommended to not set to 100% unless you want players to flood the server with items.");
        if (probability <= 0)
            FrameDupe.getPrefixedLogger().warning("Probability percentage needs to be a value greater than 0. Glow frame dupe will not enable.");
        this.blacklistEnabled = config.getBoolean("GLOW_FrameDupe.Blacklist.Enabled", false,
                "If enabled, all items in this list will not be duplicated.");
        this.blacklistCheckShulkers = config.getBoolean("GLOW_FrameDupe.Blacklist.Check-shulkers", FrameDupe.serverHasShulkers,
                "Whether to check inside shulkers for blacklisted items.");
        config.getList("GLOW_FrameDupe.Blacklist.Items", Collections.singletonList("DRAGON_EGG")).forEach(configuredItem -> {
            try {
                Material material = Material.valueOf(configuredItem);
                this.blacklist.add(material);
            } catch (IllegalArgumentException e) {
                FrameDupe.getPrefixedLogger().warning("Configured item '"+configuredItem+"' was not recognized. " +
                        "Please use the correct Material enums for your server version.");
            }
        });
        this.whitelistEnabled = config.getBoolean("GLOW_FrameDupe.Whitelist.Enabled", false,
                "If enabled, only items in this list can be duped.");
        this.whitelistCheckShulkers = config.getBoolean("GLOW_FrameDupe.Whitelist.Check-shulkers", FrameDupe.serverHasShulkers);
        config.getList("FrameDupe.Whitelist.Items", Collections.singletonList("DIAMOND")).forEach(configuredItem -> {
            try {
                Material material = Material.valueOf(configuredItem);
                this.whitelist.add(material);
            } catch (IllegalArgumentException e) {
                FrameDupe.getPrefixedLogger().warning("Configured item '"+configuredItem+"' was not recognized. " +
                        "Please use the correct Material enums for your server version.");
            }
        });
    }

    @Override
    public boolean shouldEnable() {
        return FrameDupe.getConfiguration().getBoolean("GLOW_FrameDupe.Enabled", FrameDupe.serverHasGlowItemFrames)
                && probability > 0 && FrameDupe.serverHasGlowItemFrames;
    }

    @Override
    public void enable() {
        FrameDupe plugin = FrameDupe.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onHangingBreak(HangingBreakEvent event) {
        final Hanging hanging = event.getEntity();
        if (!hanging.getType().equals(glowItemFrame)) return;
        if (probability < 100 && new Random().nextDouble() > probability) return;

        final ItemStack frameItem = ((ItemFrame) hanging).getItem();
        // Don't do anything if the frame has no item inside
        if (frameItem == null || frameItem.getType().equals(Material.AIR)) return;

        if (blacklistEnabled) {
            if (blacklist.contains(frameItem.getType())) return;
            if (blacklistCheckShulkers && FrameDupe.serverHasShulkers && ShulkerUtil.isShulkerBox(frameItem)) {
                for (ItemStack shulkerItem : ShulkerUtil.getContents(frameItem)) {
                    if (blacklist.contains(shulkerItem.getType())) return;
                }
            }
        }

        if (whitelistEnabled) {
            if (!whitelist.contains(frameItem.getType())) return;
            if (whitelistCheckShulkers && FrameDupe.serverHasShulkers && ShulkerUtil.isShulkerBox(frameItem)) {
                for (ItemStack shulkerItem : ShulkerUtil.getContents(frameItem)) {
                    if (!whitelist.contains(shulkerItem.getType())) return;
                }
            }
        }

        if (!isFolia) {
            hanging.getWorld().dropItemNaturally(hanging.getLocation(), frameItem);
        } else {
            final Location dropLoc = hanging.getLocation();
            scheduler.runAtLocation(dropLoc, dropAdditional -> hanging.getWorld().dropItemNaturally(dropLoc, frameItem));
        }
    }
}