package me.yourname.ranksystem.commands;

import me.yourname.ranksystem.RankSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetRankCommand implements CommandExecutor {
    private final RankSystem plugin;
    public SetRankCommand(RankSystem plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ranksystem.admin")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /setrank <player> <rank>");
            return true;
        }
        if (!plugin.getRankManager().exists(args[1])) {
            sender.sendMessage("§cThat rank does not exist.");
            return true;
        }
        org.bukkit.OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);
        plugin.getPlayerRankManager().setRank(target.getUniqueId(), args[1]);
        if (target.isOnline()) {
            plugin.getPermissionManager().apply(target.getPlayer());
            plugin.getTabManager().update(target.getPlayer());
        }
        sender.sendMessage("§aSet §f" + target.getName() + "§a's rank to §f" + args[1] + "§a.");
        return true;
    }
}
