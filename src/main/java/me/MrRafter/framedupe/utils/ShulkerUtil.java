package me.MrRafter.framedupe.utils;

import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class ShulkerUtil {

    /*
    * Metas as well as instanceof checks are slower than Material checks, but since we don't have to trigger this check
    * multiple times a second, it doesn't matter and also enables us to easily stay cross-version compatible.
    */
    public static boolean isNonEmptyShulker(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (!itemStack.hasItemMeta()) return false;
        if (!(itemStack.getItemMeta() instanceof BlockStateMeta)) return false;
        return ((BlockStateMeta) itemStack.getItemMeta()).getBlockState() instanceof ShulkerBox;
    }

    /*
    * Only save to use if called once it has been confirmed that the passed ItemStack is indeed a ShulkerBox.
    */
    public static Inventory getShulkerInventory(ItemStack shulkerItem) {
        return ((ShulkerBox) ((BlockStateMeta) shulkerItem.getItemMeta()).getBlockState()).getInventory();
    }
}