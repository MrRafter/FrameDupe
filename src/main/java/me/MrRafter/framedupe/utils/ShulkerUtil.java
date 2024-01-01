package me.MrRafter.framedupe.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class ShulkerUtil {

    private static final Set<Material> SHULKER_BOXES = XTag.SHULKER_BOXES.getValues()
            .stream()
            .filter(XMaterial::isSupported)
            .map(XMaterial::parseMaterial)
            .collect(Collectors.toSet());

    /**
     *  Checks if an ItemStack is a ShulkerBox.
     *
     *  @param itemStack ItemStack to check
     *  @return true if the ItemStack is indeed a ShulkerBox
     */
    public static boolean isShulkerBox(final ItemStack itemStack) {
        if (itemStack == null) return false;
        return SHULKER_BOXES.contains(itemStack.getType());
    }

    /**
     *  Gets the content of a ShulkerBox if indeed a ShulkerBox
     *
     *  @param shulker the shulker as ItemStack
     *  @return An Iterable of ItemStacks that are inside the ShulkerBox or an empty Iterable if somehow not a ShulkerBox
     */
    public static Iterable<ItemStack> getShulkerBoxItems(final ItemStack shulker) {
        if (!isShulkerBox(shulker)) return Collections.emptyList();
        return ((ShulkerBox) ((BlockStateMeta) shulker.getItemMeta()).getBlockState()).getInventory();
    }
}