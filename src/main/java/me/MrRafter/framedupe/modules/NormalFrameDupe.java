package me.MrRafter.framedupe.modules;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.MrRafter.framedupe.DupeConfig;
import me.MrRafter.framedupe.FrameDupe;
import me.MrRafter.framedupe.enums.Permissions;
import me.MrRafter.framedupe.utils.ItemUtil;
import me.MrRafter.framedupe.utils.ExpiringSet;
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
import java.util.stream.Collectors;

public class NormalFrameDupe implements FrameDupeModule, Listener {

    private final ServerImplementation scheduler;
    private final ExpiringSet<UUID> dupersOnCooldown;
    private final Set<Material> blacklist, whitelist;
    private final double probability;
    private final boolean isFolia, cooldownEnabled,
            blacklistEnabled, blacklistCheckInsideItems,
            whitelistEnabled, whitelistCheckInsideItems;

    protected NormalFrameDupe() {
        shouldEnable(); // make enable option appear on top
        final FoliaLib foliaLib = FrameDupe.getFoliaLib();
        this.isFolia = foliaLib.isFolia();
        this.scheduler = isFolia ? foliaLib.getImpl() : null;
        DupeConfig config = FrameDupe.getConfiguration();
        config.master().addSection("FrameDupe", "Item Frame Dupe");
        config.master().addComment("FrameDupe.Enabled", "Enable duping by removing items from normal item frames.");
        this.probability = config.getDouble("FrameDupe.Probability-Percentage", 50.0,
                "50.0 = 50%. Has to be greater than 0. Recommended not to set to 100% unless\n" +
                        "you are okay with players gaining items very quickly (May also increase lag for low spec clients).") / 100;
        if (probability <= 0) FrameDupe.getPrefixedLogger().warning("Probability percentage is 0 or lower. Frame dupe will not enable.");
        this.cooldownEnabled = config.getBoolean("FrameDupe.Cooldown.Enabled", true,
                "Prevent abuse by players using automation mods.");
        this.dupersOnCooldown = cooldownEnabled ? new ExpiringSet<>(Duration.ofMillis(
                Math.max(config.getInt("FrameDupe.Cooldown.Ticks", 15, "1 sec = 20 ticks"), 1) * 50L
        )) : null;
        this.blacklistEnabled = config.getBoolean("FrameDupe.Blacklist.Enabled", false,
                "If enabled, all items in this list will not be duplicated.");
        this.blacklistCheckInsideItems = config.getBoolean("FrameDupe.Blacklist.Check-Inside-Items", true);
        this.blacklist = config.getList("FrameDupe.Blacklist.Items", Collections.singletonList("DRAGON_EGG"),
                "Please use correct Spigot Material values for your minecraft version.")
                .stream()
                .map(configuredItem -> {
                    try {
                        return Material.valueOf(configuredItem);
                    } catch (IllegalArgumentException e) {
                        FrameDupe.getPrefixedLogger().warning("Configured item '"+configuredItem+"' was not recognized. " +
                                "Please use the correct Material enums for your server version.");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
        this.whitelistEnabled = config.getBoolean("FrameDupe.Whitelist.Enabled", false,
                "If enabled, only items in this list can be duped.");
        this.whitelistCheckInsideItems = config.getBoolean("FrameDupe.Whitelist.Check-Inside-Items", true);
        this.whitelist = config.getList("FrameDupe.Whitelist.Items", Collections.singletonList("DIAMOND"))
                .stream()
                .map(configuredItem -> {
                    try {
                        return Material.valueOf(configuredItem);
                    } catch (IllegalArgumentException e) {
                        FrameDupe.getPrefixedLogger().warning("Configured item '"+configuredItem+"' was not recognized. " +
                                "Please use the correct Material enums for your server version.");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
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
                || FrameDupe.getRandom().nextDouble() <= probability
                || (isPlayer && damager.hasPermission(Permissions.BYPASS_CHANCE.get()))
        ) {
            final ItemFrame itemFrame = (ItemFrame) damaged;
            final ItemStack itemInItemFrame = itemFrame.getItem();
            // Don't do anything if the frame has no item inside
            if (itemInItemFrame == null || itemInItemFrame.getType().equals(Material.AIR)) return;

            if (cooldownEnabled) {
                if (dupersOnCooldown.contains(damager.getUniqueId())) return;
                if (!isPlayer || !damager.hasPermission(Permissions.BYPASS_COOLDOWN.get()))
                    dupersOnCooldown.add(damager.getUniqueId());
            }

            if (blacklistEnabled && (!isPlayer || !damager.hasPermission(Permissions.BYPASS_BLACKLIST.get()))) {
                if (blacklist.contains(itemInItemFrame.getType())) return;
                if (blacklistCheckInsideItems) {
                    Iterable<ItemStack> storedItems = ItemUtil.getStoredItems(itemInItemFrame);
                    if (storedItems == null) return;
                    for (ItemStack shulkerItem : storedItems) {
                        if (shulkerItem == null) continue;
                        if (blacklist.contains(shulkerItem.getType())) return;
                        Iterable<ItemStack> nested = ItemUtil.getStoredItems(itemInItemFrame);
                        if (nested == null) continue;
                        for (ItemStack nestedItem : nested) {
                            if (nestedItem != null && blacklist.contains(nestedItem.getType())) return;
                        }
                    }
                }
            }

            if (whitelistEnabled && (!isPlayer || !damager.hasPermission(Permissions.BYPASS_WHITELIST.get()))) {
                if (!whitelist.contains(itemInItemFrame.getType())) return;
                if (whitelistCheckInsideItems) {
                    Iterable<ItemStack> storedItems = ItemUtil.getStoredItems(itemInItemFrame);
                    if (storedItems == null) return;
                    for (ItemStack shulkerItem : storedItems) {
                        if (shulkerItem == null) continue;
                        if (!whitelist.contains(shulkerItem.getType())) return;
                        Iterable<ItemStack> nested = ItemUtil.getStoredItems(itemInItemFrame);
                        if (nested == null) continue;
                        for (ItemStack nestedItem : nested) {
                            if (nestedItem != null && !whitelist.contains(nestedItem.getType())) return;
                        }
                    }
                }
            }

            // Adjust drop location so the item doesn't glitch into the block behind the frame
            Location dropLoc = itemFrame.getLocation().getBlock().getRelative(itemFrame.getFacing()).getLocation().clone();
            dropLoc.setX(dropLoc.getBlockX() + 0.5);
            dropLoc.setY(dropLoc.getBlockY() + 0.5);
            dropLoc.setZ(dropLoc.getBlockZ() + 0.5);

            if (!isFolia) {
                itemFrame.getWorld().dropItemNaturally(dropLoc, itemInItemFrame);
            } else {
                scheduler.runAtEntity(itemFrame, dropAdditional -> itemFrame.getWorld().dropItemNaturally(dropLoc, itemInItemFrame));
            }
        }
    }
}