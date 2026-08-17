package me.yourname.ranksystem.managers;

import me.yourname.ranksystem.RankSystem;
import me.yourname.ranksystem.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TabManager {
    private final RankSystem plugin;
    private final RankManager rankManager;
    private final PlayerRankManager playerRankManager;

    public TabManager(RankSystem plugin, RankManager rankManager, PlayerRankManager playerRankManager) {
        this.plugin = plugin;
        this.rankManager = rankManager;
        this.playerRankManager = playerRankManager;
    }

    public void update(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        String rank = playerRankManager.getRank(player.getUniqueId());
        String teamName = teamName(rank);
        Team team = board.getTeam(teamName);
        if (team == null) team = board.registerNewTeam(teamName);

        String prefix = ColorUtils.color(rankManager.getPrefix(rank) + " ");
        if (prefix.length() > 16) prefix = prefix.substring(0, 16);

        String suffix = ColorUtils.color(" " + rankManager.getSuffix(rank));
        if (suffix.length() > 16) suffix = suffix.substring(0, 16);

        team.setPrefix(prefix);
        team.setSuffix(suffix);

        for (Team other : board.getTeams()) {
            if (other != team && other.hasPlayer(player)) other.removePlayer(player);
        }
        if (!team.hasPlayer(player)) team.addPlayer(player);
        player.setScoreboard(board);
    }

    private String teamName(String rank) {
        int value = Math.max(0, 999 - rankManager.getPriority(rank));
        String clean = rank.replaceAll("[^A-Za-z0-9_]", "");
        String result = String.format("%03d_", value) + clean;
        return result.length() > 16 ? result.substring(0, 16) : result;
    }
}
