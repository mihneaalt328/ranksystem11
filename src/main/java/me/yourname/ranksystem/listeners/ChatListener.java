package me.yourname.ranksystem.listeners;

import me.yourname.ranksystem.RankSystem;
import me.yourname.ranksystem.utils.ColorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final RankSystem plugin;

    public ChatListener(RankSystem plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String rank = plugin.getPlayerRankManager().getRank(player.getUniqueId());
        String prefix = ColorUtils.color(plugin.getRankManager().getPrefix(rank));
        String suffix = ColorUtils.color(plugin.getRankManager().getSuffix(rank));

        String format = plugin.getConfig().getString(
                "settings.chat-format",
                "{prefix} &f{player}{suffix}&7: &r{message}"
        );

        format = format
                .replace("{prefix}", prefix)
                .replace("{player}", player.getName())
                .replace("{suffix}", suffix)
                .replace("{message}", "%2$s");

        event.setFormat(ColorUtils.color(format));
    }
}
