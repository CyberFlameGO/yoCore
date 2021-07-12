package me.yochran.yocore;

import me.yochran.yocore.commands.*;
import me.yochran.yocore.commands.bungee.*;
import me.yochran.yocore.commands.economy.BalanceCommand;
import me.yochran.yocore.commands.economy.BountyCommand;
import me.yochran.yocore.commands.economy.PayCommand;
import me.yochran.yocore.commands.economy.staff.EconomyCommand;
import me.yochran.yocore.commands.economy.staff.UnbountyCommand;
import me.yochran.yocore.commands.punishments.*;
import me.yochran.yocore.commands.staff.*;
import me.yochran.yocore.commands.stats.StatsCommand;
import me.yochran.yocore.commands.stats.staff.ResetStatsCommand;
import me.yochran.yocore.data.*;
import me.yochran.yocore.listeners.*;
import me.yochran.yocore.management.PermissionManagement;
import me.yochran.yocore.management.ServerManagement;
import me.yochran.yocore.runnables.*;
import me.yochran.yocore.scoreboard.ScoreboardSetter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class yoCore extends JavaPlugin {

    public PlayerData playerData;
    public PunishmentData punishmentData;
    public GrantData grantData;
    public StatsData statsData;
    public EconomyData economyData;
    public PermissionsData permissionsData;
    public WorldData worldData;

    private PermissionManagement permissionManagement;
    private final PluginManager manager = getServer().getPluginManager();

    public boolean chat_muted;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().setWhitelist(true);
        Bukkit.getConsoleSender().sendMessage("yoCore v1.0 by Yochran is loading...");

        permissionManagement = new PermissionManagement();

        saveDefaultConfig();
        registerData();
        registerCommands();
        registerListeners();
        runRunnables();
        refreshPunishments();
        registerRanks();
        registerTags();

        permissionManagement.initialize();
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (player_permissions.containsKey(players.getUniqueId()))
                permissionManagement.setupPlayer(players);

            permissionManagement.refreshPlayer(players);
        }

        chat_muted = false;

        new BukkitRunnable() {
            @Override
            public void run() { registerLastLocations(); }
        }.runTaskLater(this, 20 * 5);

        Bukkit.getConsoleSender().sendMessage("yoCore v1.0 by Yochran has successfully loaded.");
        getServer().setWhitelist(false);
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (last_location.get(player.getUniqueId()) == null) {
                Map<String, Location> location = new HashMap<>();
                location.put(new ServerManagement().getServer(player), player.getLocation());
                last_location.put(player.getUniqueId(), location);
            }

            last_location.get(player.getUniqueId()).put(new ServerManagement().getServer(player), player.getLocation());
        }

        for (Map.Entry<UUID, Map<String, Location>> entry : last_location.entrySet())
            for (Map.Entry<String, Location> data : entry.getValue().entrySet()) {
                playerData.config.set(entry.getKey().toString() + ".LastLocation." + data.getKey() + ".World", data.getValue().getWorld().getName());
                playerData.config.set(entry.getKey().toString() + ".LastLocation." + data.getKey() + ".X", data.getValue().getX());
                playerData.config.set(entry.getKey().toString() + ".LastLocation." + data.getKey() + ".Y", data.getValue().getY());
                playerData.config.set(entry.getKey().toString() + ".LastLocation." + data.getKey() + ".Z", data.getValue().getZ());
                playerData.config.set(entry.getKey().toString() + ".LastLocation." + data.getKey() + ".Yaw", data.getValue().getYaw());
                playerData.config.set(entry.getKey().toString() + ".LastLocation." + data.getKey() + ".Pitch", data.getValue().getPitch());
            }

        playerData.saveData();
    }

    public List<String> ranks = new ArrayList<>();
    public List<String> tags = new ArrayList<>();
    public List<UUID> vanished_players = new ArrayList<>();
    public List<UUID> vanish_logged = new ArrayList<>();
    public List<UUID> staff_alerts = new ArrayList<>();
    public List<UUID> frozen_players = new ArrayList<>();
    public List<UUID> frozen_cooldown = new ArrayList<>();
    public List<UUID> modmode_players = new ArrayList<>();
    public List<UUID> buildmode_players = new ArrayList<>();
    public List<UUID> message_toggled = new ArrayList<>();
    public List<UUID> message_sounds_toggled = new ArrayList<>();
    public List<UUID> chat_toggled = new ArrayList<>();
    public List<UUID> tsb = new ArrayList<>();
    public List<UUID> grant_custom_reason = new ArrayList<>();
    public List<UUID> bchat_toggle = new ArrayList<>();
    public List<UUID> schat_toggle = new ArrayList<>();
    public List<UUID> achat_toggle = new ArrayList<>();
    public List<UUID> mchat_toggle = new ArrayList<>();

    public Map<UUID, ItemStack[]> inventory_contents = new HashMap<>();
    public Map<UUID, ItemStack[]> armor_contents = new HashMap<>();
    public Map<UUID, List<Double>> frozen_coordinates = new HashMap<>();
    public Map<UUID, UUID> reply = new HashMap<>();
    public Map<UUID, String> chat_color = new HashMap<>();
    public Map<UUID, Boolean> muted_players = new HashMap();
    public Map<UUID, Boolean> banned_players = new HashMap<>();
    public Map<UUID, String> blacklisted_players = new HashMap<>();
    public Map<UUID, UUID> selected_history = new HashMap<>();
    public Map<UUID, UUID> selected_grant_history = new HashMap<>();
    public Map<UUID, UUID> grant_player = new HashMap<>();
    public Map<UUID, String> grant_grant = new HashMap<>();
    public Map<UUID, String> grant_type = new HashMap<>();
    public Map<UUID, String> grant_duration = new HashMap<>();
    public Map<UUID, String> grant_reason = new HashMap<>();
    public Map<UUID, String> rank_disguise = new HashMap<>();
    public Map<UUID, String> nickname = new HashMap<>();
    public Map<UUID, String> tag = new HashMap<>();
    public Map<UUID, PermissionAttachment> player_permissions = new HashMap<>();
    public Map<UUID, String> powertool_command = new HashMap<>();
    public Map<UUID, Material> powertool_material = new HashMap<>();
    public Map<UUID, UUID> tpa = new HashMap<>();
    public Map<UUID, Location> tpa_coords = new HashMap<>();
    public Map<UUID, Integer> tpa_timer = new HashMap<>();
    public Map<UUID, Map<String, Location>> last_location = new HashMap<>();

    private void registerListeners() {
        manager.registerEvents(new PlayerLogListener(), this);
        manager.registerEvents(new PlayerChatListener(), this);
        manager.registerEvents(new GUIClickListener(), this);
        manager.registerEvents(new GUIExitListener(), this);
        manager.registerEvents(new VanishCheckListeners(), this);
        manager.registerEvents(new ModmodeListeners(), this);
        manager.registerEvents(new FreezeListener(), this);
        manager.registerEvents(new BuildModeListener(), this);
        manager.registerEvents(new ListCommand(), this);
        manager.registerEvents(new PlayerDeathListener(), this);
        manager.registerEvents(new ScoreboardSetter(), this);
        manager.registerEvents(new WorldChangeListener(), this);
        manager.registerEvents(new GrantCustomReasonListener(), this);
        manager.registerEvents(new PlayerInteractListener(), this);
        manager.registerEvents(new TPAListener(), this);
    }

    private void runRunnables() {
        new MuteUpdater().runTaskTimer(this, 10, 20);
        new BanUpdater().runTaskTimer(this, 10, 20);
        new GrantUpdater().runTaskTimer(this, 10, 20);
        new VanishUpdater().runTaskTimer(this, 10, 10);
        new TPAUpdater().runTaskTimer(this, 10, 20);
        if (getConfig().getBoolean("Nametags.Enabled")) new NametagUpdater().runTaskTimer(this, 0, 5);
        if (getConfig().getBoolean("Scoreboard.Enabled")) new ScoreboardUpdater().runTaskTimer(this, 0, 5);
        if (getConfig().getBoolean("Servers.WorldSeparation")) new WorldSeparator().runTaskTimer(this, 0, 5);
    }

    private void registerData() {
        playerData = new PlayerData();
        playerData.setupData();
        playerData.saveData();
        playerData.reloadData();

        punishmentData = new PunishmentData();
        punishmentData.setupData();
        punishmentData.saveData();
        punishmentData.reloadData();

        grantData = new GrantData();
        grantData.setupData();
        grantData.saveData();
        grantData.reloadData();

        statsData = new StatsData();
        statsData.setupData();
        statsData.saveData();
        statsData.reloadData();

        economyData = new EconomyData();
        economyData.setupData();
        economyData.saveData();
        economyData.reloadData();

        permissionsData = new PermissionsData();
        permissionsData.setupData();
        permissionsData.saveData();
        permissionsData.reloadData();

        worldData = new WorldData();
        worldData.setupData();
        worldData.saveData();
        worldData.reloadData();

        if (!worldData.config.contains("Servers")) {
            List<String> worlds = new ArrayList<>();
            for (World world : Bukkit.getWorlds())
                worlds.add(world.getName());

            worldData.config.set("Servers.SERVER.ID", "SERVER");
            worldData.config.set("Servers.SERVER.Name", "server");
            worldData.config.set("Servers.SERVER.Worlds", worlds);
            worldData.config.set("Servers.SERVER.Spawn.World", "world");
            worldData.config.set("Servers.SERVER.Spawn.X", 0.5);
            worldData.config.set("Servers.SERVER.Spawn.Y", 75.0);
            worldData.config.set("Servers.SERVER.Spawn.Z", 0.5);
            worldData.config.set("Servers.SERVER.Spawn.Yaw", 0.0);
            worldData.config.set("Servers.SERVER.Spawn.Pitch", 0.0);
            worldData.saveData();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                playerData.saveData();
                punishmentData.saveData();
                grantData.saveData();
                economyData.saveData();
                statsData.saveData();
                permissionsData.saveData();
                worldData.saveData();
            }
        }.runTaskLater(this, 10);
    }

    private void registerRanks() {
        for (Permission grant_permission : permissionManagement.getAllServerPerms())
            if (grant_permission.getName().contains("yocore.grant.")) getServer().getPluginManager().removePermission(grant_permission);

        for (String rank : getConfig().getConfigurationSection("Ranks").getKeys(false)) {
            Permission permission = new Permission("yocore.grant." + getConfig().getString("Ranks." + rank + ".ID").toLowerCase());

            if (!permissionManagement.getAllServerPerms().contains(permission)) {
                permission.setDescription("Permission");
                getServer().getPluginManager().addPermission(permission);
            }

            ranks.add(getConfig().getString("Ranks." + rank + ".ID"));
        }
    }

    private void registerTags() {
        for (Permission tag_permission : getServer().getPluginManager().getPermissions()) {
            if (tag_permission.getName().contains("yocore.tags.")) getServer().getPluginManager().removePermission(tag_permission);
        }

        for (String tag : getConfig().getConfigurationSection("Tags").getKeys(false)) {
            Permission permission = new Permission(getConfig().getString("Tags." + tag + ".Permission"));

            if (!permissionManagement.getAllServerPerms().contains(permission)) {
                permission.setDescription("Permission");
                getServer().getPluginManager().addPermission(permission);
            }

            tags.add(getConfig().getString("Tags." + tag + ".ID"));
        }
    }

    private void refreshPunishments() {
        if (punishmentData.config.contains("MutedPlayers")) {
            for (String player : punishmentData.config.getConfigurationSection("MutedPlayers").getKeys(false))
                muted_players.put(UUID.fromString(player), punishmentData.config.getBoolean("MutedPlayers." + player + ".Temporary"));
        }

        if (punishmentData.config.contains("BannedPlayers")) {
            for (String player : punishmentData.config.getConfigurationSection("BannedPlayers").getKeys(false))
                banned_players.put(UUID.fromString(player), punishmentData.config.getBoolean("BannedPlayers." + player + ".Temporary"));
        }

        if (punishmentData.config.contains("BlacklistedPlayers")) {
            for (String player : punishmentData.config.getConfigurationSection("BlacklistedPlayers").getKeys(false))
                banned_players.put(UUID.fromString(player), punishmentData.config.getBoolean("BlacklistedPlayers." + player + ".Reason"));
        }
    }

    private void registerLastLocations() {
        for (String player : playerData.config.getKeys(false)) {
            if (playerData.config.contains(player + ".LastLocation")) {
                last_location.put(UUID.fromString(player), new HashMap<>());
                for (String server : playerData.config.getConfigurationSection(player + ".LastLocation").getKeys(false)) {
                    String world = playerData.config.getString(player + ".LastLocation." + server + ".World");
                    double X = playerData.config.getDouble(player + ".LastLocation." + server + ".X");
                    double Y = playerData.config.getDouble(player + ".LastLocation." + server + ".Y");
                    double Z = playerData.config.getDouble(player + ".LastLocation." + server + ".Z");
                    double Yaw = playerData.config.getDouble(player + ".LastLocation." + server + ".Yaw");
                    double Pitch = playerData.config.getDouble(player + ".LastLocation." + server + ".Pitch");
                    last_location.get(UUID.fromString(player)).put(server,
                            new Location(Bukkit.getWorld(world), X, Y, Z, (float) Yaw, (float) Pitch));
                }
            }

            playerData.config.set(player + ".LastLocation", null);
        }

        playerData.saveData();
    }

    private void registerCommands() {
        getCommand("Setrank").setExecutor(new SetrankCommand());
        getCommand("Warn").setExecutor(new WarnCommand());
        getCommand("Kick").setExecutor(new KickCommand());
        getCommand("Mute").setExecutor(new MuteCommand());
        getCommand("Unmute").setExecutor(new UnmuteCommand());
        getCommand("Tempmute").setExecutor(new TempmuteCommand());
        getCommand("Ban").setExecutor(new BanCommand());
        getCommand("Unban").setExecutor(new UnbanCommand());
        getCommand("Tempban").setExecutor(new TempbanCommand());
        getCommand("Blacklist").setExecutor(new BlacklistCommand());
        getCommand("Unblacklist").setExecutor(new UnblacklistCommand());
        getCommand("History").setExecutor(new HistoryCommand());
        getCommand("ClearHistory").setExecutor(new ClearHistoryCommand());
        getCommand("Grant").setExecutor(new GrantCommand());
        getCommand("Grants").setExecutor(new GrantsCommand());
        getCommand("Ungrant").setExecutor(new UngrantCommand());
        getCommand("ClearGrantHistory").setExecutor(new ClearGrantHistoryCommand());
        getCommand("StaffChat").setExecutor(new StaffChatCommand());
        getCommand("AdminChat").setExecutor(new AdminChatCommand());
        getCommand("ManagementChat").setExecutor(new ManagementChatCommand());
        getCommand("Vanish").setExecutor(new VanishCommand());
        getCommand("ToggleStaffAlerts").setExecutor(new ToggleStaffAlertsCommand());
        getCommand("Gamemode").setExecutor(new GamemodeCommands());
        getCommand("Gmc").setExecutor(new GamemodeCommands());
        getCommand("Gms").setExecutor(new GamemodeCommands());
        getCommand("Gmsp").setExecutor(new GamemodeCommands());
        getCommand("Gma").setExecutor(new GamemodeCommands());
        getCommand("Heal").setExecutor(new HealCommand());
        getCommand("Feed").setExecutor(new FeedCommand());
        getCommand("Clear").setExecutor(new ClearCommand());
        getCommand("ClearChat").setExecutor(new ClearChatCommand());
        getCommand("MuteChat").setExecutor(new MuteChatCommand());
        getCommand("Fly").setExecutor(new FlyCommand());
        getCommand("Teleport").setExecutor(new TeleportCommands());
        getCommand("TeleportHere").setExecutor(new TeleportCommands());
        getCommand("TeleportAll").setExecutor(new TeleportCommands());
        getCommand("Modmode").setExecutor(new ModmodeCommand());
        getCommand("Freeze").setExecutor(new FreezeCommand());
        getCommand("Report").setExecutor(new ReportCommand());
        getCommand("BuildMode").setExecutor(new BuildModeCommand());
        getCommand("ToggleMessages").setExecutor(new ToggleMessagesCommand());
        getCommand("Alts").setExecutor(new AltsCommand());
        getCommand("OnlinePlayers").setExecutor(new ListCommand());
        getCommand("Invsee").setExecutor(new InvseeCommand());
        getCommand("Rank").setExecutor(new RankCommand());
        getCommand("ChatColor").setExecutor(new ChatColorCommand());
        getCommand("Broadcast").setExecutor(new BroadcastCommand());
        getCommand("Settings").setExecutor(new SettingsCommand());
        getCommand("Speed").setExecutor(new SpeedCommand());
        getCommand("Sudo").setExecutor(new SudoCommand());
        getCommand("Balance").setExecutor(new BalanceCommand());
        getCommand("Bounty").setExecutor(new BountyCommand());
        getCommand("Unbounty").setExecutor(new UnbountyCommand());
        getCommand("Pay").setExecutor(new PayCommand());
        getCommand("Economy").setExecutor(new EconomyCommand());
        getCommand("Stats").setExecutor(new StatsCommand());
        getCommand("ResetStats").setExecutor(new ResetStatsCommand());
        getCommand("ToggleScoreboard").setExecutor(new ToggleScoreboardCommand());
        getCommand("EnderChest").setExecutor(new EnderChestCommand());
        getCommand("ReWarn").setExecutor(new ReWarnCommand());
        getCommand("ReMute").setExecutor(new ReMuteCommand());
        getCommand("ReTempMute").setExecutor(new ReTempmuteCommand());
        getCommand("ReBan").setExecutor(new ReBanCommand());
        getCommand("ReTempBan").setExecutor(new ReTempbanCommand());
        getCommand("ReBlacklist").setExecutor(new ReBlacklistCommand());
        getCommand("ServerManager").setExecutor(new ServerManagerCommand());
        getCommand("Ping").setExecutor(new PingCommand());
        getCommand("Reports").setExecutor(new ReportsCommand());
        getCommand("ClearReports").setExecutor(new ClearReportsCommand());
        getCommand("Seen").setExecutor(new SeenCommand());
        getCommand("RankDisguise").setExecutor(new RankDisguiseCommand());
        getCommand("Nickname").setExecutor(new NickCommand());
        getCommand("RealName").setExecutor(new RealNameCommand());
        getCommand("Tags").setExecutor(new TagsCommand());
        getCommand("Tag").setExecutor(new TagCommand());
        getCommand("User").setExecutor(new UserCommand());
        getCommand("Powertool").setExecutor(new PowertoolCommand());
        getCommand("Find").setExecutor(new FindCommand());
        getCommand("Server").setExecutor(new ServerCommand());
        getCommand("Send").setExecutor(new SendCommand());
        getCommand("Glist").setExecutor(new GListCommand());
        getCommand("Hub").setExecutor(new HubCommand());
        getCommand("Spawn").setExecutor(new SpawnCommand());
        getCommand("Skull").setExecutor(new SkullCommand());
        getCommand("ItemName").setExecutor(new ItemNameCommand());
        getCommand("TeleportA").setExecutor(new TeleportCommands());
        getCommand("TeleportAccept").setExecutor(new TeleportCommands());
        getCommand("TeleportDeny").setExecutor(new TeleportCommands());
        getCommand("TeleportCancel").setExecutor(new TeleportCommands());
        getCommand("BuildChat").setExecutor(new BuilderChatCommand());
        getCommand("Message").setExecutor(new MessageCommand());
        getCommand("Reply").setExecutor(new ReplyCommand());
    }
}