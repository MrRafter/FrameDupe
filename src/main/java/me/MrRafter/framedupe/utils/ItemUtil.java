package me.MrRafter.framedupe.utils;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Set;

public class ItemUtil {

    /**
     *  Returns the content stored inside the passed ItemStack or null if there are no stored items.
     *  Elements inside the returned Iterable can be null.
     *
     *  @param itemStack ItemStack with potential items inside
     *  @return An Iterable of ItemStacks that are inside the passed ItemStack or null
     */
    public static Iterable<ItemStack> getStoredItems(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return null;

        if (!(itemStack.getItemMeta() instanceof BlockStateMeta)) {
            return BundleUtil.serverHasBundles() ? BundleUtil.getItems(itemStack) : null;
        }

        try {
            BlockStateMeta blockStateMeta = (BlockStateMeta) itemStack.getItemMeta();
            if (!blockStateMeta.hasBlockState()) return null;

            BlockState blockState = blockStateMeta.getBlockState();

            if (blockState instanceof InventoryHolder) {
                return ((InventoryHolder) blockState).getInventory();
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    /**
     *  Returns true if the ItemStack is a storage item that contains one or more of the
     *  Materials from the set.
     *
     *  @param itemStack ItemStack with potential items inside
     *  @return true if an item is inside that matches one of the materials in the set
     */
    public static boolean containsOfMaterial(ItemStack itemStack, Set<Material> materials) {
        Iterable<ItemStack> storedItems = getStoredItems(itemStack);
        if (storedItems == null) return false;

        for (final ItemStack item : storedItems) {
            if (item != null) {
                return materials.contains(item.getType()) || containsOfMaterial(item, materials);
            }
        }

        return false;
    }
}