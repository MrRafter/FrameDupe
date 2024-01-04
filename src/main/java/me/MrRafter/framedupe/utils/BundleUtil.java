package me.MrRafter.framedupe.utils;

import com.cryptomorin.xseries.XMaterial;
import me.MrRafter.framedupe.FrameDupe;
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
    public static boolean isBundle(ItemStack itemStack) {
        if (itemStack == null) return false;
        return itemStack.getType().equals(BUNDLE);
    }

    /**
     *  Gets the content of a Bundle if indeed a bundle.
     *
     *  @param bundle bundle with items inside
     *  @return An Iterable of ItemStacks that are inside the Bundle or an empty Iterable if somehow not a Bundle
     */
    public static Iterable<ItemStack> getItems(ItemStack bundle) {
        try {
            return ((BundleMeta) bundle.getItemMeta()).getItems();
        } catch (ClassCastException e) { // This should only throw in modded servers that for some reason run bukkit api
            FrameDupe.getPrefixedLogger().warning("Tried to read Bundle content from ItemStack but got something else:");
            FrameDupe.getPrefixedLogger().warning("(!) "+e.getLocalizedMessage());
            FrameDupe.getPrefixedLogger().warning("ItemStack that should be a bundle: "+bundle);
            return Collections.emptyList();
        }
    }
}