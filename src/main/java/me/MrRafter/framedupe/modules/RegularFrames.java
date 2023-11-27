package me.MrRafter.framedupe.modules;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.MrRafter.framedupe.FrameConfig;
import me.MrRafter.framedupe.FrameDupe;
import me.MrRafter.framedupe.utils.BundleUtil;
import me.MrRafter.framedupe.utils.ShulkerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class RegularFrames implements FrameModule, Listener {

    private final ServerImplementation scheduler;
    private final HashSet<Material> blacklist = new HashSet<>();
    private final HashSet<Material> whitelist = new HashSet<>();
    private final double probability;
    private final boolean isFolia, blacklistEnabled, blacklistCheckShulkers, blacklistCheckBundles,
            whitelistEnabled, whitelistCheckShulkers, whitelistCheckBundles;

    public RegularFrames() {
        shouldEnable(); // make enable option appear on top
        FoliaLib foliaLib = FrameDupe.getFoliaLib();
        this.isFolia = foliaLib.isFolia();
        this.scheduler = isFolia ? foliaLib.getImpl() : null;
        FrameConfig config = FrameDupe.getConfiguration();
        this.probability = config.getDouble("FrameDupe.Probability-Percentage", 50.0,
                "Value has to be greater than 0. Recommended not to set to 100% unless\n" +
                        "you are okay with players flooding the server with items.") / 100;
        if (probability <= 0)
            FrameDupe.getPrefixedLogger().warning("Probability percentage needs to be a value greater than 0. Regular frame dupe will not enable.");
        this.blacklistEnabled = config.getBoolean("FrameDupe.Blacklist.Enabled", false,
                "If enabled, all items in this list will not be duplicated.");
        this.blacklistCheckShulkers = config.getBoolean("FrameDupe.Blacklist.Check-Shulkers", FrameDupe.serverHasShulkers(),
                "Whether to check inside shulkers for blacklisted items.") && FrameDupe.serverHasShulkers();
        this.blacklistCheckBundles = config.getBoolean("FrameDupe.Blacklist.Check-Bundles", FrameDupe.serverHasBundles(),
                "Whether to check inside bundles for blacklisted items.") && FrameDupe.serverHasBundles();
        config.getList("FrameDupe.Blacklist.Items", Collections.singletonList("DRAGON_EGG")).forEach(configuredItem -> {
            try {
                Material material = Material.valueOf(configuredItem);
                this.blacklist.add(material);
            } catch (IllegalArgumentException e) {
                FrameDupe.getPrefixedLogger().warning("Configured item '"+configuredItem+"' was not recognized. " +
                        "Please use the correct Material enums for your server version.");
            }
        });
        this.whitelistEnabled = config.getBoolean("FrameDupe.Whitelist.Enabled", false,
                "If enabled, only items in this list can be duped.");
        this.whitelistCheckShulkers = config.getBoolean("FrameDupe.Whitelist.Check-Shulkers", FrameDupe.serverHasShulkers(),
                "Whether to check inside shulkers for whitelisted items.") && FrameDupe.serverHasShulkers();
        this.whitelistCheckBundles = config.getBoolean("FrameDupe.Whitelist.Check-Bundles", FrameDupe.serverHasBundles(),
                "Whether to check inside bundles for whitelisted items.") && FrameDupe.serverHasBundles();
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
        return FrameDupe.getConfiguration().getBoolean("FrameDupe.Enabled", true) && probability > 0;
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
    private void onFramePunch(EntityDamageByEntityEvent event) {
        final Entity punched = event.getEntity();
        if (!punched.getType().equals(EntityType.ITEM_FRAME)) return;
        if (probability >= 100 || new Random().nextDouble() <= probability) {
            performFrameDupe(((ItemFrame) punched));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onFrameBreak(HangingBreakEvent event) {
        final Hanging hanging = event.getEntity();
        if (!hanging.getType().equals(EntityType.ITEM_FRAME)) return;
        if (probability >= 100 || new Random().nextDouble() <= probability) {
            performFrameDupe(((ItemFrame) hanging));
        }
    }

    private void performFrameDupe(final ItemFrame itemFrame) {
        final ItemStack frameItem = itemFrame.getItem();
        // Don't do anything if the frame has no item inside
        if (frameItem == null || frameItem.getType().equals(Material.AIR)) return;

        if (blacklistEnabled) {
            if (blacklist.contains(frameItem.getType())) return;
            if (blacklistCheckShulkers && ShulkerUtil.isNonEmptyShulker(frameItem)) {
                for (ItemStack shulkerItem : ShulkerUtil.getShulkerInventory(frameItem)) {
                    if (blacklist.contains(shulkerItem.getType())) return;
                }
            }
            if (blacklistCheckBundles && BundleUtil.isNonEmptyBundle(frameItem)) {
                for (ItemStack bundleItem : BundleUtil.getBundleItems(frameItem)) {
                    if (blacklist.contains(bundleItem.getType())) return;
                }
            }
        }

        if (whitelistEnabled) {
            if (!whitelist.contains(frameItem.getType())) return;
            if (whitelistCheckShulkers && ShulkerUtil.isNonEmptyShulker(frameItem)) {
                for (ItemStack shulkerItem : ShulkerUtil.getShulkerInventory(frameItem)) {
                    if (!whitelist.contains(shulkerItem.getType())) return;
                }
            }
            if (whitelistCheckBundles && BundleUtil.isNonEmptyBundle(frameItem)) {
                for (ItemStack bundleItem : BundleUtil.getBundleItems(frameItem)) {
                    if (!whitelist.contains(bundleItem.getType())) return;
                }
            }
        }

        if (!isFolia) {
            itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), frameItem.clone());
        } else {
            scheduler.runAtEntity(itemFrame,
                    dropAdditional -> itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), frameItem.clone()));
        }
    }
}