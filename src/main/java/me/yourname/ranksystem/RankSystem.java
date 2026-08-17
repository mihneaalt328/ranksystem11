package me.yourname.ranksystem;

import me.yourname.ranksystem.commands.RankCommand;
import me.yourname.ranksystem.commands.SetPermsCommand;
import me.yourname.ranksystem.commands.SetRankCommand;
import me.yourname.ranksystem.commands.RankTabCompleter;
import me.yourname.ranksystem.listeners.ChatListener;
import me.yourname.ranksystem.listeners.PlayerListener;
import me.yourname.ranksystem.managers.PermissionManager;
import me.yourname.ranksystem.managers.PlayerRankManager;
import me.yourname.ranksystem.managers.RankManager;
import me.yourname.ranksystem.managers.TabManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RankSystem extends JavaPlugin {

    private RankManager rankManager;
    private PlayerRankManager playerRankManager;
    private PermissionManager permissionManager;
    private TabManager tabManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("ranks.yml", false);
        saveResource("players.yml", false);

        rankManager = new RankManager(this);
        playerRankManager = new PlayerRankManager(this, rankManager);
        permissionManager = new PermissionManager(this, rankManager, playerRankManager);
        tabManager = new TabManager(this, rankManager, playerRankManager);

        getCommand("rank").setExecutor(new RankCommand(this));
        getCommand("setrank").setExecutor(new SetRankCommand(this));
        getCommand("setperms").setExecutor(new SetPermsCommand(this));
        getCommand("rank").setTabCompleter(new RankTabCompleter(this));

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info("RankSystem 1.0.1 enabled.");
    }

    @Override
    public void onDisable() {
        if (playerRankManager != null) playerRankManager.save();
        getLogger().info("RankSystem disabled.");
    }

    public RankManager getRankManager() { return rankManager; }
    public PlayerRankManager getPlayerRankManager() { return playerRankManager; }
    public PermissionManager getPermissionManager() { return permissionManager; }
    public TabManager getTabManager() { return tabManager; }
}
