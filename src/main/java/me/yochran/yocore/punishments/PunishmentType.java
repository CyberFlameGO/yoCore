package me.yochran.yocore.punishments;

import me.yochran.yocore.utils.Utils;

public enum PunishmentType {

    WARN,
    MUTE,
    KICK,
    BAN,
    BLACKLIST;

    public static String convertToString(PunishmentType type) {
        return Utils.capitalizeFirst(type.toString().toLowerCase());
    }
}
