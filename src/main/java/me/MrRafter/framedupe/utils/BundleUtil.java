package me.MrRafter.framedupe.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import java.util.List;

public class BundleUtil {

    /*
     * We can check Material here since, unlike with shulkers, it hasn't been changed between versions yet.
     */
    public static boolean isNonEmptyBundle(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (!itemStack.getType().equals(Material.BUNDLE)) return false;
        if (!itemStack.hasItemMeta()) return false;
        return ((BundleMeta) itemStack.getItemMeta()).hasItems();
    }/*
     * Only save to use if called once it has been confirmed that the passed ItemStack is indeed a bundle.
     * */

    public static List<ItemStack> getBundleItems(ItemStack itemStack) {
        return ((BundleMeta) itemStack.getItemMeta()).getItems();
    }
}