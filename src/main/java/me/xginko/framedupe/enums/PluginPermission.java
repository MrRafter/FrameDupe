package me.xginko.framedupe.enums;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum PluginPermission {

    CMD_RELOAD(new Permission("framedupe.cmd.reload", PermissionDefault.FALSE)),
    CMD_VERSION(new Permission("framedupe.cmd.version", PermissionDefault.FALSE)),
    BYPASS_COOLDOWN(new Permission("framedupe.bypass.cooldown", PermissionDefault.FALSE)),
    BYPASS_CHANCE(new Permission("framedupe.bypass.chance", PermissionDefault.FALSE)),
    BYPASS_WHITELIST(new Permission("framedupe.bypass.whitelist", PermissionDefault.FALSE)),
    BYPASS_BLACKLIST(new Permission("framedupe.bypass.blacklist", PermissionDefault.FALSE));

    private final Permission permission;

    PluginPermission(Permission permission) {
        this.permission = permission;
    }

    public Permission get() {
        return permission;
    }

    public static void registerAll() {
        for (PluginPermission perm : PluginPermission.values()) {
            try {
                Bukkit.getPluginManager().addPermission(perm.get());
            } catch (IllegalArgumentException ignored) {}
        }
    }
}