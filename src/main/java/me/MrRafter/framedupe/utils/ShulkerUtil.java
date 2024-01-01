package me.MrRafter.framedupe.utils;

import me.MrRafter.framedupe.FrameDupe;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Collections;

public class ShulkerUtil {

    /**
     *  Checks if an ItemStack is a ShulkerBox.
     *
     *  @param itemStack ItemStack to check
     *  @return true if the ItemStack is indeed a ShulkerBox
     */
    public static boolean isShulkerBox(final ItemStack itemStack) {
        if (itemStack == null) return false;
        return FrameDupe.SHULKER_BOXES.contains(itemStack.getType());
    }

    /**
     *  Gets the content of a ShulkerBox if indeed a ShulkerBox
     *
     *  @param shulker the shulker as ItemStack
     *  @return An Iterable of ItemStacks that are inside the ShulkerBox or an empty Iterable if somehow not a ShulkerBox
     */
    public static Iterable<ItemStack> getShulkerBoxItems(final ItemStack shulker) {
        try {
            return ((ShulkerBox) ((BlockStateMeta) shulker.getItemMeta()).getBlockState()).getInventory();
        } catch (ClassCastException e) {
            FrameDupe.getPrefixedLogger().warning("Tried to read ShulkerBox content from ItemStack but got something else:");
            FrameDupe.getPrefixedLogger().warning("(!) "+e.getLocalizedMessage());
            FrameDupe.getPrefixedLogger().warning("ItemStack that should be a bundle: "+shulker);
            return Collections.emptyList();
        }
    }
}