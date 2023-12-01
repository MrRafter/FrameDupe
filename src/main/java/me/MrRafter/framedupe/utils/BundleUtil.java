package me.MrRafter.framedupe.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import java.util.List;

public class BundleUtil {

    /**
     *  Checks if an ItemStack is a bundle that contains at least one item.
     *
     *  @param itemStack ItemStack to check
     *  @return true if the item is a bundle with at least one bundle inside
     */
    public static boolean isNonEmptyBundle(final ItemStack itemStack) {
        if (itemStack == null) return false;
        // We can check Material here since, unlike with shulkers, it hasn't been changed between versions yet.
        if (!itemStack.getType().equals(Material.BUNDLE)) return false;
        return ((BundleMeta) itemStack.getItemMeta()).hasItems();
    }

    /**
     *  Only save to use if called once it has been confirmed that the passed ItemStack is indeed a bundle.
     *
     *  @param itemStack bundle with items inside
     *  @return A list of itemStacks that are inside the bundle
     */
    public static List<ItemStack> getBundleItems(final ItemStack itemStack) {
        return ((BundleMeta) itemStack.getItemMeta()).getItems();
    }
}