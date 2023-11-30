package me.MrRafter.framedupe.enums;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum Permissions {
    CMD_RELOAD(new Permission("framedupe.cmd.reload", PermissionDefault.FALSE)),
    CMD_VERSION(new Permission("framedupe.cmd.version", PermissionDefault.FALSE)),
    BYPASS_COOLDOWN(new Permission("framedupe.bypass.cooldown", PermissionDefault.FALSE)),
    BYPASS_CHANCE(new Permission("framedupe.bypass.chance", PermissionDefault.FALSE));
    private final Permission permission;
    Permissions(Permission permission) {
        this.permission = permission;
    }
    public Permission get() {
        return permission;
    }
}