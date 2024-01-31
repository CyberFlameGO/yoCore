package me.yochran.yocore.punishments;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.yoCore;

import java.util.*;

public class Punishment {

    private final yoCore plugin = yoCore.getInstance();
    private static final yoCore splugin = yoCore.getInstance();

    private PunishmentType type;
    private yoPlayer target;
    private String executor;
    private Object duration;
    private boolean silent;
    private String reason;
    private int ID;
    private String status;
    private long date;

    private static final Map<Integer, Punishment> punishments;

    static {
        punishments = new HashMap<>();
    }

    public Punishment(PunishmentType type, yoPlayer target, String executor, Object duration, boolean silent, String reason) {
        this.type = type;
        this.target = target;
        this.executor = executor;
        this.duration = duration;
        this.silent = silent;
        this.reason = reason;

        List<Integer> ids = new ArrayList<>(getPunishments().keySet());
        if (ids.size() < 1) ID = 1;
        else ID = Collections.max(ids) + 1;
    }

    public static Map<Integer, Punishment> getPunishments() { return punishments; }

    public static Map<Integer, Punishment> getPunishments(yoPlayer player) {
        Map<Integer, Punishment> punishments = new HashMap<>();

        for (Map.Entry<Integer, Punishment> entry : getPunishments().entrySet()) {
            if (entry.getValue().getTarget().getPlayer().getUniqueId().equals(player.getPlayer().getUniqueId()))
                punishments.put(entry.getKey(), entry.getValue());
        }

        return punishments;
    }


    public PunishmentType getType() { return type; }
    public yoPlayer getTarget() { return target; }
    public String getExecutor() { return executor; }
    public Object getDuration() { return duration; }
    public boolean getSilent() { return silent; }
    public String getReason() { return reason; }
    public int getID() { return ID; }
    public String getStatus() { return status; }
    public long getDate() { return date; }

    public void setExecutor(String executor) { this.executor = executor; }
    public void setDuration(Object duration) { this.duration = duration; }
    public void setSilent(boolean silent) { this.silent = silent; }
    public void setReason(String reason) { this.reason = reason; }
    public void setDate(long date) { this.date = date; }
    public void setStatus(String status) { this.status = status; }

    public boolean isTemporary() { return !(duration instanceof String); }

    public void create() {
        plugin.punishmentData.config.set(getTarget().getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(getType()) + "." + getID() + ".ID", getID());
        plugin.punishmentData.config.set(getTarget().getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(getType()) + "." + getID() + ".Executor", getExecutor());
        plugin.punishmentData.config.set(getTarget().getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(getType()) + "." + getID() + ".Reason", getReason());
        plugin.punishmentData.config.set(getTarget().getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(getType()) + "." + getID() + ".Silent", getSilent());

        plugin.punishmentData.config.set(getTarget().getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(getType()) + "." + getID() + ".Date", System.currentTimeMillis());
        setDate(System.currentTimeMillis());

        plugin.punishmentData.config.set(getTarget().getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(getType()) + "." + getID() + ".Duration", getDuration());
        plugin.punishmentData.config.set(getTarget().getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(getType()) + "." + getID() + ".Status", "Active");

        if (getType() == PunishmentType.KICK) {
            splugin.punishmentData.config.set(getTarget().getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(getType()) + "." + getID() + ".Status", "Expired");
            setStatus("Expired");
        } else setStatus("Active");

        plugin.punishmentData.saveData();

        getPunishments().put(getID(), this);
    }

    public static void redo(Punishment punishment, yoPlayer target, String executor, Object duration, boolean silent, String reason) {
        splugin.punishmentData.config.set(target.getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(punishment.getType()) + "." + punishment.getID() + ".Executor", executor);
        splugin.punishmentData.config.set(target.getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(punishment.getType()) + "." + punishment.getID() + ".Reason", reason);
        splugin.punishmentData.config.set(target.getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(punishment.getType()) + "." + punishment.getID() + ".Silent", silent);

        splugin.punishmentData.config.set(target.getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(punishment.getType()) + "." + punishment.getID() + ".Date", System.currentTimeMillis());
        punishment.setDate(System.currentTimeMillis());

        splugin.punishmentData.config.set(target.getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(punishment.getType()) + "." + punishment.getID() + ".Duration", duration);
        splugin.punishmentData.config.set(target.getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(punishment.getType()) + "." + punishment.getID() + ".Status", "Active");

        if (punishment.getType() == PunishmentType.KICK) {
            splugin.punishmentData.config.set(target.getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(punishment.getType()) + "." + punishment.getID() + ".Status", "Expired");
            punishment.setStatus("Expired");
        } else punishment.setStatus("Active");

        splugin.punishmentData.saveData();

        punishment.setExecutor(executor);
        punishment.setDuration(duration);
        punishment.setSilent(silent);
        punishment.setReason(reason);

        getPunishments().put(punishment.getID(), punishment);
    }

