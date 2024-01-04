package me.MrRafter.framedupe.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import me.MrRafter.framedupe.FrameDupe;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ShulkerUtil {

    private static final HashSet<Material> SHULKER_BOXES = XTag.SHULKER_BOXES.getValues().stream()
            .filter(XMaterial::isSupported)
            .map(XMaterial::parseMaterial)
            .collect(Collectors.toCollection(HashSet::new));

    /**
     *  Checks if an ItemStack is a ShulkerBox.
     *
     *  @param itemStack ItemStack to check
     *  @return true if the ItemStack is indeed a ShulkerBox
     */
    public static boolean isShulkerBox(ItemStack itemStack) {
        if (itemStack == null) return false;
        return SHULKER_BOXES.contains(itemStack.getType());
    }

    /**
     *  Gets the content of a ShulkerBox if indeed a ShulkerBox
     *
     *  @param shulkerBox the ShulkerBox as ItemStack
     *  @return An Iterable of ItemStacks that are inside the ShulkerBox or an empty Iterable if somehow not a ShulkerBox
     */
    public static Iterable<ItemStack> getItems(ItemStack shulkerBox) {
        try {
            return ((ShulkerBox) ((BlockStateMeta) shulkerBox.getItemMeta()).getBlockState()).getInventory();
        } catch (ClassCastException e) { // This should only throw in modded servers that for some reason run bukkit api
            FrameDupe.getPrefixedLogger().warning("Tried to read ShulkerBox content from ItemStack but got something else:");
            FrameDupe.getPrefixedLogger().warning("(!) "+e.getLocalizedMessage());
            FrameDupe.getPrefixedLogger().warning("ItemStack that should be a ShulkerBox: "+shulkerBox);
            return Collections.emptyList();
        }
    }
}