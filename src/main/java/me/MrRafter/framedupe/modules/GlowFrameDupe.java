package me.MrRafter.framedupe.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.MrRafter.framedupe.FrameConfig;
import me.MrRafter.framedupe.FrameDupe;
import me.MrRafter.framedupe.utils.BundleUtil;
import me.MrRafter.framedupe.utils.ShulkerUtil;
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

public class GlowFrameDupe implements FrameDupeModule, Listener {

    private final ServerImplementation scheduler;
    private final Cache<UUID, Boolean> dupersOnCooldown;
    private final HashSet<Material> blacklist;
    private final HashSet<Material> whitelist;
    private EntityType GLOW_ITEM_FRAME;
    private final double probability;
    private final boolean isFolia, blacklistEnabled, blacklistCheckShulkers, blacklistCheckBundles,
            whitelistEnabled, whitelistCheckShulkers, whitelistCheckBundles, cooldownEnabled;

    protected GlowFrameDupe() {
        shouldEnable(); // make enable option appear on top
        final FoliaLib foliaLib = FrameDupe.getFoliaLib();
        this.isFolia = foliaLib.isFolia();
        this.scheduler = isFolia ? foliaLib.getImpl() : null;
        try { this.GLOW_ITEM_FRAME = EntityType.valueOf("GLOW_ITEM_FRAME"); } catch (IllegalArgumentException ignored) {}
        FrameConfig config = FrameDupe.getConfiguration();
        config.master().addSection("GLOW_FrameDupe", "Glow Frame Dupe");
        config.master().addComment("GLOW_FrameDupe.Enabled",
                "Enable duping with glow item frames. (Will only enable if your game version has them)");
        this.probability = config.getDouble("GLOW_FrameDupe.Probability-Percentage", 50.0,
                "50.0 = 50%. Has to be greater than 0.") / 100;
        this.cooldownEnabled = config.getBoolean("GLOW_FrameDupe.Cooldown.Enabled", true);
        this.dupersOnCooldown = cooldownEnabled ? Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMillis(config.getInt("GLOW_FrameDupe.Cooldown.Ticks", 15, "1 sec = 20 ticks") * 50L))
                .build() : null;
        if (probability <= 0)
            FrameDupe.getPrefixedLogger().warning("Probability percentage is 0 or lower. Not enabling glow frame dupe.");
        this.blacklistEnabled = config.getBoolean("GLOW_FrameDupe.Blacklist.Enabled", false);
        this.blacklistCheckShulkers = config.getBoolean("GLOW_FrameDupe.Blacklist.Check-Shulkers", true) && FrameDupe.serverHasShulkers();
        this.blacklistCheckBundles = config.getBoolean("GLOW_FrameDupe.Blacklist.Check-Bundles", true) && FrameDupe.serverHasBundles();
        final List<String> configuredBlacklist = config.getList("GLOW_FrameDupe.Blacklist.Items", Collections.singletonList("DRAGON_EGG"));
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
        this.whitelistEnabled = config.getBoolean("GLOW_FrameDupe.Whitelist.Enabled", false);
        this.whitelistCheckShulkers = config.getBoolean("GLOW_FrameDupe.Whitelist.Check-Shulkers", true) && FrameDupe.serverHasShulkers();
        this.whitelistCheckBundles = config.getBoolean("GLOW_FrameDupe.Whitelist.Check-Bundles", true) && FrameDupe.serverHasBundles();
        final List<String> configuredWhitelist = config.getList("GLOW_FrameDupe.Whitelist.Items", Collections.singletonList("DIAMOND"));
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
        return FrameDupe.getConfiguration().getBoolean("GLOW_FrameDupe.Enabled", true) && FrameDupe.serverHasGlowItemFrames() && probability > 0;
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
        if (!punched.getType().equals(GLOW_ITEM_FRAME)) return;
        if (probability < 100 && new Random().nextDouble() > probability) return;

        final ItemFrame itemFrame = (ItemFrame) punched;

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

        if (cooldownEnabled) {
            final UUID duper = event.getDamager().getUniqueId();
            if (this.dupersOnCooldown.getIfPresent(duper) != null) return;
            else dupersOnCooldown.put(duper, true);
        }

        if (!isFolia) {
            itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), frameItem.clone());
        } else {
            scheduler.runAtEntity(itemFrame,
                    dropAdditional -> itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), frameItem.clone()));
        }
    }
}