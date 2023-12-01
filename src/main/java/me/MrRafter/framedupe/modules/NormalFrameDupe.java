package me.MrRafter.framedupe.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.MrRafter.framedupe.FrameConfig;
import me.MrRafter.framedupe.FrameDupe;
import me.MrRafter.framedupe.enums.Permissions;
import me.MrRafter.framedupe.utils.BundleUtil;
import me.MrRafter.framedupe.utils.ShulkerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.*;

public class NormalFrameDupe implements FrameDupeModule, Listener {

    private final ServerImplementation scheduler;
    private final Cache<UUID, Boolean> dupersOnCooldown;
    private final Set<Material> blacklist, whitelist;
    private final double probability;
    private final boolean isFolia, blacklistEnabled, blacklistCheckShulkers, blacklistCheckBundles,
            whitelistEnabled, whitelistCheckShulkers, whitelistCheckBundles, cooldownEnabled;

    protected NormalFrameDupe() {
        shouldEnable(); // make enable option appear on top
        final FoliaLib foliaLib = FrameDupe.getFoliaLib();
        this.isFolia = foliaLib.isFolia();
        this.scheduler = isFolia ? foliaLib.getImpl() : null;
        FrameConfig config = FrameDupe.getConfiguration();
        config.master().addSection("FrameDupe", "Item Frame Dupe");
        config.master().addComment("FrameDupe.Enabled", "Enable duping by removing items from normal item frames.");
        this.probability = config.getDouble("FrameDupe.Probability-Percentage", 50.0,
                "50.0 = 50%. Has to be greater than 0. Recommended not to set to 100% unless\n" +
                        "you are okay with players gaining items very quickly (May also increase lag for low spec clients).") / 100;
        if (probability <= 0) FrameDupe.getPrefixedLogger().warning("Probability percentage is 0 or lower. Not enabling frame dupe.");
        this.cooldownEnabled = config.getBoolean("FrameDupe.Cooldown.Enabled", true,
                "Prevent abuse by players using automation mods.");
        final long cooldownMillis = config.getInt("FrameDupe.Cooldown.Ticks", 15,
                "1 sec = 20 ticks") * 50L;
        this.dupersOnCooldown = cooldownEnabled ? Caffeine.newBuilder().expireAfterWrite(Duration.ofMillis(cooldownMillis)).build() : null;
        this.blacklistEnabled = config.getBoolean("FrameDupe.Blacklist.Enabled", false,
                "If enabled, all items in this list will not be duplicated.");
        this.blacklistCheckShulkers = config.getBoolean("FrameDupe.Blacklist.Check-Shulkers", true,
                "Whether to check inside shulkers for blacklisted items.") && FrameDupe.serverHasShulkers();
        this.blacklistCheckBundles = config.getBoolean("FrameDupe.Blacklist.Check-Bundles", true,
                "Whether to check inside bundles for blacklisted items.") && FrameDupe.serverHasBundles();
        final List<String> configuredBlacklist = config.getList("FrameDupe.Blacklist.Items", Collections.singletonList("DRAGON_EGG"),
                "Please use correct Spigot Material values for your minecraft version.");
        this.blacklist = new HashSet<>(configuredBlacklist.size());
        configuredBlacklist.forEach(configuredItem -> {
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
        this.whitelistCheckShulkers = config.getBoolean("FrameDupe.Whitelist.Check-Shulkers", true,
                "Whether to check inside shulkers for whitelisted items.") && FrameDupe.serverHasShulkers();
        this.whitelistCheckBundles = config.getBoolean("FrameDupe.Whitelist.Check-Bundles", true,
                "Whether to check inside bundles for whitelisted items.") && FrameDupe.serverHasBundles();
        final List<String> configuredWhitelist = config.getList("FrameDupe.Whitelist.Items", Collections.singletonList("DIAMOND"));
        this.whitelist = new HashSet<>(configuredWhitelist.size());
        configuredWhitelist.forEach(configuredItem -> {
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
        final Entity damaged = event.getEntity();
        if (damaged == null || !damaged.getType().equals(EntityType.ITEM_FRAME)) return;
        final Entity damager = event.getDamager();
        if (damager == null) return;

        // Check once so we can reuse that info
        final boolean isPlayer = damager.getType().equals(EntityType.PLAYER);

        if (
                probability >= 100
                || new Random().nextDouble() <= probability
                || (isPlayer && damager.hasPermission(Permissions.BYPASS_CHANCE.get()))
        ) {
            final ItemFrame itemFrame = (ItemFrame) damaged;
            final ItemStack frameItem = itemFrame.getItem();
            // Don't do anything if the frame has no item inside
            if (frameItem == null || frameItem.getType().equals(Material.AIR)) return;

            if (cooldownEnabled) {
                final UUID duper = damager.getUniqueId();
                if (dupersOnCooldown.getIfPresent(duper) != null) return;
                if (!isPlayer || !damager.hasPermission(Permissions.BYPASS_COOLDOWN.get())) {
                    dupersOnCooldown.put(duper, true);
                }
            }

            if (blacklistEnabled && (!isPlayer || !damager.hasPermission(Permissions.BYPASS_BLACKLIST.get()))) {
                if (blacklist.contains(frameItem.getType())) return;
                if (blacklistCheckBundles && BundleUtil.isNonEmptyBundle(frameItem)) {
                    for (ItemStack bundleItem : BundleUtil.getBundleItems(frameItem)) {
                        if (bundleItem != null && blacklist.contains(bundleItem.getType())) return;
                    }
                }
                if (blacklistCheckShulkers && ShulkerUtil.isShulker(frameItem)) {
                    for (ItemStack shulkerItem : ShulkerUtil.getShulkerInventory(frameItem)) {
                        if (shulkerItem == null) continue;
                        if (blacklist.contains(shulkerItem.getType())) return;
                        if (blacklistCheckBundles && BundleUtil.isNonEmptyBundle(shulkerItem)) {
                            for (ItemStack bundleItem : BundleUtil.getBundleItems(shulkerItem)) {
                                if (bundleItem != null && blacklist.contains(bundleItem.getType())) return;
                            }
                        }
                    }
                }
            }

            if (whitelistEnabled && (!isPlayer || !damager.hasPermission(Permissions.BYPASS_WHITELIST.get()))) {
                if (!whitelist.contains(frameItem.getType())) return;
                if (whitelistCheckBundles && BundleUtil.isNonEmptyBundle(frameItem)) {
                    for (ItemStack bundleItem : BundleUtil.getBundleItems(frameItem)) {
                        if (bundleItem != null && !whitelist.contains(bundleItem.getType())) return;
                    }
                }
                if (whitelistCheckShulkers && ShulkerUtil.isShulker(frameItem)) {
                    for (ItemStack shulkerItem : ShulkerUtil.getShulkerInventory(frameItem)) {
                        if (shulkerItem == null) continue;
                        if (!whitelist.contains(shulkerItem.getType())) return;
                        if (whitelistCheckBundles && BundleUtil.isNonEmptyBundle(shulkerItem)) {
                            for (ItemStack bundleItem : BundleUtil.getBundleItems(shulkerItem)) {
                                if (bundleItem != null && !whitelist.contains(bundleItem.getType())) return;
                            }
                        }
                    }
                }
            }

            // Adjust drop location so the item doesn't glitch into the block behind the frame
            Location dropLoc = itemFrame.getLocation().getBlock().getRelative(itemFrame.getFacing()).getLocation().clone();
            dropLoc.setX(dropLoc.getX() + 0.5);
            dropLoc.setY(dropLoc.getY() - 0.5);
            dropLoc.setZ(dropLoc.getZ() + 0.5);

            if (!isFolia) {
                itemFrame.getWorld().dropItemNaturally(dropLoc, frameItem.clone()).setPickupDelay(0);
            } else {
                scheduler.runAtEntity(itemFrame,
                        dropAdditional -> itemFrame.getWorld().dropItemNaturally(dropLoc, frameItem.clone()).setPickupDelay(0));
            }
        }
    }
}