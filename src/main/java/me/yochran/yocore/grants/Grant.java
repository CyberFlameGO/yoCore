package me.yochran.yocore.grants;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class Grant {

    private final yoCore plugin = yoCore.getInstance();

    private GrantType type;
    private String grant;
    private yoPlayer target;
    private OfflinePlayer executor;
    private Object duration;
    private String reason;
    private int ID;
    private String previousRank;
    private String status;
    private long date;

    private static final Map<Integer, Grant> grants;

    static {
        grants = new HashMap<>();
    }

    public Grant(GrantType type, String grant, yoPlayer target, OfflinePlayer executor, Object duration, String reason, String previousRank) {
        this.type = type;
        this.grant = grant;
        this.target = target;
        this.executor = executor;
        this.duration = duration;
        this.reason = reason;
        this.previousRank = previousRank;

        List<Integer> ids = new ArrayList<>(getGrants().keySet());
        if (ids.size() < 1) ID = 1;
        else ID = Collections.max(ids) + 1;
    }

    public GrantType getType() { return type; }
    public String getGrant() { return grant; }
    public yoPlayer getTarget() { return target; }
    public OfflinePlayer getExecutor() { return executor; }
    public Object getDuration() { return duration; }
    public String getReason() { return reason; }
    public int getID() { return ID; }
    public String getPreviousRank() { return previousRank; }
    public String getStatus() { return status; }
    public long getDate() { return date; }

    public void setStatus(String status) { this.status = status; }
    public void setDate(long date) { this.date = date; }
    public void setPreviousRank(String previousRank) { this.previousRank = previousRank; }

    public static Map<Integer, Grant> getGrants() {
        return grants;
    }

    public static Map<Integer, Grant> getGrants(yoPlayer player) {
        Map<Integer, Grant> grants = new HashMap<>();

        for (Map.Entry<Integer, Grant> entry : getGrants().entrySet()) {
            if (entry.getValue().getTarget().getPlayer().getUniqueId().equals(player.getPlayer().getUniqueId()))
                grants.put(entry.getKey(), entry.getValue());
        }

        return grants;
    }

    public void create() {
        plugin.grantData.config.set(getTarget().getPlayer().getUniqueId().toString() + ".Grants." + getID() + ".ID", getID());
        plugin.grantData.config.set(getTarget().getPlayer().getUniqueId().toString() + ".Grants." + getID() + ".Type", getType().toString());
        plugin.grantData.config.set(getTarget().getPlayer().getUniqueId().toString() + ".Grants." + getID() + ".Grant", getGrant());
        plugin.grantData.config.set(getTarget().getPlayer().getUniqueId().toString() + ".Grants." + getID() + ".Executor", getExecutor().getUniqueId().toString());
        plugin.grantData.config.set(getTarget().getPlayer().getUniqueId().toString() + ".Grants." + getID() + ".Duration", getDuration());

        plugin.grantData.config.set(getTarget().getPlayer().getUniqueId().toString() + ".Grants." + getID() + ".Date", System.currentTimeMillis());
        setDate(System.currentTimeMillis());

        plugin.grantData.config.set(getTarget().getPlayer().getUniqueId().toString() + ".Grants." + getID() + ".Reason", getReason());

        plugin.grantData.config.set(getTarget().getPlayer().getUniqueId().toString() + ".Grants." + getID() + ".PreviousRank", getPreviousRank());

        plugin.grantData.config.set(getTarget().getPlayer().getUniqueId().toString() + ".Grants." + getID() + ".Status", "Active");
        setStatus("Active");

        plugin.grantData.saveData();

        getGrants().put(getID(), this);
    }

    public void grant(GrantType type, String grant) {
        if (type == GrantType.RANK)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + getTarget().getPlayer().getName() + " " + grant);
        else if (type == GrantType.PERMISSION)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "user " + getTarget().getPlayer().getName() + " add " + grant);
    }

    public void ungrant(GrantType type, String grant) {
        if (type == GrantType.RANK)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + getTarget().getPlayer().getName() + " " + grant);
        else if (type == GrantType.PERMISSION)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "user " + getTarget().getPlayer().getName() + " remove " + grant);
    }

    public void revoke() {
        setStatus("Revoked");
        plugin.grantData.config.set(getTarget().getPlayer().getUniqueId().toString() + ".Grants." + getID() + ".Status", getStatus());
        plugin.grantData.saveData();

        String toSetTo;
        if (getType() == GrantType.RANK) toSetTo = getPreviousRank();
        else toSetTo = getGrant();

        ungrant(getType(), toSetTo);
    }

    public void expire() {
        revoke();
        setStatus("Expired");
        plugin.grantData.config.set(getTarget().getPlayer().getUniqueId().toString() + ".Grants." + getID() + ".Status", getStatus());
        plugin.grantData.saveData();
    }
}
