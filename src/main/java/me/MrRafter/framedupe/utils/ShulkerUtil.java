package me.MrRafter.framedupe.utils;

import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class ShulkerUtil {

    /**
     *  Checks if an ItemStack is a shulker.
     *
     *  @param itemStack ItemStack to check
     *  @return true if the ItemStack is indeed a shulker
     */
    public static boolean isShulker(final ItemStack itemStack) {
        if (itemStack == null) return false;
        // Instanceof checks and metas are slower than Material checks, but since we don't have to trigger this logic
        // that often in a short time, it doesn't matter too much and also enables us to easily stay cross-version compatible.
        if (!(itemStack.getItemMeta() instanceof BlockStateMeta)) return false;
        return ((BlockStateMeta) itemStack.getItemMeta()).getBlockState() instanceof ShulkerBox;
    }

    /**
     *  Only save to use if called once it has been confirmed that the passed ItemStack is indeed a ShulkerBox.
     *
     *  @param shulker the shulker ItemStack
     *  @return The inventory of the shulker
     */
    public static Inventory getShulkerInventory(final ItemStack shulker) {
        return ((ShulkerBox) ((BlockStateMeta) shulker.getItemMeta()).getBlockState()).getInventory();
    }
}