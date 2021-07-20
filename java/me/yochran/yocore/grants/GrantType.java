package me.yochran.yocore.grants;

import me.yochran.yocore.utils.Utils;

public enum GrantType {

    RANK,
    PERMISSION;

    public static String convertToString(GrantType type) {
        return Utils.capitalizeFirst(type.toString().toLowerCase());
    }
}
