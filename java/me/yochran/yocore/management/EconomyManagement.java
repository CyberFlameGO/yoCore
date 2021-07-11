package me.yochran.yocore.management;

import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;

public class EconomyManagement {

    private final yoCore plugin;
    private final ServerManagement serverManagement = new ServerManagement();

    public EconomyManagement() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setupPlayer(OfflinePlayer target) {
        plugin.economyData.config.set(target.getUniqueId().toString() + ".Name", target.getName());
        for (String server : serverManagement.getServers()) {
            plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Balance", plugin.getConfig().getDouble("Economy.StartingAmount"));
            plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Bountied", false);
            plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Bounty", null);
        }

        plugin.economyData.saveData();
    }

    public boolean isInitialized(OfflinePlayer target) {
        return plugin.economyData.config.contains(target.getUniqueId().toString());
    }

    public void resetPlayer(String server, OfflinePlayer target) {
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Balance", plugin.getConfig().getDouble("Economy.StartingAmount"));
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Bountied", false);
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Bounty", null);
        plugin.economyData.saveData();
    }

    public boolean economyIsEnabled(String server) {
        return plugin.getConfig().getStringList("Economy.EnabledServers").contains(server.toLowerCase());
    }

    public double getMoney(String server, OfflinePlayer target) {
        return plugin.economyData.config.getDouble(target.getUniqueId().toString() + "." + server + ".Balance");
    }

    public void addMoney(String server, OfflinePlayer target, double amount) {
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Balance", getMoney(server, target) + amount);
        plugin.economyData.saveData();
    }

    public void removeMoney(String server, OfflinePlayer target, double amount) {
        if (getMoney(server, target) - amount < 0) {
            plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Balance", plugin.getConfig().getString("Economy.StartingAmount"));
            plugin.economyData.saveData();
            return;
        }

        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Balance", getMoney(server, target) - amount);
        plugin.economyData.saveData();
    }

    public boolean hasEnoughMoney(String server, OfflinePlayer target, double amount) {
        return getMoney(server, target) >= amount;
    }

    public boolean isBountied(String server, OfflinePlayer target) {
        return plugin.economyData.config.getBoolean(target.getUniqueId().toString() + "." + server + ".Bountied");
    }

    public void setBounty(String server, OfflinePlayer executor, OfflinePlayer target, double amount) {
        removeMoney(server, executor, amount);

        if (!isBountied(server, target)) {
            removeBounty(server, target);
        }

        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Bountied", true);
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Bounty.Amount", amount);
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Bounty.Executor", executor.getUniqueId().toString());
        plugin.economyData.saveData();
    }

    public void increaseBounty(String server, OfflinePlayer executor, OfflinePlayer target, double amount) {
        removeMoney(server, executor, amount);

        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Bounty.Amount", amount + getBountyAmount(server, target));
        plugin.economyData.saveData();
    }

    public void removeBounty(String server, OfflinePlayer target) {
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Bountied", false);
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server + ".Bounty", null);
        plugin.economyData.saveData();
    }

    public void claimBounty(String server, OfflinePlayer target, OfflinePlayer claimer, double amount) {
        addMoney(server, claimer, amount);
        removeBounty(server, target);
    }

    public double getBountyAmount(String server, OfflinePlayer target) {
        return plugin.economyData.config.getDouble(target.getUniqueId().toString() + "." + server + ".Bounty.Amount");
    }

    public String getBountyExecutor(String server, OfflinePlayer target) {
        return plugin.economyData.config.getString(target.getUniqueId().toString() + "." + server + ".Bounty.Executor");
    }

    public boolean isOverMaximum(double amount) {
        return amount > plugin.getConfig().getDouble("Economy.MaximumAmount");
    }

    public boolean isUnderPayMinimum(double amount) {
        return amount < plugin.getConfig().getDouble("Pay.MinimumAmount");
    }

    public boolean isUnderBountyMinimum(double amount) {
        return amount < plugin.getConfig().getDouble("Bounty.MinimumAmount");
    }

    public boolean bountyIsEnabled(String server) {
        return plugin.getConfig().getStringList("Bounty.EnabledServers").contains(server.toLowerCase());
    }

    public boolean moneyPerKillEnabled(String server) {
        return plugin.getConfig().getStringList("Economy.MoneyPerKill.EnabledServers").contains(server.toLowerCase());
    }

    public double getMoneyPerKill() {
        double highest = plugin.getConfig().getDouble("Economy.MoneyPerKill.Highest");
        double lowest = plugin.getConfig().getDouble("Economy.MoneyPerKill.Lowest");

        double range = (highest - lowest) + 1;

        double outcome = (Math.random() * range) + lowest;

        return outcome;
    }
}
