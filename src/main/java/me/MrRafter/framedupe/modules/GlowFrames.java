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
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

public class GlowFrames implements FrameModule, Listener {

    private final ServerImplementation scheduler;
    private final Cache<UUID, Boolean> dupersOnCooldown;
    private final HashSet<Material> blacklist = new HashSet<>();
    private final HashSet<Material> whitelist = new HashSet<>();
    private EntityType GLOW_ITEM_FRAME;
    private final double probability;
    private final boolean isFolia, blacklistEnabled, blacklistCheckShulkers, blacklistCheckBundles,
            whitelistEnabled, whitelistCheckShulkers, whitelistCheckBundles;

    public GlowFrames() {
        shouldEnable(); // make enable option appear on top
        FoliaLib foliaLib = FrameDupe.getFoliaLib();
        this.isFolia = foliaLib.isFolia();
        this.scheduler = isFolia ? foliaLib.getImpl() : null;
        try { this.GLOW_ITEM_FRAME = EntityType.valueOf("GLOW_ITEM_FRAME"); } catch (IllegalArgumentException ignored) {}
        FrameConfig config = FrameDupe.getConfiguration();
        this.dupersOnCooldown = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMillis(
                        config.getInt("GLOW_FrameDupe.Cooldown-Ticks", 10,
                                "Prevents abuse by players using cheats. 1 sec = 20 ticks.") * 50L))
                .build();
        this.probability = config.getDouble("GLOW_FrameDupe.Probability-Percentage", 50.0,
                "Value has to be greater than 0. Recommended not to set to 100% unless\n" +
                        "you are okay with players flooding the server with items.") / 100;
        if (probability <= 0)
            FrameDupe.getPrefixedLogger().warning("Probability percentage needs to be a value greater than 0. Glow frame dupe will not enable.");
        this.blacklistEnabled = config.getBoolean("GLOW_FrameDupe.Blacklist.Enabled", false,
                "If enabled, all items in this list will not be duplicated.");
        this.blacklistCheckShulkers = config.getBoolean("GLOW_FrameDupe.Blacklist.Check-Shulkers", FrameDupe.serverHasShulkers(),
                "Whether to check inside shulkers for blacklisted items.") && FrameDupe.serverHasShulkers();
        this.blacklistCheckBundles = config.getBoolean("GLOW_FrameDupe.Blacklist.Check-Bundles", FrameDupe.serverHasBundles(),
                "Whether to check inside bundles for blacklisted items.") && FrameDupe.serverHasBundles();
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
        this.whitelistCheckShulkers = config.getBoolean("GLOW_FrameDupe.Whitelist.Check-Shulkers", FrameDupe.serverHasShulkers(),
                "Whether to check inside shulkers for whitelisted items.") && FrameDupe.serverHasShulkers();
        this.whitelistCheckBundles = config.getBoolean("GLOW_FrameDupe.Whitelist.Check-Bundles", FrameDupe.serverHasBundles(),
                "Whether to check inside bundles for whitelisted items.") && FrameDupe.serverHasBundles();
        config.getList("GLOW_FrameDupe.Whitelist.Items", Collections.singletonList("DIAMOND")).forEach(configuredItem -> {
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
        return FrameDupe.getConfiguration().getBoolean("GLOW_FrameDupe.Enabled", FrameDupe.serverHasGlowItemFrames())
                && probability > 0 && FrameDupe.serverHasGlowItemFrames();
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

        // Cooldown to slow down cheaters
        final UUID duper  = event.getDamager().getUniqueId();
        if (this.dupersOnCooldown.getIfPresent(duper) != null) return;
        else dupersOnCooldown.put(duper, true);

        if (!isFolia) {
            itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), frameItem.clone());
        } else {
            scheduler.runAtEntity(itemFrame,
                    dropAdditional -> itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), frameItem.clone()));
        }
    }
}