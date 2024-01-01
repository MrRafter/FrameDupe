package me.MrRafter.framedupe.utils;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import java.util.Collections;

public class BundleUtil {

    private static final Material BUNDLE = XMaterial.BUNDLE.parseMaterial();

    /**
     *  Checks if an ItemStack is a Bundle that contains at least one Item.
     *
     *  @param itemStack ItemStack to check
     *  @return true if the item is a bundle with at least one bundle inside
     */
    public static boolean isBundle(final ItemStack itemStack) {
        if (itemStack == null) return false;
        // We can check Material here since, unlike with shulkers, it hasn't been changed between versions yet.
        return itemStack.getType().equals(BUNDLE);
    }

    /**
     *  Gets the content of a Bundle if indeed a bundle.
     *
     *  @param itemStack bundle with items inside
     *  @return An Iterable of ItemStacks that are inside the Bundle or an empty Iterable if somehow not a Bundle
     */
    public static Iterable<ItemStack> getBundleItems(final ItemStack itemStack) {
        if (!isBundle(itemStack)) return Collections.emptyList();
        return ((BundleMeta) itemStack.getItemMeta()).getItems();
    }
}