    public void execute() {
        switch (getType()) {
            case BLACKLIST:
                plugin.punishmentData.config.set("BlacklistedPlayers." + getTarget().getPlayer().getUniqueId().toString() + ".Name", getTarget().getPlayer().getName());
                plugin.punishmentData.config.set("BlacklistedPlayers." + getTarget().getPlayer().getUniqueId().toString() + ".Reason", getReason());

                plugin.blacklisted_players.put(getTarget().getPlayer().getUniqueId(), getReason());
                break;
            case BAN:
                plugin.punishmentData.config.set("BannedPlayers." + getTarget().getPlayer().getUniqueId().toString() + ".Name", getTarget().getPlayer().getName());
                plugin.punishmentData.config.set("BannedPlayers." + getTarget().getPlayer().getUniqueId().toString() + ".Temporary", isTemporary());

                plugin.banned_players.put(getTarget().getPlayer().getUniqueId(), isTemporary());
                break;
            case MUTE:
                plugin.punishmentData.config.set("MutedPlayers." + getTarget().getPlayer().getUniqueId().toString() + ".Name", getTarget().getPlayer().getName());
                plugin.punishmentData.config.set("MutedPlayers." + getTarget().getPlayer().getUniqueId().toString() + ".Temporary", isTemporary());

                plugin.muted_players.put(getTarget().getPlayer().getUniqueId(), isTemporary());
                break;
        }

        plugin.punishmentData.saveData();
    }

    public void reexecute() {
        switch (getType()) {
            case BLACKLIST:
                plugin.punishmentData.config.set("BlacklistedPlayers." + getTarget().getPlayer().getUniqueId().toString() + ".Name", getTarget().getPlayer().getName());
                plugin.punishmentData.config.set("BlacklistedPlayers." + getTarget().getPlayer().getUniqueId().toString() + ".Reason", getReason());
                break;
            case BAN:
                plugin.punishmentData.config.set("BannedPlayers." + getTarget().getPlayer().getUniqueId().toString() + ".Name", getTarget().getPlayer().getName());
                plugin.punishmentData.config.set("BannedPlayers." + getTarget().getPlayer().getUniqueId().toString() + ".Temporary", isTemporary());
                break;
            case MUTE:
                plugin.punishmentData.config.set("MutedPlayers." + getTarget().getPlayer().getUniqueId().toString() + ".Name", getTarget().getPlayer().getName());
                plugin.punishmentData.config.set("MutedPlayers." + getTarget().getPlayer().getUniqueId().toString() + ".Temporary", isTemporary());
                break;
        }

        plugin.punishmentData.saveData();
    }

    public void revoke() {
        setStatus("Revoked");
        plugin.punishmentData.config.set(getTarget().getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(getType()) + "." + getID() + ".Status", getStatus());
        plugin.punishmentData.saveData();

        switch (getType()) {
            case BLACKLIST:
                plugin.blacklisted_players.remove(getTarget().getPlayer().getUniqueId());
                plugin.punishmentData.config.set("BlacklistedPlayers." + getTarget().getPlayer().getUniqueId().toString(), null);
                break;
            case BAN:
                plugin.banned_players.remove(getTarget().getPlayer().getUniqueId());
                plugin.punishmentData.config.set("BannedPlayers." + getTarget().getPlayer().getUniqueId().toString(), null);
                break;
            case MUTE:
                plugin.muted_players.remove(getTarget().getPlayer().getUniqueId());
                plugin.punishmentData.config.set("MutedPlayers." + getTarget().getPlayer().getUniqueId().toString(), null);
                break;
        }

        plugin.punishmentData.saveData();
    }

    public void expire() {
        revoke();
        setStatus("Expired");
        plugin.punishmentData.config.set(getTarget().getPlayer().getUniqueId().toString() + "." + PunishmentType.convertToString(getType()) + "." + getID() + ".Status", getStatus());
        plugin.punishmentData.saveData();
    }
}
