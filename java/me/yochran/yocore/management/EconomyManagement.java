package me.yochran.yocore.management;

import me.yochran.yocore.server.Server;
import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;

import java.util.Map;

public class EconomyManagement {

    private final yoCore plugin;

    public EconomyManagement() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setupPlayer(OfflinePlayer target) {
        plugin.economyData.config.set(target.getUniqueId().toString() + ".Name", target.getName());
        for (Map.Entry<String, Server> entry : Server.getServers().entrySet()) {
            plugin.economyData.config.set(target.getUniqueId().toString() + "." + entry.getValue().getName().toUpperCase() + ".Balance", plugin.getConfig().getDouble("Economy.StartingAmount"));
            plugin.economyData.config.set(target.getUniqueId().toString() + "." + entry.getValue().getName().toUpperCase() + ".Bountied", false);
            plugin.economyData.config.set(target.getUniqueId().toString() + "." + entry.getValue().getName().toUpperCase() + ".Bounty", null);
        }

        plugin.economyData.saveData();
    }

    public boolean isInitialized(OfflinePlayer target) {
        return plugin.economyData.config.contains(target.getUniqueId().toString());
    }

    public void resetPlayer(Server server, OfflinePlayer target) {
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Balance", plugin.getConfig().getDouble("Economy.StartingAmount"));
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Bountied", false);
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Bounty", null);
        plugin.economyData.saveData();
    }

    public boolean economyIsEnabled(Server server) {
        return plugin.getConfig().getStringList("Economy.EnabledServers").contains(server.getName());
    }

    public double getMoney(Server server, OfflinePlayer target) {
        return plugin.economyData.config.getDouble(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Balance");
    }

    public void addMoney(Server server, OfflinePlayer target, double amount) {
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Balance", getMoney(server, target) + amount);
        plugin.economyData.saveData();
    }

    public void removeMoney(Server server, OfflinePlayer target, double amount) {
        if (getMoney(server, target) - amount < 0) {
            plugin.economyData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Balance", plugin.getConfig().getString("Economy.StartingAmount"));
            plugin.economyData.saveData();
            return;
        }

        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Balance", getMoney(server, target) - amount);
        plugin.economyData.saveData();
    }

    public boolean hasEnoughMoney(Server server, OfflinePlayer target, double amount) {
        return getMoney(server, target) >= amount;
    }

    public boolean isBountied(Server server, OfflinePlayer target) {
        return plugin.economyData.config.getBoolean(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Bountied");
    }

    public void setBounty(Server server, OfflinePlayer executor, OfflinePlayer target, double amount) {
        removeMoney(server, executor, amount);

        if (!isBountied(server, target)) {
            removeBounty(server, target);
        }

        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Bountied", true);
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Bounty.Amount", amount);
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Bounty.Executor", executor.getUniqueId().toString());
        plugin.economyData.saveData();
    }

    public void increaseBounty(Server server, OfflinePlayer executor, OfflinePlayer target, double amount) {
        removeMoney(server, executor, amount);

        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Bounty.Amount", amount + getBountyAmount(server, target));
        plugin.economyData.saveData();
    }

    public void removeBounty(Server server, OfflinePlayer target) {
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Bountied", false);
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Bounty", null);
        plugin.economyData.saveData();
    }

    public void claimBounty(Server server, OfflinePlayer target, OfflinePlayer claimer, double amount) {
        addMoney(server, claimer, amount);
        removeBounty(server, target);
    }

    public double getBountyAmount(Server server, OfflinePlayer target) {
        return plugin.economyData.config.getDouble(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Bounty.Amount");
    }

    public String getBountyExecutor(Server server, OfflinePlayer target) {
        return plugin.economyData.config.getString(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Bounty.Executor");
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

    public boolean bountyIsEnabled(Server server) {
        return plugin.getConfig().getStringList("Bounty.EnabledServers").contains(server.getName());
    }

    public boolean moneyPerKillEnabled(Server server) {
        return plugin.getConfig().getStringList("Economy.MoneyPerKill.EnabledServers").contains(server.getName());
    }

    public double getMoneyPerKill() {
        double highest = plugin.getConfig().getDouble("Economy.MoneyPerKill.Highest");
        double lowest = plugin.getConfig().getDouble("Economy.MoneyPerKill.Lowest");

        double range = (highest - lowest) + 1;

        return (Math.random() * range) + lowest;
    }
}
