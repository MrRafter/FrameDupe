package me.xginko.framedupe.modules;

import me.xginko.framedupe.DupeConfig;
import me.xginko.framedupe.FrameDupe;
import me.xginko.framedupe.enums.PluginPermission;
import me.xginko.framedupe.utils.ExpiringSet;
import me.xginko.framedupe.utils.ItemUtil;
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
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class NormalFrameDupe extends FrameDupeModule implements Listener {

    private final ExpiringSet<UUID> dupersOnCooldown;
    private final Set<Material> blacklist, whitelist;
    private final double probability;
    private final boolean cooldownEnabled, blacklistEnabled, blacklistCheckInsideItems, whitelistEnabled,
            whitelistCheckInsideItems;

    protected NormalFrameDupe() {
        shouldEnable(); // make enable option appear on top
        DupeConfig config = FrameDupe.config();
        config.master().addSection("FrameDupe", "Item Frame Dupe");
        config.master().addComment("FrameDupe.Enabled", "Enable duping by removing items from normal item frames.");
        this.probability = config.getDouble("FrameDupe.Probability-Percentage", 50.0,
                "50.0 = 50%. Has to be greater than 0. Recommended not to set to 100% unless\n" +
                        "you are okay with players gaining items very quickly (May also increase lag for low spec clients).") / 100;
        if (probability <= 0)
            FrameDupe.logger().warning("Probability percentage is 0 or lower. Frame dupe will not enable.");
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
                        FrameDupe.logger().warning("Configured item '"+configuredItem+"' was not recognized. " +
                                "Please use the correct Material enums for your server version.");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Material.class)));
        this.whitelistEnabled = config.getBoolean("FrameDupe.Whitelist.Enabled", false,
                "If enabled, only items in this list can be duped.");
        this.whitelistCheckInsideItems = config.getBoolean("FrameDupe.Whitelist.Check-Inside-Items", true);
        this.whitelist = config.getList("FrameDupe.Whitelist.Items", Collections.singletonList("DIAMOND"))
                .stream()
                .map(configuredItem -> {
                    try {
                        return Material.valueOf(configuredItem);
                    } catch (IllegalArgumentException e) {
                        FrameDupe.logger().warning("Configured item '"+configuredItem+"' was not recognized. " +
                                "Please use the correct Material enums for your server version.");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Material.class)));
    }

    @Override
    public boolean shouldEnable() {
        return FrameDupe.config().getBoolean("FrameDupe.Enabled", true) && probability > 0;
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onFramePunch(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        if (damaged == null || damaged.getType() != EntityType.ITEM_FRAME) return;
        Entity damager = event.getDamager();
        if (damager == null) return;

        // Check once so we can reuse that info
        boolean isPlayer = damager.getType().equals(EntityType.PLAYER);

        if (
                probability >= 100
                || FrameDupe.random().nextDouble() <= probability
                || (isPlayer && damager.hasPermission(PluginPermission.BYPASS_CHANCE.get()))
        ) {
            ItemStack itemInItemFrame = ((ItemFrame) damaged).getItem();
            // Don't do anything if the frame has no item inside
            if (itemInItemFrame == null || itemInItemFrame.getType() == Material.AIR) return;

            if (cooldownEnabled) {
                if (dupersOnCooldown.contains(damager.getUniqueId())) return;
                if (!isPlayer || !damager.hasPermission(PluginPermission.BYPASS_COOLDOWN.get()))
                    dupersOnCooldown.add(damager.getUniqueId());
            }

            if (blacklistEnabled && (!isPlayer || !damager.hasPermission(PluginPermission.BYPASS_BLACKLIST.get()))) {
                if (blacklist.contains(itemInItemFrame.getType())) return;
                if (blacklistCheckInsideItems && ItemUtil.containsAny(itemInItemFrame, blacklist)) return;
            }

            if (whitelistEnabled && (!isPlayer || !damager.hasPermission(PluginPermission.BYPASS_WHITELIST.get()))) {
                if (!whitelist.contains(itemInItemFrame.getType())) return;
                if (whitelistCheckInsideItems && !ItemUtil.containsAny(itemInItemFrame, whitelist)) return;
            }

            FrameDupe.scheduling().entitySpecificScheduler(damaged).run(() ->
                    damaged.getWorld().dropItem(ItemUtil.toCenterLocation(damaged.getLocation()), itemInItemFrame), null);
        }
    }
}