package me.yourname.ranksystem.managers;

import me.yourname.ranksystem.RankSystem;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;

public class PermissionManager {
    private final RankSystem plugin;
    private final RankManager rankManager;
    private final PlayerRankManager playerRankManager;
    private final Map<Player, PermissionAttachment> attachments = new HashMap<Player, PermissionAttachment>();

    public PermissionManager(RankSystem plugin, RankManager rankManager, PlayerRankManager playerRankManager) {
        this.plugin = plugin;
        this.rankManager = rankManager;
        this.playerRankManager = playerRankManager;
    }

    public void apply(Player player) {
        clear(player);
        PermissionAttachment attachment = player.addAttachment(plugin);
        String rank = playerRankManager.getRank(player.getUniqueId());

        for (String permission : rankManager.getAllPermissions(rank)) {
            if (permission == null || permission.trim().isEmpty()) continue;
            permission = permission.toLowerCase();
            if (permission.equals("*")) {
                attachment.setPermission("*", true);
            } else {
                attachment.setPermission(permission, true);
            }
        }

        attachments.put(player, attachment);
    }

    public void clear(Player player) {
        PermissionAttachment old = attachments.remove(player);
        if (old != null) player.removeAttachment(old);
    }
}
