package me.yourname.ranksystem.managers;

import me.yourname.ranksystem.RankSystem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RankManager {
    private final RankSystem plugin;
    private final File file;
    private FileConfiguration config;

    public RankManager(RankSystem plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "ranks.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean exists(String rank) {
        return rank != null && config.isConfigurationSection("ranks." + rank);
    }

    public Set<String> getRanks() {
        ConfigurationSection section = config.getConfigurationSection("ranks");
        return section == null ? new TreeSet<String>(String.CASE_INSENSITIVE_ORDER) : section.getKeys(false);
    }

    public String getPrefix(String rank) {
        return config.getString("ranks." + rank + ".prefix", "&7[" + rank + "]");
    }

    public String getSuffix(String rank) {
        return config.getString("ranks." + rank + ".suffix", "");
    }

    public int getPriority(String rank) {
        return config.getInt("ranks." + rank + ".priority", 0);
    }

    public List<String> getPermissions(String rank) {
        return config.getStringList("ranks." + rank + ".permissions");
    }

    public List<String> getParents(String rank) {
        return config.getStringList("ranks." + rank + ".inherits");
    }

    public Set<String> getAllPermissions(String rank) {
        Set<String> result = new LinkedHashSet<String>();
        collectPermissions(rank, result, new HashSet<String>());
        return result;
    }

    private void collectPermissions(String rank, Set<String> result, Set<String> visited) {
        if (!exists(rank) || !visited.add(rank)) return;
        result.addAll(getPermissions(rank));
        for (String parent : getParents(rank)) collectPermissions(parent, result, visited);
    }

    public void createRank(String rank) {
        config.set("ranks." + rank + ".priority", 0);
        config.set("ranks." + rank + ".prefix", "&7[" + rank + "]");
        config.set("ranks." + rank + ".suffix", "");
        config.set("ranks." + rank + ".inherits", new ArrayList<String>());
        config.set("ranks." + rank + ".permissions", new ArrayList<String>());
        save();
    }

    public void deleteRank(String rank) {
        config.set("ranks." + rank, null);
        save();
    }

    public void setPriority(String rank, int priority) {
        config.set("ranks." + rank + ".priority", priority);
        save();
    }

    public void setPrefix(String rank, String prefix) {
        config.set("ranks." + rank + ".prefix", prefix);
        save();
    }

    public void setSuffix(String rank, String suffix) {
        config.set("ranks." + rank + ".suffix", suffix);
        save();
    }

    public void addPermission(String rank, String permission) {
        List<String> list = getPermissions(rank);
        if (!list.contains(permission)) list.add(permission);
        config.set("ranks." + rank + ".permissions", list);
        save();
    }

    public void removePermission(String rank, String permission) {
        List<String> list = getPermissions(rank);
        list.remove(permission);
        config.set("ranks." + rank + ".permissions", list);
        save();
    }

    public void addParent(String rank, String parent) {
        List<String> parents = getParents(rank);
        if (!parents.contains(parent)) parents.add(parent);
        config.set("ranks." + rank + ".inherits", parents);
        save();
    }

    public void removeParent(String rank, String parent) {
        List<String> parents = getParents(rank);
        parents.remove(parent);
        config.set("ranks." + rank + ".inherits", parents);
        save();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save ranks.yml: " + e.getMessage());
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }
}
