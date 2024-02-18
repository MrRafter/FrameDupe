package me.MrRafter.framedupe.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

public class BundleUtil {

    private static final Material BUNDLE = loadBundleMaterial();

    private static Material loadBundleMaterial() {
        try {
            return Material.valueOf("BUNDLE");
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean serverHasBundles() {
        return BUNDLE != null;
    }

    /**
     *  Gets the content of a Bundle if indeed a bundle.
     *
     *  @param itemStack bundle with items inside
     *  @return An Iterable of ItemStacks that are inside the Bundle or null if not a Bundle
     */
    @SuppressWarnings("UnstableApiUsage")
    public static Iterable<ItemStack> getItems(ItemStack itemStack) {
        if (itemStack.getType() != BUNDLE) return null;
        try {
            return ((BundleMeta) itemStack.getItemMeta()).getItems();
        } catch (Throwable t) {
            return null;
        }
    }
}