package me.yourname.ranksystem.listeners;

import me.yourname.ranksystem.RankSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final RankSystem plugin;

    public PlayerListener(RankSystem plugin) { this.plugin = plugin; }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getPermissionManager().apply(event.getPlayer());
        plugin.getTabManager().update(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPermissionManager().clear(event.getPlayer());
    }
}
