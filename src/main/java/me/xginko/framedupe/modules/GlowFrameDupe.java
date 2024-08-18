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

public class GlowFrameDupe extends FrameDupeModule implements Listener {

    private final ExpiringSet<UUID> dupersOnCooldown;
    private final Set<Material> blacklist, whitelist;
    private EntityType GLOW_ITEM_FRAME;
    private final double probability;
    private final boolean cooldownEnabled, blacklistEnabled, blacklistCheckInsideItems, whitelistEnabled,
            whitelistCheckInsideItems;

    protected GlowFrameDupe() {
        shouldEnable(); // make enable option appear on top
        try { this.GLOW_ITEM_FRAME = EntityType.valueOf("GLOW_ITEM_FRAME"); } catch (IllegalArgumentException ignored) {}
        DupeConfig config = FrameDupe.config();
        config.master().addSection("GLOW_FrameDupe", "Glow Frame Dupe");
        config.master().addComment("GLOW_FrameDupe.Enabled",
                "Enable duping with glow item frames. (Will only enable if your game has them)");
        this.probability = config.getDouble("GLOW_FrameDupe.Probability-Percentage", 50.0,
                "50.0 = 50%. Has to be greater than 0.") / 100;
        if (probability <= 0)
            FrameDupe.logger().warning("Probability percentage is 0 or lower. Not enabling glow frame dupe.");
        this.cooldownEnabled = config.getBoolean("GLOW_FrameDupe.Cooldown.Enabled", true);
        this.dupersOnCooldown = cooldownEnabled ? new ExpiringSet<>(Duration.ofMillis(
                Math.max(config.getInt("GLOW_FrameDupe.Cooldown.Ticks", 15, "1 sec = 20 ticks"), 1) * 50L
        )) : null;
        this.blacklistEnabled = config.getBoolean("GLOW_FrameDupe.Blacklist.Enabled", false);
        this.blacklistCheckInsideItems = config.getBoolean("GLOW_FrameDupe.Blacklist.Check-Inside-Items", true);
        this.blacklist = config.getList("GLOW_FrameDupe.Blacklist.Items", Collections.singletonList("DRAGON_EGG"))
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
        this.whitelistEnabled = config.getBoolean("GLOW_FrameDupe.Whitelist.Enabled", false);
        this.whitelistCheckInsideItems = config.getBoolean("GLOW_FrameDupe.Whitelist.Check-Inside-Items", true);
        this.whitelist = config.getList("GLOW_FrameDupe.Whitelist.Items", Collections.singletonList("DIAMOND"))
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
        return FrameDupe.config().getBoolean("GLOW_FrameDupe.Enabled", GLOW_ITEM_FRAME != null)
                && GLOW_ITEM_FRAME != null && probability > 0;
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
        if (damaged == null || damaged.getType() != GLOW_ITEM_FRAME) return;
        Entity damager = event.getDamager();
        if (damager == null) return;

        boolean isPlayer = damager.getType() == EntityType.PLAYER;

        if (
                probability >= 100
                || FrameDupe.random().nextDouble() <= probability
                || (isPlayer && damager.hasPermission(PluginPermission.BYPASS_CHANCE.get()))
        ) {
            ItemStack itemInItemFrame = ((ItemFrame) damaged).getItem();
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