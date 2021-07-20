package me.yochran.yocore.grants;

import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class Grant {

    private final yoCore plugin = yoCore.getInstance();

    private GrantType type;
    private String grant;
    private OfflinePlayer target;
    private OfflinePlayer executor;
    private Object duration;
    private String reason;
    private int ID;
    private String status;
    private long date;

    private static final Map<Integer, Grant> grants;

    static {
        grants = new HashMap<>();
    }

    public Grant(GrantType type, String grant, OfflinePlayer target, OfflinePlayer executor, Object duration, String reason) {
        this.type = type;
        this.grant = grant;
        this.target = target;
        this.executor = executor;
        this.duration = duration;
        this.reason = reason;

        List<Integer> ids = new ArrayList<>(getGrants().keySet());
        if (ids.size() < 1) ID = 1;
        else ID = Collections.max(ids) + 1;
    }

    public GrantType getType() { return type; }
    public String getGrant() { return grant; }
    public OfflinePlayer getTarget() { return target; }
    public OfflinePlayer getExecutor() { return executor; }
    public Object getDuration() { return duration; }
    public String getReason() { return reason; }
    public int getID() { return ID; }
    public String getPreviousRank() { return plugin.grantData.config.getString(getTarget().getUniqueId().toString() + ".Grants." + getID() + ".PreviousRank"); }
    public String getStatus() { return status; }
    public long getDate() { return date; }

    public void setStatus(String status) { this.status = status; }
    public void setDate(long date) { this.date = date; }

    public static Map<Integer, Grant> getGrants() {
        return grants;
    }

    public static Map<Integer, Grant> getGrants(OfflinePlayer player) {
        Map<Integer, Grant> grants = new HashMap<>();

        for (Map.Entry<Integer, Grant> entry : getGrants().entrySet()) {
            if (entry.getValue().getTarget().getUniqueId().equals(player.getUniqueId()))
                grants.put(entry.getKey(), entry.getValue());
        }

        return grants;
    }

    public void create() {
        plugin.grantData.config.set(getTarget().getUniqueId().toString() + ".Grants." + getID() + ".ID", getID());
        plugin.grantData.config.set(getTarget().getUniqueId().toString() + ".Grants." + getID() + ".Type", getType().toString());
        plugin.grantData.config.set(getTarget().getUniqueId().toString() + ".Grants." + getID() + ".Grant", getGrant());
        plugin.grantData.config.set(getTarget().getUniqueId().toString() + ".Grants." + getID() + ".Executor", getExecutor().getUniqueId().toString());
        plugin.grantData.config.set(getTarget().getUniqueId().toString() + ".Grants." + getID() + ".Duration", getDuration());

        plugin.grantData.config.set(getTarget().getUniqueId().toString() + ".Grants." + getID() + ".Date", System.currentTimeMillis());
        setDate(System.currentTimeMillis());

        plugin.grantData.config.set(getTarget().getUniqueId().toString() + ".Grants." + getID() + ".Reason", getReason());
        plugin.grantData.config.set(getTarget().getUniqueId().toString() + ".Grants." + getID() + ".PreviousRank", plugin.playerData.config.getString(getTarget().getUniqueId().toString() + ".Rank"));

        plugin.grantData.config.set(getTarget().getUniqueId().toString() + ".Grants." + getID() + ".Status", "Active");
        setStatus("Active");

        plugin.grantData.saveData();

        getGrants().put(getID(), this);
    }

    public void grant(GrantType type, String grant) {
        if (type == GrantType.RANK)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + getTarget().getName() + " " + grant);
        else if (type == GrantType.PERMISSION)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "user " + getTarget().getName() + " add " + grant);
    }

    public void ungrant(GrantType type, String grant) {
        if (type == GrantType.RANK)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + getTarget().getName() + " " + grant);
        else if (type == GrantType.PERMISSION)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "user " + getTarget().getName() + " remove " + grant);
    }

    public void revoke() {
        setStatus("Revoked");
        plugin.grantData.config.set(getTarget().getUniqueId().toString() + ".Grants." + getID() + ".Status", getStatus());
        plugin.grantData.saveData();

        String toSetTo;
        if (getType() == GrantType.RANK) toSetTo = getPreviousRank();
        else toSetTo = getGrant();

        ungrant(getType(), toSetTo);
    }

    public void expire() {
        revoke();
        setStatus("Expired");
        plugin.grantData.config.set(getTarget().getUniqueId().toString() + ".Grants." + getID() + ".Status", getStatus());
        plugin.grantData.saveData();
    }
}
