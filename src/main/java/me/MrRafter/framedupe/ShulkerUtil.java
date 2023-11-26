package me.MrRafter.framedupe;

import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class ShulkerUtil {

    /*
    * Metas as well as instanceof checks are slow, but since we don't have to trigger this check multiple times a second
    * it doesn't matter and also enables us to easily stay cross version compatible.
    */
    public static boolean isShulkerBox(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (!itemStack.hasItemMeta()) return false;
        if (!(itemStack.getItemMeta() instanceof BlockStateMeta)) return false;
        return ((BlockStateMeta) itemStack.getItemMeta()).getBlockState() instanceof ShulkerBox;
    }

    /*
    * Save to use if it is only called once it has been confirmed that the passed ItemStack is indeed a ShulkerBox,
    * otherwise will throw a NullPointerException.
    * */
    public static ItemStack[] getContents (ItemStack shulkerItem) {
        return ((ShulkerBox) ((BlockStateMeta) shulkerItem.getItemMeta()).getBlockState()).getInventory().getContents();
    }
}
