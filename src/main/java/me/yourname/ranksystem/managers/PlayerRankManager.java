package me.yourname.ranksystem.managers;

import me.yourname.ranksystem.RankSystem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerRankManager {
    private final RankSystem plugin;
    private final RankManager rankManager;
    private final File file;
    private FileConfiguration config;

    public PlayerRankManager(RankSystem plugin, RankManager rankManager) {
        this.plugin = plugin;
        this.rankManager = rankManager;
        this.file = new File(plugin.getDataFolder(), "players.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public String getRank(UUID uuid) {
        String rank = config.getString("players." + uuid + ".rank");
        if (rank == null || !rankManager.exists(rank)) {
            return plugin.getConfig().getString("settings.default-rank", "Default");
        }
        return rank;
    }

    public void setRank(UUID uuid, String rank) {
        config.set("players." + uuid + ".rank", rank);
        save();
    }

    public void removeRank(UUID uuid) {
        config.set("players." + uuid, null);
        save();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save players.yml: " + e.getMessage());
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }
}
