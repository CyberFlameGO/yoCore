package me.yochran.yocore.management;

import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

public class EconomyManagement {

    private final yoCore plugin;

    public EconomyManagement() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setupPlayer(OfflinePlayer target) {
        plugin.economyData.config.set(target.getUniqueId().toString() + ".Name", target.getName());
        for (World world : Bukkit.getWorlds()) {
            plugin.economyData.config.set(target.getUniqueId().toString() + "." + world.getName() + ".Balance", plugin.getConfig().getDouble("Economy.StartingAmount"));
            plugin.economyData.config.set(target.getUniqueId().toString() + "." + world.getName() + ".Bountied", false);
            plugin.economyData.config.set(target.getUniqueId().toString() + "." + world.getName() + ".Bounty", null);
        }

        plugin.economyData.saveData();
    }

    public boolean isInitialized(String world, OfflinePlayer target) {
        return plugin.economyData.config.contains(target.getUniqueId().toString());
    }

    public void resetPlayer(String world, OfflinePlayer target) {
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + world + ".Balance", plugin.getConfig().getDouble("Economy.StartingAmount"));
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + world + ".Bountied", false);
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + world + ".Bounty", null);
        plugin.economyData.saveData();
    }

    public boolean economyIsEnabled(String world) {
        return plugin.getConfig().getStringList("Economy.EnabledWorlds").contains(world);
    }

    public double getMoney(String world, OfflinePlayer target) {
        return plugin.economyData.config.getDouble(target.getUniqueId().toString() + "." + world + ".Balance");
    }

    public void addMoney(String world, OfflinePlayer target, double amount) {
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + world + ".Balance", getMoney(world, target) + amount);
        plugin.economyData.saveData();
    }

    public void removeMoney(String world, OfflinePlayer target, double amount) {
        if (getMoney(world, target) - amount < 0) {
            plugin.economyData.config.set(target.getUniqueId().toString() + "." + world + ".Balance", plugin.getConfig().getString("Economy.StartingAmount"));
            plugin.economyData.saveData();
            return;
        }

        plugin.economyData.config.set(target.getUniqueId().toString() + "." + world + ".Balance", getMoney(world, target) - amount);
        plugin.economyData.saveData();
    }

    public boolean hasEnoughMoney(String world, OfflinePlayer target, double amount) {
        return getMoney(world, target) >= amount;
    }

    public boolean isBountied(String world, OfflinePlayer target) {
        return plugin.economyData.config.getBoolean(target.getUniqueId().toString() + "." + world + ".Bountied");
    }

    public void setBounty(String world, OfflinePlayer executor, OfflinePlayer target, double amount) {
        removeMoney(world, executor, amount);

        if (!isBountied(world, target)) {
            removeBounty(world, target);
        }

        plugin.economyData.config.set(target.getUniqueId().toString() + "." + world + ".Bountied", true);
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + world + ".Bounty.Amount", amount);
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + world + ".Bounty.Executor", executor.getUniqueId().toString());
        plugin.economyData.saveData();
    }

    public void increaseBounty(String world, OfflinePlayer executor, OfflinePlayer target, double amount) {
        removeMoney(world, executor, amount);

        plugin.economyData.config.set(target.getUniqueId().toString() + "." + world + ".Bounty.Amount", amount + getBountyAmount(world, target));
        plugin.economyData.saveData();
    }

    public void removeBounty(String world, OfflinePlayer target) {
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + world + ".Bountied", false);
        plugin.economyData.config.set(target.getUniqueId().toString() + "." + world + ".Bounty", null);
        plugin.economyData.saveData();
    }

    public void claimBounty(String world, OfflinePlayer target, OfflinePlayer claimer, double amount) {
        addMoney(world, claimer, amount);
        removeBounty(world, target);
    }

    public double getBountyAmount(String world, OfflinePlayer target) {
        return plugin.economyData.config.getDouble(target.getUniqueId().toString() + "." + world + ".Bounty.Amount");
    }

    public String getBountyExecutor(String world, OfflinePlayer target) {
        return plugin.economyData.config.getString(target.getUniqueId().toString() + "." + world + ".Bounty.Executor");
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

    public boolean bountyIsEnabled(String world) {
        return plugin.getConfig().getStringList("Bounty.EnabledWorlds").contains(world);
    }

    public boolean moneyPerKillEnabled(String server) {
        return plugin.getConfig().getStringList("Economy.MoneyPerKill.EnabledWorlds").contains(server);
    }

    public double getMoneyPerKill() {
        double highest = plugin.getConfig().getDouble("Economy.MoneyPerKill.Highest");
        double lowest = plugin.getConfig().getDouble("Economy.MoneyPerKill.Lowest");

        double range = (highest - lowest) + 1;

        double outcome = (Math.random() * range) + lowest;

        return outcome;
    }
}
