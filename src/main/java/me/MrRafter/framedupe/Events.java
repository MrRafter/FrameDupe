package me.MrRafter.framedupe;

import me.MrRafter.FrameDupe;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.List;

public class Events {

    private FrameDupe plugin;

    public Events(FrameDupe plugin) {
        this.plugin = plugin;
    }

    // Evento para ItemFrames
    public class FrameAll implements Listener {

        @EventHandler
        private void onFrameBreak(EntityDamageByEntityEvent event) {
            if (plugin.getConfig().getBoolean("FrameDupe.Enabled") && event.getEntityType() == EntityType.ITEM_FRAME) {
                ItemFrame itemFrame = (ItemFrame) event.getEntity();
                ItemStack item = itemFrame.getItem();
                String versionString = Bukkit.getVersion();
                int versionNumber = Integer.parseInt(versionString.split("\\.")[1]);

                // Comprobamos la whitelist
                if (plugin.getConfig().getBoolean("FrameDupe.Whitelist.Enabled")) {
                    List<String> whitelist = plugin.getConfig().getStringList("FrameDupe.Whitelist.Items");
                    if (!whitelist.contains(item.getType().toString())) {
                        return;
                    }
                }

                // Comprobamos la blacklist
                if (plugin.getConfig().getBoolean("FrameDupe.Blacklist.Enabled")) {
                    List<String> blacklist = plugin.getConfig().getStringList("FrameDupe.Blacklist.Items");
                    if (blacklist.contains(item.getType().toString())) {
                        return;
                    }
                }

                // Comprobamos si es ShulkerBox (solo a partir de la versión 1.9)
                if (plugin.getConfig().getBoolean("FrameDupe.Fix-Shulkers") && versionNumber >= 9) {
                    if (item.getItemMeta() instanceof BlockStateMeta) {
                        BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
                        if (blockStateMeta.getBlockState() instanceof ShulkerBox) {
                            ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
                            Inventory shulkerInventory = shulkerBox.getInventory();

                            // Verificamos los items dentro del ShulkerBox
                            for (ItemStack shulkerItem : shulkerInventory.getContents()) {
                                if (shulkerItem != null) {
                                    String itemTypeName = shulkerItem.getType().toString();

                                    // Comprobamos la blacklist de los items dentro del ShulkerBox
                                    if (plugin.getConfig().getBoolean("FrameDupe.Blacklist.Enabled")) {
                                        List<String> blacklist = plugin.getConfig().getStringList("FrameDupe.Blacklist.Items");
                                        if (blacklist.contains(itemTypeName)) {
                                            return;
                                        }
                                    }

                                    // Comprobamos la whitelist de los items dentro del ShulkerBox
                                    if (plugin.getConfig().getBoolean("FrameDupe.Whitelist.Enabled")) {
                                        List<String> whitelist = plugin.getConfig().getStringList("FrameDupe.Whitelist.Items");
                                        if (!whitelist.contains(itemTypeName)) {
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Probabilidad de duplicado
                int rng = (int) Math.round(Math.random() * 100);
                if (rng < plugin.getConfig().getInt("FrameDupe.Probability-percentage")) {
                    event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), item);
                }
            }
        }
    }

    // Evento para GlowItemFrames
    public class FrameSpecific implements Listener {
        @EventHandler
        private void onFrameBreak(EntityDamageByEntityEvent event) {
            if (plugin.getConfig().getBoolean("GLOW_FrameDupe.Enabled") && event.getEntityType() == EntityType.GLOW_ITEM_FRAME) {
                GlowItemFrame itemFrame = (GlowItemFrame) event.getEntity();
                ItemStack item = itemFrame.getItem();

                // Comprobamos la whitelist para GlowItemFrames
                if (plugin.getConfig().getBoolean("GLOW_FrameDupe.Whitelist.Enabled")) {
                    List<String> whitelist = plugin.getConfig().getStringList("GLOW_FrameDupe.Whitelist.Items");
                    if (!whitelist.contains(item.getType().toString())) {
                        return;
                    }
                }

                // Comprobamos la blacklist para GlowItemFrames
                if (plugin.getConfig().getBoolean("GLOW_FrameDupe.Blacklist.Enabled")) {
                    List<String> blacklist = plugin.getConfig().getStringList("GLOW_FrameDupe.Blacklist.Items");
                    if (blacklist.contains(item.getType().toString())) {
                        return;
                    }
                }

                // Comprobamos si es ShulkerBox para GlowItemFrames
                if (plugin.getConfig().getBoolean("GLOW_FrameDupe.Fix-Shulkers")) {
                    if (item.getItemMeta() instanceof BlockStateMeta) {
                        BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
                        if (blockStateMeta.getBlockState() instanceof ShulkerBox) {
                            ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
                            Inventory shulkerInventory = shulkerBox.getInventory();

                            // Verificamos los items dentro del ShulkerBox
                            for (ItemStack shulkerItem : shulkerInventory.getContents()) {
                                if (shulkerItem != null) {
                                    String itemTypeName = shulkerItem.getType().toString();

                                    // Comprobamos la blacklist de los items dentro del ShulkerBox
                                    if (plugin.getConfig().getBoolean("GLOW_FrameDupe.Blacklist.Enabled")) {
                                        List<String> blacklist = plugin.getConfig().getStringList("GLOW_FrameDupe.Blacklist.Items");
                                        if (blacklist.contains(itemTypeName)) {
                                            return;
                                        }
                                    }

                                    // Comprobamos la whitelist de los items dentro del ShulkerBox
                                    if (plugin.getConfig().getBoolean("GLOW_FrameDupe.Whitelist.Enabled")) {
                                        List<String> whitelist = plugin.getConfig().getStringList("GLOW_FrameDupe.Whitelist.Items");
                                        if (!whitelist.contains(itemTypeName)) {
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Probabilidad de duplicado para GlowItemFrames
                int rng = (int) Math.round(Math.random() * 100);
                if (rng < plugin.getConfig().getInt("GLOW_FrameDupe.Probability-percentage")) {
                    event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), item);
                }
            }
        }
    }
}
