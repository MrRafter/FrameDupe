package me.xginko.framedupe.utils;

import me.xginko.framedupe.FrameDupe;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class ItemUtil {

    private static final boolean BUNDLES_SUPPORTED;
    public static final Set<Material> INVENTORY_HOLDER_TYPES;

    static {
        BUNDLES_SUPPORTED = FrameDupe.hasClass("org.bukkit.inventory.meta.BundleMeta");
        // Collects all material types on init that have a BlockStateMeta implementing InventoryHolder
        // In other words, collects all Material types in the game that can hold one or more Items.
        // This makes checking for stored items a lot faster compared to using instanceof
        INVENTORY_HOLDER_TYPES = Arrays.stream(Material.values())
                .filter(Material::isItem) // Prevents loading issues in 1.20.6+
                .map(ItemStack::new)
                .filter(itemStack -> itemStack.getItemMeta() instanceof BlockStateMeta)
                .map(itemStack -> ((BlockStateMeta) itemStack.getItemMeta()).getBlockState())
                .filter(blockState -> blockState instanceof InventoryHolder)
                .map(BlockState::getType)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Material.class)));
    }

    /**
     *  Returns the content stored inside the passed ItemStack or null if there are no stored items.
     *  Elements inside the returned Iterable can be null.
     *
     *  @param itemStack ItemStack with potential items inside
     *  @return An Iterable of ItemStacks that are inside the passed ItemStack or null
     */
    @SuppressWarnings("UnstableApiUsage")
    public static Iterable<ItemStack> getStoredItems(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return null;
        }

        if (INVENTORY_HOLDER_TYPES.contains(itemStack.getType())) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) itemStack.getItemMeta();
            if (blockStateMeta.hasBlockState()) {
                return ((InventoryHolder) blockStateMeta.getBlockState()).getInventory();
            }
        }

        if (BUNDLES_SUPPORTED && itemStack.getType() == Material.BUNDLE) {
            return ((BundleMeta) itemStack.getItemMeta()).getItems();
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
    public static boolean containsAny(ItemStack itemStack, Set<Material> materials) {
        Iterable<ItemStack> storedItems = getStoredItems(itemStack);
        if (storedItems == null) return false;

        for (final ItemStack item : storedItems) {
            if (item != null && (materials.contains(item.getType()) || containsAny(item, materials))) {
                return true;
            }
        }

        return false;
    }

    /**
     *  @return A new location where X/Y/Z are the center of the block
     */
    public static Location toCenterLocation(Location frameLocation) {
        Location dropLoc = frameLocation.clone();
        dropLoc.setX(dropLoc.getBlockX() + 0.5);
        dropLoc.setY(dropLoc.getBlockY() + 0.5);
        dropLoc.setZ(dropLoc.getBlockZ() + 0.5);
        return dropLoc;
    }
}